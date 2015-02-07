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
    $('.toggle').click(function () {
        $(this).find('span').toggleClass('glyphicon-chevron-right glyphicon-chevron-down');
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
        solrSearch(searchTerm);
        if (window.location.hash) {
            applyFacet();
        }
        else {
            // no facets to apply, so make sure we are showing all results tables
            clearFacetting();
        }
        $('#lower_container').show();
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
        else if (facet == "singlenucleotidepolymorphism") {
            last.text("SNPs");
        }
        breadcrumbs.append(last);
    }
}

function solrSearch(queryTerm) {
    console.log("Solr research request received for " + queryTerm);
    var searchTerm = 'text:"'.concat(queryTerm).concat('"');
    setState(SearchState.LOADING);
    $.getJSON('api/search', {'q': searchTerm, 'max': 100000})
        .done(function (data) {
            console.log(data);
            processData(data);
        });
}

function processData(data) {
    var documents = data.response.docs;
    console.log("Solr search returned " + documents.length + " documents");
    updateCountBadges(data.facet_counts.facet_fields.resourcename);
    if (documents.length != 0) {
        var studyTable = $('#study-table-body').empty();
        var associationTable = $('#association-table-body').empty();
        var traitTable = $('#diseasetrait-table-body').empty();
        var snpTable = $('#singlenucleotidepolymorphism-table-body').empty();

        $(".results-container .toggle").hide();

        for (var j = 0; j < documents.length; j++) {
            var doc = documents[j];

            if (doc.resourcename == "study") {
                if (studyTable.find('tr').length == 10) {
                    $('#study-summaries .toggle').show();
                    $('#study-summaries').addClass("more-results");

                }
                processStudy(doc, studyTable);
            }
            else if (doc.resourcename == "association") {
                if (associationTable.find('tr').length == 5) {
                    $('#association-summaries .toggle').show();
                    $('#association-summaries').addClass("more-results");
                }
                processAssociation(doc, associationTable);
            }
            else if (doc.resourcename == "diseaseTrait") {
                if (traitTable.find('tr').length == 5) {
                    $('#diseasetrait-summaries .toggle').show();
                    $('#diseasetrait-summaries').addClass("more-results");                }
                processTrait(doc, traitTable);
            }
            else if (doc.resourcename == "singleNucleotidePolymorphism") {
                if (snpTable.find('tr').length == 5) {
                    $('#singlenucleotidepolymorphism-summaries .toggle').show();
                    $('#singlenucleotidepolymorphism-summaries').addClass("more-results");
                }
                processSnp(doc, snpTable);
            }
        }
        setState(SearchState.RESULTS);
    }
    else {
        setState(SearchState.NO_RESULTS);
    }

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

    if (table.find('tr').length >= 10) {
        row.addClass('accordion-body');
        row.addClass('collapse');
        row.addClass('hidden-resource');
    }
    var europepmc = "http://www.europepmc.org/abstract/MED/".concat(study.pubmedId);
    var link = "<a href='".concat(europepmc).concat("' target='_blank'>").concat(study.author).concat("</a>");

    row.append($("<td>").html(link));
    row.append($("<td>").html(study.publicationDate.substring(0, 10)));
    row.append($("<td>").html(study.publication));
    row.append($("<td>").html(study.title));
    row.append($("<td>").html(study.trait));
    row.append($("<td>").html(study.associationCount));
    var plusicon = "<button class='btn btn-default btn-xs accordion-toggle' data-toggle='collapse' data-target='.".concat(study.id).concat("' aria-expanded='false' aria-controls='").concat(study.id).concat("'><span class='glyphicon glyphicon-plus'></span></button>");

    row.append($("<td>").html(plusicon));
    table.append(row);


    var hiddenrow = $("<tr>");
    hiddenrow.addClass(study.id);
    hiddenrow.addClass('collapse');
    hiddenrow.addClass('accordion-body');
    hiddenrow.addClass('hidden-study-row');

    table.append(hiddenrow);
}

