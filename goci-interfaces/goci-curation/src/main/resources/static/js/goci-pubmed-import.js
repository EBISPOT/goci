$(document).ready(function() {
    

    
    
    $('#pubmedIdDatatable').DataTable({
        "columns": [
            { "mData": "pubmedId","sTitle": "pubmed ID" },
            { "mData": "study_id", "sDefaultContent": "", "sTitle": "Study",
                "fnCreatedCell": function (nTd, sData, oData, iRow, iCol) {
                    if (sData != "") {
                        $(nTd).html("<a href='" + contextPath + sData + "'>Edit Study</a>");
                    }
                }
            },
            { "mData": "author", "sDefaultContent": "", "sTitle":"Author" },
            { "mData": "title", "sDefaultContent": "", "sTitle": "Title"   }, // <-- which values to use inside object
            { "mData": "error", "sDefaultContent": "", "sTitle": "Error" },
            
        ],
        "aLengthMenu": [
            [5,10,25, 50, 100, -1],
            [5,10,25, 50, 100, "All"]
        ],
        "iDisplayLength": -1,
        //"order": [[ 0, "desc" ]],
    } );
    
    $("#add_study").click(function () {
        $("#error_text").html("");
        $("#main_error").hide();
        var action = contextPath + "studies/new/import";

        var listPubmeds = $("#pubmedId").val();
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=utf-8'
            },
            dataType: 'json',
            type: "POST",
            url: action,
            data: listPubmeds,
            success: function (data, textStatus, jqXHR) {
                console.log(data);
                if (data[0]["key"] == "general") {
                    $("#error_text").html(data[0]["error"]);
                    $("#main_error").show();
                }
                else {
                    var table = $('#pubmedIdDatatable').dataTable();
                    var oSettings = table.fnSettings();
                    table.fnClearTable(this);
                    data.forEach(function(entry) {
                        table.oApi._fnAddData(oSettings, entry);
                    });
                    oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
                    table.fnDraw();
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                $("#error_text").html("Something went wrong. Please, contact the helpdesk.");
                $("#main_error").show();
            }
        });
        
    });
    
});