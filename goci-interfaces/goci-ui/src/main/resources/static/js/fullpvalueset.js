/**
 * Created by dwelter on 20/10/16.
 */

$(document).ready(function() {

    if (window.location.pathname.indexOf('/downloads/summary-statistics') != -1 && $('#pvalue-sets-table-body').children().length == 0) {
        console.log("About to load all studies with full p-value sets");
        $("#downloads-item").addClass("active");

        loadStudiesList();

        loadResourcesList();
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

    $.getJSON('../api/search/summaryStatistics', {
                'q': searchTerm,
                'max': 200,
                'fl': 'author,publicationDate,pubmedId,publication,title,traitName,associationCount,author_s,accessionId',
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

    $('#query').text('fullPvalueSet:true');


};

function processStudyDoc(study, table) {

    var row = $("<tr>");
    var authorsearch = "<span><a href='../search?query=".concat(study.author).concat("'>").concat(study.author).concat(
            "</a></span>");

    var pubdate = study.publicationDate.substring(0, 10);
    row.append($("<td>").html(authorsearch.concat(' (PMID: ').concat(study.pubmedId).concat(')')));
    row.append($("<td>").html(pubdate));
    row.append($("<td>").html(study.publication));
    row.append($("<td>").html(study.title));
    var traitsearch = "<span><a href='../search?query=".concat(study.traitName).concat("'>").concat(study.traitName).concat(
            "</a></span>");
    row.append($("<td>").html(traitsearch));


    row.append($("<td>").html(study.associationCount));

    var a = (study.authorAscii_s).replace(/\s/g,"");
    var dir = a.concat("_").concat(study.pubmedId).concat("_").concat(study.accessionId);
    var ftplink = "<a href='ftp://ftp.ebi.ac.uk/pub/databases/gwas/summary_statistics/".concat(dir).concat("' target='_blank'>Click for summary statistics</a>");

    row.append($("<td>").html(ftplink));

    table.append(row);

};


function loadResourcesList(){
    var table = $('#sum-stats-table-body').empty();

    $.getJSON('../api/search/summaryStatsResources')
            .done(function(data) {
                renderResources(data, table);
            });
    console.log("Resources loaded from file!");

}

function renderResources(data, table){

    var resources = data.resources;

    for(var i = 0; i < resources.length; i++){
        var row = $("<tr>");

        var r = resources[i].split(',');
        row.append($("<td>").html(r[0]));
        row.append($("<td>").html(r[1]));

        var link = "<a href='".concat(r[2]).concat("' target='_blank'>").concat(r[2]).concat("</a>");

        row.append($("<td>").html(link));
        table.append(row);
    }



    $('#otherSumStatsDisplay').show();

}

function doSummaryStatsSort(field, id) {

    var queryTerm = $('#query').text();
    if (queryTerm == 'fullPvalueSet:true'){
        var searchTerm = queryTerm;
    }
    else {
        console.log("Something went wrong, the summary stats sort should not have been called");
    }


    $.getJSON('../api/search/summaryStatistics', {
        'q': searchTerm,
        'max': 100,
        'sort': field
    })
            .done(function(data) {
                processSortedSummaryStats(data, id);
            });
};


function processSortedSummaryStats(data, id){
    if (data.error != null) {
        var sorter = $('#' + id).find('span.sorted');
        sorter.removeClass('asc desc glyphicon-arrow-up glyphicon-arrow-down').addClass("glyphicon-sort unsorted");
    }
    else {
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
    }

}