function processAssociation(association, table) {
    var row = $("<tr>");
    if (table.find('tr').length >= 5) {
        row.addClass('accordion-body');
        row.addClass('collapse');
        row.addClass('hidden-resource');
    }

    if (association.rsId != null) {
        if (association.rsId.length == 1 && (association.rsId[0].indexOf('x') == -1)) {
            var dbsnp = "<a href='http://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=".concat(association.rsId[0].substring(2)).concat("'>").concat(association.strongestAllele).concat("</a>");
            row.append($("<td>").html(dbsnp));
        }
        else {
            row.append($("<td>").html(association.strongestAllele));

        }
    }

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
    row.append($("<td>").html(association.chromosomePosition));

    var repgene = '';
    if (association.reportedGene != null) {
        for (var j = 0; j < association.reportedGene.length; j++) {
            if (repgene == '') {
                repgene = association.reportedGene[j];
            }

            else {
                repgene = repgene.concat(", ").concat(association.reportedGene[j]);
            }
        }
    }
    row.append($("<td>").html(repgene));

    var mapgene = '';
    if (association.mappedGene != null) {
        for (var j = 0; j < association.mappedGene.length; j++) {
            if (mapgene == '') {
                mapgene = association.mappedGene[j];
            }

            else {
                mapgene = mapgene.concat(", ").concat(association.mappedGene[j]);
            }
        }
    }
    row.append($("<td>").html(mapgene));
//    TO DO: make the author field into a link to the study page using the pmid
    var europepmc = "http://www.europepmc.org/abstract/MED/".concat(association.pubmedId);
    var study = "<a href='".concat(europepmc).concat("' target='_blank'>").concat(association.author).concat("</a>");
    row.append($("<td>").html(study));


    table.append(row);
}

function processTrait(diseasetrait, table) {
    var row = $("<tr>");
    if (table.find('tr').length >= 5) {
        row.addClass('accordion-body');
        row.addClass('collapse');
        row.addClass('hidden-resource');
    }
    row.append($("<td>").html(diseasetrait.trait));

    var efo = '';
    if (diseasetrait.efoLink != null) {
        for (var j = 0; j < diseasetrait.efoLink.length; j++) {
            var data = diseasetrait.efoLink[j].split("|");
            var link = "<a href='".concat(data[2]).concat("' target='_blank'>").concat(data[0]).concat("</a>");

            if (efo == '') {
                efo = link;
            }
            else {
                efo = efo.concat(", <br>").concat(link);
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
            if (syns == '') {
                syns = diseasetrait.synonym[j];
            }
            else if (j > 4) {
                syns = syns.concat(", [...]");
                break;
            }
            else {
                syns = syns.concat(", ").concat(diseasetrait.synonym[j]);
            }
        }
    }

    row.append($("<td>").html(syns));

    table.append(row);
}

function processSnp(snp, table) {
    var row = $("<tr>");
    if (table.find('tr').length >= 5) {
        row.addClass('accordion-body');
        row.addClass('collapse');
        row.addClass('hidden-resource');
    }
    row.append($("<td>").html(snp.rsId));

    var location = "chr".concat(snp.chromosomeName).concat(":").concat(snp.chromosomePosition);
    row.append($("<td>").html(location));
    row.append($("<td>").html(snp.region));
    row.append($("<td>").html(snp.context));

    var gene = '';
    if (snp.mappedGene != null) {
        for (var j = 0; j < snp.mappedGene.length; j++) {
            if (gene == '') {
                gene = snp.mappedGene[j];
            }

            else {
                gene = gene.concat(", ").concat(snp.mappedGene[j]);
            }
        }
    }
    row.append($("<td>").html(gene));

    var efo = '';
    if (snp.efoLink != null) {
        for (var j = 0; j < snp.efoLink.length; j++) {
            var data = snp.efoLink[j].split("|");
            var link = "<a href='".concat(data[2]).concat("' target='_blank'>").concat(data[0]).concat("</a>");

            if (efo == '') {
                efo = link;
            }
            else {
                efo = efo.concat(", <br>").concat(link);
            }
        }
    }
    else {
        efo = "N/A";
    }
    row.append($("<td>").html(efo));
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

        if (count == 0) {
            $('#' + resource + '-facet').addClass("disabled");
            var summary = $('#' + resource + '-summaries');
            summary.addClass("no-results");
            summary.hide();
        }
    }
}
