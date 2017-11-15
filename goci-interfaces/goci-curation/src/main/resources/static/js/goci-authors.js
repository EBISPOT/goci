$(document).ready(function() {

    $("#change_first_author").click(function () {
        var action = contextPath + "studies/change_first_author";
        var e = document.getElementById("authorsList");
        var strUser = e.options[e.selectedIndex].value;
        console.log(strUser);
/*
        $.ajax({
                   headers: {
                       'Accept': 'application/json',
                       'Content-Type': 'application/json; charset=utf-8'
                   },
                   type: "POST",
                   url: action,
                   data: "",
                   success: function (data, textStatus, jqXHR) {
                       console.log(data);
                   },
                   error: function (XMLHttpRequest, textStatus, errorThrown) {
                       console.log("errore");
                   },
                   dataType: 'json'
               });
*/
    });

});
