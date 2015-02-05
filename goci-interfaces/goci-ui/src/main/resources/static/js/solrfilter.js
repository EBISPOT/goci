/**
 * Created by dwelter on 04/02/15.
 */


var resources = ['study', 'association', 'diseasetrait', 'singlenucleotidepolymorphism'];


function doFiltering(){

    var pvalRange = '';
    var orRange = '';
    var betaRange = '';
    var dateRange = '';

    //if($('#pval-min').val()!= '' || $('#pval-max').val()!= ''){
        if($('#pval-min').val()!= '' && $('#pval-max').val()!= ''){
            pvalRange = "[".concat($('#pval-min').val()).concat("+TO+").concat($('#pval-max').val()).concat("]");
        }
        else if($('#pval-min').val() == '' && $('#pval-max').val()!= ''){
            pvalRange = "[*+TO+".concat($('#pval-max').val()).concat("]");
        }
        else if($('#pval-min').val()!= '' && $('#pval-max').val()== ''){
            pvalRange = "[".concat($('#pval-min').val()).concat("+TO+*]");
        }
    //}
    console.log(pvalRange);

    //if($('#or-min').val()!= '' || $('#or-max').val()!= ''){
        if($('#or-min').val()!= '' && $('#or-max').val()!= ''){
            orRange = "[".concat($('#or-min').val()).concat("+TO+").concat($('#or-max').val()).concat("]");
        }
        else if($('#or-min').val() == '' && $('#or-max').val()!= ''){
            orRange = "[*+TO+".concat($('#or-max').val()).concat("]");
        }
        else if($('#or-min').val()!= '' && $('#or-max').val()== ''){
            orRange = "[".concat($('#or-min').val()).concat("+TO+*]");
        }
    //}
    console.log(orRange);

    //if($('#beta-min').val()!= '' || $('#beta-max').val()!= ''){
        if($('#beta-min').val()!= '' && $('#beta-max').val()!= ''){
            betaRange = "[".concat($('#beta-min').val()).concat("+TO+").concat($('#beta-max').val()).concat("]");
        }
        else if($('#beta-min').val() == '' && $('#beta-max').val()!= ''){
            betaRange = "[*+TO+".concat($('#beta-max').val()).concat("]");
        }
        else if($('#beta-min').val()!= '' && $('#beta-max').val()== ''){
            betaRange = "[".concat($('#beta-min').val()).concat("+TO+*]");
        }
    //}
    console.log(betaRange);

    //if($('#date-min').val()!= '' || $('#date-max').val()!= ''){
        if($('#date-min').val()!= '' && $('#date-max').val()!= ''){
            dateRange = "[".concat($('#date-min').val()).concat("T00:00:00Z+TO+").concat($('#date-max').val()).concat("T00:00:00Z]");
        }
        else if($('#date-min').val() == '' && $('#date-max').val()!= ''){
            dateRange = "[*+TO+".concat($('#date-max').val()).concat("T00:00:00Z]");
        }
        else if($('#date-min').val()!= '' && $('#date-max').val()== ''){
            dateRange = "[".concat($('#date-min').val()).concat("T00:00:00Z+TO+*]");
        }
    //}
    console.log(dateRange);

    solrfilter(pvalRange, orRange, betaRange, dateRange);

}



function solrfilter(pval, or, beta, date){

    console.log("Solr research request received for " + $('#query').text() + " and filters " + pval + ", " + or + ", " + beta + " and " + date);
    var searchTerm = 'text:"'.concat($('#query').text()).concat('"');

    $.getJSON('api/search/filter', {'q': searchTerm,
        'max' : 100000,
        'pvalfilter': pval,
        'orfilter': or,
        'betafilter': beta,
        'datefilter': date
    })
        .done(function(data) {
            console.log(data);
            processData(data);
        });


}
