/**
 * Created by Dani on 27/02/2015.
 */

/**
 * Created by dwelter on 04/02/15.
 */


$(document).ready(function () {

    if($('#traitList ul').children().length == 0){
        console.log("About to load all the traits");
        loadTraitList();
    }


    //$('#traitList-submit').click(function(){
    //    searchCheckedTraits();
    //});

    $('#traitForm').submit(function (event) {
        event.preventDefault();
        console.log("Trait submission request received");
        getCheckedTraits();
    });
});

function loadTraitList() {

    $('#loadingTraits').show();

    var searchTerm = 'traitName_s:*';
    var facet = 'traitName_s';
    var sort = 'index';

    $.getJSON('../api/search/alltraits', {
        'q': searchTerm,
        'max': 1,
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

    var traitlist = $('#traitList ul');


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

    $('#loadingTraits').hide();
    $('#allTraitsDisplay').show();

};

function processTraitDocument(trait, traitList){

    var row = $("<li>");

    //var trait = doc.traitName_s;
    var input = "<input type='checkbox' class='trait-check' value='".concat(trait).concat("'/>&nbsp;").concat(trait);

    row.html(input);
    row.attr('id', trait);

    traitList.append(row);
};

//function searchCheckedTraits() {
    //var traits = getCheckedTraits();

    //var searchTerm = 'traitName:"';
    //for (var i = 0; i < traits.length; i++) {
    //    var trait = traits[i];
    //    trait = trait.replace(/\s/g, '+');
    //
    //    if (i == 0) {
    //        searchTerm = searchTerm.concat(trait).concat('"');
    //    }
    //    else {
    //        searchTerm = searchTerm.concat('+OR+"').concat(trait).concat('"');
    //    }
    //}

    //$("#search-box").val(searchTerm.substring(13));

//}

function getCheckedTraits(){
    var traits = '';
    var traitInput = $('#traitList ul li input:checked');
    for(var i=0; i<traitInput.length; i++){

        var trait = traitInput[i].value;
        trait = trait.replace(/\s/g, '+');

        if(i ==0){
            traits = trait;
        }
        else{
            traits = traits.concat('|').concat(trait);
        }

    }
    console.log(traits);

    //$('#traitOnly').text(traits);

    localStorage.setItem("traits", traits);

    window.location = "../search?query=*";
};

