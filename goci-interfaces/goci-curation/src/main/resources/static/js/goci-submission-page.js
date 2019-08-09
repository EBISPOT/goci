$(document).ready(function () {
    $('#submissionDataTable').DataTable({
        "columns": [
            {"mData": "status", "sDefaultContent": "", "sTitle": "Submission Status"},
            {"mData": "id", "sTitle": "Submission ID"},
            {"mData": "created", "sDefaultContent": "", "sTitle": "Date Created"},
            {"mData": "curator", "sDefaultContent": "", "sTitle": "Curator"},
            {"mData": "title", "sDefaultContent": "", "sTitle": "Title"}, // <-- which values to use inside object
            {"mData": "author", "sDefaultContent": "", "sTitle": "Author"},
            {"mData": "pubMedID", "sTitle": "PubMed ID"},
            {"mData": "publicationStatus", "sTitle": "Publication Status"}
        ],
        "aLengthMenu": [
            [5, 10, 25, 50, 100, -1],
            [5, 10, 25, 50, 100, "All"]
        ],
        "iDisplayLength": -1,
        //"order": [[ 0, "desc" ]],
    });
});