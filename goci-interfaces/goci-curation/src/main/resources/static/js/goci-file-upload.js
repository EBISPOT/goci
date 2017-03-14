/**
 * Created by dwelter on 09/03/17.
 */


$(document).ready(function() {
        setTimeout(checkStatus, 100);
});



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
    //window.alert("This actually worked!");

    window.location.pathname.replace('uploadResults');
     //$.ajax({
     //           url: 'uploadResults',
     //            dataType: 'html',
     //            success: function(response) {
     //                alert(response);
     //                //window.location.href(response);
     //                $.html(response);
     //            },
     //            error: function(request, status, error) {
     //                alert(error + ": (" + request.responseText + ")");
     //            }
     //       });
}
