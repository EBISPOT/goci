/**
 * Created by dwelter on 24/02/15.
 */


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
    var authorsearch = "<span><a href='search?query=".concat(study.author).concat("'>").concat(study.author).concat("</a></span>");
    var epmclink = "<span><a href='".concat(europepmc).concat("' target='_blank'>").concat("<img alt='externalLink' class='link-icon' src='icons/external1.png' th:src='@{icons/external1.png}'/></a></span>");

    var pubdate = study.publicationDate.substring(0, 10);
    row.append($("<td>").html(authorsearch.concat(' (PMID: ').concat(study.pubmedId).concat(') &nbsp;&nbsp;').concat(epmclink)));
    row.append($("<td>").html(pubdate));
    row.append($("<td>").html(study.publication));
    row.append($("<td>").html(study.title));
    var traitsearch = "<span><a href='search?query=".concat(study.traitName).concat("'>").concat(study.traitName).concat("</a></span>");
    row.append($("<td>").html(traitsearch));

    //var associationsearch = "<span><a href='search?query=".concat(study.id.substring(0,6)).concat("'>").concat(study.associationCount).concat("</a></span>");
    row.append($("<td>").html(study.associationCount));

    var id = (study.id).replace(':', '-');
    var plusicon = "<button class='row-toggle btn btn-default btn-xs accordion-toggle' data-toggle='collapse' data-target='.".concat(id).concat(".hidden-study-row' aria-expanded='false' aria-controls='").concat(study.id).concat("'><span class='glyphicon glyphicon-plus tgb'></span></button>");

    row.append($("<td>").html(plusicon));
    table.append(row);


    hiddenrow.addClass(id);
    hiddenrow.addClass('collapse');
    hiddenrow.addClass('accordion-body');
    hiddenrow.addClass('hidden-study-row');

    var innerTable = $("<table>").addClass('sample-info');

    innerTable.append($("<tr>").append($("<th>").attr('style', 'width: 30%').html("Initial sample description")).append($("<td>").html(study.initialSampleDescription)));
    innerTable.append($("<tr>").append($("<th>").attr('style', 'width: 30%').html("Replication sample description")).append($("<td>").html(study.replicateSampleDescription)));

    if($.datepicker.parseDate("yy-mm-dd", pubdate) > $.datepicker.parseDate("yy-mm-dd", "2010-12-31") && study.ancestryLinks != null){
        var ancestries = '';
        var countries = '';
        for(var j = 0; j < study.ancestryLinks.length; j++){
            var link = study.ancestryLinks[j].split("|");
            var cor = link[1];
            var ancestry = link[2];
            var num = link[3];

            if(ancestries == ''){
                ancestries = ancestries.concat(num).concat(' ').concat(ancestry);
            }
            else{
                ancestries = ancestries.concat(', ').concat(num).concat(' ').concat(ancestry);
            }
            if(countries == ''){
                countries = countries.concat(cor);
            }
            else{
                countries = countries.concat(', ').concat(cor);
            }

        }
        innerTable.append($("<tr>").append($("<th>").attr('style', 'width: 30%').html("Ancestry")).append($("<td>").html(ancestries)));
        innerTable.append($("<tr>").append($("<th>").attr('style', 'width: 30%').html("Country of recruitment")).append($("<td>").html(countries)));
    }
    else{
        innerTable.append($("<tr>").attr('style', 'display: none'));
        innerTable.append($("<tr>").attr('style', 'display: none'));
    }

    innerTable.append($("<tr>").append($("<th>").attr('style', 'width: 30%').html("Platform [SNPs passing QC]")).append($("<td>").html(study.platform)));

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
            var rsidsearch = "<span><a href='search?query=".concat(association.rsId[0]).concat("'>").concat(association.strongestAllele[0]).concat("</a></span>");
            var dbsnp = "<span><a href='http://www.ensembl.org/Homo_sapiens/Variation/Summary?v=".concat(association.rsId[0]).concat("'  target='_blank'>").concat("<img alt='externalLink' class='link-icon' src='icons/external1.png' th:src='@{icons/external1.png}'/></a></span>");
            row.append($("<td>").html(rsidsearch.concat('&nbsp;&nbsp;').concat(dbsnp)));
        }
        else {
            var content = '';
            var rsIds = '';
            var alleles = '';
            var type = '';
            var description = '';

            if(association.rsId[0].indexOf(',') != -1) {
                rsIds = association.rsId[0].split(',');
                alleles = association.strongestAllele[0].split(',');
                type = ',';

                if(association.locusDescription != null && association.locusDescription.indexOf('aplotype') != -1){
                    description = association.locusDescription;
                }
            }
            else if(association.rsId[0].indexOf('x') != -1){
                rsIds = association.rsId[0].split('x');
                alleles = association.strongestAllele[0].split('x');
                type = 'x';
            }

            for(var i=0; i<alleles.length; i++){
                for (var j=0; j<rsIds.length; j++){
                    if(alleles[i].trim().indexOf(rsIds[j].trim()) != -1){
                        var rsidsearch = "<span><a href='search?query=".concat(rsIds[j].trim()).concat("'>").concat(alleles[i].trim()).concat("</a></span>");
                        var ensembl = "<span><a href='http://www.ensembl.org/Homo_sapiens/Variation/Summary?v=".concat(rsIds[j].trim()).concat("'  target='_blank'>").concat("<img alt='externalLink' class='link-icon' src='icons/external1.png' th:src='@{icons/external1.png}'/></a></span>");
                        if(content == ''){
                            content = content.concat(rsidsearch.concat('&nbsp;&nbsp;').concat(ensembl));
                        }
                        else{
                            if(type == 'x'){
                                content = content.concat(' x ').concat(rsidsearch.concat('&nbsp;&nbsp;').concat(ensembl));
                            }
                            else{
                                content = content.concat(', <br>').concat(rsidsearch.concat('&nbsp;&nbsp;').concat(ensembl));
                            }
                        }
                    }
                }
            }
            if(description != ''){
                content = content.concat('&nbsp;&nbsp;(').concat(description).concat(')');
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

    var mantissa = association.pValueMantissa;
    var exponent = association.pValueExponent;

    var pval = "".concat(mantissa).concat(" x10").concat("<sup>").concat(exponent).concat("</sup>");


    //var pval = association.pValue;
    //if(pval >= 1e-6){
    //    pval = pval.toExponential(0);
    //}
    //else{
    //    pval = pval.toString();
    //}
    //if(pval != "0") {
    //    var mant = pval.substr(0, 1);
    //    var exp = pval.substr(2);
    //    pval = mant.concat(" x10").concat("<sup>").concat(exp).concat("</sup>");
    //}

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
            var regionsearch = "<span><a href='search?query=".concat(region).concat("'>").concat(association.region[0]).concat("</a></span>");
            row.append($("<td>").html(regionsearch));
        }
        else {
            var regionsearch = "<span><a href='search?query=".concat(association.region).concat("'>").concat(association.region).concat("</a></span>");
            row.append($("<td>").html(regionsearch));
        }
    }
    else{
        row.append($("<td>"));
    }

    var location = "chr";
    if(association.chromosomeName != null){
        location = location.concat(association.chromosomeName[0]);
    }
    else{
        location = location.concat("?");
    }
    if(association.chromosomePosition != null){
        var position = association.chromosomePosition[0];
        location = location.concat(":").concat(position);

        var min = parseInt(position)-500;
        var max = parseInt(position)+500;
        var locationsearch = min.toString().concat("-").concat(max.toString());

        if(association.chromosomeName != null){
            locationsearch = association.chromosomeName[0].concat(':').concat(locationsearch);
        }

        var ensembl = "<span><a href='http://www.ensembl.org/Homo_sapiens/Location/View?r=".concat(locationsearch).concat("'  target='_blank'>").concat("<img alt='externalLink' class='link-icon' src='icons/external1.png' th:src='@{icons/external1.png}'/></a></span>");

        location = location.concat('&nbsp;&nbsp;').concat(ensembl);
    }
    else{
        location = location.concat(":").concat("?");
    }
    row.append($("<td>").html(location));

    //row.append($("<td>"));
    row.append($("<td>").html(association.context));

    var repgene = '';


    if (association.reportedGene != null) {
        if (association.reportedGeneLinks != null) {
            if(association.reportedGeneLinks.length == association.reportedGene.length){

                for (var j = 0; j < association.reportedGeneLinks.length; j++) {
                    var gene = association.reportedGeneLinks[j].split("|")[0];
                    var geneId = association.reportedGeneLinks[j].split("|")[1];

                    var repgenesearch = "<span><a href='search?query=".concat(gene).concat("'>").concat(gene).concat("</a></span>");
                    var ensembl = "<span><a href='http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=".concat(geneId).concat("'  target='_blank'>").concat("<img alt='externalLink' class='link-icon' src='icons/external1.png' th:src='@{icons/external1.png}'/></a></span>");

                    if (repgene == '') {
                        repgene = repgenesearch.concat('&nbsp;&nbsp;').concat(ensembl);
                    }

                    else {
                        repgene = repgene.concat(", ").concat(repgenesearch).concat('&nbsp;&nbsp;').concat(ensembl);
                    }
                }
            }
            else{
                for (var j = 0; j < association.reportedGene.length; j++) {
                    var repgenesearch = "<span><a href='search?query=".concat(association.reportedGene[j]).concat("'>").concat(association.reportedGene[j]).concat("</a></span>");
                    if (repgene == '') {
                        repgene = repgenesearch;
                    }

                    else {
                        repgene = repgene.concat(", ").concat(repgenesearch);
                    }
                }
            }
        }
        else {
            if (association.reportedGene[0] == "NR") {
                repgene = association.reportedGene[0];
            }
            else {
                for (var j = 0; j < association.reportedGene.length; j++) {
                    var repgenesearch = "<span><a href='search?query=".concat(association.reportedGene[j]).concat("'>").concat(association.reportedGene[j]).concat("</a></span>");
                    if (repgene == '') {
                        repgene = repgenesearch;
                    }

                    else {
                        repgene = repgene.concat(", ").concat(repgenesearch);
                    }
                }
            }
        }
    }
    row.append($("<td>").html(repgene));

    var mapgene = '';
    if (association.mappedGeneLinks != null && association.mappedGene != null) {
        mapgene = association.mappedGene[0];
        for (var j = 0; j < association.mappedGeneLinks.length; j++) {
            var gene = association.mappedGeneLinks[j].split("|")[0];
            var geneId = association.mappedGeneLinks[j].split("|")[1];

            var mapgenesearch = "<span><a href='search?query=".concat(gene).concat("'>").concat(gene).concat("</a></span>");
            var ensembl = "<span><a href='http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=".concat(geneId).concat("'  target='_blank'>").concat("<img alt='externalLink' class='link-icon' src='icons/external1.png' th:src='@{icons/external1.png}'/></a></span>");

            mapgene = mapgene.replace(gene, mapgenesearch.concat('&nbsp;&nbsp;').concat(ensembl));

        }
    }
    else if (association.mappedGene != null) {
        for (var j = 0; j < association.mappedGene.length; j++) {
            var mapgenesearch = "<span><a href='search?query=".concat(association.mappedGene[j]).concat("'>").concat(association.mappedGene[j]).concat("</a></span>");
            if (mapgene == '') {
                mapgene = mapgenesearch;
            }

            else {
                mapgene = mapgene.concat("-").concat(mapgenesearch);
            }
        }
    }
    row.append($("<td>").html(mapgene));

    if(association.traitName != null){var traitsearch = "<span><a href='search?query=".concat(association.traitName).concat("'>").concat(association.traitName).concat("</a></span>");
        row.append($("<td>").html(traitsearch));
    }
    else {
        row.append($("<td>"));
    }

    var studydate = association.publicationDate.substring(0, 4);
    var author = association.author[0].concat(' (PMID: ').concat(association.pubmedId).concat("), ").concat(studydate);

    var europepmc = "http://www.europepmc.org/abstract/MED/".concat(association.pubmedId);
    var searchlink = "<span><a href='search?query=".concat(association.author).concat("'>").concat(author).concat("</a></span>");
    var epmclink = "<span><a href='".concat(europepmc).concat("' target='_blank'>").concat("<img alt='externalLink' class='link-icon' src='icons/external1.png' th:src='@{icons/external1.png}'/></a></span>");
    row.append($("<td>").html(searchlink.concat('&nbsp;&nbsp;').concat(epmclink)));


    table.append(row);
}

