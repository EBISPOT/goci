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
        var selectedSubject_id= $(this).val()
        //overlap is not working atm !!!???
//            showLoadingOverLay('#'+$("textarea[readonly!='readonly']").prop('id'));
        promiseGet(window.location.pathname.split('/curation/')[0] + `/curation/studies/note/subject/${selectedSubject_id}`).then(JSON.parse).then((subject)=>{
            var template  = subject.template;
            if(template != '' && template!= null){
                var old = $("textarea[readonly!='readonly']").val();
                if(old != ''){
                    $("textarea[readonly!='readonly']").val(old + '\n' + template);
                }else{
                    $("textarea[readonly!='readonly']").val(template);
                }
            }
        }).catch((err)=>{
            console.log(`Error loading subject template. ${err}`);
        }).then(()=>{
//                hideLoadingOverLay('#'+$("textarea[readonly!='readonly']").prop('id'));
        })
    });

    // //popup to confirm public note
    $(".noteStatus").change(function(e){
        var isPublic = $(".noteStatus").val()==1
        if(isPublic){
            $('.btn-save-note').confirmation({
                                                 btnOkLabel: "&nbsp;Yes",
                                                 btnCancelLabel: "&nbsp;No",
                                                 popout: true,
                                                 title : "Are you sure you want to create/save a public note?"
                                             });
        }else{
            //The pop up need to be removed if the subject is private,
            //Currently I did not find a way to remove the popup from the tag, once it is initliazed

        }

    })

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

