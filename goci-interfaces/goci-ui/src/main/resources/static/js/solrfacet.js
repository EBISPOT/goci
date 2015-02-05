/**
 * Created by dwelter on 28/01/15.
 */


var solrBaseURL = 'http://orange.ebi.ac.uk:8983/solr/gwas/select'

var resources = ['study', 'association', 'diseasetrait', 'singlenucleotidepolymorphism'];

function solrFacet(queryTerm, facet){

    console.log("Solr research request received for " + queryTerm + " and facet " + facet);
    var searchTerm = 'text:"'.concat(queryTerm).concat('"');
    //var searchFacet = 'resourcename:'.concat(facet);

    //var group = 'false';
    //var field = '';
    var url = 'api/search/' + facet;

    //if(facet == "study"){
    //    //group = 'true';
    //    //field = 'pubmedId';
    //}
    //else if(facet == "association" || facet == "singlenucleotidepolymorphism"){
    //    group = 'true';
    //    field = 'rsId';
    //}

    //$.ajax({
    //    url: solrBaseURL,
    //    data: {'q': searchTerm,
    //        'indent': 'true',
    //        'rows' : 1000000,
    //        'fq' : searchFacet,
    //        'facet':'true',
    //        'facet.field':'resourcename',
    //        'group' : group,
    //        'group.field' : field,
    //        'group.limit' : '-1',
    //        'wt':'json'},
    //    dataType: 'jsonp',
    //    jsonp: 'json.wrf',
    //    //success: function(response){
    //    //    console.log(response);
    //    //},
    //    success: processFacet,
    //    error: fail
    //});

    $.getJSON(url, {'q': searchTerm,
        'max' : 100000
        //'facet':'true',
        //'facet.field':'resourcename',
        //'group' : 'true',
        //'group.by' : 'pubmedId'
    })
        .done(function(data) {
            console.log(data);
            processFacet(data);
        });

}

function processFacet(data){
    console.log("Received data and ready to process");
    //setCountBadges(data.facet_counts.facet_fields.resourcename);

    var resource = data.responseHeader.params.fq.substring(13).toLowerCase();
    console.log("Facet is " + resource);

    for(var f=0; f < resources.length; f++){
        if(resources[f] != resource){
            $('#' + resources[f] + 'Summaries tbody').remove();
            $('#' + resources[f] + 'Table').hide();
        }
        else{
            $('#' + resource + 'Summaries tbody').remove();
            $('#' + resource + 'Table').show();
            $('#' + resource + 'Toggle').hide();
        }

    }

    var table = $("<tbody>");

    //if(data.responseHeader.params.group == 'true'){
        //if(resource == "study"){
        //    processStudies(data.grouped.pubmedId.groups, table);
        //}
        //else if(resource == "association"){
        //    processAssociations(data.grouped.rsId.groups, table)
        //}
        //else{
        //    processSnps(data.grouped.rsId.groups, table)
        //
        //}
    //}
    //else {
        if(resource == "study") {
            processStudies(data.response.docs, table);
        }
        else if (resource == "association") {
            processAssociations(data.response.docs, table)
        }
        else if (resource == "singlenucleotidepolymorphism") {
            processSnps(data.response.docs, table)

        }
        else{
            processTraits(data.response.docs, table);
         }
    //}


    $('#' +resource+ 'Summaries').append(table);

}


function processStudies(studies, table){
    for(var i=0; i<studies.length; i++) {
        //var documents = studies[i].doclist.docs;
        //
        //for (var j = 0; j < documents.length; j++) {
        //    var study = documents[j];
        var study = studies[i];

        var row = $("<tr>");
        //row.addClass('clickable');
        //row.attr('data-toggle', 'collapse');
        //row.attr('id', study.id);
        //row.attr('data-target', '.'.concat(study.id));

        var ukpmc = "http://www.ukpmc.ac.uk/abstract/MED/".concat(study.pubmedId);
        var link = "<a href='".concat(ukpmc).concat("' target='_blank'>").concat(study.author).concat("</a>");

        row.append($("<td>").html(link));
        row.append($("<td>").html(study.publicationDate.substring(0,10)));
        row.append($("<td>").html(study.publication));
        row.append($("<td>").html(study.title));
        row.append($("<td>").html(study.trait));
        row.append($("<td>").html(study.associationCount));
        var plusicon = "<button class='btn btn-default btn-xs accordion-toggle' id='".concat(study.id).concat("' data-toggle='collapse' data-target='.").concat(study.id).concat("' aria-expanded='false' aria-controls='").concat(study.id).concat("'><span class='glyphicon glyphicon-plus'></span></button>");

        row.append($("<td>").html(plusicon));
        table.append(row);


        var hiddenrow = $("<tr>");
        hiddenrow.addClass(study.id);
        hiddenrow.addClass('collapse');
        hiddenrow.append($("<td>"));
        hiddenrow.append($("<td colspan='3'>").html(study.initialSampleDescription));
        hiddenrow.append($("<td>").html(study.replicateSampleDescription));
        hiddenrow.append($("<td>").html(study.platform));

        if(study.cnv){
            hiddenrow.append($("<td>").html("yes"));
        }
        else{
            hiddenrow.append($("<td>").html("no"));
        }
        table.append(hiddenrow);

        //}
    }
}

