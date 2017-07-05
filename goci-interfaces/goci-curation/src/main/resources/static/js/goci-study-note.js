/**
 * Created by xinhe on 19/04/2017.
 */

$(document).ready(function() {
    $('#mainTable').DataTable({
                                  "info": true, "paging": false, "order": [[6, "desc"]]
                              });
    $('#sysTable').DataTable({
                                 "info": false, "paging": false, "order": [[5, "desc"]]
                             });

    $('[data-toggle="confirmation"]').confirmation({ btnOkLabel: "&nbsp;Yes", btnCancelLabel: "&nbsp;No" });
});


var formSubmitting = false;
var setFormSubmitting = function() { formSubmitting = true; };
var isEditing = ()=>{
    return $('#editing_flag').prop('value')=="true";
}
window.onload = function() {
    window.addEventListener("beforeunload", function (e) {
        if (formSubmitting || !isEditing()) {
            return undefined;
        }

        var confirmationMessage = 'It looks like you have been editing a note. '
                + 'If you leave before saving, your changes will be lost.';

        (e || window.event).returnValue = confirmationMessage; //Gecko + IE
        return confirmationMessage; //Gecko + Webkit, Safari, Chrome etc.
    });
};
