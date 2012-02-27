/**
 * Core javascript for GOCI Tracker.  This contains most of the scripts for initializing the GOCI Tracker UI and
 * performing ajax calls on submissions etc.
 *
 * Note that this requires jQuery to be present for UI effects and ajax functionality
 *
 * @author Tony Burdett
 * @date 27th September 2010
 */

// cached global variables to minimise ajax requests
var userMap;
var states;
var eligibilities;
var publications;
var publicationsTimeoutID;

/*
 *
 *
 * Initialisation functions for GOCI UI =====>
 *
 *
 */

/**
 * Does initialisation of the main GOCI interface.  This configures the broad aspects of the UI (progress tables etc)
 * given the current options, and authenticates the current user before using callback functions to display the
 * pipelines that the logged in user can access.
 */
function initUI() {
    $(document).ready(function () {
        // setup the goci ui
        configureUI();
        // authenticate users
        authenticate({
                         error:requestUser,
                         success:requestPublications
                     })
    });
}

/**
 * Do any initial setup for the GOCI UI.  This configures jQuery UI plugin effects, datatables plugin and (optionally)
 * jCarousel plugin, if the user selects a pipeline that requires multiple parameters.
 */
function configureUI() {
    // create datatables
    $("#goci-publications-table").dataTable({
                                                "aaSorting":[
                                                    [ 0, "asc" ]
                                                ],
                                                "bPaginate":false,
                                                "bStateSave":true,
                                                "bSort":true,
                                                "bInfo":false,
                                                "fnDrawCallback":redrawPublicationsTableLater,
                                                "sScrollY":"400px"
                                            });

    // add extra classes to search boxes
    $(".dataTables_filter input").addClass("ui-widget ui-widget-content");

    // add search icon after this
    $(".dataTables_filter input").after("<span class=\"ui-icon ui-icon-search\"></span>");

    // add icons to relevant buttons
    $(".first").prepend("<div style=\"float: left; margin-right: 0.3em;\" class=\"ui-icon ui-icon-seek-start\"></div>");
    $(".first").addClass("ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-secondary");
    $(".previous")
            .prepend("<div style=\"float: left; margin-right: 0.3em;\" class=\"ui-icon ui-icon-seek-prev\"></div>");
    $(".previous").addClass("ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-secondary");
    $(".next").prepend("<div style=\"float: right; margin-left: 0.3em;\" class=\"ui-icon ui-icon-seek-next\"></div>");
    $(".next").addClass("ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-secondary");
    $(".last").prepend("<div style=\"float: right; margin-left: 0.3em;\" class=\"ui-icon ui-icon-seek-end\"></div>");
    $(".last").addClass("ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-secondary");

    // add button hover states
    $(".ui-button").hover(
            function () {
                $(this).addClass('ui-state-hover');
            },
            function () {
                $(this).removeClass('ui-state-hover');
            }
    );

    // fetch users, states, eligibilities
    requestUsers();
    requestStates();
    requestEligibilities();

    $("#goci-owner-dialog").dialog({
                                       autoOpen:false
                                   });
    $("#goci-state-dialog").dialog({
                                       autoOpen:false
                                   });
    $("#goci-eligibility-dialog").dialog({
                                             autoOpen:false
                                         });
    $("#goci-error-dialog").dialog({
                                       autoOpen:false,
                                       modal:true,
                                       dialogClass:'alert',
                                       buttons:{
                                           Ok:function () {
                                               $(this).dialog("close");
                                           }
                                       }
                                   });
}

/*
 *
 *
 * Ajax request functions ======>
 *
 *
 */

/**
 * Requests the user with this email from the server.  This retrieves a JSON object representing a known user if the
 * user with this email was found, and forwards that user to a callback request to retrieve the users REST API key.
 * This double call is necessary because REST API keys for all known users are not publicly visible (e.g. from a
 * pipeline creator).
 */
