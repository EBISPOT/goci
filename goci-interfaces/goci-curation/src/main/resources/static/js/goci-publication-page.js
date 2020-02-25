$(document).ready(function () {
    $('#loadingResults').hide();
    var table = $('#submissionDataTable').DataTable({
        "columns": [
            {"mData": "select_all", "sDefaultContent": "", "bSearchable": false, "bSortable": false},
            {"mData": "studyId", "sDefaultContent": "", "sTitle": "Study Id", "bSearchable": true},
            {"mData": "accession", "sDefaultContent": "", "sTitle": "Study Accession", "bSearchable": true},
            {"mData": "hasSumStats", "sDefaultContent": "", "sTitle": "Has SumStats", "bSearchable": true},
            {"mData": "hasStudyFiles", "sDefaultContent": "", "sTitle": "Has Study Files", "bSearchable": true},
            {"mData": "diseaseTrait", "sDefaultContent": "", "sTitle": "Disease Trait", "bSearchable": true},
            {"mData": "efoTraits", "sDefaultContent": "", "sTitle": "EFO Traits", "bSearchable": true},
            {"mData": "backgroundTrait", "sDefaultContent": "", "sTitle": "Background Trait", "bSearchable": true},
            {"mData": "backgroundEfoTraits", "sDefaultContent": "", "sTitle": "Background EFO Traits", "bSearchable": true},
            {"mData": "associationCount", "sTitle": "Association Count", "bSearchable": true},
            {"mData": "status", "sDefaultContent": "", "sTitle": "Curation Status", "bSearchable": true},
            {"mData": "curator", "sTitle": "Curator", "bSearchable": true},
            {"mData": "studyTag", "sDefaultContent": "", "sTitle": "Study Tag", "bSearchable": true},
            {"mData": "tagDuplicatedNote", "sDefaultContent": "", "sTitle": "Duplicate", "bSearchable": true}
        ],
        "aLengthMenu": [
            [5, 10, 25, 50, 100, -1],
            [5, 10, 25, 50, 100, "All"]
        ],
        "iDisplayLength": -1,
        "order": [[1, "desc"]]
    });
    $('#select_all').click(function () {
        $('.table-checkbox').prop("checked", $('#select_all').prop("checked"));
    });

    $('#assignStatus').click(function () {
        $('#assignStatus').prop( "disabled", true );
        $('#loadingResults').show();
        var data = {}
        data.status = $('#curationStatus').val();
        data.ids = []
        $('.table-checkbox:checked').each(function () {
            var v = $(this).attr('id')
            v = this.id
            data.ids.push(v)
        })
        $.ajax({
            type: "POST",
            url: "status_update",
            data: JSON.stringify(data),
            contentType: 'application/json',
            //Response
            success: function (data) {
                $('#assignStatus').prop( "disabled", false );
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
    $('#assignCurator').click(function () {
        $('#assignCurator').prop( "disabled", true );
        $('#loadingResults').show();
        var data = {}
        data.curator = $('#curator').val();
        data.ids = []
        $('.table-checkbox:checked').each(function () {
            var v = $(this).attr('id')
            v = this.id
            data.ids.push(v)
        })
        $.ajax({
            type: "POST",
            url: "assign_curator",
            data: JSON.stringify(data),
            contentType: 'application/json',
            //Response
            success: function (data) {
                $('#assignCurator').prop( "disabled", false );
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
    $('#saveBackgroundTrait').click(function () {
        $('#saveBackgroundTrait').prop( "disabled", true );
        $('#loadingResults').show();
        var data = {}
        data.backgroundTrait = $('#backgroundTrait').val();
        data.ids = []
        $('.table-checkbox:checked').each(function () {
            var v = $(this).attr('id')
            v = this.id
            data.ids.push(v)
        })
        $.ajax({
            type: "POST",
            url: "update_background_traits",
            data: JSON.stringify(data),
            contentType: 'application/json',
            //Response
            success: function (data) {
                $('#saveBackgroundTrait').prop( "disabled", false );
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
    $('#saveDiseaseTrait').click(function () {
        $('#saveDiseaseTrait').prop( "disabled", true );
        $('#loadingResults').show();
        var data = {}
        data.diseaseTrait = $('#diseaseTrait').val();
        data.ids = []
        $('.table-checkbox:checked').each(function () {
            var v = $(this).attr('id')
            v = this.id
            data.ids.push(v)
        })
        $.ajax({
            type: "POST",
            url: "update_disease_traits",
            data: JSON.stringify(data),
            contentType: 'application/json',
            //Response
            success: function (data) {
                $('#saveDiseaseTrait').prop( "disabled", false );
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
    $('#saveEfoTrait').click(function () {
        $('#loadingResults').show();
        $('#saveEfoTrait').prop( "disabled", true );
        var data = {}
        data.efoTraits = $('#EFOTrait').val();
        data.ids = []
        $('.table-checkbox:checked').each(function () {
            var v = $(this).attr('id')
            v = this.id
            data.ids.push(v)
        })
        $.ajax({
            type: "POST",
            url: "update_efo_traits",
            data: JSON.stringify(data),
            contentType: 'application/json',
            //Response
            success: function (data) {
                $('#saveEfoTrait').prop( "disabled", false );
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
    $('#saveBackgroundEfos').click(function () {
        $('#loadingResults').show();
        $('#saveBackgroundEfos').prop( "disabled", true );
        var data = {}
        data.backgroundEfoTraits = $('#mappedBackgroundTraits').val();
        data.ids = []
        $('.table-checkbox:checked').each(function () {
            var v = $(this).attr('id')
            v = this.id
            data.ids.push(v)
        })
        $.ajax({
            type: "POST",
            url: "update_background_efo_traits",
            data: JSON.stringify(data),
            contentType: 'application/json',
            //Response
            success: function (data) {
                $('#saveBackgroundEfos').prop( "disabled", false );
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
    $('#deleteStudies').click(function () {
        $('#loadingResults').show();
        $('#deleteStudies').prop( "disabled", true );
        var data = {}
        data.ids = []
        $('.table-checkbox:checked').each(function () {
            var v = $(this).attr('id')
            v = this.id
            data.ids.push(v)
        })
        $.ajax({
            type: "POST",
            url: "delete_studies",
            data: JSON.stringify(data),
            contentType: 'application/json',
            //Response
            success: function (data) {
                $('#deleteStudies').prop( "disabled", false );
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
    $('#addSumStats').click(function () {
        $('#loadingResults').show();
        $('#addSumStats').prop( "disabled", true );
        var data = {}
        data.ids = []
        $('.table-checkbox:checked').each(function () {
            var v = $(this).attr('id')
            v = this.id
            data.ids.push(v)
        })
        $.ajax({
            type: "POST",
            url: "add_sum_stats",
            data: JSON.stringify(data),
            contentType: 'application/json',
            //Response
            success: function (data) {
                $('#deleteStudies').prop( "disabled", false );
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
    // turn the element to select2 select style
    $('#curationStatus').select2({
        placeholder: "Select a Curation Status",
        allowClear: true
    });
    $('#curator').select2({
        placeholder: "Select a Curator",
        allowClear: true
    });
    $('#EFOTrait').select2({
        placeholder: "Select an EFO Trait",
        allowClear: true
    });
    $('#diseaseTrait').select2({
        placeholder: "Select a Disease Trait",
        allowClear: true
    });
    $('#backgroundTrait').select2({
        placeholder: "Select a Background Disease Trait",
        allowClear: true
    });
    $('#mappedBackgroundTraits').select2({
        placeholder: "Select a Background EFO trait",
        allowClear: true
    });

})