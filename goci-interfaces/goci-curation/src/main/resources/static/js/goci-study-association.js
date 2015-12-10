// Approve individual SNP associations

$(document).ready(function() {
    $("#approve-button").click(function() {
        approveSelectedAssociations();
    });
});

function approveSelectedAssociations() {
    var associationIds = [];
    //create an array of association ids
    $('input.association-selector:checked').each(
            function() {
                associationIds.push($(this).attr("value"))
            }
    );
    updateAssociations(associationIds);
}

function updateAssociations(associationIds) {
    // Pass details to method in controller which handles database changes
    $.getJSON("associations/approve_checked",
              {"associationIds[]": associationIds},
              //Response
              function(data) {
                  alert(data.message);
                  //Reload page
                  location.reload();
              });
}


// Unpprove individual SNP associations
$(document).ready(function() {
    $("#unapprove-button").click(function() {
        unapproveSelectedAssociations();
    });
});

function unapproveSelectedAssociations() {
    var associationIds = [];
    //create an array of association ids
    $('input.association-selector:checked').each(
            function() {
                associationIds.push($(this).attr("value"))
            }
    );
    unapproveAssociations(associationIds);
}

function unapproveAssociations(associationIds) {
    // Pass details to method in controller which handles database changes
    $.getJSON("associations/unapprove_checked",
              {"associationIds[]": associationIds},
              //Response
              function(data) {
                  alert(data.message);
                  //Reload page
                  location.reload();
              });
}


// Delete individual SNP associations
$(document).ready(function() {
    $("#delete-button").click(function() {
        // Passes study id to method
        deleteSelectedAssociations();
    });
});

function deleteSelectedAssociations() {
    var associationIds = [];
    //create an array of association ids
    $('input.association-selector:checked').each(
            function() {
                associationIds.push($(this).attr("value"))
            }
    );
    deleteAssociations(associationIds);
}

function deleteAssociations(associationIds) {

    $.getJSON("associations/delete_checked",
              {"associationIds[]": associationIds},
              //Response
              function(data) {
                  alert(data.message);
                  //Reload page
                  location.reload();
              });
}
