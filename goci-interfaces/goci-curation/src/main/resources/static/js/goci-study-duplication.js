$(document).ready(function() {

    $("#duplicateStudy").click(function () {

        console.log(contextPath);
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
                success: function (data, textStatus, jqXHR) {
                    if ("failed" in data) {
                       alert(data["failed"]);
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