function requestUser() {
    // try to get users email from goci-user-email-address, if possible
    var email = $("#goci-user-email-address").val();
    if (email == undefined || email == "") {
        // we can't get any user details, make sure we're showing login form
        $("#goci-publications-loading").show();
        $("#goci-publications-content").hide();
        $("#goci-login-overlay").show();
        $("#goci-registration-overlay").hide();
    }
    else {
        // user email address has been obtained, so remove login panel and display the loading one
        $("#goci-publications-loading").show();
        $("#goci-publications-content").hide();
        $("#goci-login-overlay").hide();
        $("#goci-registration-overlay").hide();

        // we've got an email, can we get a rest api key?
        var emailStr = encodeURIComponent(email);
        // send email with ajax request, but register error handler to detect failure to communicate with server
        $.ajax({
                   url:'api/users/query?email=' + emailStr,
                   dataType:'json',
                   success:loginCallback,
                   error:serverCommunicationFail
               });
    }
}

function startRegistration() {
    $("#goci-login-overlay").hide();
    $("#goci-registration-overlay").show();
}

/**
 * Register and create a new user for the GOCI tracking system.  This checks that all required data is provided, and
 * that no users with the given email and username already exist.  If these conditions are met, a new user is created.
 */
function registerNewUser() {
    // get fields
    var email = $("#goci-register-email").val();
    var firstname = $("#goci-register-firstname").val();
    var surname = $("#goci-register-surname").val();

    var allSupplied = true;
    if (email == undefined || email == "") {
        allSupplied = false;
        $("#email-missing").show();
    }
    else {
        $("#email-missing").hide();
    }
    if (firstname == undefined || firstname == "") {
        allSupplied = false;
        $("#firstname-missing").show();
    }
    else {
        $("#firstname-missing").hide();
    }
    if (surname == undefined || surname == "") {
        allSupplied = false;
        $("#surname-missing").show();
    }
    else {
        $("#surname-missing").hide();
    }

    // all required data supplied?
    if (!allSupplied) {
        $("#missing-data-error-message").show();
    }
    else {
        $("#username-missing").hide();
        $("#email-missing").hide();
        $("#firstname-missing").hide();
        $("#surname-missing").hide();
        $("#missing-data-error-message").hide();
        $("#email-exists-error-message").hide();

        var emailStr = encodeURIComponent(email);
        // send email with ajax request, but register error handler to detect failure to communicate with server
        $.ajax({
                   url:'api/users/query?email=' + emailStr,
                   dataType:'json',
                   success:createUserIfNoneExists,
                   error:serverCommunicationFail
               });
    }
}

function createUserIfNoneExists(userJson) {
    if (userJson.id == undefined || userJson.id == "") {
        // ok to create
        var jsonString = "{" +
                "\"firstName\":\"" + $("#goci-register-firstname").val() + "\", " +
                "\"surname\":\"" + $("#goci-register-surname").val() + "\", " +
                "\"email\":\"" + $("#goci-register-email").val() + "\"}";
        $.ajax({
                   type:'POST',
                   url:'api/users',
                   contentType:'application/json',
                   data:jsonString,
                   processData:false,
                   success:loginCallback,
                   error:function (request, status, error) {
                       // inform user by showing the dialog
                       alert("Error:" + error);
                   }
               });
        // hide the registration overlay
        $("#goci-registration-overlay").hide();
        // and update users list
        requestUsers();
    }
    else {
        // already exists
        $("#email-missing").show();
        $("#email-exists-error-message").show();
    }
}

/**
 * A callback function that grabs a rest api key for a user obtained by email login
 * @param userJson
 */
function loginCallback(userJson) {
    obtainRestApiKey(userJson, {error:requestUser, success:requestPublications});
}

/**
 * Displays an error box if we got a failure response from a pipelines request from the server.
 */
