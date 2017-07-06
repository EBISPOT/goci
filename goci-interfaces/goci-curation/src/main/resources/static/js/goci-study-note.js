/**
 * Created by xinhe on 19/04/2017.
 */

//init tables
$(document).ready(function() {
    $('#mainTable').DataTable({
                                  "info": true, "paging": false, "order": [[1, "desc"],[6, "desc"]],
                                  "columnDefs": [
                                      { "orderable": false, "targets": 1 }
                                  ]
                              });

    $('#sysTable').DataTable({
                                 "info": false, "paging": false, "order": [[5, "desc"]]
                             });

    $('[data-toggle="confirmation"]').confirmation({ btnOkLabel: "&nbsp;Yes", btnCancelLabel: "&nbsp;No" });
});


//add a warning when leaving the page in Editing mode
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





//apply template to Initial extraction subject
$( ".noteSubjectSelect" ).change(function() {
    var template = `****
        **Study design (e.g.reason for split, part of study not eligible)
        **Summary stats (e.g. whether box ticked, file location, added to Confluence page and/or ftp)
        **Platform/ SNP n/ imputation
        **Sample n/ ancestry/ CoR
        **SNPs

        **EFO/ trait:

        *Queries-   [v brief note if unsure about anything, use this as note to self or reminder for queries to be discussed]
        Discussed with [initials]-
        Decided based on discussion –

        *Uploaded study files
        `


    var subjectText = $( ".noteSubjectSelect option:selected" ).text();
    if(subjectText == 'Initial extraction'){
        $("textarea[readonly!='readonly']").text(template)
    }else{
        $("textarea[readonly!='readonly']").text('')

    }

});