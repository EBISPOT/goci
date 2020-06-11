$(document).ready(function () {
    $('#loadingResults').hide();
    var table = $('#diseaseTraitTable').DataTable({
        "columns": [
            {"mData": "edit", "sDefaultContent": "", "bSearchable": false, "bSortable": false},
            {"mData": "diseasetrait", "sDefaultContent": "", "sTitle": "Disease Trait", "bSearchable": true},
            {"mData": "delete", "sDefaultContent": "", "sTitle": "Delete", "bSearchable": false}        ],
        "aLengthMenu": [
            [5, 10, 25, 50, 100, -1],
            [5, 10, 25, 50, 100, "All"]
        ],
        "iDisplayLength": 25
    });

    var table = $('#newDiseaseTraitTable').DataTable({
        "columns": [
            {"mData": "edit", "sDefaultContent": "", "bSearchable": false, "bSortable": false,
                "fnCreatedCell": function (nTd, sData, oData, iRow, iCol) {
                    $(nTd).html("<input type=\"checkbox\" class=\"table-checkbox checkbox\"/>");
                }
            },
            {"mData": "newdiseasetrait", "sDefaultContent": "", "sTitle": "New Disease Trait", "bSearchable": true},
            {"mData": "match", "sDefaultContent": "", "sTitle": "Best Match", "bSearchable": true},
            {"mData": "matchscore", "sDefaultContent": "", "sTitle": "Best Match Score", "bSearchable": true} ],
        "aLengthMenu": [
            [5, 10, 25, 50, 100, -1],
            [5, 10, 25, 50, 100, "All"]
        ],
        "iDisplayLength": 25
    });

    $('#select_all').click(function () {
        $('.table-checkbox').prop("checked", $('#select_all').prop("checked"));
    });
    $('#check_new_disease_traits').click(function () {
            $('#check_new_disease_traits').prop( "disabled", true );
            $('#loadingResults').show();
            var data = {}
            data = $('#new_disease_traits').val().split("\n");
            $.ajax({
                type: "POST",
                url: "/diseasetraits/check_new_traits",
                data: JSON.stringify(data),
                contentType: 'application/json',
                //Response
                success: function (data) {
                    $('#check_new_disease_traits').prop( "disabled", false );
                    $('#loadingResults').hide();
                    var table = $('#newDiseaseTraitTable').dataTable();
                    var oSettings = table.fnSettings();
                    table.fnClearTable(this);
                    data.forEach(function(entry) {
                        table.oApi._fnAddData(oSettings, entry);
                    });
                    oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
                    table.fnDraw();
                }
            })
    });
    $('#remove_button').click(function () {
        var table = $('#newDiseaseTraitTable').DataTable();
        $('.table-checkbox:checked').each(function () {
            table
                .row( $(this).parent().parents('tr') )
                .remove()
        })
        table.draw();
    });
    $('#save_new_traits_button').click(function () {
            $('#save_new_traits_button').prop( "disabled", true );
            $('#loadingResults').show();
            var data = []
            $('.table-checkbox:checked').each(function () {
                var v = $(this).closest('td').next();
                v = v.text();
                data.push(v)
            })
            $.ajax({
                type: "POST",
                url: "/diseasetraits/create_disease_traits",
                data: JSON.stringify(data),
                contentType: 'application/json',
                //Response
                success: function (data) {
                    $('#save_new_traits_button').prop( "disabled", false );
                    $('#loadingResults').hide();
                    var msg = '';
                    for (let [key, value] of Object.entries(data)) {
                        msg += key + ': ' + value + '\n';
                    }
                    alert(msg);
                    //Reload page
                    location.reload();
                }
            })
    });

    // $('#assignStatus').click(function () {
    //     $('#assignStatus').prop( "disabled", true );
    //     $('#loadingResults').show();
    //     var data = {}
    //     data.status = $('#curationStatus').val();
    //     data.ids = []
    //     $('.table-checkbox:checked').each(function () {
    //         var v = $(this).attr('id')
    //         v = this.id
    //         data.ids.push(v)
    //     })
    //     $.ajax({
    //         type: "POST",
    //         url: "status_update",
    //         data: JSON.stringify(data),
    //         contentType: 'application/json',
    //         //Response
    //         success: function (data) {
    //             $('#assignStatus').prop( "disabled", false );
    //             $('#loadingResults').hide();
    //             var msg = '';
    //             for (let [key, value] of Object.entries(data)) {
    //                 msg += key + ': ' + value + '\n';
    //             }
    //             alert(msg);
    //             //Reload page
    //             location.reload();
    //         }
    //     })
    // });
    // $('#assignCurator').click(function () {
    //     $('#assignCurator').prop( "disabled", true );
    //     $('#loadingResults').show();
    //     var data = {}
    //     data.curator = $('#curator').val();
    //     data.ids = []
    //     $('.table-checkbox:checked').each(function () {
    //         var v = $(this).attr('id')
    //         v = this.id
    //         data.ids.push(v)
    //     })
    //     $.ajax({
    //         type: "POST",
    //         url: "assign_curator",
    //         data: JSON.stringify(data),
    //         contentType: 'application/json',
    //         //Response
    //         success: function (data) {
    //             $('#assignCurator').prop( "disabled", false );
    //             $('#loadingResults').hide();
    //             var msg = '';
    //             for (let [key, value] of Object.entries(data)) {
    //                 msg += key + ': ' + value + '\n';
    //             }
    //             alert(msg);
    //             //Reload page
    //             location.reload();
    //         }
    //     })
    // });
    // $('#saveBackgroundTrait').click(function () {
    //     $('#saveBackgroundTrait').prop( "disabled", true );
    //     $('#loadingResults').show();
    //     var data = {}
    //     data.backgroundTrait = $('#backgroundTrait').val();
    //     data.ids = []
    //     $('.table-checkbox:checked').each(function () {
    //         var v = $(this).attr('id')
    //         v = this.id
    //         data.ids.push(v)
    //     })
    //     $.ajax({
    //         type: "POST",
    //         url: "update_background_traits",
    //         data: JSON.stringify(data),
    //         contentType: 'application/json',
    //         //Response
    //         success: function (data) {
    //             $('#saveBackgroundTrait').prop( "disabled", false );
    //             $('#loadingResults').hide();
    //             var msg = '';
    //             for (let [key, value] of Object.entries(data)) {
    //                 msg += key + ': ' + value + '\n';
    //             }
    //             alert(msg);
    //             //Reload page
    //             location.reload();
    //         }
    //     })
    // });
    // $('#saveDiseaseTrait').click(function () {
    //     $('#saveDiseaseTrait').prop( "disabled", true );
    //     $('#loadingResults').show();
    //     var data = {}
    //     data.diseaseTrait = $('#diseaseTrait').val();
    //     data.ids = []
    //     $('.table-checkbox:checked').each(function () {
    //         var v = $(this).attr('id')
    //         v = this.id
    //         data.ids.push(v)
    //     })
    //     $.ajax({
    //         type: "POST",
    //         url: "update_disease_traits",
    //         data: JSON.stringify(data),
    //         contentType: 'application/json',
    //         //Response
    //         success: function (data) {
    //             $('#saveDiseaseTrait').prop( "disabled", false );
    //             $('#loadingResults').hide();
    //             var msg = '';
    //             for (let [key, value] of Object.entries(data)) {
    //                 msg += key + ': ' + value + '\n';
    //             }
    //             alert(msg);
    //             //Reload page
    //             location.reload();
    //         }
    //     })
    // });
    // $('#saveEfoTrait').click(function () {
    //     $('#loadingResults').show();
    //     $('#saveEfoTrait').prop( "disabled", true );
    //     var data = {}
    //     data.efoTraits = $('#EFOTrait').val();
    //     data.ids = []
    //     $('.table-checkbox:checked').each(function () {
    //         var v = $(this).attr('id')
    //         v = this.id
    //         data.ids.push(v)
    //     })
    //     $.ajax({
    //         type: "POST",
    //         url: "update_efo_traits",
    //         data: JSON.stringify(data),
    //         contentType: 'application/json',
    //         //Response
    //         success: function (data) {
    //             $('#saveEfoTrait').prop( "disabled", false );
    //             $('#loadingResults').hide();
    //             var msg = '';
    //             for (let [key, value] of Object.entries(data)) {
    //                 msg += key + ': ' + value + '\n';
    //             }
    //             alert(msg);
    //             //Reload page
    //             location.reload();
    //         }
    //     })
    // });
    // $('#saveBackgroundEfos').click(function () {
    //     $('#loadingResults').show();
    //     $('#saveBackgroundEfos').prop( "disabled", true );
    //     var data = {}
    //     data.backgroundEfoTraits = $('#mappedBackgroundTraits').val();
    //     data.ids = []
    //     $('.table-checkbox:checked').each(function () {
    //         var v = $(this).attr('id')
    //         v = this.id
    //         data.ids.push(v)
    //     })
    //     $.ajax({
    //         type: "POST",
    //         url: "update_background_efo_traits",
    //         data: JSON.stringify(data),
    //         contentType: 'application/json',
    //         //Response
    //         success: function (data) {
    //             $('#saveBackgroundEfos').prop( "disabled", false );
    //             $('#loadingResults').hide();
    //             var msg = '';
    //             for (let [key, value] of Object.entries(data)) {
    //                 msg += key + ': ' + value + '\n';
    //             }
    //             alert(msg);
    //             //Reload page
    //             location.reload();
    //         }
    //     })
    // });
    // $('#deleteStudies').click(function () {
    //     $('#loadingResults').show();
    //     $('#deleteStudies').prop( "disabled", true );
    //     var data = {}
    //     data.ids = []
    //     $('.table-checkbox:checked').each(function () {
    //         var v = $(this).attr('id')
    //         v = this.id
    //         data.ids.push(v)
    //     })
    //     $.ajax({
    //         type: "POST",
    //         url: "delete_studies",
    //         data: JSON.stringify(data),
    //         contentType: 'application/json',
    //         //Response
    //         success: function (data) {
    //             $('#deleteStudies').prop( "disabled", false );
    //             $('#loadingResults').hide();
    //             var msg = '';
    //             for (let [key, value] of Object.entries(data)) {
    //                 msg += key + ': ' + value + '\n';
    //             }
    //             alert(msg);
    //             //Reload page
    //             location.reload();
    //         }
    //     })
    // });
    // $('#addSumStats').click(function () {
    //     $('#loadingResults').show();
    //     $('#addSumStats').prop( "disabled", true );
    //     var data = {}
    //     data.ids = []
    //     $('.table-checkbox:checked').each(function () {
    //         var v = $(this).attr('id')
    //         v = this.id
    //         data.ids.push(v)
    //     })
    //     $.ajax({
    //         type: "POST",
    //         url: "add_sum_stats",
    //         data: JSON.stringify(data),
    //         contentType: 'application/json',
    //         //Response
    //         success: function (data) {
    //             $('#deleteStudies').prop( "disabled", false );
    //             $('#loadingResults').hide();
    //             var msg = '';
    //             for (let [key, value] of Object.entries(data)) {
    //                 msg += key + ': ' + value + '\n';
    //             }
    //             alert(msg);
    //             //Reload page
    //             location.reload();
    //         }
    //     })
    // });

})