function serverCommunicationFail() {
    // couldn't find the server, so all ajax content will be empty.  To stop it looking ugly and show whats wrong,
    // show an error instead of empty pipeline dropdowns
    alert("Failed to communicate with server.  Some details may be missing from the interface.  Please notify the system administrator.");
    $("#goci-publications-loading").show();
    $("#goci-publications-content").hide();
    $("#goci-login-overlay").hide();
    $("#goci-guest-greeting").show();
}

/**
 * Requests a list of tasks that are currently pending, via an ajax request to the server.
 */
function requestPublications() {
    $("#goci-publications-loading").show();
    $("#goci-publications-content").hide();
    $.getJSON('api/studies?processable=true', function (json) {
        publications = json;
        displayPublications();
    })
}

function registerNewPublication() {
    var pubmedID = $('#goci-pubmed-submission').val();
    // create a POST request to /api/studies
    $.ajax({
               type:'POST',
               url:'api/studies?pubmedID=' + pubmedID,
               contentType:'application/json',
               success:function (response) {
                   if (response.operationSuccessful) {
                       requestPublications();
                   }
                   else {
                       // get error message and show alert
                       $("#goci-error-text").html(response.statusMessage);
                       $("#goci-error-dialog").dialog("open");
                   }
               },
               error:function (request, status, error) {
                   // inform user by showing the dialog
                   alert("Error:" + error);
               }
           });

    $('#goci-pubmed-submission').val("");
}

function requestUsers() {
    userMap = new Object();
    $.ajax({
               url:'api/users',
               dataType:'json',
               success:function (json) {
                   // create a list of userNames to provide to autocomplete
                   var userNames = new Array();

                   // populate the submitter map, linking each user name to user ID
                   for (var i = 0; i < json.length; i++) {
                       var user = json[i];
                       var name = user.firstName + " " + user.surname;
                       userNames.push(name);
                       userMap[name] = user.userName;
                   }

                   // now also populate the autocomplete
                   $("#goci-user-search").autocomplete({
                                                           source:userNames
                                                       });
               },
               error:function () {
                   alert("Failed to retrieve users from server, you will not be able to assign papers.  " +
                                 "Please inform the system administrator.");
               }
           });
}

function requestStates() {
    states = new Array();
    $.ajax({
               url:'api/studies/states',
               dataType:'json',
               success:function (json) {
                   // populate the submitter map, linking each user name to user ID
                   for (var i = 0; i < json.length; i++) {
                       var state = json[i];
                       states.push(state);
                   }
               },
               error:function () {
                   alert("Failed to retrieve states from server, you will not be able to assign papers.  " +
                                 "Please inform the system administrator.");
               }
           });
}

function requestEligibilities() {
    eligibilities = new Array();
    $.ajax({
               url:'api/studies/eligibilities',
               dataType:'json',
               success:function (json) {
                   // populate the submitter map, linking each user name to user ID
                   for (var i = 0; i < json.length; i++) {
                       var eligibility = json[i];
                       eligibilities.push(eligibility);
                   }
               },
               error:function () {
                   alert("Failed to retrieve eligibilities from server, you will not be able to assign papers.  " +
                                 "Please inform the system administrator.");
               }
           });
}

/**
 * Updates the queue table with the list of tasks that are currently pending.  This includes any that are paused
 * pending some user intervention.
 */
function displayPublications() {
    // get the current table
    var table = $("#goci-publications-table").dataTable();

    // clear it
    table.fnClearTable(false);

    // add all our publications
    for (var i = 0; i < publications.length && i < 200; i++) {
        var publication = publications[i];

        // column contents
        var pubMedIDCol =
                "<a target=\"_blank\" href=\"http://www.ncbi.nlm.nih.gov/pubmed/" + publication.pubMedID + "\">" +
                        publication.pubMedID + "</a>";
        var titleCol = publication.title;
        var ownerCol = getOwnerHTML(publication.id, publication.owner);
        var stateCol = getStateHTML(publication.id, publication.state);
        var gwasEligibleCol = getEligibilityHTML(publication.id, publication.gwasEligibility);

        var dataItem = [
            pubMedIDCol,
            titleCol,
            ownerCol,
            stateCol,
            gwasEligibleCol];

        table.fnAddData(dataItem, false);
    }

    $("#goci-publications-loading").hide();
    $("#goci-publications-content").show();
    table.fnStandingRedraw();
}

