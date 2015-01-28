/**
 * Created by dwelter on 20/01/15.
 */

var solrBaseURL = 'http://orange.ebi.ac.uk:8983/solr/gwas/select'

function solrSearch(queryTerm){

    console.log("Solr research request received for " + queryTerm);
    var searchTerm = 'text:"'.concat(queryTerm).concat('"');

    $.ajax({
        url: solrBaseURL,
        data: {'q': searchTerm,
                'indent': 'true',
                'rows' : 1000000,
                'facet':'true',
                'facet.field':'resourcename',
                'group' : 'true',
                'group.field' : 'pubmedId',
                'group.limit' : '-1',
                'wt':'json'},
        dataType: 'jsonp',
        jsonp: 'json.wrf',
        //success: function(response){
        //    console.log(response);
        //},
        success: processData,
        error: fail
    });

}

function processData(data){
   console.log("Solr search return data");

    setCountBadges(data.facet_counts.facet_fields.resourcename);

    //var documents = data.response.docs;
    var groups = data.grouped.pubmedId.groups;
    var queryTerm = data.responseHeader.params.q.substring(5);
    //$('#search-term').text(queryTerm);



    console.log("Due to process " + groups.length + " groups");

    if(groups.length != 0){var studyTable = $("<tbody>");
        var associationTable = $("<tbody>");
        var traitTable = $("<tbody>");
        var snpTable = $("<tbody>");

        for(var i=0; i<groups.length; i++){
            var documents = groups[i].doclist.docs;

            for(var j=0; j< documents.length; j++) {
               var doc = documents[j];

                if (doc.resourcename == "study") {
                    processStudy(doc, studyTable);
                }
                else if (doc.resourcename == "association") {
                    processAssociation(doc, associationTable);
                }
                else if (doc.resourcename == "efoTrait") {
                    processTrait(doc, traitTable);
                }
                else if (doc.resourcename == "singleNucleotidePolymorphism") {
                    processSnp(doc, snpTable);
                }
            }
        }
        $('#studySummaries').append(studyTable);
        $('#associationSummaries').append(associationTable);
        $('#efotraitSummaries').append(traitTable);
        $('#snpSummaries').append(snpTable);
    }
    else{
        $('#noResults').show();
        //$('#search-term-noResult').text(queryTerm);
        $('#results').hide();
    }

    console.log("Data display complete");
}

function fail(){
    console.log("Oh dear, something went wrong there");
}



function setCountBadges(countArray){
    for(var i=0; i<countArray.length; i=i+2){
        var resource = countArray[i];
        var count = countArray[i+1];

        if(count > 0){
            var facet = $('#' +resource + 'Facet span');
            facet.empty();
            facet.append(count);
        }
        else{
            $('#' +resource+ 'Facet').addClass("disabled");
            $('#' +resource+ 'Table').hide();
        }
    }
}


function processStudy(study, table){
    var row = $("<tr>");
    row.append($("<td>").html(study.author));
    row.append($("<td>").html(study.publication));
    row.append($("<td>").html(study.title));
    row.append($("<td>").html(study.trait));
    row.append($("<td>").html(study.publicationDate.substring(0,10)));
    row.append($("<td>").html(study.pubmedId));
    table.append(row);
}


function processAssociation(association, table){
    var row = $("<tr>");
    row.append($("<td>").html(association.rsId));
    row.append($("<td>").html(association.chromosomePosition));
    row.append($("<td>").html(association.strongestAllele));
    row.append($("<td>").html(association.pValue));

    if(association.orType == '1'){
        row.append($("<td>").html(association.orPerCopyNum));
        row.append($("<td>").html(''));
    }
    else {
        row.append($("<td>").html(''));
        if (association.orPerCopyUnitDescr != null) {
            var beta = (association.orPerCopyNum).concat(' ').concat(association.orPerCopyUnitDescr);
            console.log(beta);
            row.append($("<td>").html(beta));
        }
        else{
            row.append($("<td>").html(association.orPerCopyNum));
        }
    }

    table.append(row);
}

function processTrait(efotrait, table){

    var row = $("<tr>");
    row.append($("<td>").html(efotrait.trait));
    row.append($("<td>").html(efotrait.shortForm));

    var syns = '';
    if(efotrait.synonym != null){
        for(var j=0; j < efotrait.synonym.length; j++){
            if(syns == ''){
                syns = efotrait.synonym[j];
            }
            else if(j > 4){
                syns = syns.concat(", [...]");
                break;
            }
            else{
                syns = syns.concat(", ").concat(efotrait.synonym[j]);
            }
        }
    }

    row.append($("<td>").html(syns));

    //subtract one from child count as each term is a child of itself in the Solr index
    row.append($("<td>").html(efotrait.child.length-1));
    table.append(row);
}

function processSnp(snp, table){
    var row = $("<tr>");
    row.append($("<td>").html(snp.rsId));
    row.append($("<td>").html(snp.chromosomePosition));
    row.append($("<td>").html(snp.region));

    var gene = '';
    if(snp.gene != null){
        for(var j=0; j < snp.gene.length; j++){
            if(gene == ''){
                gene = snp.gene[j];
            }

            else{
                gene = gene.concat(", ").concat(snp.gene[j]);
            }
        }
    }
    row.append($("<td>").html(gene));
    table.append(row);
}