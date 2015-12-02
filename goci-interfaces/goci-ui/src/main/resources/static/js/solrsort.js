/**
 * Created by dwelter on 18/02/15.
 */


$(document).ready(function() {

    $('.sorting').click(function() {

        if ($(this).hasClass('unsorted')) {
            if ($(this).parents('thead').find('span.sorted').length != 0) {
                console.log("Another column in this table has already been sorted");
                var previous = $(this).parents('thead').find('span.sorted');
                previous.removeClass('sorted').addClass('unsorted');
                previous.removeClass('asc desc glyphicon-arrow-up glyphicon-arrow-down').addClass("glyphicon-sort");
            }

            $(this).removeClass('glyphicon-sort').addClass('glyphicon-arrow-up');
            $(this).removeClass('unsorted').addClass('sorted asc');
        }
        else {
            $(this).toggleClass('glyhicon-arrow-up glyphicon-arrow-down');
            $(this).toggleClass('asc desc');
        }

        var facet = $(this).parents('thead').attr('id').split("-")[0];

        if ($('#' + facet + '-summaries').hasClass('more-results')) {
            var id = $(this).parent('th').attr('id');
            var field = id;

            if (id.indexOf('-') != -1) {
                field = id.split('-')[0];
            }

            if ($(this).hasClass('asc')) {
                field = field.concat('+asc');
            }
            else {
                field = field.concat('+desc');
            }
            console.log(facet);
            console.log(field);

            doSortingSearch(facet, field, id);
        }
        else if ($('#expand-table').hasClass('table-expanded')) {
            loadAdditionalResults(facet, true);
        }
        else {
            loadAdditionalResults(facet, false);
        }

    });

});


function doSortingSearch(facet, field, id) {

    var queryTerm = $('#query').text();
    if (queryTerm == '*') {
        var searchTerm = 'text:'.concat(queryTerm);
    }
    else {
        var searchTerm = 'text:"'.concat(queryTerm).concat('"');
    }
    var pval = processPval();
    var or = processOR();
    var beta = processBeta();
    var date = processDate();
    var traits = processTraitDropdown();

    if ($('#filter').text() != '') {
        if ($('#filter').text() != 'recent' && traits == '') {
            var terms = $('#filter').text();
            terms = terms.replace(/\s/g, '+');

            traits = terms.split('|');
        }
        else if ($('#filter').text() == 'recent' && date == '') {
            date = "[NOW-3MONTH+TO+*]";
        }
    }


    $.getJSON('api/search/sort', {
                'q': searchTerm,
                'facet': facet,
                'group': 'true',
                'group.by': 'resourcename',
                'group.limit': 5,
                'pvalfilter': pval,
                'orfilter': or,
                'betafilter': beta,
                'datefilter': date,
                'traitfilter[]': traits,
                'sort': field
            })
            .done(function(data) {
                processSortedData(data, id);
            });
};

function processSortedData(data, id) {
    if (data.error != null) {
        var sorter = $('#' + id).find('span.sorted');
        sorter.removeClass('asc desc glyphicon-arrow-up glyphicon-arrow-down').addClass("glyphicon-sort unsorted");
    }
    else {
        var documents = data.grouped.resourcename.groups;

        setDownloadLink(data.responseHeader.params);

        for (var j = 0; j < documents.length; j++) {
            var group = documents[j];

            if (group.groupValue == "study") {
                var studyTable = $('#study-table-body').empty();

                for (var k = 0; k < group.doclist.docs.length; k++) {
                    try {
                        var doc = group.doclist.docs[k];
                        processStudy(doc, studyTable);
                    }
                    catch (ex) {
                        console.log("Failure to process document " + ex);
                    }
                }
                if (group.doclist.numFound > 5) {
                    $('#study-summaries .table-toggle').show();
                    $('#study-summaries').addClass("more-results");
                    $('.study-toggle').empty().text("Show more results");

                }
            }
            else if (group.groupValue == "association") {
                var associationTable = $('#association-table-body').empty();

                for (var k = 0; k < group.doclist.docs.length; k++) {
                    try {
                        var doc = group.doclist.docs[k];
                        processAssociation(doc, associationTable);
                    }
                    catch (ex) {
                        console.log("Failure to process document " + ex);
                    }
                }
                if (group.doclist.numFound > 5) {
                    $('#association-summaries .table-toggle').show();
                    $('#association-summaries').addClass("more-results");
                    $('.association-toggle').empty().text("Show more results");

                }
            }
            else if (group.groupValue == "diseasetrait") {
                var traitTable = $('#diseasetrait-table-body').empty();
                for (var k = 0; k < group.doclist.docs.length; k++) {
                    try {
                        var doc = group.doclist.docs[k];
                        processTrait(doc, traitTable);
                    }
                    catch (ex) {
                        console.log("Failure to process document " + ex);
                    }
                }
                if (group.doclist.numFound > 5) {
                    $('#diseasetrait-summaries .table-toggle').show();
                    $('#diseasetrait-summaries').addClass("more-results");
                    $('.diseasetrait-toggle').empty().text("Show more results");

                }
            }
        }
    }
};

