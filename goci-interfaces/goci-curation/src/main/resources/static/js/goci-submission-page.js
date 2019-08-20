$(document).ready(function () {
    $('#submissionDataTable').DataTable({
        "columns": [
            {"mData": "status", "sDefaultContent": "", "sTitle": "Submission Status", "bSearchable": true },
            {"mData": "id", "sTitle": "Submission ID", "bSearchable": true},
            {"mData": "created", "sDefaultContent": "", "sTitle": "Date Created", "bSearchable": true},
            {"mData": "curator", "sDefaultContent": "", "sTitle": "Submitter", "bSearchable": true},
            {"mData": "title", "sDefaultContent": "", "sTitle": "Title", "bSearchable": true}, // <-- which values to use inside object
            {"mData": "author", "sDefaultContent": "", "sTitle": "Author", "bSearchable": true},
            {"mData": "pubMedID", "sTitle": "PubMed ID", "bSearchable": true},
            {"mData": "publicationStatus", "sTitle": "Publication Status", "bSearchable": true}
        ],
        "aLengthMenu": [
            [5, 10, 25, 50, 100, -1],
            [5, 10, 25, 50, 100, "All"]
        ],
        "iDisplayLength": -1,
        //"order": [[ 0, "desc" ]],
    });
});