$(document).ready(function () {
    var table = $('#submissionDataTable').DataTable({
        "columns": [
            {"mData": "select_all", "sDefaultContent": "", "bSearchable": false, "bSortable": false},
            {"mData": "studyId", "sDefaultContent": "", "sTitle": "Study Id", "bSearchable": true},
            {"mData": "accession", "sDefaultContent": "", "sTitle": "Study Accession", "bSearchable": true},
            {"mData": "diseaseTrait", "sDefaultContent": "", "sTitle": "Disease Trait", "bSearchable": true},
            {"mData": "efoTraits", "sDefaultContent": "", "sTitle": "EFO Traits", "bSearchable": true},
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

    $('#assignStatusForm').click(function () {
        var data = {}
        data.status = $('#curationStatus').val();
        data.ids = []
        $('.table-checkbox:checked').each(function () {
            var v = $(this).attr('id')
            v = this.id
            data.ids.push(v)
        })
        $.getJSON("status_update",
            {"data": data},
            //Response
            function(data) {
                alert(data.message);
                //Reload page
                location.reload();
            });
    });
})