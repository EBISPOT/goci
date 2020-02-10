$(document).ready(function () {
    $('#toggleSubmission').click(function () {
        $('#submissionData').toggle();
    });
    $('#studyTable').DataTable();
    $('#sampleTable').DataTable();
    $('#assocTable').DataTable();

    $('#loadingResults').hide();
    $('#importButton').click(function () {
        $('#loadingResults').show();
        $('#importButton').prop( "disabled", true );

    });
    $('#testButton').click(function () {
        $('#loadingResults').show();
        $('#importButton').prop( "disabled", true );
    });
})