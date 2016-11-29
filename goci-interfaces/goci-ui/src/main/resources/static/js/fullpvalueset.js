/**
 * Created by dwelter on 20/10/16.
 */

$(document).ready(function() {

    if (window.location.pathname.indexOf('/downloads/summary-statistics') != -1 && $('#pvalue-sets-table-body').children().length == 0) {
        console.log("About to load all studies with full p-value sets");
        $("#downloads-item").addClass("active");

        loadStudiesList();
    }


    $('#traitForm').submit(function(event) {
        event.preventDefault();
        console.log("Trait submission request received");
        getCheckedTraits();
    });
});

function loadStudiesList() {

    $('#loadingStudies').show();

    var searchTerm = 'fullPvalueSet:true';

    $.getJSON('../api/search/alltraits', {
                'q': searchTerm,
                'max': 1,
            })
            .done(function(data) {
                displayStudies(data);
            });
}


function displayStudies(data) {

    var documents = data.response.docs;
    console.log("Got a bunch of docs" + documents.length);


    if (data.responseHeader.params.fq == "resourcename:study" ||
            $.inArray("resourcename:study", data.responseHeader.params.fq) != -1) {
        console.log("Processing studies");
        var table = $('#pvalue-sets-table-body').empty();

        for (var j = 0; j < documents.length; j++) {
            try {
                var doc = documents[j];
                processStudyDoc(doc, table);
            }
            catch (ex) {
                console.log("Failure to process document " + ex);
            }
        }
    }

    $('#loadingStudies').hide();
    $('#pvalueSetDisplay').show();


};

function processStudyDoc(study, table) {

    var row = $("<tr>");
    var authorsearch = "<span><a href='search?query=".concat(study.author).concat("'>").concat(study.author).concat(
            "</a></span>");

    var pubdate = study.publicationDate.substring(0, 10);
    row.append($("<td>").html(authorsearch.concat(' (PMID: ').concat(study.pubmedId).concat(')')));
    row.append($("<td>").html(pubdate));
    row.append($("<td>").html(study.publication));
    row.append($("<td>").html(study.title));
    var traitsearch = "<span><a href='search?query=".concat(study.traitName).concat("'>").concat(study.traitName).concat(
            "</a></span>");
    row.append($("<td>").html(traitsearch));


    row.append($("<td>").html(study.associationCount));



    var ftplink = "<a href='ftp://ftp.ebi.ac.uk/pub/databases/gwas/full_pvalue_sets/".concat(study.pubmedId).concat("' target='_blank'>Click for full p-value set</a>");

    row.append($("<td>").html(ftplink));

    table.append(row);

};
