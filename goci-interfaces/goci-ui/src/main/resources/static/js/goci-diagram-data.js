/**
 * Created by dwelter on 05/08/15.
 */


function doFilter() {
    console.log("Detected bioportal widget filtering event - " + $("#trait-filter").val());
    filterTraits($("#trait").val());
}


function filterTraits(traitName) {
    hideAllTraits();

    // expand query to get all filtered sets
    $.getJSON('http://localhost:8080/pussycat/filter/' + traitName, function(data) {
        $.each(data, function(index, val) {
            try {
                var trait = val.replace(":", "\\:");
                console.log("Showing trait '" + trait + "' element");
                $("." + trait).attr("mask", "");
                $("circle." + trait).attr("fading", "false");
            }
            catch (ex) {
                console.log("Failed to show element '" + val + "'");
            }
        });
        console.log("All filtered traits should now be shown");
    });
}

function showAllTraits() {
    console.log("Showing all 'gwas-trait' elements");
    $(".gwas-trait").attr("mask", "");
    $("#trait-filter").val("");
    $("circle.gwas-trait").attr("fading", "false");
}

function hideAllTraits() {
    console.log("Hiding all 'gwas-trait' elements");
    $(".gwas-trait").attr("mask", "url(#traitMask)");
    $("circle.gwas-trait").attr("fading", "true");
}



function showSummary(associations, name) {
    $("#tooltip").hide();

    var assocs = associations.split(",");
    console.log(assocs);

    var searchTerm = '';
    for(var i = 0; i < assocs.length; i++){
        var id = assocs[i].split("_")[1];
        console.log(assocs[i].split("_")[1]);
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


    try {
        var index = documents.length;
        for (var i = 0; i < index; i++) {
            var row = $("<tr>");
            var summary = documents[i];
            var snp = "http://www.ensembl.org/Homo_sapiens/Variation/Summary?v=".concat(summary.rsId[0]);
            var snpurl = "<a href='".concat(snp).concat("' target='_blank'>").concat(summary.rsId[0]).concat("</a>");
            row.append($("<td>").html(snpurl));
            var pval = "".concat(summary.pValueMantissa).concat(" x10").concat("<sup>").concat(summary.pValueExponent).concat("</sup>");
            row.append($("<td class='center'>").html(pval));
            var efoterms = summary.efoLink;

            if(efoterms.length > 1){
                for(var j = 0; j < efoterms.length; j++){
                    console.log(efoterms[j]);
                    if(efoterms[j].indexOf(name) != -1){
                        var link = efoterms[j];
                    }
                }
            }
            else {
                var link = efoterms[0];
            }
            console.log(link);

            var efourl = "<a href='".concat(link.split("|")[2]).concat("' target='_blank'>").concat(name).concat("</a>");
            row.append($("<td class='center'>").html(efourl));
            row.append($("<td class='center'>").html(summary.traitName_s));
            var study = summary.author_s.concat(" et al., ").concat(summary.publicationDate.substring(0, 4));
            var studyurl = "http://www.ukpmc.ac.uk/abstract/MED/".concat(summary.pubmedId);
            var studyentry = "<a href='".concat(studyurl).concat("' target='_blank'>").concat(study).concat("</a>");
            row.append($("<td>").html(studyentry));
            //var gwasurl = "http://www.genome.gov/gwastudies/index.cfm?snp=".concat(summary.snp).concat("#result_table");
            //var gwaslink = "<a href='".concat(gwasurl).concat("' target='_blank'>More information</a>");
            var gwaslink = "Link to association page goes here";
            row.append($("<td>").html(gwaslink));
            summaryTable.append(row);
        }

        $('#traitPopupTitle').html(trait);

        $('#traitPopup').modal('show');
    }
    catch (ex) {
        alert(ex);
    }
}

