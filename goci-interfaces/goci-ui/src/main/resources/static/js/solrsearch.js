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
    //toggle the chevron on the expand/collapse table button
    $('.table-toggle').click(function () {
        $(this).find('span').toggleClass('glyphicon-chevron-down glyphicon-chevron-up');

    });

    //toggle the +/- sign on the expand study button and expand the appropriate study
    $('#study-table-body').on('click', 'button.row-toggle', function () {
        $(this).find('span').toggleClass('glyphicon-plus glyphicon-minus');
        var target = $(this).attr('data-target');
        $(target).collapse('toggle');

    });


    //$('.study-toggle').click(function () {
    //    $('#study-table-body').find('.hidden-study-row.in').collapse('hide');
    //});

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
    loadResults();
});

function loadResults() {
    var searchTerm = $('#query').text();
    console.log("Search term is " + searchTerm);
    if (searchTerm) {
        console.log("Loading results for " + searchTerm);

        buildBreadcrumbs();

        $('#lower_container').show();
        $('#loadingResults').show();

        solrSearch(searchTerm);
        if (window.location.hash) {
            console.log("Applying a facet");
            applyFacet();
        }
        //else {
        //    console.log("Clearing all facets");
        //    // no facets to apply, so make sure we are showing all results tables
        //    clearFacetting();
        //}
        //$('#lower_container').show();
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
        //else if (facet == "singlenucleotidepolymorphism") {
        //    last.text("SNPs");
        //}
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

function processData(data) {
    var documents = data.grouped.resourcename.groups;
    console.log("Solr search returned " + documents.length + " documents");
    updateCountBadges(data.facet_counts.facet_fields.resourcename);

    if(!$('#filter-form').hasClass('in-use')){
        generateTraitDropdown(data.responseHeader.params.q);
    }

    if (documents.length != 0) {
        //var snpTable = $('#singlenucleotidepolymorphism-table-body').empty();

        $(".results-container .table-toggle").hide();
        for (var j = 0; j < documents.length; j++) {
            var group = documents[j];

            if (group.groupValue == "study") {
                var studyTable = $('#study-table-body').empty();

                for (var k = 0; k < group.doclist.docs.length; k++) {
                    var doc = group.doclist.docs[k];
                    processStudy(doc, studyTable);
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
                    var doc = group.doclist.docs[k];
                    processAssociation(doc, associationTable);
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
                    var doc = group.doclist.docs[k];
                    processTrait(doc, traitTable);
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
            window.location = "/search";
    }
}

function processStudy(study, table) {
    var row = $("<tr>");
    //row.addClass('mainrow');
    var hiddenrow = $("<tr>");


    //if (table.find('.mainrow').length >= 5) {
    //    row.addClass('accordion-body');
    //    row.addClass('collapse');
    //    row.addClass('hidden-resource');
    //}
    var europepmc = "http://www.europepmc.org/abstract/MED/".concat(study.pubmedId);
    var authorsearch = "<span><a href='/search?query=".concat(study.author).concat("'>").concat(study.author).concat("</a></span>");
    var epmclink = "<span><a href='".concat(europepmc).concat("' target='_blank'>").concat("<span class='glyphicon glyphicon-link'></span></a></span>");

    row.append($("<td>").html(authorsearch.concat('&nbsp;&nbsp;').concat(epmclink)));
    row.append($("<td>").html(study.publicationDate[0].substring(0, 10)));
    row.append($("<td>").html(study.publication));
    row.append($("<td>").html(study.title));
    var traitsearch = "<span><a href='/search?query=".concat(study.traitName).concat("'>").concat(study.traitName).concat("</a></span>");
    row.append($("<td>").html(traitsearch));

    //var associationsearch = "<span><a href='/search?query=".concat(study.id.substring(0,6)).concat("'>").concat(study.associationCount).concat("</a></span>");
    row.append($("<td>").html(study.associationCount));

    var id = (study.id).replace(':', '-');
    var plusicon = "<button class='row-toggle btn btn-default btn-xs accordion-toggle' data-toggle='collapse' data-target='.".concat(id).concat(".hidden-study-row' aria-expanded='false' aria-controls='").concat(study.id).concat("'><span class='glyphicon glyphicon-plus tgb'></span></button>");

    row.append($("<td>").html(plusicon));
    table.append(row);


    hiddenrow.addClass(id);
    hiddenrow.addClass('collapse');
    hiddenrow.addClass('accordion-body');
    hiddenrow.addClass('hidden-study-row');

    //var innerTable = $("<table>").addClass('table').addClass('sample-info');
    var innerTable = $("<table>").addClass('sample-info');

    innerTable.append($("<tr>").append($("<th>").attr('style', 'width: 30%').html("Initial sample description")).append($("<td>").html(study.initialSampleDescription)));
    innerTable.append($("<tr>").append($("<th>").attr('style', 'width: 30%').html("Replication sample description")).append($("<td>").html(study.replicateSampleDescription)));
    innerTable.append($("<tr>").append($("<th>").attr('style', 'width: 30%').html("Platform [SNPs passing QC]")).append($("<td>").html(study.platform)));

    var r4 = $("<tr>");
    r4.append($("<th>").attr('style', 'width: 30%').html("CNV study?"));

    if (study.cnv) {
        r4.append($("<td>").html("yes"));
    }
    else {
        r4.append($("<td>").html("no"));
    }
    innerTable.append(r4);

    hiddenrow.append($('<td>').attr('colspan', 7).attr('style', 'border-top: none').append(innerTable));

    table.append(hiddenrow);
}

function processAssociation(association, table) {
    var row = $("<tr>");
    //if (table.find('tr').length >= 5) {
    //    row.addClass('accordion-body');
    //    row.addClass('collapse');
    //    row.addClass('hidden-resource');
    //}

    if (association.rsId != null && association.strongestAllele != null) {
        if ((association.rsId[0].indexOf(',') == -1) && (association.rsId[0].indexOf('x') == -1)) {
            var rsidsearch = "<span><a href='/search?query=".concat(association.rsId[0]).concat("'>").concat(association.strongestAllele[0]).concat("</a></span>");
            var dbsnp = "<span><a href='http://www.ensembl.org/Homo_sapiens/Variation/Summary?v=".concat(association.rsId[0]).concat("'  target='_blank'>").concat("<span class='glyphicon glyphicon-link'></span></a></span>");
            row.append($("<td>").html(rsidsearch.concat('&nbsp;&nbsp;').concat(dbsnp)));
        }
        else {
            var content = '';
            var rsIds = '';
            var alleles = '';
            var type = '';
            if(association.rsId[0].indexOf(',') != -1) {
                rsIds = association.rsId[0].split(',');
                alleles = association.strongestAllele[0].split(',');
                type = ',';
            }
            else if(association.rsId[0].indexOf('x') != -1){
                rsIds = association.rsId[0].split('x');
                alleles = association.strongestAllele[0].split('x');
                type = 'x';
            }

            for(var i=0; i<alleles.length; i++){
                console.log(alleles[i].trim()) ;
                for (var j=0; j<rsIds.length; j++){
                    if(alleles[i].trim().indexOf(rsIds[j].trim()) != -1){
                        var rsidsearch = "<span><a href='/search?query=".concat(rsIds[j].trim()).concat("'>").concat(alleles[i].trim()).concat("</a></span>");
                        var dbsnp = "<span><a href='http://www.ensembl.org/Homo_sapiens/Variation/Summary?v=".concat(rsIds[j].trim()).concat("'  target='_blank'>").concat("<span class='glyphicon glyphicon-link'></span></a></span>");
                        if(content == ''){
                            content = content.concat(rsidsearch.concat('&nbsp;&nbsp;').concat(dbsnp));
                            console.log(content);
                        }
                        else{
                            if(type == 'x'){
                                content = content.concat(' x ').concat(rsidsearch.concat('&nbsp;&nbsp;').concat(dbsnp));
                            }
                            else{
                                content = content.concat(', <br>').concat(rsidsearch.concat('&nbsp;&nbsp;').concat(dbsnp));
                            }
                            console.log(content);
                        }
                    }
                }
            }
            row.append($("<td>").html(content));

        }
    }
    else if (association.rsId != null && association.strongestAllele == null){
        row.append($("<td>").html(association.rsId));
    }
    else{
        row.append($("<td>"));
    }
    row.append($("<td>").html(association.riskFrequency));
    var pval = association.pValue;

    if (association.qualifier != null && association.qualifier != '') {
        pval = pval.toString().concat(" ").concat(association.qualifier[0]);
    }
    row.append($("<td>").html(pval));

    if (association.orType == true) {
        row.append($("<td>").html(association.orPerCopyNum));
        row.append($("<td>").html(''));
    }
    else {
        row.append($("<td>").html(''));
        if (association.orPerCopyUnitDescr != null) {
            var beta = (association.orPerCopyNum).toString().concat(" ").concat(association.orPerCopyUnitDescr);
            row.append($("<td>").html(beta));
        }
        else {
            row.append($("<td>").html(association.orPerCopyNum));
        }
    }
    row.append($("<td>").html(association.orPerCopyRange));
    if(association.region != null) {
        if (association.region[0].indexOf('[') != -1) {
            var region = association.region[0].split('[')[0];
            var regionsearch = "<span><a href='/search?query=".concat(region).concat("'>").concat(association.region[0]).concat("</a></span>");
            row.append($("<td>").html(regionsearch));
        }
        else {
            var regionsearch = "<span><a href='/search?query=".concat(association.region).concat("'>").concat(association.region).concat("</a></span>");
            row.append($("<td>").html(regionsearch));
        }
    }
    else{
        row.append($("<td>"));
    }

    var location = "chr";
    if(association.chromosomeName != null){
        location = location.concat(association.chromosomeName);
    }
    else{
        location = location.concat("?");
    }
    if(association.chromosomePosition){
        var locationsearch = "<span><a href='/search?query=".concat(association.chromosomePosition).concat("'>").concat(association.chromosomePosition).concat("</a></span>");
        location = location.concat(":").concat(locationsearch);
    }
    else{
        location = location.concat(":").concat("?");
    }
    row.append($("<td>").html(location));

    //row.append($("<td>"));
    row.append($("<td>").html(association.context));

    var repgene = '';
    if (association.reportedGene != null) {
        if(association.reportedGene[0] == "NR"){
            repgene = association.reportedGene[0];
        }
        else{
            for (var j = 0; j < association.reportedGene.length; j++) {
                var repgeneearch = "<span><a href='/search?query=".concat(association.reportedGene[j]).concat("'>").concat(association.reportedGene[j]).concat("</a></span>");
                if (repgene == '') {
                    repgene = repgeneearch;
                }

                else {
                    repgene = repgene.concat(", ").concat(repgeneearch);
                }
            }
        }
    }
    row.append($("<td>").html(repgene));

    var mapgene = '';
    if (association.mappedGene != null) {
        for (var j = 0; j < association.mappedGene.length; j++) {
            var mapgeneearch = "<span><a href='/search?query=".concat(association.mappedGene[j]).concat("'>").concat(association.mappedGene[j]).concat("</a></span>");
            if (mapgene == '') {
                mapgene = mapgeneearch;
            }

            else {
                mapgene = mapgene.concat(", ").concat(mapgeneearch);
            }
        }
    }
    row.append($("<td>").html(mapgene));

    if(association.traitName != null){var traitsearch = "<span><a href='/search?query=".concat(association.traitName).concat("'>").concat(association.traitName).concat("</a></span>");
        row.append($("<td>").html(traitsearch));
    }
    else {
        row.append($("<td>"));
    }

    var studydate = association.publicationDate[0].substring(0, 4);
    var author = association.author[0].concat(", ").concat(studydate);

    var europepmc = "http://www.europepmc.org/abstract/MED/".concat(association.pubmedId);
    var searchlink = "<span><a href='/search?query=".concat(association.author).concat("'>").concat(author).concat("</a></span>");
    var epmclink = "<span><a href='".concat(europepmc).concat("' target='_blank'>").concat("<span class='glyphicon glyphicon-link'></span></a></span>");
    row.append($("<td>").html(searchlink.concat('&nbsp;&nbsp;').concat(epmclink)));


    table.append(row);
}

function processTrait(diseasetrait, table) {
    var row = $("<tr>");
    //if (table.find('tr').length >= 5) { row.addClass('accordion-body');
    //    row.addClass('collapse');
    //    row.addClass('hidden-resource');
    //}
    var traitsearch = "<span><a href='/search?query=".concat(diseasetrait.traitName).concat("'>").concat(diseasetrait.traitName).concat("</a></span>");
    row.append($("<td>").html(traitsearch));

    var efo = '';
    if (diseasetrait.efoLink != null) {
        for (var j = 0; j < diseasetrait.efoLink.length; j++) {
            var data = diseasetrait.efoLink[j].split("|");
            var efosearch = "<span><a href='/search?query=".concat(data[0]).concat("'>").concat(data[0]).concat("</a></span>");
            var link = "<a href='".concat(data[2]).concat("' target='_blank'>").concat("<span class='glyphicon glyphicon-link'></span></a></span>");

            if (efo == '') {
                efo = efosearch.concat('&nbsp;&nbsp;').concat(link);
            }
            else {
                efo = efo.concat(", <br>").concat(efosearch.concat('&nbsp;&nbsp;').concat(link));
            }
        }
    }
    else {
        efo = "N/A";
    }
    row.append($("<td>").html(efo));

    var syns = '';
    if (diseasetrait.synonym != null) {
        for (var j = 0; j < diseasetrait.synonym.length; j++) {
            var synonymsearch = "<span><a href='/search?query=".concat(diseasetrait.synonym[j]).concat("'>").concat(diseasetrait.synonym[j]).concat("</a></span>");
            if (syns == '') {
                syns = synonymsearch;
            }
            else if (j > 4) {
                syns = syns.concat(", [...]");
                break;
            }
            else {
                syns = syns.concat(", ").concat(synonymsearch);
            }
        }
    }

    row.append($("<td>").html(syns));

    var studies = '';
    for(var d=0; d<diseasetrait.study_pubmedId.length; d++){
        var studydate = diseasetrait.study_publicationDate[d].substring(0, 4);
        var author = diseasetrait.study_author[d];
        var authorLabel = author.concat(", ").concat(studydate);
        var pubmedid = diseasetrait.study_pubmedId[d];

        var europepmc = "http://www.europepmc.org/abstract/MED/".concat(pubmedid);
        var searchlink = "<span><a href='/search?query=".concat(author).concat("'>").concat(authorLabel).concat("</a></span>");
        var epmclink = "<span><a href='".concat(europepmc).concat("' target='_blank'>").concat("<span class='glyphicon glyphicon-link'></span></a></span>");

        if(studies == ''){
            studies = studies.concat(searchlink).concat('&nbsp;&nbsp;').concat(epmclink);
        }
        else{
            studies = studies.concat(",<br>").concat(searchlink).concat('&nbsp;&nbsp;').concat(epmclink);

        }
    }

    row.append($("<td>").html(studies));

    table.append(row);
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

function generateTraitDropdown(queryTrait) {
    $.getJSON('api/search/traitcount', {'q': queryTrait})
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