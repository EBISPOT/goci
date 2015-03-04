/**
 * Created by Dani on 27/02/2015.
 */

/**
 * Created by dwelter on 04/02/15.
 */


$(document).ready(function () {

    if($('#traitList').children().length == 0){
        console.log("About to load all the traits");
        loadTraitList();
    }

  //  loadTraitList();

});

function loadTraitList() {

    var searchTerm = 'traitName_s:*';
    var facet = 'traitName_s';
    var sort = 'index';

    $.getJSON('api/search/alltraits', {
        'q': searchTerm,
        'max': 2000,
        'facet': facet,
        'facet.sort': sort,
        'facet.limit': 2000
    })
            .done(function (data) {
                      displayTraits(data);
                      });
}


function displayTraits(data){

    //var documents = data.response.docs;

    var traitlist = $('#traitList');


    var traits = data.facet_counts.facet_fields.traitName_s;

    for (var i = 0; i < traits.length; i = i + 2) {
        try{
            var trait = traits[i];
            //var count = traits[i + 1];
            processTraitDocument(trait, traitlist);
        }
        catch (ex){
            console.log("Failure to process document " + ex);
        }
    }

    //for (var j = 0; j < documents.length; j++) {
    //    try{
    //        var doc = documents[j];
    //        processTraitDocument(doc, traitlist);
    //    }
    //    catch (ex){
    //        console.log("Failure to process document " + ex);
    //    }
    //}


};

function processTraitDocument(trait, traitList){

    var row = $("<div>");

    //var trait = doc.traitName_s;
    var input = "<input type='checkbox' class='trait-check' value='".concat(trait).concat("'/>&nbsp;").concat(trait);

    row.append(input);
    row.attr('id', trait);

    traitList.append(row);
};

