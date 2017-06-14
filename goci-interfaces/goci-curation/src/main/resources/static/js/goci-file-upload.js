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

    window.location.replace('getUploadResults');

}
