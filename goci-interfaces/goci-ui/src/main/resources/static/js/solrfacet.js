/**
 * Created by dwelter on 28/01/15.
 */


var solrBaseURL = 'http://orange.ebi.ac.uk:8983/solr/gwas/select'

var resources = ['study', 'association', 'diseasetrait', 'singlenucleotidepolymorphism'];

function solrFacet(queryTerm, facet){

    console.log("Solr research request received for " + queryTerm + " and facet " + facet);
    var searchTerm = 'text:"'.concat(queryTerm).concat('"');
    var searchFacet = 'resourcename:'.concat(facet);

    var group = 'false';
    var field = '';

    if(facet == "study"){
        group = 'true';
        field = 'pubmedId';
    }
    else if(facet == "association" || facet == "singlenucleotidepolymorphism"){
        group = 'true';
        field = 'rsId';
    }

    $.ajax({
        url: solrBaseURL,
        data: {'q': searchTerm,
            'indent': 'true',
            'rows' : 1000000,
            'fq' : searchFacet,
            'facet':'true',
            'facet.field':'resourcename',
            'group' : group,
            'group.field' : field,
            'group.limit' : '-1',
            'wt':'json'},
        dataType: 'jsonp',
        jsonp: 'json.wrf',
        //success: function(response){
        //    console.log(response);
        //},
        success: processFacet,
        error: fail
    });

}

function processFacet(data){
    console.log("Received data and ready to process");
    //setCountBadges(data.facet_counts.facet_fields.resourcename);

    var resource = data.responseHeader.params.fq.substring(13);
    console.log("Facet is " + resource);

    for(var f=0; f < resources.length; f++){
        if(resources[f] != resource){
            $('#' +resources[f]+ 'Table').hide();
        }
    }

    var table = $("<tbody>");

    if(data.responseHeader.params.group == 'true'){
        if(resource == "study"){
            processStudies(data.grouped.pubmedId.groups, table);
        }
        else if(resource == "association"){
            processAssociations(data.grouped.rsId.groups, table)
        }
        else{
            processSnps(data.grouped.rsId.groups, table)

        }
    }
    else{
        processTraits(data.resource.docs, table);
    }


    $('#' +resource+ 'Summaries').append(table);

}


function processStudies(studies, table){
    for(var i=0; i<studies.length; i++) {
        var documents = studies[i].doclist.docs;

        for (var j = 0; j < documents.length; j++) {
            var study = documents[j];

            var row = $("<tr>");
            row.append($("<td>").html(study.author));
            row.append($("<td>").html(study.publication));
            row.append($("<td>").html(study.title));
            row.append($("<td>").html(study.trait));
            row.append($("<td>").html(study.publicationDate.substring(0, 10)));
            row.append($("<td>").html(study.pubmedId));
            table.append(row);
        }
    }
}

function processAssociations(associations, table) {
    for (var i = 0; i < associations.length; i++) {
        var documents = associations[i].doclist.docs;

        for (var j = 0; j < documents.length; j++) {
            var association = documents[j];

            var row = $("<tr>");
            row.append($("<td>").html(association.strongestAllele));
            row.append($("<td>").html(association.pValue));

            if (association.orType == '1') {
                row.append($("<td>").html(association.orPerCopyNum));
                row.append($("<td>").html(''));
            }
            else {
                row.append($("<td>").html(''));
                if (association.orPerCopyUnitDescr != null) {
                    var beta = (association.orPerCopyNum).concat(' ').concat(association.orPerCopyUnitDescr);
                    row.append($("<td>").html(beta));
                }
                else {
                    row.append($("<td>").html(association.orPerCopyNum));
                }
            }
            row.append($("<td>").html(association.chromosomePosition));

            var gene = '';
            if (association.gene != null) {
                for (var j = 0; j < association.gene.length; j++) {
                    if (gene == '') {
                        gene = association.gene[j];
                    }

                    else {
                        gene = gene.concat(", ").concat(association.gene[j]);
                    }
                }
            }
            row.append($("<td>").html(gene));
            //    TO DO: make the author field into a link to the study page using the pmid
            var study = association.author.concat(" et al.");
            row.append($("<td>").html(study));
            table.append(row);
        }
    }
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
        var documents = snps[i].doclist.docs;

        for (var j = 0; j < documents.length; j++) {
            var snp = documents[j];
            var row = $("<tr>");
            row.append($("<td>").html(snp.rsId));
            row.append($("<td>").html(snp.chromosomePosition));
            row.append($("<td>").html(snp.region));

            var gene = '';
            if (snp.gene != null) {
                for (var j = 0; j < snp.gene.length; j++) {
                    if (gene == '') {
                        gene = snp.gene[j];
                    }

                    else {
                        gene = gene.concat(", ").concat(snp.gene[j]);
                    }
                }
            }
            row.append($("<td>").html(gene));
            table.append(row);
        }
    }
}