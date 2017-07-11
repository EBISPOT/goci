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





    //apply template to Initial extraction subject
    $( ".noteSubjectSelect" ).change(function() {
        var template = `**Summary stats
        **Study design
        **Platform/ SNP n/ imputation
        **Sample n/ ancestry/ CoR
        **SNPs
        **EFO/ trait
        **Queries
        `

        var subjectText = $( ".noteSubjectSelect option:selected" ).text();
        if(subjectText == 'Initial extraction' || subjectText == 'review/secondary extraction'){
            var old = $("textarea[readonly!='readonly']").val();
            $("textarea[readonly!='readonly']").val(old + '\n' + template);
        }

    });

    // //popup to confirm public note
    // $(".noteStatus").change(function(e){
    //     var optionSelected = $("option:selected", this);
    //     var valueSelected = this.value;
    //
    //     e.preventDefault()
    //     var areYouSure = confirm('If you sure you wish to leave this tab?  Any data entered will NOT be saved.  To save information, use the Save buttons.');
    //     if (areYouSure === true) {
    //         $(this).tab('show')
    //     } else {
    //         // do other stuff
    //         return false;
    //     }
    //
    //    this.confirmation('show');
    // })
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