function getOwnerHTML(studyID, owner) {
    // add interactions for owner
    var ownerCol = "";

    // if the user is logged in, they can interact with tasks
    if (restApiKey != undefined) {
        var ownerName;
        if (owner == undefined) {
            ownerName = "Not yet assigned";
        }
        else {
            ownerName = owner.firstName + " " + owner.surname;
        }

        // create a span element that is going to be highlighted and clickable
        if (owner == undefined) {
            ownerCol =
                    "<p class=\"ui-state-highlight ui-corner-all clickable\" " +
                            "onclick=\"requestOwnerInteraction(\'" + studyID + "\', \'not yet assigned\')\"" + ">" +
                            "<span style=\"float: left; margin-right: 0.3em;\" class=\"ui-icon ui-icon-info\"></span>" +
                            ownerName +
                            "</p>";
        }
        else {
            ownerCol =
                    "<p class=\"ui-state-highlight ui-corner-all clickable\" " +
                            "onclick=\"requestOwnerInteraction(\'" + studyID + "\', \'" + owner.firstName + " " +
                            owner.surname + "\')\"" + ">" +
                            "<span style=\"float: left; margin-right: 0.3em;\" class=\"ui-icon ui-icon-info\"></span>" +
                            ownerName +
                            "</p>";
        }
    }
    else {
        // the progress column is not clickable
        if (owner == undefined) {
            ownerCol = "Not yet assigned";
        }
        else {
            ownerCol = owner.firstName + " " + owner.surname;
        }
    }
    return ownerCol;
}

function getStateHTML(studyID, studyState) {
    // add interactions for owner
    var stateCol = "";

    // if the user is logged in, they can interact with tasks
    if (restApiKey != undefined) {
        // create a span element that is going to be highlighted and clickable
        var stateText = studyState.replace(/_/g, " ");
        stateCol =
                "<p class=\"ui-state-highlight ui-corner-all clickable\" " +
                        "onclick=\"requestStateInteraction(\'" + studyID + "\', \'" + studyState + "\')\"" +
                        ">" +
                        "<span style=\"float: left; margin-right: 0.3em;\" class=\"ui-icon ui-icon-info\"></span>" +
                        stateText +
                        "</p>";
    }
    else {
        // the progress column is not clickable
        stateCol = studyState;
    }
    return stateCol;
}

function getEligibilityHTML(studyID, studyEligibility) {
    // add interactions for owner
    var eligCol = "";

    // if the user is logged in, they can interact with tasks
    if (restApiKey != undefined) {
        // create a span element that is going to be highlighted and clickable
        var eligText = studyEligibility.replace(/_/g, " ");
        eligCol =
                "<p class=\"ui-state-highlight ui-corner-all clickable\" " +
                        "onclick=\"requestEligInteraction(\'" + studyID + "\', \'" + studyEligibility + "\')\"" +
                        ">" +
                        "<span style=\"float: left; margin-right: 0.3em;\" class=\"ui-icon ui-icon-info\"></span>" +
                        eligText +
                        "</p>";
    }
    else {
        // the progress column is not clickable
        eligCol = studyEligibility;
    }
    return eligCol;
}

