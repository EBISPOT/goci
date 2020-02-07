$(document).ready(function () {
    $('#toggleSubmission').click(function () {
        $('#submissionData').toggle();
    });
    $('#studyTable').DataTable();
    $('#sampleTable').DataTable();
    $('#assocTable').DataTable();
})