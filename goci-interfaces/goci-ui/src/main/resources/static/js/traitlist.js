/**
 * Created by Dani on 27/02/2015.
 */

/**
 * Created by dwelter on 04/02/15.
 */


$(document).ready(function () {

    //if($('#traitList').children() == null){
    //    console.log("About to load all the traits");
    //    loadTraitList();
    //}

    loadTraitList();

});

function loadTraitList() {

    var searchTerm = 'traitName_s:*';
    var facet = 'diseasetrait';
    var sort = 'traitName_s+asc';

    $.getJSON('api/search/alltraits', {
        'q': searchTerm,
        'max': 1000,
        'facet': facet,
        'sort': sort
    })
            .done(function (data) {
                      displayTraits(data);
                      });
}


function displayTraits(data){

    var documents = data.response.docs;

    var traitlist = $('#traitList');



    for (var j = 0; j < documents.length; j++) {
        try{
            var doc = documents[j];
            processTraitDocument(doc, traitlist);
        }
        catch (ex){
            console.log("Failure to process document " + ex);
        }
    }


};

function processTraitDocument(doc, traitList){

    var row = $("<div>");

    var trait = doc.traitName_s;
    var input = "<input type='checkbox' class='trait-check' value='".concat(trait).concat("'/>&nbsp;").concat(trait);

    row.append(input);
    row.attr('id', trait);

    traitList.append(row);
};

