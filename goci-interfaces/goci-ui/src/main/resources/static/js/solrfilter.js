/**
 * Created by dwelter on 04/02/15.
 */


$(document).ready(function() {

    //used to display the error msg of form validation
    $('#filter-form :input').tooltipster({ //find more options on the tooltipster page
                                             trigger: 'custom', // default is 'hover' which is no good here
                                             position: 'top',
                                             animation: 'grow'
                                         });

    $.validator.addMethod("greaterThan", function(value, element, param) {
        return this.optional(element) || parseInt(value) >= parseInt($(param).val());
    }, "invalid value range");


    $.validator.addMethod("laterThan", function(value, element, param) {
        return this.optional(element) || new Date(value) >= new Date($(param).val());
    }, "invalid date range");

    $.validator.addMethod("validChrom", function(value, element) {
        var valid_chrom = ['1','2','3','4','5','6','7','8','9','10','11','12','13','14','15','16','17','18','19','20','21','22', '23', 'X', 'x','Y', 'y'];
        return this.optional(element) || valid_chrom.indexOf($('#chrom').val()) != -1
    }, "invalid chromosome location");


    $('#filter-form').submit(function(event) {
        event.preventDefault();
    }).validate({
                    rules: {
                        chrom: {
                            validChrom: true,
                        },
                        'bp-min': {
                            digits: true,
                        },
                        'bp-max': {
                            digits: true,
                            greaterThan: '#bp-min'
                        },
                        'date-max':{
                            laterThan : '#date-min',
                        },
                        'or-min' : {
                            digits: true,
                        },
                        'or-max' : {
                            digits: true,
                            greaterThan: '#or-min'
                        },
                        'beta-min' : {
                            digits: true,
                        },
                        'beta-max' : {
                            digits: true,
                            greaterThan: '#beta-min'
                        }
                    },
                    messages: {
                        'bp-max': {
                            greaterThan: 'end-bp must be greater than start-bp',
                        },
                    },
                    errorPlacement: function (error, element) {
                        var ele = $(element),
                                err = $(error),
                                msg = err.text();
                        if (msg != null && msg !== '') {
                            ele.tooltipster('content', msg);
                            ele.tooltipster('open');
                        }
                    },
                    unhighlight: function(element, errorClass, validClass) {
                        $(element).removeClass(errorClass).addClass(validClass).tooltipster('close');
                    },
                    submitHandler: function(form) {
                        //do the filtering only when passing the validation
                        doFiltering();
                        return false;  //This doesn't prevent the form from submitting.
                    }

                });

    $('#clear-filter').click(function() {
        //remove the error popup when click clear filter
        $('#filter-form :input[class*=error]').removeClass('has-error').tooltipster('close')
        clearFilters();
    });



    $('#date-min').datepicker({
                                  format: "yyyy-mm",
                                  endDate: "today",
                                  startView: 1,
                                  minViewMode: 1
                              });

    $('#date-max').datepicker({
                                  format: "yyyy-mm",
                                  endDate: "today",
                                  startView: 1,
                                  minViewMode: 1
                              });

//if the dropdown button is clicked, toggle the open element on the div
    $('.dropdown-toggle.multiSelect').on('click', function(event) {
        $(this).parent().toggleClass("open");
    });

//if there is a click anywhere in the page body and the trait selector is open but the click was in the dropdown, close the dropdown
    $('body').on('click', function(e) {
        if ($('#trait-dropdown').hasClass('open') && !$('#trait-dropdown').is(e.target) &&
                !$('#trait-dropdown').children().is(e.target) &&
                !$('#trait-dropdown').children().find('input').is(e.target)) {
            $('#trait-dropdown').removeClass("open");
        }
    });

});

function doFiltering() {
    var pvalRange = processPval();
    var orRange = processOR();
    var betaRange = processBeta();
    var dateRange = processDate();
    var region = processGenomicRegion();
    var traits = processTraitDropdown();
    var genotypingTechnologies = processGenotypingTechnologyDropdown();
    var addeddate = '';

    if ($('#filter').text() != '') {

        if ($('#filter').text() != 'recent' && traits == '') {
            var terms = $('#filter').text();
            terms = terms.replace(/\s/g, '+');

            traits = terms.split('|');
        }
        else if ($('#filter').text() == 'recent') {
            addeddate = "[NOW-1MONTH+TO+*]";
        }
    }


    $('#filter-form').addClass('in-use')
    solrfilter(pvalRange, orRange, betaRange, dateRange, region, traits, genotypingTechnologies, addeddate);
}

function clearFilters() {
    console.log("Clearing all filters");
    $('#filter-form').find('input').val('');
    $('#filter-form').removeClass('in-use')

    loadResults();

    //if ($('#facet').text()) {
    //    console.log("No facet, so I'm redoing the search");
    //doSearch();
    //}
    //else {
    //    console.log("Reapplying the facet without filters for facet " + $('#facet').text());
    //    applyFacet();
    //}
}

function processPval() {

    var pvalRange = '';
    var pvalMant = $('#pval-mant').val();
    var pvalExp = $('#pval-exp').val();
    if (pvalMant || pvalExp) {
        //pvalRange = "[*+TO+";
        if (pvalMant) {
            pvalRange = pvalRange.concat(pvalMant);
        }
        else {
            //if no mantissa was entered, assume 1
            pvalRange = pvalRange.concat("1");
        }
        pvalRange = pvalRange.concat("e");
        if (pvalExp) {
            pvalRange = pvalRange.concat(pvalExp);
        }
        else {
            //if no exponent was entered, use the catalog cut-off
            pvalRange = pvalRange.concat("-5");
        }
        //   pvalRange = pvalRange.concat("]");
        console.log(pvalRange);
    }
    return pvalRange;
}