function requestOwnerInteraction(studyID, owner) {
    // update the contents of the interaction dialog
    var optionsHtml = "Owner update";
    optionsHtml = optionsHtml + "<p>The current owner is " + owner + ".</p>";
    optionsHtml = optionsHtml + "<p>Please select new owner:</p><select id=\"owner_" + studyID + "\">";
    for (var username in userMap) {
        optionsHtml = optionsHtml + "<option value=\"" + userMap[username] + "\">" + username + "</option>";
    }
    optionsHtml = optionsHtml + "</select>";

    optionsHtml = optionsHtml + "<button id=\"goci-update-button\"" +
            "onclick=\"updateOwner(" + studyID + ", $('#owner_" + studyID + " option:selected').val()); " +
            "$(\'#goci-owner-dialog\').dialog(\'close\');\" " +
            "title=\"Update the current owner\">" +
            "<span class=\"ui-button-text\">Update</span>" +
            "</button>";
    optionsHtml = optionsHtml + "<p>The new owner will be notified by email</p>";

    $("#goci-owner-dialog-options").html(optionsHtml);

    // and open it
    $("#goci-owner-dialog").dialog("open");
}

function requestStateInteraction(studyID, studyState) {
    // update the contents of the interaction dialog
    var optionsHtml = "Update Current State";
    optionsHtml = optionsHtml + "<p>The current state is " + studyState + ".</p>";
    optionsHtml = optionsHtml + "<p>Please select new state:</p><select id=\"state_" + studyID + "\">";
    for (var i = 0; i < states.length; i++) {
        var state = states[i];
        var stateText = state.replace(/_/g, " ");
        optionsHtml = optionsHtml + "<option value=\"" + state + "\">" + stateText + "</option>";
    }
    optionsHtml = optionsHtml + "</select>";

    optionsHtml = optionsHtml + "<button id=\"goci-update-button\"" +
            "onclick=\"updateState(" + studyID + ", $('#state_" + studyID + " option:selected').val()); " +
            "$(\'#goci-state-dialog\').dialog(\'close\');\" " +
            "title=\"Update the current state\">" +
            "<span class=\"ui-button-text\">Update</span>" +
            "</button>";

    $("#goci-state-dialog-options").html(optionsHtml);

    // and open it
    $("#goci-state-dialog").dialog("open");
}

function requestEligInteraction(studyID, studyEligibility) {
    // update the contents of the interaction dialog
    var optionsHtml = "Update GWAS eligibility";

    optionsHtml = optionsHtml + "<p>The current eligibility is " + studyEligibility + ".</p>";
    optionsHtml = optionsHtml + "<p>Please select new eligibility:</p><select id=\"eligibility_" + studyID + "\">";
    for (var i = 0; i < eligibilities.length; i++) {
        var eligibility = eligibilities[i];
        var eligText = eligibility.replace(/_/g, " ");
        optionsHtml = optionsHtml + "<option value=\"" + eligibility + "\">" + eligText + "</option>";
    }
    optionsHtml = optionsHtml + "</select>";

    optionsHtml = optionsHtml + "<button id=\"goci-update-button\"" +
            "onclick=\"updateEligibility(" + studyID + ", $('#eligibility_" + studyID + " option:selected').val()); " +
            "$(\'#goci-eligibility-dialog\').dialog(\'close\');\" " +
            "title=\"Update GWAS eligibility\">" +
            "<span class=\"ui-button-text\">Update</span>" +
            "</button>";

    $("#goci-eligibility-dialog-options").html(optionsHtml);

    // and open it
    $("#goci-eligibility-dialog").dialog("open");
}

function updateOwner(studyID, newOwner) {
    var jsonString = "{" +
            "\"updatedState\":\"\", " +
            "\"updatedEligibility\":\"\", " +
            "\"updatedOwner\":\"" + newOwner + "\", " +
            "\"restApiKey\":\"" + restApiKey + "\"}";
    $.ajax({
               type:'POST',
               url:'api/studies/' + studyID,
               contentType:'application/json',
               data:jsonString,
               processData:false,
               success:function (response) {
                   if (response.operationSuccessful) {
                       requestPublications();
                   }
                   else {
                       // get error message and show alert
                       $("#goci-error-text").html(response.statusMessage);
                       $("#goci-error-dialog").dialog("open");
                   }
               },
               error:function (request, status, error) {
                   // inform user by showing the dialog
                   alert("Error:" + error);
               }
           });
}

