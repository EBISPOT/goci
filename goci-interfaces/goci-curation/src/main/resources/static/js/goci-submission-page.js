$(document).ready(function () {


    $('#submissionDataTable').DataTable({
        "columns": [
            {"mData": "id", "sTitle": "Submission ID"},
            {"mData": "pubMedID", "sTitle": "PubMed ID"},
            {"mData": "title", "sDefaultContent": "", "sTitle": "Title"}, // <-- which values to use inside object
            {"mData": "curator", "sDefaultContent": "", "sTitle": "Curator"},
            {"mData": "author", "sDefaultContent": "", "sTitle": "Author"},
            {"mData": "created", "sDefaultContent": "", "sTitle": "Date Created"},
            {"mData": "status", "sDefaultContent": "", "sTitle": "Status"}
        ],
        "aLengthMenu": [
            [5, 10, 25, 50, 100, -1],
            [5, 10, 25, 50, 100, "All"]
        ],
        "iDisplayLength": -1,
        //"order": [[ 0, "desc" ]],
    });
});