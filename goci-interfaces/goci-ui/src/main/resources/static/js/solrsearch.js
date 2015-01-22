/**
 * Created by dwelter on 20/01/15.
 */

var solrBaseURL = 'http://ramesesii.windows.ebi.ac.uk:8983/solr/gwas/select'

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
                //'group' : 'true',
                //'group.field' : 'resourcename',
                //'group.limit' : '-1',
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

    //var documents = data.grouped.resourcename.groups;
    //
    //for(var i=0; i<documents.length; i++){
    //    var group = documents[i];
    //
    //    if(group.groupValue == "study"){
    //        processStudies(group.doclist.docs);
    //    }
    //    else if (group.groupValue == "association"){
    //        processAssociations(group.doclist.docs);
    //    }
    //    if(group.groupValue == "efotrait"){
    //        processTraits(group.doclist.docs);
    //    }
    //}

    var documents = data.response.docs;
    var studyTable = $("<tbody>");
    var associationTable = $("<tbody>");
    var traitTable = $("<tbody>");


    for(var i=0; i<documents.length; i++){
        console.log("Due to process " + documents.length + " documents");
        var doc = documents[i];

        if(doc.resourcename == "study"){
            processStudy(doc, studyTable);
        }
        else if (doc.resourcename == "association"){
            processAssociation(doc, associationTable);
        }
        else if(doc.resourcename == "efoTrait"){
            processTrait(doc, traitTable);
        }
    }
    $('#studySummaries').append(studyTable);
    $('#associationSummaries').append(associationTable);
    $('#efotraitSummaries').append(traitTable);

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
    }
}

function processStudies(studies){
    var table = $("<tbody>");
    for(var i=0; i<studies.length; i++){
        var study = studies[i];

         proccessStudy(study,table);
    }
    $('#studySummaries').append(table);
};


function processStudy(study, table){
    var row = $("<tr>");
    row.append($("<td>").html(study.author));
    row.append($("<td>").html(study.publication));
    row.append($("<td>").html(study.title));
    row.append($("<td>").html(study.trait));
    row.append($("<td>").html(''));
    row.append($("<td>").html(study.pubmedId));
    table.append(row);
}


function processAssociations(associations){
    var table = $("<tbody>");
    for(var i=0; i<associations.length; i++){
        var association = associations[i];
         proccessAssociation(association,table);

    }
    $('#associationSummaries').append(table);

}

function processAssociation(association, table){
    var row = $("<tr>");
    row.append($("<td>").html(''));
    row.append($("<td>").html(''));
    row.append($("<td>").html(association.strongestAllele));
    row.append($("<td>").html(association.pValue));
    row.append($("<td>").html(''));
    row.append($("<td>").html(''));
    table.append(row);
}

function processTraits(efoTraits){
    var table = $("<tbody>");
    for(var i=0; i<efoTraits.length; i++){
        var efotrait = efoTraits[i];
        processStudies(efotrait,table);
    }
    $('#efotraitSummaries').append(table);
}

function processTrait(efotrait, table){

    var row = $("<tr>");
    row.append($("<td>").html(efotrait.trait));
    row.append($("<td>").html(efotrait.shortForm));

    var syns = '';
    if(efotrait.synonym.length > 0){
        for(var j=0; j < efotrait.synonym.length ; j++){
            syns.concat(efotrait.synonym[j]);
        }
    }

    row.append($("<td>").html(syns));
    row.append($("<td>").html(efotrait.child.length));
    table.append(row);
}