function updateState(studyID, newState) {
    var jsonString = "{" +
            "\"updatedState\":\"" + newState + "\", " +
            "\"updatedEligibility\":\"\", " +
            "\"updatedOwner\":\"\", " +
            "\"restApiKey\":\"" + restApiKey + "\"}";
    $.ajax({
               type:'POST',
               url:'api/studies/' + studyID,
               contentType:'application/json',
               data:jsonString,
               processData:false,
               success:function (response) {
                   if (response.operationSuccessful) {
                       requestPublications();
                   }
                   else {
                       // get error message and show alert
                       $("#goci-error-text").html(response.statusMessage);
                       $("#goci-error-dialog").dialog("open");
                   }
               },
               error:function (request, status, error) {
                   // inform user by showing the dialog
                   alert("Error:" + error);
               }

           });
}

function updateEligibility(studyID, newEligibility) {
    var jsonString = "{" +
            "\"updatedState\":\"\", " +
            "\"updatedEligibility\":\"" + newEligibility + "\", " +
            "\"updatedOwner\":\"\", " +
            "\"restApiKey\":\"" + restApiKey + "\"}";
    $.ajax({
               type:'POST',
               url:'api/studies/' + studyID,
               contentType:'application/json',
               data:jsonString,
               processData:false,
               success:function (response) {
                   if (response.operationSuccessful) {
                       requestPublications();
                   }
                   else {
                       // get error message and show alert
                       $("#goci-error-text").html(response.statusMessage);
                       $("#goci-error-dialog").dialog("open");
                   }
               },
               error:function (request, status, error) {
                   // inform user by showing the dialog
                   alert("Error:" + error);
               }

           });
}

/*
 *
 *
 * Other utility functions for GOCI ======>
 *
 *
 */

/**
 * Callback function that schedules an update of the pending tasks table
 */
function redrawPublicationsTableLater() {
    // pending tasks are updating, so clear any scheduled update
    clearTimeout(publicationsTimeoutID);

    // once all displayed, callback to itself to update in 600 seconds
    publicationsTimeoutID = setTimeout("requestPublications()", 600000);
}

/**
 * Handles the case when the user hits enter - this automatically submits a task with the supplied values.
 *
 * @param e the keypress event
 */
function submitOnEnter(e) {
    var key = e.keyCode || e.which;
    if (key == 13) {
        registerNewPublication();
    }
}

/**
 * Handles the case when the user hits enter - this automatically logs in a user when they hit enter.
 *
 * @param e the keypress event
 */
function logInOnEnter(e) {
    var key = e.keyCode || e.which;
    if (key == 13) {
        requestUser();
    }
}

function focusNext(e, input) {
    var key = e.keyCode || e.which;
    if (key == 13) {
        input.focus();
    }
}

function registerNewUserOnEnter(e) {
    var key = e.keyCode || e.which;
    if (key == 13) {
        registerNewUser();
    }
}

/**
 * DataTables fsStandingRedraw API plugin
 *
 * @author Jonathan Hoguet
 * @param oSettings
 */
$.fn.dataTableExt.oApi.fnStandingRedraw = function (oSettings) {
    //redraw to account for filtering and sorting
    // concept here is that (for client side) there is a row got inserted at the end (for an add)
    // or when a record was modified it could be in the middle of the table
    // that is probably not supposed to be there - due to filtering / sorting
    // so we need to re process filtering and sorting
    // BUT - if it is server side - then this should be handled by the server - so skip this step
    if (oSettings.oFeatures.bServerSide === false) {
        var before = oSettings._iDisplayStart;
        oSettings.oApi._fnReDraw(oSettings);
        //iDisplayStart has been reset to zero - so lets change it back
        oSettings._iDisplayStart = before;
        oSettings.oApi._fnCalculateEnd(oSettings);
    }

    //draw the 'current' page
    oSettings.oApi._fnDraw(oSettings);
};