function processTrait(diseasetrait, table) {
    var row = $("<tr>");
    //if (table.find('tr').length >= 5) { row.addClass('accordion-body');
    //    row.addClass('collapse');
    //    row.addClass('hidden-resource');
    //}
    var traitsearch = "<span><a href='search?query=".concat(diseasetrait.traitName[0]).concat("'>").concat(diseasetrait.traitName[0]).concat("</a></span>");
    row.append($("<td>").html(traitsearch));

    var efo = '';
    if (diseasetrait.efoLink != null) {
        for (var j = 0; j < diseasetrait.efoLink.length; j++) {
            var data = diseasetrait.efoLink[j].split("|");
            var efosearch = "<span><a href='search?query=".concat(data[0]).concat("'>").concat(data[0]).concat("</a></span>");
            var link = "<a href='".concat(data[2]).concat("' target='_blank'>").concat("<img alt='externalLink' class='link-icon' src='icons/external1.png' th:src='@{icons/external1.png}'/></a></span>");

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
            var synonymsearch = "<span><a href='search?query=".concat(diseasetrait.synonym[j]).concat("'>").concat(diseasetrait.synonym[j]).concat("</a></span>");
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

    if(diseasetrait.study_publicationLink != null){
        for(var d=0; d<diseasetrait.study_publicationLink.length; d++){
            var data = diseasetrait.study_publicationLink[d].split("|");
            var author = data[0];
            var authorLabel = author.concat(", ").concat(data[1]);
            var pubmedid = data[2];

            var europepmc = "http://www.europepmc.org/abstract/MED/".concat(pubmedid);
            var searchlink = "<span><a href='search?query=".concat(author).concat("'>").concat(authorLabel).concat("</a></span>");
            var epmclink = "<span><a href='".concat(europepmc).concat("' target='_blank'>").concat("<img alt='externalLink' class='link-icon' src='icons/external1.png' th:src='@{icons/external1.png}'/></a></span>");

            if(studies == ''){
                studies = studies.concat(searchlink).concat('&nbsp;&nbsp;').concat(epmclink);
            }
            else{
                studies = studies.concat(",<br>").concat(searchlink).concat('&nbsp;&nbsp;').concat(epmclink);
            }
        }
    }
    else{
        studies = 'N/A';
    }

    row.append($("<td>").html(studies));

    table.append(row);
}