function processOR() {
    var orRange = '';
    var orMin = $('#or-min').val();
    var orMax = $('#or-max').val();
    if (orMin || orMax) {
        orRange = "[";
        if (orMin) {
            orRange = orRange.concat(orMin);
        }
        else {
            orRange = orRange.concat("1");
        }
        orRange = orRange.concat("+TO+");
        if (orMax) {
            orRange = orRange.concat(orMax);
        }
        else {
            orRange = orRange.concat("*");
        }
        orRange = orRange.concat("]");
        console.log(orRange);
    }
    return orRange;
}

function processBeta() {
    var betaRange = '';
    var betaMin = $('#beta-min').val();
    var betaMax = $('#beta-max').val();
    if (betaMin || betaMax) {
        betaRange = "[";
        if (betaMin) {
            betaRange = betaRange.concat(betaMin);
        }
        else {
            betaRange = betaRange.concat("*");
        }
        betaRange = betaRange.concat("+TO+");
        if (betaMax) {
            betaRange = betaRange.concat(betaMax);
        }
        else {
            betaRange = betaRange.concat("*");
        }
        betaRange = betaRange.concat("]");
        console.log(betaRange);
    }
    return betaRange;
}

function processGenomicRegion(){
    var genomicRegion = '';

    var chrom = $('#chrom').val();
    var bpMin = $('#bp-min').val();
    var bpMax = $('#bp-max').val();

    if(chrom){
        if(chrom == 23){
            chrom = 'X'
        }
        genomicRegion = chrom.concat("-")
    }

    if (bpMin || bpMax) {
        genomicRegion = genomicRegion.concat("[");
        if (bpMin) {
            genomicRegion = genomicRegion.concat(bpMin);
        }
        else {
            genomicRegion = genomicRegion.concat("*");
        }
        genomicRegion = genomicRegion.concat("+TO+");
        if (bpMax) {
            genomicRegion = genomicRegion.concat(bpMax);
        }
        else {
            genomicRegion = genomicRegion.concat("*");
        }
        genomicRegion = genomicRegion.concat("]");
    }

    return genomicRegion;
}

function processDate() {
    var dateRange = '';
    var dateMin = $('#date-min').val();
    var dateMax = $('#date-max').val();
    if (dateMin || dateMax) {
        dateRange = "[";
        if (dateMin) {
            dateRange = dateRange.concat(dateMin).concat("-01T00:00:00Z");
        }
        else {
            dateRange = dateRange.concat("*");
        }
        dateRange = dateRange.concat("+TO+");
        if (dateMax) {
            var year = dateMax.split("-")[0];
            var month = parseInt(dateMax.split("-")[1]);
            var newdate = dateMax;
            if (month === 12) {
                newdate = (parseInt(year) + 1).toString().concat('-01');
            }
            else if (month > 9 && month < 12) {
                newdate = year.concat("-").concat(month + 1);
            }
            else {
                newdate = year.concat("-0").concat(month + 1);
            }
            dateRange = dateRange.concat(newdate).concat("-01T00:00:00Z");
        }
        else {
            dateRange = dateRange.concat("*");
        }
        dateRange = dateRange.concat("]");
        console.log(dateRange);
    }
    return dateRange;
}

function processTraitDropdown() {
    var traits = [];
    var traitInput = $('#trait-dropdown ul li input:checked');
    for (var i = 0; i < traitInput.length; i++) {

        var trait = traitInput[i].value;
        trait = trait.replace(/\s/g, '+');
        trait = trait.replace('%2B', '+');
        trait = trait.replace('%27', "'");
        console.log(trait);
        traits[i] = trait;

    }
    console.log(traits);
    return traits;
}

function processGenotypingTechnologyDropdown() {
    var genotypingTechnologies = [];
    var genotypingTechnologyInput = $('#genotyping-dropdown ul li input:checked');
    for (var i = 0; i < genotypingTechnologyInput.length; i++) {
        
        var genotyping = genotypingTechnologyInput[i].value;
        genotyping = genotyping.replace(/\s/g, '+');
        genotyping = genotyping.replace('%2B', '+');
        genotyping = genotyping.replace('%27', "'");
        console.log(genotyping);
        genotypingTechnologies[i] = genotyping;
        
    }
    console.log(genotypingTechnologies);
    return genotypingTechnologies;
}

function solrfilter(pval, or, beta, date, region, traits,genotypingTechnologies, addeddate) {
    var query = $('#query').text();
    console.log("Solr research request received for " + query + " and filters " + pval + ", " + or + ", " + beta +
                ", " + date + ", " + traits + " and " + genotypingTechnologies + " and " + addeddate);
    if (query == '*') {
        var searchTerm = 'text:'.concat(query);
    }
    else if(query.indexOf(':') != -1 && query.indexOf('-') != -1){
        var elements = query.split(':');
        var chrom = elements[0].trim();
        if(chrom == 23){
            chrom = 'X'
        }
        var bp1 = elements[1].split('-')[0].trim();
        var bp2 = elements[1].split('-')[1].trim();

        var searchTerm = 'chromosomeName:'.concat(chrom).concat(' AND chromosomePosition:[').concat(bp1).concat(' TO ').concat(bp2).concat(']');

    }
    else {
        var searchTerm = 'text:"'.concat(query).concat('"');
    }
    $.getJSON('api/search/filter', {
                'q': searchTerm,
                'group': 'true',
                'group.by': 'resourcename',
                'group.limit': 5,
                'pvalfilter': pval,
                'orfilter': or,
                'betafilter': beta,
                'datefilter': date,
                'genomicfilter': region,
                'traitfilter[]': traits,
                'genotypingfilter[]': genotypingTechnologies,
                'dateaddedfilter': addeddate
            })
            .done(function(data) {
                processData(data);
            });
}
