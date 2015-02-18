/**
 * Created by dwelter on 17/02/15.
 */


$(document).ready(function () {
    $('#expand-table').click(function () {
        //if the table is collapsed, expand it
        if ($(this).hasClass('table-collapsed')) {
            loadAdditionalResults("study", true);
        }
        //else collapse it
        else {
            //$('#study-table-body').find('.hidden-resource').collapse('hide');
            $('#study-table-body').find('.hidden-study-row').collapse('hide');
            $('#study-table-body').find('span.tgb').removeClass('glyphicon-minus').addClass('glyphicon-plus');
            $('#study-table-body').find('tr:gt(29)').remove();

            $(this).addClass('table-collapsed');
            $(this).empty().text("Expand all studies");
            $('#study-summaries').addClass('more-results');
            $('.study-toggle').empty().text("Show more results");
        }
    });

    $('.study-toggle').click(function () {
         if($('#study-summaries').hasClass('more-results')){
              console.log("More results to load");
             loadAdditionalResults("study", false);
             $(this).empty().text("Show fewer results");
        }
        else{
             $(this).empty().text("Show more results");
             $('#study-summaries').addClass('more-results');
             if($('#filter-form').hasClass('in-use')){
                 doFiltering();
             }
             else{
                loadResults();
             }
         }
    });

    $('.association-toggle').click(function () {
        if($('#association-summaries').hasClass('more-results')){
            console.log("More results to load");
            loadAdditionalResults("association", false);
            $(this).empty().text("Show fewer results");
        }
        else{
            $(this).empty().text("Show more results");
            $('#association-summaries').addClass('more-results');
            if($('#filter-form').hasClass('in-use')){
                doFiltering();
            }
            else{
                loadResults();
            }        }
    });

    $('.diseasetrait-toggle').click(function () {
        if($('#diseasetrait-summaries').hasClass('more-results')){
            console.log("More results to load");
            loadAdditionalResults("diseasetrait", false);
            $(this).empty().text("Show fewer results");

        }
        else{
            $(this).empty().text("Show more results");
            $('#diseasetrait-summaries').addClass('more-results');
            if($('#filter-form').hasClass('in-use')){
                doFiltering();
            }
            else{
                loadResults();
            }        }
    });



});


function loadAdditionalResults(facet, expand){
    var queryTerm = $('#query').text();
    var pval = processPval();
    var or = processOR();
    var beta = processBeta();
    var date = processDate();
    var traits = processTraitDropdown();


    var searchTerm = 'text:"'.concat(queryTerm).concat('"');
    $.getJSON('api/search/moreresults',
        {'q': searchTerm,
            'max': 1000,
            'facet': facet,
            'pvalfilter': pval,
            'orfilter': or,
            'betafilter': beta,
            'datefilter': date,
            'traitfilter[]': traits})
        .done(function (data) {
            addResults(data, expand);
        });
}

function addResults(data, expand){
    var documents = data.response.docs;
    console.log("Got a bunch of docs" + documents.length);

    if(data.responseHeader.params.fq == "resourcename:study" || $.inArray("resourcename:study", data.responseHeader.params.fq) != -1) {
        console.log("Processing studies");
        var studyTable = $('#study-table-body').empty();
        $('#study-summaries').removeClass('more-results');

        for (var j = 0; j < documents.length; j++) {
            var doc = documents[j];
            processStudy(doc, studyTable);
        }
    }

    else if(data.responseHeader.params.fq == "resourcename:association" || $.inArray("resourcename:association", data.responseHeader.params.fq) != -1){
        console.log("Processing associations");
        var associationTable = $('#association-table-body').empty();
        $('#association-summaries').removeClass('more-results');

        for (var j = 0; j < documents.length; j++) {
            var doc = documents[j];
            processAssociation(doc, associationTable);
        }
    }

    else if(data.responseHeader.params.fq == "resourcename:diseasetrait" || $.inArray("resourcename:diseasetrait", data.responseHeader.params.fq) != -1){
        console.log("Processing diseasetraits");
        var traitTable = $('#diseasetrait-table-body').empty();
        $('#diseasetrait-summaries').removeClass('more-results');

        for (var j = 0; j < documents.length; j++) {
            var doc = documents[j];
            processTrait(doc, traitTable);
        }

    }

    if(expand){
        $('.study-toggle').empty().text("Show fewer results");
        $('#study-table-body').find('.hidden-study-row').collapse('show');
        $('#study-table-body').find('span.tgb').removeClass('glyphicon-plus').addClass('glyphicon-minus');
        $('#expand-table').removeClass('table-collapsed')
        $('#expand-table').empty().text("Collapse all studies");
    }
}

