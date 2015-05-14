/**
 * Created by emma on 12/05/2015.
 */


// Delete individual sample descriptions
$(document).ready(function() {
    $("#delete-button").click(function() {
        // call method to store ids
        deleteSelectedSampleDescriptions();
    });
});

function deleteSelectedSampleDescriptions() {
    var sampleDescriptionIds = [];
    //create an array of sample description ids
    $('input.sample-description-selector:checked').each(
            function() {
                sampleDescriptionIds.push($(this).attr("value"))
            }
    );
    // delete each id
    deleteSampleDescriptions(sampleDescriptionIds);
}

function deleteSampleDescriptions(sampleDescriptionIds) {
    // Pass details to method in controller which handles database changes
    $.getJSON("sampledescription/delete_checked",
            {"sampleDescriptionIds[]": sampleDescriptionIds},
            //Response
              function(data) {
                  alert(data.message);
                  //Reload page
                  location.reload();
              });
}
