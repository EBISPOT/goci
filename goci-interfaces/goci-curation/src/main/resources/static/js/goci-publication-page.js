$(document).ready(function () {
    var table = $('#submissionDataTable').DataTable({
        "columns": [
            {"mData": "select_all", "sDefaultContent": "", "bSearchable": false, "bSortable": false},
            {"mData": "studyId", "sDefaultContent": "", "sTitle": "Study Id", "bSearchable": true},
            {"mData": "accession", "sDefaultContent": "", "sTitle": "Study Accession", "bSearchable": true},
            {"mData": "hasSumStats", "sDefaultContent": "", "sTitle": "Has SumStats", "bSearchable": true},
            {"mData": "diseaseTrait", "sDefaultContent": "", "sTitle": "Disease Trait", "bSearchable": true},
            {"mData": "efoTraits", "sDefaultContent": "", "sTitle": "EFO Traits", "bSearchable": true},
            {"mData": "backgroundTrait", "sDefaultContent": "", "sTitle": "Background Trait", "bSearchable": true},
            {"mData": "backgroundEfoTraits", "sDefaultContent": "", "sTitle": "Background EFO Traits", "bSearchable": true},
            {"mData": "snps", "sTitle": "SNPs", "bSearchable": true},
            {"mData": "status", "sDefaultContent": "", "sTitle": "Curation Status", "bSearchable": true},
            {"mData": "curator", "sTitle": "Curator", "bSearchable": true},
            {"mData": "studyTag", "sDefaultContent": "", "sTitle": "Study Tag", "bSearchable": true}
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