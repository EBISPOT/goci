/**
 * Created by dwelter on 24/11/16.
 */


$(document).ready(function() {

    // read the window location to set the breadcrumb
    //var path = window.location.pathname;
    //var pagename = path.substr(path.lastIndexOf('/') + 1);
    //if (pagename == 'docs') {
    //    pagename = "/index";
    //}
    //var url_header = "../documents/".concat(pagename).concat(".html #header");
    //var url_footer = "../documents/".concat(pagename).concat(".html #footer");
    //var url = "../documents/".concat(pagename).concat(".html #content");
    //console.log("Documentation should be loaded from " + url + "...");

    //$("#docs-header").load (url_header);
  //  $("#docs-content").load (url);
    //$("#docs-footer").load (url_footer);

    //if (pagename == 'about') {
    //    $('#local-nav-about').addClass('active');
    //} else {
    //    $('#local-nav-docs').addClass('active');
    //
    //}

    // load the page content
//    $.get(url, loadDocumentation(pagename, content)).fail(console.log("Failed to get content from " + url));

    var content = $("#docs-content");

    // read the window location to set the breadcrumb
    var path = window.location.pathname;
    var pagename = path.substr(path.lastIndexOf('/') + 1);
    var url = "../documents/".concat(pagename).concat(".html #content");
    //var url = "content/".concat(pagename).concat("-content.html");
    console.log("Documentation should be loaded from " + url + "...");

    // load the page content
    $.get(url, loadDocumentation(pagename, content)).fail(console.log("Failed to get content from " + url));
});


//$(document).ready(function() {
//    var content = $("#docs-content");
//
//    // read the window location to set the breadcrumb
//    var path = window.location.pathname;
//    var pagename = path.substr(path.lastIndexOf('/') + 1);
//    var url = "content/".concat(pagename).concat("-content.html");
//    console.log("Documentation should be loaded from " + url + "...");
//
//    // load the page content
//    $.get(url, loadDocumentation(pagename, content)).fail(console.log("Failed to get content from " + url));
//});

var loadDocumentation = function(pagename, content) {
    console.log("Attempting to load documentation...");
    return function(data, textStatus, jqXHR) {
        // set breadcrumb
        var displayName = pagename.replace(/(^| )(\w)/g, function(x) {
            return x.toUpperCase();
        });

        displayName = displayName.replace("-", " ");

        //if (displayName.toLowerCase() == "downloads" || displayName.toLowerCase() == "file downloads" || displayName.toLowerCase() == "diagram downloads" || displayName.toLowerCase() == "summary statistics" ) {
        //    // $("#about-item").removeClass("active");
        //    $("#documentation-item").removeClass("active");
        //    $("#downloads-item").addClass("active");
        //    $("#docs-crumb").hide();
        //    $("#downloads-crumb").show();
        //
        //}
        //else {
        //    // $("#about-item").removeClass("active");
        //    $("#downloads-item").removeClass("active");
        //    $("#documentation-item").addClass("active");
        //    $("#docs-crumb").show();
        //    $("#downloads-crumb").hide();
        //}
        //$("#current-page").text(displayName);
        //


        console.log("Updated breadcrumb (" + displayName + ")");
        // load the data content
        console.log("Updating " + content + "...");
        //console.log(data);
        content.html(data);

        //$.getJSON('../api/search/stats')
        //        .done(function(stats) {
        //            setBuilds(stats);
        //        });

        console.log("Done!");

    }

    //function setBuilds(data) {
    //    try {
    //        $('#genomeBuild').text(data.genebuild);
    //        $('#dbSNP').text(data.dbsnpbuild);
    //    }
    //    catch (ex) {
    //        console.log("Failure to process build variables " + ex);
    //    }
    //}
};