function processAssociations(associations, table) {
    for (var i = 0; i < associations.length; i++) {
        //var documents = associations[i].doclist.docs;
        //
        //for (var j = 0; j < documents.length; j++) {
        //    var association = documents[j];
        var association = associations[i];

        var row = $("<tr>");
        if(association.rsId != null){
            if(association.rsId.length == 1 && !association.rsId[0].contains('x')){
                var dbsnp = "<a href='http://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=".concat(association.rsId[0].substring(2)).concat("'>").concat(association.strongestAllele).concat("</a>");
                row.append($("<td>").html(dbsnp));
            }
            else {
                //for (var k = 0; k < association.rsId.length; k++) {
                //
                //}
                row.append($("<td>").html(association.strongestAllele));

            }
        }
        var pval = association.pValue;
        if(association.qualifier != null && association.qualifier != ''){
            pval = pval.toString().concat(" ").concat(association.qualifier[0]);
            console.log(pval);
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
                else{
                    row.append($("<td>").html(association.orPerCopyNum));
                }
            }
        //if(association.orPerCopyRange != null){
            row.append($("<td>").html(association.orPerCopyRange));
        //}
        //else{
        //    row.append($("<td>").html(''));
        //}
        row.append($("<td>").html(association.chromosomePosition));

        var repgene = '';
        if(association.reportedGene != null){
            for(var j=0; j < association.reportedGene.length; j++){
                if(repgene == ''){
                    repgene = association.reportedGene[j];
                }

                else{
                    repgene = repgene.concat(", ").concat(association.reportedGene[j]);
                }
            }
        }
        row.append($("<td>").html(repgene));

        var mapgene = '';
        if(association.mappedGene != null){
            for(var j=0; j < association.mappedGene.length; j++){
                if(mapgene == ''){
                    mapgene = association.mappedGene[j];
                }

                else{
                    mapgene = mapgene.concat(", ").concat(association.mappedGene[j]);
                }
            }
        }
        row.append($("<td>").html(mapgene));
            //    TO DO: make the author field into a link to the study page using the pmid

        var ukpmc = "http://www.ukpmc.ac.uk/abstract/MED/".concat(association.pubmedId);
        var study = "<a href='".concat(ukpmc).concat("' target='_blank'>").concat(association.author).concat("</a>");
        row.append($("<td>").html(study));
            table.append(row);
        }
    //}
}

function processTraits(diseasetraits, table){
    for(var i=0; i<diseasetraits.length; i++) {
        var diseasetrait = diseasetraits[i];


        var row = $("<tr>");
        row.append($("<td>").html(diseasetrait.trait));

        var efo = '';
        if(diseasetrait.efoLink != null){
            for(var j=0; j < diseasetrait.efoLink.length; j++){
                var data = diseasetrait.efoLink[j].split("|");
                var link = "<a href='".concat(data[2]).concat("' target='_blank'>").concat(data[0]).concat("</a>");

                if(efo == ''){
                    efo = link;
                }
                //else if(j > 4){
                //    efo = efo.concat(", [...]");
                //    break;
                //}
                else{
                    efo = efo.concat(", <br>").concat(link);
                }
            }
        }
        else{
            efo = "N/A";
        }
        row.append($("<td>").html(efo));



        var syns = '';
        if(diseasetrait.synonym != null){
            for(var j=0; j < diseasetrait.synonym.length; j++){
                if(syns == ''){
                    syns = diseasetrait.synonym[j];
                }
                else if(j > 4){
                    syns = syns.concat(", [...]");
                    break;
                }
                else{
                    syns = syns.concat(", ").concat(diseasetrait.synonym[j]);
                }
            }
        }

        row.append($("<td>").html(syns));

        //subtract one from child count as each term is a child of itself in the Solr index
        //row.append($("<td>").html(diseasetrait.child.length-1));
        table.append(row);
    }
}

function processSnps(snps, table){
    for (var i = 0; i < snps.length; i++) {
        //var documents = snps[i].doclist.docs;
        //
        //for (var j = 0; j < documents.length; j++) {
        //    var snp = documents[j];

        var snp = snps[i];
        var row = $("<tr>");
        row.append($("<td>").html(snp.rsId));
        var location = "chr".concat(snp.chromosomeName).concat(":").concat(snp.chromosomePosition);
        row.append($("<td>").html(location));
        row.append($("<td>").html(snp.region));
        row.append($("<td>").html(snp.context));

        var gene = '';
        if(snp.mappedGene != null){
            for(var j=0; j < snp.mappedGene.length; j++){
                if(gene == ''){
                    gene = snp.mappedGene[j];
                }

                else{
                    gene = gene.concat(", ").concat(snp.mappedGene[j]);
                }
            }
        }
        row.append($("<td>").html(gene));

        var efo = '';
        if(snp.efoLink != null){
            for(var j=0; j < snp.efoLink.length; j++){
                var data = snp.efoLink[j].split("|");
                var link = "<a href='".concat(data[2]).concat("' target='_blank'>").concat(data[0]).concat("</a>");

                if(efo == ''){
                    efo = link;
                }
                else{
                    efo = efo.concat(", <br>").concat(link);
                }
            }
        }
        else{
            efo = "N/A";
        }
        row.append($("<td>").html(efo));
            table.append(row);
        }
    //}
}