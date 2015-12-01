/**
 * Created by dwelter on 05/08/15.
 */


function doFilter() {
    //console.log("Detected bioportal widget filtering event - " + $("#trait-filter").val());
    filterTraits($("#trait-filter").val());
}


function filterTraits(traitName) {
    hideAllTraits();

    var existing = 0;

    // expand query to get all filtered sets
    $.getJSON('pussycat/filter/' + traitName, function(data) {
        $.each(data, function(index, val) {
            try {
                var trait = val.replace(":", "\\:");
                console.log("Showing trait '" + trait + "' element");
                $("." + trait).attr("mask", "");
                $("circle." + trait).attr("fading", "false");

                existing = existing + $("." + trait).length;
            }
            catch (ex) {
                console.log("Failed to show element '" + val + "'");
            }
        });
        console.log("All filtered traits should now be shown");

        var traitCount = '<p>SNP-trait associations for "' + traitName + '" on the diagram: ' + existing + '</p>';

        $('#filter-counter').empty().append(traitCount);
        $('#filter-counter').show();

    });
}

function showAllTraits() {
    console.log("Showing all 'gwas-trait' elements");
    $(".gwas-trait").attr("mask", "");
    $("#trait-filter").val("");
    $("circle.gwas-trait").attr("fading", "false");
    $('#filter-counter').hide();
}

function hideAllTraits() {
    console.log("Hiding all 'gwas-trait' elements");
    $(".gwas-trait").attr("mask", "url(#traitMask)");
    $("circle.gwas-trait").attr("fading", "true");
}



function showSummary(associations, name) {
    $("#tooltip").hide();

    var assocs = associations.split(",");

    var searchTerm = '';
    for(var i = 0; i < assocs.length; i++){
        var id = assocs[i];
        searchTerm = searchTerm.concat('id:"association:').concat(id).concat('"');
    }

    $.getJSON('api/search/association', {'q': searchTerm, 'max': assocs.length})
        .done(function (data) {
            console.log(data);
            processAssociationSummary(data, name);
        });

}

function processAssociationSummary(data, name){

    var documents = data.response.docs;

    var trait = "SNPs associated with trait '".concat(name).concat("'");


    var summaryTable = $('#summary-table-body').empty();

    var cytoLoc = '';

    try {
        var index = documents.length;
        for (var i = 0; i < index; i++) {
            var row = $("<tr>");
            var summary = documents[i];

            if(cytoLoc == '' && summary.region != null && summary.region.length == 1){
                cytoLoc = summary.region[0];
            }

            //build SNP search and Ensembl link
            var rsidsearch = "<span><a href='search?query=".concat(summary.rsId[0]).concat("'  target='_blank'>").concat(summary.rsId[0]).concat("</a></span>");
            var dbsnp = "<span><a href='http://www.ensembl.org/Homo_sapiens/Variation/Summary?v=".concat(summary.rsId[0]).concat("'  target='_blank'>").concat("<img alt='externalLink' class='link-icon' src='icons/external1.png' th:src='@{icons/external1.png}'/></a></span>");
            row.append($("<td>").html(rsidsearch.concat('&nbsp;&nbsp;').concat(dbsnp)));


            var pval = "".concat(summary.pValueMantissa).concat(" x10").concat("<sup>").concat(summary.pValueExponent).concat("</sup>");
            row.append($("<td class='center'>").html(pval));

            //build EFO term search and link

            var link = null;
            if(summary.efoLink != null){
                var efoterms = summary.efoLink;

                if(efoterms.length > 1){
                    for(var j = 0; j < efoterms.length; j++){
                        console.log(efoterms[j]);
                        if(efoterms[j].indexOf(name) != -1){
                            var efoLink = efoterms[j];
                        }
                    }
                }
                else {
                    var efoLink = efoterms[0];
                }
                link = "<a href='".concat(efoLink.split("|")[2]).concat("' target='_blank'>").concat("<img alt='externalLink' class='link-icon' src='icons/external1.png' th:src='@{icons/external1.png}'/></a></span>");
            }

            var efosearch = "<span><a href='search?query=".concat(name).concat("'  target='_blank'>").concat(name).concat("</a></span>");

            row.append($("<td class='center'>").html(efosearch.concat('&nbsp;&nbsp;').concat(link)));

            //build disease trait search link
            var traitsearch = "<span><a href='search?query=".concat(summary.traitName_s).concat("'  target='_blank'>").concat(summary.traitName_s).concat("</a></span>");
            row.append($("<td class='center'>").html(traitsearch));

            //build study search and EuropePMC links
            var study = summary.author_s.concat(" et al., ").concat(summary.publicationDate.substring(0, 4));
            var europepmc = "http://www.europepmc.org/abstract/MED/".concat(summary.pubmedId);
            var authorsearch = "<span><a href='search?query=".concat(summary.author_s).concat("'  target='_blank'>").concat(study).concat("</a></span>");
            var epmclink = "<span><a href='".concat(europepmc).concat("' target='_blank'>").concat("<img alt='externalLink' class='link-icon' src='icons/external1.png' th:src='@{icons/external1.png}'/></a></span>");

            var studyentry = authorsearch.concat('&nbsp;&nbsp;').concat(epmclink);

            row.append($("<td>").html(studyentry));

            //var gwaslink = "<a href='search?query=".concat(summary.rsId[0]).concat("' target='_blank'>More information</a>");
            //row.append($("<td>").html(gwaslink));

            summaryTable.append(row);
        }

        if(cytoLoc != ''){
            trait = trait.concat(" in region ").concat(cytoLoc);
        }
        $('#traitPopupTitle').html(trait);

        $('#traitPopup').modal('show');
    }
    catch (ex) {
        alert(ex);
    }
}

