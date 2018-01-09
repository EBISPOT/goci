$(document).ready(function() {

    function disableForm() {
        var inputs= $('#study-duplications-form-container').find('.studyItemFormInput');
        inputs.each(function(j, input) {
            $(input).prop('disabled', true);
        });
    
        var removes= $('#study-duplications-form-container').find('.remove-study-duplications');
        removes.each(function(j, remove) {
            $(remove).attr("disabled", "disabled");
        });
        $('#multi-add-study-duplications').attr("disabled", "disabled");
        $('#multi-add-study-duplications-button').attr("disabled", "disabled");
        $('#duplicateStudy').attr("disabled", "disabled");
        
    }
    
    function enableForm() {
        var inputs= $('#study-duplications-form-container').find('.studyItemFormInput');
        inputs.each(function(j, input) {
            $(input).prop('disabled', false);
        });
    
        var removes= $('#study-duplications-form-container').find('.remove-study-duplications');
        removes.each(function(j, remove) {
            $(remove).removeAttr("disabled");
        });
        $('#multi-add-study-duplications').removeAttr("disabled");
        $('#multi-add-study-duplications-button').removeAttr("disabled");
        $('#duplicateStudy').removeAttr("disabled");
    }
    
    $("#duplicateStudy").click(function () {

        //console.log(contextPath);
        var listTags = {};
        var studyId = $('#studyId').val();
        var action = contextPath + "studies/"+studyId+"/duplicate";
        var inputs= $('#study-duplications-form-container').find('.studyItemFormInput');
        inputs.each(function(j, input) {
            listTags[input.name] = input.value;
        });
        
        if (listTags != {}) {
            var dataJSON = JSON.stringify(listTags);
            console.log("if ");
            $.ajax({
                headers: {
                    'Content-Type': 'application/json; charset=utf-8'
                },
                dataType: 'json',
                type: "POST",
                url: action,
                data: dataJSON,
                beforeSend: function() {
                    disableForm();
                },
                success: function (data, textStatus, jqXHR) {
                    if ("failed" in data) {
                       alert(data["failed"]);
                       enableForm();
                    }
                    else {
                        window.location.href = contextPath+data.success;
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    alert("Something went wrong. Please, contact the helpdesk.");
                }
            });
        }
        else alert("You must add at least a study.");
    });
    
});