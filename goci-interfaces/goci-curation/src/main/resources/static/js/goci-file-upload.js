/**
 * Created by dwelter on 09/03/17.
 */


$(document).ready(function() {
    // $("#uploadFile").click(function() {
    //     // var file = $("#uploadSNPs").value;
    //     // var studyId = $('#studyId').text();
    //     // uploadFile(file, studyId);
    //     $("#loadingResults").show();
    //     setTimeout(checkStatus, 100);
    //
    // });


    // if($("#upload_progress") == 'true'){

        setTimeout(checkStatus, 100);
    // }
});



// function uploadFile(file, studyId) {
//     var url = 'associations/upload?studyId='&studyId;
//
//     $.ajax({
//                type: 'POST',
//                url: url,
//                contentType: 'application/json',
//                data: file,
//                beforeSend: function() {
//                    $("#loadingResults").show();
//                },
//                success: function(response) {
//                    setTimeout(checkStatus, 100);
//                },
//                error: function(request, status, error) {
//                    alert(error + ": (" + request.responseText + ")");
//                }
//            });
// }


function checkStatus() {
    $.get('status', function(progress) {
        if (progress) {
            getResults(); }
        else {
            setTimeout(checkStatus, 500);
        }
    });
}

function getResults() {
    window.alert("This actually worked!");
    // $.ajax({
    //            url: 'associations/uploadResults',
    //            // dataType: 'json',
    //            // success: function(response) {
    //            //     renderResults(response);
    //            // },
    //            // error: function(request, status, error) {
    //            //     alert(error + ": (" + request.responseText + ")");
    //            // }
    //        });
}
