/**
 * Created by dwelter on 20/01/15.
 */
var SearchState = {
    LOADING: {value: 0},
    NO_RESULTS: {value: 1},
    RESULTS: {value: 2}
};

$(document).ready(function () {
    console.log("solr search loaded and ready");

    //toggle the +/- sign on the expand study button and expand the appropriate study
    $('#study-table-body').on('click', 'button.row-toggle', function () {
        $(this).find('span').toggleClass('glyphicon-plus glyphicon-minus');
        var target = $(this).attr('data-target');
        $(target).collapse('toggle');

    });

    $.getJSON('api/search/stats')
            .done(function (data) {
                      setStats(data);
                  });


    // Tooltips for various filter and table headings
    $('[data-toggle="tooltip"]').tooltip({
        placement: 'top',
        container: 'body'
    });

    if (window.history && window.history.pushState) {
        $(window).on('popstate', function () {
            applyFacet();
        });
    }

    if($('#query').text() != '') {
        loadResults();
    }
});

function loadResults() {
    var searchTerm = $('#query').text();

    console.log("Loading results for " + searchTerm);
    buildBreadcrumbs();
    $('#welcome-container').hide();
    $('#search-results-container').show();
    $('#loadingResults').show();

    //if(localStorage.getItem("traits") != null || $('#filter').text() != ''){
    if($('#filter').text() != ''){
        //console.log("I found something in the local storage");
        var traits;
      /*  if(localStorage.getItem("traits") != null)        {
            console.log("Value from localstorage: " + localStorage.getItem("traits"));
            traits = localStorage.getItem("traits");
            localStorage.clear();
        }
        else{*/
            console.log("Value from filter variable: " + $('#filter').text());
            traits = $('#filter').text();
        //}
        traitOnlySearch(traits);

    }
    else{
        $('#welcome-container').hide();
        $('#search-results-container').show();
        $('#loadingResults').show();

        solrSearch(searchTerm);
        if (window.location.hash) {
            console.log("Applying a facet");
            applyFacet();
        }
        else{
            $('#facet').text();
        }

    }
}

function buildBreadcrumbs() {
    // build breadcrumb trail
    console.log("Updating breadcrumbs...");
    $(".breadcrumb").empty();
    var breadcrumbs = $("ol.breadcrumb");
    // defaults
    breadcrumbs.append('<li><a href="home">GWAS</a></li>');
    breadcrumbs.append('<li><a href="search">Search</a></li>');
    var searchTerm = $('#query').text();

    if (searchTerm == '*'){
        searchTerm = '';
    }
    if (!window.location.hash) {
        console.log("Final breadcrumb is for '" + searchTerm + "'");
        breadcrumbs.append('<li class="active">' + searchTerm + '</li>');
    }
    else {
        var facet = window.location.hash.substr(1);
        console.log("Need breadcrumbs for '" + searchTerm + "' and '" + facet + "'");
        breadcrumbs.append('<li><a href="search?query=' + searchTerm + '">' + searchTerm + '</a></li>');
        var last = $("<li></li>").attr("class", "active");
        if (facet == "study") {
            last.text("Studies");
        }
        else if (facet == "association") {
            last.text("Associations");
        }
        else if (facet == "diseasetrait") {
            last.text("Catalog traits");
        }

        breadcrumbs.append(last);
    }
}

function solrSearch(queryTerm) {
    console.log("Solr research request received for " + queryTerm);
    var searchTerm = 'text:"'.concat(queryTerm).concat('"');
    setState(SearchState.LOADING);
    $.getJSON('api/search', {'q': searchTerm, 'group': 'true', 'group.by': 'resourcename', 'group.limit': 5})
        .done(function (data) {
            console.log(data);
            processData(data);
        });
}

function traitOnlySearch(traits) {
    console.log("Solr research request received for " + traits);
    setState(SearchState.LOADING);

    $('#traitOnly').text(traits);

    var searchTraits = traits.split('|');
    var searchTerm = 'text:*'

    $.getJSON('api/search/traits', {'q': searchTerm, 'group': 'true', 'group.by': 'resourcename', 'group.limit': 5, 'traitfilter[]': searchTraits}).done(function (data) {
                      console.log(data);
                      processData(data);
                  });
}

