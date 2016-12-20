$(document).ready(function() {
    $( "#generateStats" ).click(function() {
        $("#generateStats").attr("disabled", true);
        $("#downloadStatsExcel").attr("disabled", true);
        $.ajax({
            url: location.pathname+"/generateStats",
            beforeSend: function() {
                document.getElementById("loader").style.display = "block";
                document.getElementById("ButtonsMenu").style.display = "none";
            },
            error: function() {
                alert('An error has occurred');
                $("#generateStats").removeAttr("disabled");
                $("#downloadStatsExcel").removeAttr("disabled");
                document.getElementById("loader").style.display = "none";
                document.getElementById("ButtonsMenu").style.display = "block";
            },
            success: function(data) {
                $("#generateStats").removeAttr("disabled");
                $("#downloadStatsExcel").removeAttr("disabled");
                document.getElementById("loader").style.display = "none";
                document.getElementById("ButtonsMenu").style.display = "block";
                console.log(data);
            },
            complete: function() { console.log("complete");},
            type: 'GET'
        });
        return false;
    });

});