function processData(data) {
    var documents = data.grouped.resourcename.groups;

    setDownloadLink(data.responseHeader.params);
    console.log("Solr search returned " + documents.length + " documents");
    updateCountBadges(data.facet_counts.facet_fields.resourcename);

    if(!$('#filter-form').hasClass('in-use')){
        if(data.responseHeader.params.q.indexOf('*') != -1 && data.responseHeader.params.fq != null){
            var fq = data.responseHeader.params.fq;

            if(fq.charAt(fq.length-1) == '"'){
                fq = fq.substr(0, fq.length-1);
            };

            var terms = fq.split('"');
            var traits = []

            for(var i=0; i<terms.length; i++){
                if(terms[i].indexOf('traitName') == -1){
                    traits.push(terms[i].replace(/\s/g, '+'));
                }
            }
            generateTraitDropdown(data.responseHeader.params.q, traits);
        }
        else{
            generateTraitDropdown(data.responseHeader.params.q, null);
        }
    }

    if (documents.length != 0) {
        $(".results-container .table-toggle").hide();
        for (var j = 0; j < documents.length; j++) {
            var group = documents[j];


            if (group.groupValue == "study") {
                var studyTable = $('#study-table-body').empty();

                for (var k = 0; k < group.doclist.docs.length; k++) {
                    try{
                        var doc = group.doclist.docs[k];
                        processStudy(doc, studyTable);
                    }
                    catch (ex){
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
                    try{
                        var doc = group.doclist.docs[k];
                        processAssociation(doc, associationTable);
                    }
                    catch (ex){
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
                    try{
                        var doc = group.doclist.docs[k];
                         processTrait(doc, traitTable);
                    }
                    catch (ex){
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

        setState(SearchState.RESULTS);
    }
    else {
        setState(SearchState.NO_RESULTS);
    }

    $('#loadingResults').hide();
    console.log("Data display complete");
}

function setState(state) {
    var loading = $('#loading');
    var noresults = $('#noResults');
    var results = $('#results');
    console.log("Search state update...");
    console.log(state);
    switch (state.value) {
        case 0:
            loading.show();
            noresults.hide();
            results.hide();
            break;
        case 1:
            loading.hide();
            noresults.show();
            results.hide();
            break;
        case 2:
            loading.hide();
            noresults.hide();
            results.show();
            break;
        default:
            console.log("Unknown search state; redirecting to search page");
            window.location = "search";
    }
}


function updateCountBadges(countArray) {
    console.log("Updating facet counts for " + (countArray.length / 2) + " badges");
    for (var i = 0; i < countArray.length; i = i + 2) {
        var resource = countArray[i];
        var count = countArray[i + 1];

        var facet = $('#' + resource + '-facet span');
        facet.empty();
        facet.append(count);

        if ($('#' + resource + '-facet').hasClass("disabled")) {
            $('#' + resource + '-facet').removeClass("disabled");
            var summary = $('#' + resource + '-summaries');
            summary.removeClass("no-results");
            summary.show();
        }

        if (count == 0) {
            $('#' + resource + '-facet').addClass("disabled");
            var summary = $('#' + resource + '-summaries');
            summary.addClass("no-results");
            summary.hide();
        }
    }
}

function generateTraitDropdown(queryTrait, subTraits) {
    $.getJSON('api/search/traitcount', {'q': queryTrait, 'traitfilter[]': subTraits})
        .done(function (data) {
            console.log(data);
            processTraitCounts(data);
        });
}

function processTraitCounts(data) {
    var traits = data.facet_counts.facet_fields.traitName_s;

    $('#trait-dropdown ul').empty();

    for (var i = 0; i < traits.length; i = i + 2) {
        var trait = traits[i];
        var count = traits[i + 1];
        $('#trait-dropdown ul').append($("<li>").html("<input type='checkbox' class='trait-check' value='".concat(trait).concat("'/>&nbsp;").concat(trait).concat(" (").concat(count).concat(")</a>")));
    }
}

function setDownloadLink(searchParams){
    console.log(searchParams);
    var baseUrl = 'api/search/downloads?';
    var q= "q=".concat(searchParams.q);

    var trait = '';
    var traitFilter = '&traitfilter[]=';
    var pval = '&pvalfilter=';
    var or = '&orfilter=';
    var beta = '&betafilter=';
    var date = '&datefilter=';

    if(searchParams.q.indexOf('*') != -1 && searchParams.fq != null){
        console.log('Need to build the download link a bit differently');

        var fq = searchParams.fq;

        if(fq.charAt(fq.length-1) == '"'){
            fq = fq.substr(0, fq.length-1);
        };

        var terms = fq.split('"');

        for(var i=0; i<terms.length; i++){
            if(terms[i].indexOf('traitName') == -1){
                console.log(terms[i]);
                trait = trait.concat(traitFilter).concat(terms[i]);
            }
        }

    }
    else {
        console.log('Building the download link the default way');
        pval = pval.concat(processPval());
        or = or.concat(processOR());
        beta = beta.concat(processBeta());
        date = date.concat(processDate());

        var traits = processTraitDropdown();

        if (traits != '') {
            for (var t = 0; t < traits.length; t++) {
                trait = trait.concat(traitFilter).concat(traits[t]);
            }
        }
        else {
            trait = traitFilter;
        }
    }

    var url = baseUrl.concat(q).concat(pval).concat(or).concat(beta).concat(date).concat(trait);
    $('#results-download').removeAttr('href').attr('href', url);

}


function setStats(data){
    try{
        $('#releasedate-stat').text("Last data release on " + data.date);
        $('#studies-stat').text(data.studies + " studies");
        $('#associations-stat').text(data.associations + " SNP-trait associations");
        $('#genomebuild').text("Genome assembly " + data.genebuild);
        $('#dbsnpbuild').text("dbSNP Build " + data.dbsnpbuild);
        $('#catalog-stats').show();
    }
    catch (ex){
        console.log("Failure to process stats " + ex);
    }
}