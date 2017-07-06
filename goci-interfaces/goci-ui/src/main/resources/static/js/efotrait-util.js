/**
 * Created by xin on 19/04/2017.
 */

/**
 * xintodo refactor this!!!!!
 * use let instead of var
 * http://es6-features.org/#ExpressionBodies
 * http://es6-features.org/#ComputedPropertyNames
 * http://es6-features.org/#MethodProperties
 * http://es6-features.org/#StringInterpolation
 * http://es6-features.org/#ObjectMatchingShorthandNotation  //for parsing some ajax result!!
 * http://es6-features.org/#ObjectAndArrayMatchingDefaultValues  //find value from object and assign to var directly
 * http://es6-features.org/#ParameterContextMatching
 * http://es6-features.org/#ObjectPropertyAssignment //merge object
 */
var global_color_url = 'http://wwwdev.ebi.ac.uk/gwas/beta/rest/api/parentMapping/';
var global_color_url_batch = 'http://wwwdev.ebi.ac.uk/gwas/beta/rest/api/parentMappings';
var global_ols_api = 'http://www.ebi.ac.uk/ols/api/';
var global_ols = 'http://www.ebi.ac.uk/ols/';
var global_ols_seach_api =  global_ols_api + 'search';
var global_ols_restful_api_ontology =  global_ols_api + 'ontologies';

var global_efo_info_tag_id = '#efo-info';
var global_epmc_api = 'http://www.ebi.ac.uk/europepmc/webservices/rest/search';
var global_oxo_api = 'http://www.ebi.ac.uk/spot/oxo/api/';

var global_solr_url = 'http://localhost:8983/solr/gwas/select';
// var global_solr_url = 'http://ves-oy-7f.ebi.ac.uk:8983/solr/gwas/select'; //Not working  cross origin problem

/**
 * This is to optimize solr query, only keep the fields/resources we need
 */
var global_fl;
var global_raw;

global_fl = 'pubmedId,title,author_s,publication,publicationDate,catalogPublishDate,' +
        'initialSampleDescription,replicateSampleDescription,ancestralGroups,countriesOfRecruitment,' +
        'ancestryLinks,' +
        'traitName,mappedLabel,mappedUri,traitUri,shortForm,' +
        'label,' + 'efoLink,parent,id,resourcename,';
global_fl = global_fl + 'riskFrequency,qualifier,pValueMantissa,pValueExponent,snpInteraction,multiSnpHaplotype,rsId,strongestAllele,context,region,entrezMappedGenes,reportedGene,merged,currentSnp,studyId,chromosomeName,chromosomePosition,chromLocation,positionLinks,author_s,publication,publicationDate,catalogPublishDate,publicationLink,accessionId,initialSampleDescription,replicateSampleDescription,ancestralGroups,countriesOfRecruitment,numberOfIndividuals,traitName_s,mappedLabel,mappedUri,traitUri,shortForm,labelda,synonym,efoLink,id,resourcename'
global_raw = 'fq:resourcename:association or resourcename:study'

/**
 * global variable storing solr query result.
 */
var data_efo={}
var data_study={}
var data_association={}
var data_diseasetrait={}
var data_facet={}
var data_highlighting={}

/**
 * Other global setting
 */
var pageRowLimit=5;

//*************************** registering button clicking event *************************

/**
 * toggle checkAll/uncheck All for the cart item checkbox.
 */
$('#btn-cart-toggle-check-cbs').click(() =>{
    if($('.cart-item-cb:input:checked').length != $('.cart-item-cb:input').length){
        //not everthing checked, check everthing
        // $('#btn-cart-toggle-check-cbs > span').removeClass('glyphicon-check').addClass('glyphicon-unchecked')
        $('.cart-item-cb:input').attr('checked','checked')
        addDataToTag(global_efo_info_tag_id, getCurrentSelected(), 'whichDescendant')
        addEFO({});
    }else{
        //uncheck everything
        // $('#btn-cart-toggle-check-cbs > span').removeClass('glyphicon-unchecked').addClass('glyphicon-check')
        $('.cart-item-cb:input').removeAttr('checked');
        var tmp = getDataFromTag(global_efo_info_tag_id,'whichDescendant');
        Object.keys(tmp).map((id) => {
            delete tmp[id];
        })
        addEFO({});
    }

});

/**
 * Trigger the download of the current cart.
 */
$('#btn-cart-action-download').click(() =>{
    generateDownloadContentForCart().then((content) => {
        download(content,'term_list.txt')
    })
});

/**
 * Trigger the remove of highlight from locus plot.
 * This button is currently removed since it is not really important.
 * The function is kept for future reference.
 */
$('#btn-clear-highlight').click(() => {
    $('.highlightable').removeClass("highlight");
    reloadLocusZoom('#plot', data_association);
});

/**
 * toggle remove/add associations that annotated to efos which are currently not in the cart
 */
$('#cb-remove-nonunique-association').change(function() {
    updatePage()
})


/**
 * Trigger the remove of all cart items except the mainEFO.
 * This is functionally similar to reload the page, but instead, keep all the cache data.
 * This button is currently removed since it is not really important.
 * The function is kept for future reference.
 */
$('#btn-clear-cart').click(() => {
    var selected = getDataFromTag(global_efo_info_tag_id, 'selectedEfos')
    var mainEFO = getMainEFO();
    Object.keys(selected).map((key)=>{
        if(key != mainEFO)
            removeDataFromTag(global_efo_info_tag_id,'selectedEfos',key)
    })

    whichDescendant().map((key) => {
        removeDataFromTag(global_efo_info_tag_id, 'whichDescendant', key)
    })
    addEFO({});
});


/**
 * Trigger the action to add all related terms to the cart.
 */
$('#btn-query-include-related-terms').click(() => {
    OLS.getRelatedTerms(getMainEFO()).then((relatedTerms) => {
        addEFO(relatedTerms);
    });
});


/**
 * Linkout to ols page of the main EFO term
 */
$('#ols-link').click(() => {
    OLS.getOLSLink(getMainEFO()).then((link)=>{
        window.open(link, '_blank');
    })
});

/**
 * Linkout to oxo page of the main EFO term
 */
$('#oxo-link').click(() => {
    window.open("http://www.ebi.ac.uk/spot/oxo/terms/" + getMainEFO() , '_blank');
});

/**
 * Checkbox to toggle always include all descendants.
 * When uncheck, the newly added efo term will be added to the cart without any descendants.
 * The users can latter add the descendant by clicking the checkbox in front of the cart item.
 * When checked, descendant will always be included for newly added efo terms.
 */
$("#cb-query-include-descendants").change(() => {
    if(isAlwaysDescendant()){
        // $('#btn-cart-toggle-check-cbs > span').removeClass('glyphicon-check').addClass('glyphicon-unchecked')
        addEFO({},false,$("#cb-query-include-descendants").is(":checked"));
    }else{
        $('.cart-item-cb').removeAttr("disabled");
        $('#btn-cart-toggle-check-cbs').removeAttr("disabled");
    }
});


//we add the options to hide associations that have multiple efo annotations.
// $('#btn-filter-association-by-efo-number').click(() => {
//
//     if($('#btn-filter-association-by-efo-number > span').hasClass('glyphicon-minus')){
//         //remove association which have multiple efos
//         $('#btn-filter-association-by-efo-number > span').removeClass('glyphicon-minus').addClass('glyphicon-plus')
//         var multipleEFOAssociation = {}
//         data_association.docs.forEach((d, i) => {
//             if (d.numberEFO > 1){
//                 multipleEFOAssociation[d.id]=d.numberEFO;
//             }
//         })
//         reloadLocusZoom('#plot', data_association, undefined,undefined, Object.keys(multipleEFOAssociation));
//     }else{
//         //replot, adding back the association which have multiple efos
//         $('#btn-filter-association-by-efo-number > span').removeClass('glyphicon-plus').addClass('glyphicon-minus')
//         reloadLocusZoom('#plot', data_association);
//
//     }
// });




/**
 * Copy the sharable link to clipboard.
 */
new Clipboard('#sharable_link_btn', {
    text: function (trigger) {
        return $('#sharable_link').val();
    }
});

$(document).ready(() => {
    //add beta icon
    if (window.location.pathname.indexOf("beta") != -1) {
        $('#beta-icon').show();
    }
    //jump to the top of the page
    $('html,body').scrollTop(0);

    var searchTerm = getMainEFO();
    var included = $('#included').text();
    if (included != '') {
        searchTerm = searchTerm + ',' + included;
    }

    var checked = $('#checked').text();
    if (checked != '') {
        if(checked == "true"){

            $('#cb-query-include-descendants').attr('checked','checked')
        }else{
            checked.split(',').map((efoid) => {
                addDataToTag(global_efo_info_tag_id, {[efoid]:true}, 'whichDescendant')
            })
        }
        searchTerm = searchTerm + ',' + included;
    }

    console.log("Loading search module!");
    if (searchTerm != '') {
        console.log("Start search for the efotrait " + searchTerm);
        var elements = {};
        searchTerm.split(',').forEach((term) => {
                                          elements[term] = term;
                                      }
        )
        //first load
        addEFO(elements, true);
    }
});



//*********************** core functions *****************************

/**
 * When adding extra term to the cart
 * @param {Object} data - a hash, hash keys are the efo ids and value are the efo infos. These efo are to be added to the cart.
 * @param {Boolean} initLoad - if true, the efoinfo/highlighted study will be update
 * @example addEFO{{},false} - simply trigger the update page event without adding anything to the cart.
 * @example addEFO{{'EFO_0000400':{}},false} - add EFO_0000400 to the cart and trigger page update
 */
addEFO = function(data={}, initLoad=false) {

    //If this is the first load, check ever efo id is valid
    if(initLoad){
        Promise.all(Object.keys(data).map(OLS.getEFOInfo)).catch((err)=>{
            $('#lower_container').html("<h2>Invalid EFO id.</h2>");
            return ''
        })
    }


    //add these terms to seleted
    var selected = addDataToTag(global_efo_info_tag_id, data, 'selectedEfos')

    // Promise.all(Object.keys(data).map(OLS.getHierarchicalDescendants)).then(() => {
    //     console.log('finish loading descendants!')
    // })

    //load all available efo terms in the GWAS Catalog(has at least one annotation)
    //save in the global_efo_info_tag_id tag with key 'availableEFOs'
    //This needs to be fullfill before trigger any add event.
    getAvailableEFOs().then((availableEFO) => {
        updatePage(initLoad);
    }).catch((err) => {
        console.error(`Error when loading all available EFOs ${err}`);
    });
}

/**
 * When remove an efo term from the cart
 * @param {String} efoid - remove the EFO from the cart.
 */
removeEFO = function(efoid) {
    removeDataFromTag(global_efo_info_tag_id,'selectedEfos',efoid)
    removeDataFromTag(global_efo_info_tag_id,'whichDescendant',efoid)
    addEFO({});
}


/**
 * function to add a selected efo to the cart box on the page,
 * add bagdes to indicate the the toplevel efo and the number of association/trait for this efo
 * add checkkbox to indicate the descendants
 */
addToCart = function(tagID, efoid, additionalLabel) {

    var isMain = isMainEFO(efoid);

    var colour, colourLabelInit, colourLabel;
    //add badge to the cart item to show the toplevel efo and the number of association/trait for this efo
    return getColourForEFO(efoid).then((data)=>{
        colour = data.colour;
        colourLabel = data.colourLabel;
        //use the first letter to idicate the label
        colourLabelInit = colourLabel.substr(0,1)
    }).catch((err)=>{
        //if colour server is not available
        console.warn(`Error loading colour for cart item < ${efoid} > from ${global_color_url}. ${err}. Using default colour.`)
        //default value
        colour = '#808080';
        colourLabelInit = '?'
        colourLabel = 'unable to reach colour server.';
    }).then(() =>{
        //generate selectedItem
        var container = $(tagID);
        generateSelectedItemCheckBox(efoid,tagID);

        //cart item
        var item = $('<a />', {
            class: 'list-group-item highlightable cart-item col-xs-11',
            title: 'Click to highlight'
        }).appendTo(container);

        //colour badge for category
        $('<span />', { class: 'badge cart-item-category-badge', text: colourLabelInit, title:colourLabel})
                .css({'background-color' :colour})
                .appendTo(item);

        //Add a 'x' to item than can be remove from cart, this will be all terms except the mainEFO
        if (!isMain) {
            var span = $('<span />', {id: 'selected_btn_' + efoid, class: 'badge', text: '  X  '}).appendTo(item);
            span.on("click", function(e) {
                removeEFO(efoid);
            });
        }

        // This will add badge at the end of each cart item, showing the number of association/study linked.
        // This will be init probably before the association/study data is available, thus generating badges with
        // '-' as value at the beginning. The badge will be update when the data is available.
        $('<span />', { id: 'selected_btn_' + efoid + '_nos' ,class: 'badge cart-item-number-badge', text: '-' , title:'Number of study'}).appendTo(item);
        $('<span />', { id: 'selected_btn_' + efoid + '_noa', class: 'badge cart-item-number-badge', text: '-' , title:'Number of association'}).appendTo(item);


        //to highlight associations only for this term
        item.click(() => {
            //TOGGLE HIGHLIGHT
            if(item.hasClass("highlight")){
                item.removeClass("highlight");
                reloadLocusZoom('#plot', data_association)
            }else{
                $('.highlightable.highlight').removeClass("highlight");
                item.addClass("highlight");
                var highlightAssociations = $('#selected_btn_' + efoid + '_noa').data('associations')
                reloadLocusZoom('#plot', data_association, Object.keys(highlightAssociations),efoid);
            }
        })

        item.append(additionalLabel)
    })
}


/**
 * Generate a check box for an EFO in to asslow including descendants
 * @param {String} efoid
 * @param {String} tagID - usually the selected EFO item tag
 */
generateSelectedItemCheckBox = function(efoid,tagID){
    var container = $(tagID);

    //checkbox indicate require descendant
    var cb = $('<input />',
               {
                   id: 'selected_cb_' + efoid,
                   type: 'checkbox',
                   class: "cart-item-cb align-middle checkbox col-xs-1 col-sm-1 col-md-1 col-lg-1",
                   value: efoid
               }).appendTo(container);

    if (isDescendantRequired(efoid)){
        cb.attr('checked','checked')
    }

    cb.change(function(){
//                var id=this.id.split("selected_cb_")[1];
        var id=efoid;

        if(this.checked){
            addDataToTag(global_efo_info_tag_id, {[id]:true}, 'whichDescendant')
        }else{
            var tmp = getDataFromTag(global_efo_info_tag_id,'whichDescendant');
            delete tmp[id];
        }
        updatePage()
    })

    if(isAlwaysDescendant()){
        cb.attr("disabled", true);
    }else{
        cb.removeAttr("disabled");
    }

    //show the descendants number in checkbox tooltips
    OLS.getHierarchicalDescendants(efoid).then((descendants) => {
        filterAvailableEFOs(Object.keys(descendants)).then((availableDescendants)=>{
            cb.attr("title", `${Object.keys(availableDescendants).length} trait subtypes available in GWAS Catalog.`);

        })
//                cb.attr("title", `${Object.keys(descendants).length} descendants. ${Object.keys(descendants).join(',')}`);
    });
}


/**
 * Insert ancestry dropdown items, add onclick function
 * @param {Object} allAncestries - ancestry-associations mappings
 */
prepareAncestryFilter = function(allAncestries){
    //prepare the dropdown for ancestry filter and add click function to highlight ancestry group
    $('#ancestry-dropdown-menu').empty();
    Object.keys(allAncestries).map((ancestry)=>{
        var item = $('<li />')
        //store the ancestry-association in the ancestry item tag
        var a = $('<a />', {class: 'list-group-item highlightable', tabIndex: "-1", text: ancestry}).data(
                'associations',
                allAncestries[ancestry]).data(
                'ancestry',
                ancestry).appendTo(item);
        $('<span />', { class: 'badge cart-item-number-badge', text:allAncestries[ancestry].length , title:'Number of association'}).appendTo(a);
        item.appendTo($('#ancestry-dropdown-menu'));
        item.click((e) => {
            var a = $(e.currentTarget).children('a')
            //TOGGLE HIGHLIGHT
            if(a.hasClass("highlight")){
                a.removeClass("highlight");
                reloadLocusZoom('#plot', data_association)
            }else{
                $('.highlightable.highlight').removeClass("highlight");
                a.addClass("highlight");

                var highlightAssociations = a.data('associations');
                reloadLocusZoom('#plot', data_association, highlightAssociations,a.data('ancestry'));
            }
            return false;
        })

    })
    //enable dropdown
    hideLoadingOverLay('#btn-ancestry-dropdown');
}


/**
 * Insert ancestry dropdown items, add onclick function
 * @param {Object} allAncestries - ancestry-associations mappings
 */
prepareAncestryFilter = function(allAncestries){
    //prepare the dropdown for ancestry filter and add click function to highlight ancestry group
    $('#ancestry-dropdown-menu').empty();
    Object.keys(allAncestries).map((ancestry)=>{
        var item = $('<li />')
        //store the ancestry-association in the ancestry item tag
        var a = $('<a />', {class: 'list-group-item highlightable', tabIndex: "-1", text: ancestry}).data(
                'associations',
                allAncestries[ancestry]).data(
                'ancestry',
                ancestry).appendTo(item);
        $('<span />', { class: 'badge cart-item-number-badge', text:allAncestries[ancestry].length , title:'Number of association'}).appendTo(a);
        item.appendTo($('#ancestry-dropdown-menu'));
        item.click((e) => {
            var a = $(e.currentTarget).children('a')
            //TOGGLE HIGHLIGHT
            if(a.hasClass("highlight")){
                a.removeClass("highlight");
                reloadLocusZoom('#plot', data_association)
            }else{
                $('.highlightable.highlight').removeClass("highlight");
                a.addClass("highlight");

                var highlightAssociations = a.data('associations');
                reloadLocusZoom('#plot', data_association, highlightAssociations,a.data('ancestry'));
            }
            return false;
        })

    })
    //enable dropdown
    hideLoadingOverLay('#btn-ancestry-dropdown');
}


/**
 * trigger a refresh of the page content, including cart, locus plot, tables, badges.
 * @param {Boolean} initLoad - if true, the efoinfo/highlighted study will be update
 */
updatePage = function(initLoad=false) {

    //start spinner. The spinner will be stoped whenever the data is ready, thus closed by the coresponding data loading function.
    if(initLoad){
        showLoadingOverLay('#summary-panel-loading');
//            showLoadingOverLay('#highlight-study-panel-loading');
    }
    showLoadingOverLay('#locus-plot-row-loading');
    showLoadingOverLay('#association-table-loading');
    showLoadingOverLay('#study-table-loading');
    showLoadingOverLay('#cart-loading');
    showLoadingOverLay('#sharable_link_btn');
    showLoadingOverLay('#btn-ancestry-dropdown');
    //temporarily disable the button
    $('.cart-action-btn').attr('disabled',true)


    var current_select = getCurrentSelected();
    var mainEFO = getMainEFO();

    //add all items in the card to require-descendants
    if(isAlwaysDescendant()){
        addDataToTag(global_efo_info_tag_id, getCurrentSelected(), 'whichDescendant')
    }

    // ******************************
    // update cart box
    // ******************************
    var container = $('#cart');
    container.empty()
    //update cart, the badge will be 0 since the data is not ready at this stage.
    //xintodo Using reduce force the order, do I need order here?
    var updateSelectedBoxPromise = Object.keys(current_select).map(OLS.getEFOInfo).reduce(function(sequence, efoInfo) {
        return sequence.then(() => {
            return efoInfo;
        }).then(function(efoInfo) {
            return addToCart('#cart', efoInfo.short_form,  `${efoInfo.label} [${efoInfo.short_form}]`).then(() =>{
                return efoInfo;
            })
        });
    }, Promise.resolve()).catch((err) => {
        console.warning(`Error when updating cart! ${err}`);
    })


    //******************************
    // update efo information panel
    //******************************
    if (initLoad){
        //display efo trait information when data is ready
        displayEFOLabel();
        OLS.getEFOInfo(mainEFO).then(displayEfoTraitInfo).catch((err) => {
            console.warning(`Error loading efo info from OLS. ${err}`);
        }).then(() => {
            hideLoadingOverLay('#summary-panel-loading')
        })
    }

    //******************************
    // add ols graph for related term
    //******************************
    if (initLoad) {
        //addRelatedTermCheckBox();
        initOLS_GraphWiget(mainEFO);
        initOLS_TreeWiget(mainEFO)
        initOLS_AutocompleteWiget();
    }

    //******************************
    // add oxo graph
    //******************************
    if (initLoad) {
        displayOXO();
    }

    //******************************
    // update shareable link
    //******************************
    updateSharableLink();

    //******************************
    // query descendants for current selected terms
    //******************************
    var all_descendants = {}
    var desPromise = Promise.all(whichDescendant().map(OLS.getHierarchicalDescendants)).then((descendants) => {
        descendants.forEach((terms) => {
            Object.keys(terms).forEach( (term) => {
                all_descendants[term] = terms[term];
            })
        })
        return all_descendants;
    }).catch(function(err) {
        console.warning(`Error when trying to find all descendants with error messgae. ${err}`);
    })


    //******************************
    // when solr data ready, process solr data and update badges in the selection cart
    //******************************
    Promise.all([updateSelectedBoxPromise,desPromise]).then((arr)=>{
        var solrPromise = getEfoTraitDataSolr(mainEFO, Object.keys(current_select), Object.keys(all_descendants), initLoad);
        solrPromise.then(() =>{
            //update selection cart
            Object.keys(current_select).map(OLS.getEFOInfo).reduce(function(sequence, efoInfo) {
                return sequence.then(() => {return efoInfo;}).then((efoInfo)=>{
                    var target_efos;
                    //do we include descendant for this term
                    if(isDescendantRequired(efoInfo.short_form)){
                        //self and decendants
                        target_efos = OLS.getHierarchicalDescendants(efoInfo.short_form).then((descendants) => {
                            //adding self
                            return Object.keys(descendants).concat(efoInfo.short_form);
                        })
                    }else{
                        //only self
                        target_efos = Promise.resolve([efoInfo.short_form]);
                    }




                    //update the cart box badge number when data is ready
                    target_efos.then(filterAvailableEFOs).then(function(efos){
                        $('#' + 'selected_btn_' + efoInfo.short_form + '_nos').attr('text', Object.keys(findStudiesForEFOs(efos)).length)
                        $('#' + 'selected_btn_' + efoInfo.short_form + '_nos').html(Object.keys(findStudiesForEFOs(efos)).length)
                        $('#' + 'selected_btn_' + efoInfo.short_form + '_noa').attr('text', Object.keys(findAssociationForEFOs(efos)).length)
                        $('#' + 'selected_btn_' + efoInfo.short_form + '_noa').html(Object.keys(findAssociationForEFOs(efos)).length)
                        //work out what association is included (self or self+descendants), this is used for highlighting the plot.
                        $('#' + 'selected_btn_' + efoInfo.short_form + '_noa').data('associations', findAssociationForEFOs(efos))
                        $('#' + 'selected_btn_' + efoInfo.short_form + '_noa').data('efos', efos)
                    });
                })
            },Promise.resolve())


        }).catch((err) => {
            console.error(`Error searching solr. ${err}`);
            $('#lower_container').html("<h2>Solr server is not available. Please try again later.</h2>");
        }).then(() =>{
            hideLoadingOverLay('#cart-loading');
            $('.cart-action-btn').removeAttr("disabled");
            //do not remove this if alway include descendant is checked
            if(isAlwaysDescendant()){
                $('#btn-cart-toggle-check-cbs').attr('disabled',true)
                // $('#btn-cart-toggle-check-cbs > span').removeClass('glyphicon-check').addClass('glyphicon-unchecked')
            }
        })
    });
}

/**
 * Make solr query.
 * @param {String} mainEFO
 * @param {[]String} additionalEFO
 * @param {[]String} descendants
 * @param {Boolean} initLoad
 * @returns {Promise}
 */
function getEfoTraitDataSolr(mainEFO, additionalEFO, descendants, initLoad=false) {
    // initLoad will be pass to processEfotraitData, controlling whether to upload the triat information(initload)
    // or just reload the tables(adding another efo term)

    var searchQuery = mainEFO;


    if (additionalEFO.length > 0) {
        var p1 = filterAvailableEFOs(additionalEFO).then((additionalEFO_filtered) => {
            if (additionalEFO_filtered.length > 0)
                return searchQuery = searchQuery + ',' + additionalEFO_filtered.join(',');
            else
                return searchQuery;
        });

    }

    if (descendants.length > 0) {
        var p2 = filterAvailableEFOs(descendants).then((descendants_filtered) => {
            if (descendants_filtered.length > 0)
                searchQuery = searchQuery + ',' + descendants_filtered.join(',');
            else
                return searchQuery;
        });
    }

    //xintodo the url will be too long if there are a lot of terms to query, maybe change to post?
    //xintodo need a post endpoint for this
    // http://localhost:8280/gwas/api/search/efotrait?&q=EFO_0000400,EFO_0000400&max=9999&group.limit=9999&group.field=resourcename&facet.field=resourcename&hl.fl=shortForm,efoLink&hl.snippets=100
    return Promise.all([p1, p2]).then(() => {
        console.log("Solr research request received for " + searchQuery);
        //xintodo optmize the query, use fl to return less field.
        //Cross origin not enable in solr
        // return promiseGet(global_solr_url,
        //             {
        //                 'wt' : 'json',
        //                 'q': searchQuery,
        //                 'max': 99999,
        //                 group : true,
        //                 'group.limit': 99999,
        //                 'group.field': 'resourcename',
        //                 hl:true,
        //                 'hl.fl': 'shortForm,efoLink',
        //                 'hl.simple.post' : '</b>',
        //                 'hl.simple.pre' : '<b>',
        //                 'hl.snippets': 100,
        //                 facet : true,
        //                 'facet.field': 'resourcename',
        //
        //             },'application/x-www-form-urlencoded').then(JSON.parse).then(function(data) {
        //     processSolrData(data, initLoad);
        //     console.log("Solr research done for " + searchQuery);
        //     return data;
        // }).catch(function(err) {
        //     console.error('Error when seaching solr for' + searchQuery + '. ' + err);
        //     throw(err);
        // })
        // var link = window.location.pathname.split('/gwas/')[0]+'/gwas/'
        // return promiseGet( window.location.pathname.split('/gwas/')[0]+'/gwas/' + 'api/search/efotrait',
        //                   {
        //                       'q': searchQuery,
        //                       'max': 99999,
        //                       'group.limit': 99999,
        //                       'group.field': 'resourcename',
        //                       'facet.field': 'resourcename',
        //                       'hl.fl': 'shortForm,efoLink',
        //                       'hl.snippets': 100
        //                   }).then(JSON.parse).then(function(data) {
        //     processSolrData(data, initLoad);
        //     console.log("Solr research done for " + searchQuery);
        //     return data;
        // }).catch(function(err) {
        //     console.error('Error when seaching solr for' + searchQuery + '. ' + err);
        //     throw(err);
        // })
        return promisePost( window.location.pathname.split('/gwas/')[0]+'/gwas/' + 'api/search/efotrait',
                          {
                              'q': searchQuery,
                              'max': 99999,
                              'group.limit': 99999,
                              'group.field': 'resourcename',
                              'facet.field': 'resourcename',
                              'hl.fl': 'shortForm,efoLink',
                              'hl.snippets': 100,
                              'fl' : global_fl == undefined ? '*':global_fl,
                              // 'fq' : global_fq == undefined ? '*:*':global_fq,
                              'raw' : global_raw == undefined ? '' : global_raw,
                          },'application/x-www-form-urlencoded').then(JSON.parse).then(function(data) {
            processSolrData(data, initLoad);
            console.log("Solr research done for " + searchQuery);
            return data;
        }).catch(function(err) {
            console.error('Error when seaching solr for' + searchQuery + '. ' + err);
            throw(err);
        })

    })

}

/**
 * Parse the Solr results and display the data on the HTML page
 * @param {{}} data - solr result
 * @param {Boolean} initLoad
 */
function processSolrData(data, initLoad=false) {
//        if (data.grouped.resourcename.matches == 0) {
//            $('#lower_container').html("<h2>The efotrait <em>" + getMainEFO() +
//                                       "</em> cannot be found in the GWAS Catalog database</h2>");
//        }
    var isInCatalog=true;
    if (data.grouped.resourcename.matches == 0) {
        isInCatalog = false
    }
    //split the solr search by groups
    //data_efo, data_study, data_association, data_diseasetrait;
    data_facet = data.facet_counts.facet_fields.resourcename;
    data_highlighting = data.highlighting;

    $.each(data.grouped.resourcename.groups, (index, group) => {
        switch (group.groupValue) {
            case "efotrait":
                data_efo = group.doclist;
                break;
            case "study":
                data_study = group.doclist;
                break;
            case "association":
                data_association = group.doclist;
                break;
                //not sure we need this!
            case "diseasetrait":
                data_diseasetrait = group.doclist;
                break;
            default:
        }
    });

    //remove association that annotated with efos which are not in the list
    var remove = Promise.resolve();

    if($('#cb-remove-nonunique-association').is(":checked")){
        remove = removeAssociationWithNonSelectedEFO();
    }


    remove.then(()=>{
        //If no solr return,greate a fake empyt array so tables/plot are empty
        if(!isInCatalog) {
            data_association.docs = []
            data_study.docs = []
        }

        //update association/study table
        displayEfotraitAssociations(data_association.docs);
        displayEfotraitStudies(data_study.docs);


        //work out highlight study
        var highlightedStudy = findHighlightedStudiesForEFO(getMainEFO());
        if(initLoad && highlightedStudy!= undefined){
            displayHighlightedStudy(highlightedStudy);
            //display summary information like 'EFO trait first reported in GWAS Catalog in 2007, 5 studies report this efotrait'
            getSummary(findStudiesForEFO(getMainEFO()));
        }


        //we add preferEFO for each association, generate the association data for popup of data points in the locus plot.
        var allUniqueEFO = {}
        data_association.docs.forEach((d, i) => {
            d.preferedEFO = findHighlightEFOForAssociation(d.id,data_highlighting)[0];
            d.numberEFO = findAllEFOsforAssociation(d.id,data_association).length;
            allUniqueEFO[d.preferedEFO] = 1;
            //add any string data that will be use in the locus plot popover
            d.popoverHTML = buildLocusPlotPopoverHTML(d);
        })

        //get all ancestries of all associations
        var allAncestries = {};
        data_association.docs.map((d) => {
            if(d.ancestralGroups!= undefined){
                d.ancestralGroups.map((a)=>{
                    if(allAncestries[a] == undefined) allAncestries[a] = [];
                    allAncestries[a].push(d.id);
                })
            }
        })

        prepareAncestryFilter(allAncestries);


        //wwwdev.ebi.ac.uk/gwas/beta/rest/api/parentMapping/EFO_0000400
        //Load colour for unique efo
        var allColorLoaded = []
        Object.keys(allUniqueEFO).forEach((efo) => {
            allColorLoaded.push(getColourForEFO(efo).then((response) => {
                allUniqueEFO[efo] = response;
                return response;
            }));
        })

        //When all colour are received, replot
        Promise.all(allColorLoaded).then(() => {
                                             //assign colour to associations for plot
                                             console.debug(`Finish loading color from ${global_color_url}`);
                                             console.debug(allUniqueEFO);
                                             data_association.docs.forEach(function(d, i) {
                                                 d.preferedColor = allUniqueEFO[d.preferedEFO].colour;
                                                 d.preferedParentUri = allUniqueEFO[d.preferedEFO].parentUri;
                                                 d.preferedParentLabel = allUniqueEFO[d.preferedEFO].parent;
                                                 d.category = allUniqueEFO[d.preferedEFO].parent;
                                             })
                                         }
        ).catch((err) => {
            console.warn(`Error loading colour for Locus zoom plot from ${global_color_url}. ${err}. Using default colour.`)
            //xintodo create a default colour to plot, if the colour query fail
        }).then(() =>{
            //replot
            reloadLocusZoom('#plot', data_association);
        });
    })

}


/**
 * remove associations that annotated to efos which are currently not in the cart
 */
removeAssociationWithNonSelectedEFO = function() {
    return getAllSelected().then((efos) => {
        data_association.docs = data_association.docs.filter((association_doc) => {
            var allefos = findAllEFOsforAssociation(association_doc.id, data_association);
            if (allefos.filter((efoid) => {
                        return Object.keys(efos).indexOf(efoid) == -1
                    }).length == 0) {
                return true
            }
            console.log(`remove ${association_doc.id}!`);
            return false
        })
    })
}




/**
 * This is to add checkbox for each related terms
 * but this is not used atm.
 */
addRelatedTermCheckBox = () => {
    function addCheckbox(id, name, cbclass) {
        var container = $('#expension');
        $('<input />', {type: 'checkbox', id: id, class: cbclass, value: name}).appendTo(container);
        $('<label />', {'for': id, text: name}).appendTo(container);
        $('<div />', {'id': 'why' + id}).appendTo(container);

        $('<br />').appendTo(container);

    }

    OLS.getRelatedTerms(getMainEFO()).then(function(terms) {
        //make checkbox
        Object.keys(terms).forEach(function(relatedTerm) {
            var d = terms[relatedTerm];
            addCheckbox('cb' + d.obo_id, d.obo_id + ' : ' + d.label, 'efocheckbox');
            $("#" + 'whycb' + d.obo_id).html(d.logical_description);
            if (d.obo_id == getMainEFO()) {
                $("#cb" + d.obo_id).attr("checked", true);
            }
        })

        //add check event.
        $(".efocheckbox").change(() => {
            var elements = {};
            $('.efocheckbox:input:checked').each(function(cb) {
                elements[$(this).attr('value').split(" : ")[0]] = $(this).attr('value').split(" : ")[1];
            });
            addEFO(elements);
        });
    })
}

/**
 * init a network using ols-graph at the <#ontology_vis> tag.
 * The network shows the mainEFO with all the realted EFOs.
 * @param {String} mainEFO
 */
initOLS_GraphWiget = function(mainEFO) {
    var mainEFOLink = OLS.getOLSLinkAPI(mainEFO).then((termFullLink) => {
        return termFullLink + '/graph';
    })

    var mainEFOIri = OLS.getIriByShortForm(mainEFO)

    var tmpnetworkOptions = {
        webservice: {
//            URL: "http://www.ebi.ac.uk/ols/api/ontologies/cmpo/terms/http%253A%252F%252Fpurl.obolibrary.org%252Fobo%252FCHEBI_33839/graph",
            OLSschema: false
        },
        displayOptions: {showButtonBox: true, showInfoWindow: false, showLegend: true},
        callbacks: {
            onSelectNode: function(params) {
                console.debug(params);
            },
            onDoubleClick: function(params) {
                console.debug(params);
                var node = params.nodes[0];
                var efoid = node.split('/').slice(-1)[0];
                addEFO({[efoid]:node});
            },
            onSelectEdge: function(params) {
                console.debug(params);
            },
            onClick: function(params) {
                console.debug(params);
            }
        }
    }
    var visoptions = {
        physics: {
            forceAtlas2Based: {
                gravitationalConstant: -50,
                centralGravity: 0.01,
                springConstant: 0.08,
                springLength: 100,
                damping: 0.4,
                avoidOverlap: 0
            },
        },
        layout: {
            hierarchical: false
        },
        nodes: {
            borderWidth: 4,
            shape: 'ellipse'
        },
        edges: {
            arrows: {middle: {enabled: true}},
            dashes: true,
        },
    }

    var term = "Can be whatever at the moment - if you chose OLSschema false"
    var app = require("ols-graphview");
    var instance = new app();

    var reasonForRelatedEFO = {};
    //prepare the network, insert terms of interests
    mainEFOLink.then(function(mainLink) {
        tmpnetworkOptions['webservice']['URL'] = mainLink;
        instance.visstart("ontology_vis", term, tmpnetworkOptions, visoptions);

        OLS.getRelatedTerms(getMainEFO()).then((terms) => {
            //find out why this term is 'related', from its logical_description
            Object.keys(terms).forEach((relatedTerm) => {
                var d = terms[relatedTerm];
                reasonForRelatedEFO[d.obo_id] = d.logical_description;
            })

            return Object.keys(terms).map(OLS.getOLSLinkAPI).reduce((sequence, termOLSLinkPromise) => {
                return sequence.then(() => {
                    return termOLSLinkPromise;
                }).then(function(termOLSLink) {
                    //for each related term, add it to the graph
                    instance.fetchNewGraphData(termOLSLink + '/graph');
                })
            }, Promise.resolve())
        })
    }).then(() => {
        var x = instance.getGraphDataset()
        var net=instance.getNetwork()
        console.debug(net)

        //change mainEFO node shape to hightlight related terms
        mainEFOIri.then((iri) => {
            //Thi is to make sure the setting apply AFTER the nodes are loaded.
            net.once("stabilized", (params) => {
                //http://visjs.org/docs/data/dataset.html
                var defaultAttribute = x["nodes"].get(iri)
                defaultAttribute.color.border = 'blue';
                x["nodes"].update(defaultAttribute);

                //add why to the title of node
                $.each(reasonForRelatedEFO, (efoid, reason) => {
                    OLS.getIriByShortForm(efoid).then( (iri) => {
                        x["nodes"].update({id: iri, title: reason});
                    })
                })
            })
        })
    }).catch((err) => {
        console.error('Error when plotting related terms.' + err);
        throw(err);
    })

}


/**
 * init ols-tree wiget for the main efo in tag <#ols-treeview>
 * https://github.com/LLTommy/OLS-treeview
 * @param {String} initTerm
 */
initOLS_TreeWiget = function(initTerm,showSibblings=false,olsontology='efo'){
    //ols-tree
    var app = require("ols-treeview");
    var instance = new app();

    var options = {
        onclick: function(params, node, relativePath, termIRI, type){
            // var isComfirmed =  confirm('Are you sure?');
            // if(isComfirmed){
            //     var clicked = node.node.original.iri;
            //     var efoid = clicked.split('/').slice(-1)[0];
            //     var tmp = {}
            //     tmp[efoid] = clicked;
            //     addEFO(tmp);
            // }else{
            //     console.debug('not comfirmed');
            // }
            var clicked = node.node.original.iri;
            var efoid = clicked.split('/').slice(-1)[0];
            addEFO({[efoid]:clicked});
        },
    }

    //initialise the tree
    instance.draw($("#term-tree"),
                  showSibblings,
                  olsontology,
                  "terms",
                  "http://www.ebi.ac.uk/efo/" + initTerm,
                  "http://www.ebi.ac.uk/ols/",
                  options);
}

/**
 * init ols-autocomplete
 */
initOLS_AutocompleteWiget = function(){
    var app = require("ols-autocomplete");
    var instance = new app();
    options = {
        action: function(relativePath, suggestion_ontology, type, iri) {
            // console.log("In overwritten function")
            // console.log("Relative Path: " + relativePath)
            // console.log("Suggested Ontology: " + suggestion_ontology)
            // console.log("Type (optional): " + type)
            // console.log("iri (optional): " + iri)
            var efoid = 'EFO_' + iri.split("EFO_")[1];
            //xintodo auto load
            addEFO({[efoid]:efoid});
        }
    }
    instance.start(options)
}

/**
 * Display the main EFO label on the top of the page
 */
displayEFOLabel = function(){
    OLS.getEFOInfo(getMainEFO()).then((efoInfo)=>{
        $('#top-panel-trait-label').html(efoInfo.label);
    })
}

/**
 * Display highlighted study on the page
 * @param highlightedStudy
 * @example findHighlightedStudiesForEFO(getMainEFO()) give you an example study doc
 */
displayHighlightedStudy = function(highlightedStudy) {
    $('#efotrait-highlighted-study-title').html(highlightedStudy.title);
    $('#efotrait-highlighted-study-author').html(highlightedStudy.author_s + ' (' +
                                                 setExternalLink(EPMC_URL + highlightedStudy.pubmedId,
                                                                 'PMID:' + highlightedStudy.pubmedId) +
                                                 ')');
    $('#efotrait-highlighted-study-catalogPublishDate').html(new Date(highlightedStudy.catalogPublishDate).toLocaleDateString());
    // $('#efotrait-highlighted-st udy-initialSampleDescription').html(highlightedStudy.initialSampleDescription);
    // $('#efotrait-highlighted-study-replicateSampleDescription').html(highlightedStudy.replicateSampleDescription);
    // $("#efotrait-highlighted-study-all").html(longContent("efotrait-highlighted-study-all_div",
    //                                                       JSON.stringify(highlightedStudy),'all'));

    EPMC.getByPumbedId(highlightedStudy.pubmedId).then((data) => {
        var paperDetail = data.resultList.result[0];
        // $("#efotrait-highlighted-study-abstract").html(longContent("efotrait-highlighted-study-abstract_div",
        //                                                            paperDetail.abstractText,''));
        // $('#efotrait-highlighted-study-abstract').html(createPopover('detail',
        //                                                              'abstract',
        //                                                              paperDetail.abstractText));
        $('#efotrait-highlighted-study-abstract').html(EPMC.searchResult.abstractText(data));
        return paperDetail;
    }).catch((err) => {
        console.warn(`Error when loading data from EPMC! ${err}`);
    }).then(() => {
        hideLoadingOverLay('#highlight-study-panel-loading');
    });
}

/**
 * display oxo graph and a mesh id in the trait information panel
 */
displayOXO = function(){
    getOXO(getMainEFO()).then((data)=>{
        var container = $('#btn-oxo-expand');
        var totalMapping = parseInt(data.page.totalElements);
        var xrefs = [];
        var xrefs_mesh = []
        if(totalMapping > 0){
            xrefs = data._embedded.mappings.map((xref) => {
                return xref.toTerm.curie
            })

            xrefs_mesh = xrefs.filter((xref_id) => {
                return xref_id.startsWith("MeSH")
            });
        }

        if(totalMapping > 0) {
            $('#oxo-list').append(displayArrayAsList(xrefs))
        }



        //we want to show a mesh here
        if(totalMapping > 0){
            if(xrefs_mesh.length > 0){
                var xref_mesh = xrefs_mesh[0];
                container.html(`<!--<b> ${xref_mesh} </b> and <b>${totalMapping}</b>  more ontology Xrefs--> `);
                container.html(`${xref_mesh} ...`);
            }else{
                container.html(`<b> ${totalMapping} </b> ontology Xrefs`);
            }
            container.append(showHideDiv('oxo-graph'));

        }else{
            container.html(`no ontology Xrefs found.`);
        }

        $('#button-oxo-graph').click(() => {
            //To redraw the oxo graph so that it is center to the div
            drawGraph(getMainEFO().replace('_',':'),1);
        });
    })
}

/**
 * display efo information on the page
 * @param efotraitId
 */
displayEfoTraitInfo = function(efoinfo) {
    var efotrait_link = efoinfo.iri;
    var efotrait_id = efoinfo.short_form;
    var synonym = efoinfo.synonyms;
    var efotrait_label = efoinfo.label;
    addDataToTag(global_efo_info_tag_id, efoinfo, 'mainEFOInfo');
    $("#efotrait-description").html(displayArrayAsList(efoinfo.description));
    $("#efotrait-id").html(setExternalLink(efotrait_link, efotrait_id));
    $("#efotrait-label").html(efotrait_label);
    // $("#efotrait-label").html(createPopover(efotrait_label,
    //                                         'description',
    //                                         displayArrayAsList(efoinfo.description)));
    if (synonym) {
        if (synonym.length > list_min) {
            $("#efotrait-synonym").html(longContentList("gwas_efotrait_synonym_div",
                                                        synonym,
                                                        'synonyms'));
        }
        else {
            $("#efotrait-synonym").html(synonym.join(", "));
        }
    }
}

/**
 * display association table
 * @param {Object} data - association solr docs
 * @param {Boolean} cleanBeforeInsert
 */
function displayEfotraitAssociations(data, cleanBeforeInsert) {
    //by default, we clean the table before inserting data
    if (cleanBeforeInsert === undefined) {
        cleanBeforeInsert = true;
    }

    var asso_count = data.length;

    $(".association_count").html(asso_count);

    if (asso_count == 1) {
        $(".association_label").html("Association");
    }

    if(cleanBeforeInsert){
        $('#association-table').bootstrapTable('removeAll');
    }

    var data_json = []
    $.each(data, function(index,asso) {

        //if association does not have rsid, skip
        if(asso.strongestAllele == undefined){return true};

        var tmp = {};
        // Risk allele
        var riskAllele = asso.strongestAllele[0];
        var riskAlleleLabel = riskAllele;
        var riskAllele_rsid = riskAllele;
        if (riskAlleleLabel.match(/\w+-.+/)) {
            riskAlleleLabel = riskAllele.split('-').join('-<b>')+'</b>';
            riskAllele_rsid = riskAllele.split('-')[0];
        }
        // This is now linking to the variant page instead of the search page
        // riskAllele = setQueryUrl(riskAllele,riskAlleleLabel);
        riskAllele = setExternalLinkText(window.location.pathname.split('/gwas/')[0]+'/gwas/' + 'variants/' + riskAllele_rsid,riskAlleleLabel);

        tmp['riskAllele'] = riskAllele;

        // Risk allele frequency
        tmp['riskAlleleFreq'] = asso.riskFrequency;

        // p-value
        var pValue = asso.pValueMantissa;
        if (pValue) {
            var pValueExp = " x 10<sup>" + asso.pValueExponent + "</sup>";
            pValue += pValueExp;
            if (asso.qualifier) {
                if (asso.qualifier[0].match(/\w/)) {
                    pValue += " " + asso.qualifier.join(',');
                }
            }
            tmp['pValue'] = pValue;
        } else {
            tmp['pValue'] = '-';
        }

        // OR
        var orValue = asso.orPerCopyNum;
        if (orValue) {
            if (asso.orDescription) {
                orValue += " " + asso.orDescription;
            }
            tmp['orValue'] = orValue;
        } else {
            tmp['orValue'] = '-';
        }

        // Beta
        var beta = asso.betaNum;
        if (beta) {
            if (asso.betaUnit) {
                beta += " " + asso.betaUnit;
            }
            if (asso.betaDirection) {
                beta += " " + asso.betaDirection;
            }
            tmp['beta'] = beta;
        } else {
            tmp['beta'] = '-';
        }

        // CI
        var ci = (asso.range) ? asso.range : '-';
        tmp['beta'] = ci;


        // Reported genes
        var genes = [];
        var reportedGenes = asso.reportedGene;
        if (reportedGenes) {
            $.each(reportedGenes, function(index, gene) {
                genes.push(setQueryUrl(gene));
            });
            tmp['reportedGenes'] = genes.join(', ');
        } else {
            tmp['reportedGenes'] = '-';

        }

        // Mapped genes
        var genes = [];
        var mappedGenes = asso.entrezMappedGenes;
        if (mappedGenes) {
            $.each(mappedGenes, function(index, gene) {
                genes.push(setQueryUrl(gene));
            });
            tmp['mappedGenes'] = genes.join(', ');
        } else {
            tmp['mappedGenes'] = '-';

        }

        // Reported traits
        var traits = [];
        var reportedTraits = asso.traitName;
        if (reportedTraits) {
            $.each(reportedTraits, function(index, trait) {
                traits.push(setQueryUrl(trait));
            });
            tmp['reportedTraits'] = traits.join(', ');
        } else {
            tmp['reportedTraits'] = '-';
        }

        // Mapped traits
        var mappedTraits = asso.mappedLabel;
        if (mappedTraits) {
            $.each(mappedTraits, function(index, trait) {
                var link = window.location.pathname.split('/efotraits/')[0]+'/efotraits/' + asso.mappedUri[index].split('/').slice(-1)[0]
                mappedTraits[index] = setExternalLinkText(link,trait)
            });
            tmp['mappedTraits'] = mappedTraits.join(', ');
        } else {
            tmp['mappedTraits'] = '-';
        }

        // Study
        var author = asso.author_s;
        var publicationDate = asso.publicationDate;
        var pubDate = publicationDate.split("-");
        var pubmedId = asso.pubmedId;
        var study = setQueryUrl(author, author + " - " + pubDate[0]);
        study += '<div><small>'+setExternalLink(EPMC_URL+pubmedId,'PMID:'+pubmedId)+'</small></div>';
        tmp['study'] = study;

        var studyId = asso.studyId;

        // Populate the table
        data_json.push(tmp)


    });

    $('#association-table').bootstrapTable({
                                               exportDataType: 'all',
                                               columns: [{
                                                   field: 'riskAllele',
                                                   title: 'Risk allele',
                                                   sortable: true
                                               }, {
                                                   field: 'riskAlleleFreq',
                                                   title: 'RAF',
                                                   sortable: true
                                               }, {
                                                   field: 'pValue',
                                                   title: 'p-value',
                                                   sortable: true
                                               },{
                                                   field: 'orValue',
                                                   title: 'OR',
                                                   sortable: true
                                               },{
                                                   field: 'beta',
                                                   title: 'Beta',
                                                   sortable: true
                                               },{
                                                   field: 'price',
                                                   title: 'CI',
                                                   sortable: true
                                               },{
                                                   field: 'mappedGenes',
                                                   title: 'Mapped gene(s)',
                                                   sortable: true
                                               },{
                                                   field: 'reportedTraits',
                                                   title: 'Reported trait',
                                                   sortable: true
                                               },{
                                                   field: 'mappedTraits',
                                                   title: 'Mapped trait',
                                                   sortable: true
                                               },{
                                                   field: 'study',
                                                   title: 'Study',
                                                   sortable: true
                                               }],
                                               data: data_json,

                                           });

    $('#association-table').bootstrapTable('load',data_json)
    if(data_json.length>5){
        $('#association-table').bootstrapTable('refreshOptions',{pagination: true,pageSize: pageRowLimit,pageList: [5,10,25,50,100,'All']})
    }
    hideLoadingOverLay('#association-table-loading')
}

/**
 * display study table
 * @param {Object} data - study solr docs
 * @param {Boolean} cleanBeforeInsert
 */
function displayEfotraitStudies(data, cleanBeforeInsert=true) {
    //by default, we clean the table before inserting data
    var study_ids = [];
    if(cleanBeforeInsert){
        $('#study-table').bootstrapTable('removeAll');
    }

    var data_json = []
    $.each(data, (index, asso) => {
        var tmp={};
        var study_id = asso.id;
        if (jQuery.inArray(study_id, study_ids) == -1) {
            study_ids.push(study_id);
            // Author
            var author = asso.author_s;
            var publicationDate = asso.publicationDate;
            var pubDate = publicationDate.split("-");
            var pubmedId = asso.pubmedId;
            var study_author = setQueryUrl(author, author);
            study_author += '<div><small>'+setExternalLink(EPMC_URL+pubmedId,'PMID:'+pubmedId)+'</small></div>';
            tmp['Author'] = study_author;

            // Publication date
            var p_date = asso.publicationDate;
            var publi = p_date.split('T')[0];
            tmp['publi'] = publi;


            // Journal
            tmp['Journal'] = asso.publication;

            // Title
            tmp['Title'] = asso.title;

            // Initial sample desc
            var initial_sample_text = '-';
            if (asso.initialSampleDescription) {

                initial_sample_text = displayArrayAsList(asso.initialSampleDescription.split(', '));
                if(asso.initialSampleDescription.split(', ').length>1)
                    initial_sample_text = initial_sample_text.html()
            }
            tmp['initial_sample_text'] = initial_sample_text;


            // Replicate sample desc
            var replicate_sample_text = '-';
            if (asso.replicateSampleDescription) {
                replicate_sample_text = displayArrayAsList(asso.replicateSampleDescription.split(', '));
                if(asso.replicateSampleDescription.split(', ').length>1)
                    replicate_sample_text = replicate_sample_text.html()
            }
            tmp['replicate_sample_text'] = replicate_sample_text;


            // ancestralGroups
            var ancestral_groups_text = '-';
            if (asso.ancestralGroups) {
                ancestral_groups_text = displayArrayAsList(asso.ancestralGroups);
                if(asso.ancestralGroups.length>1)
                    ancestral_groups_text = ancestral_groups_text.html()
            }
            tmp['ancestral_groups_text'] = ancestral_groups_text;

            data_json.push(tmp)
        }
    });
    // Study count //
    $(".study_count").html(study_ids.length);

    if (study_ids.length == 1) {
        $(".study_label").html("Study");
    }

    $('#study-table').bootstrapTable({
                                         exportDataType: 'all',
                                         columns: [{
                                             field: 'Author',
                                             title: 'Author',
                                             sortable: true
                                         }, {
                                             field: 'publi',
                                             title: 'Publication Date',
                                             sortable: true
                                         }, {
                                             field: 'Journal',
                                             title: 'Journal',
                                             sortable: true
                                         },{
                                             field: 'Title',
                                             title: 'Title',
                                             sortable: true,
                                             width:"1000", //This works when the table is not nested into other tag, for example, in a simple Div
                                         },{
                                             field: 'initial_sample_text',
                                             title: 'Initial sample description',
                                             sortable: true
                                         },{
                                             field: 'replicate_sample_text',
                                             title: 'Replication sample description',
                                             sortable: true
                                         },{
                                             field: 'ancestral_groups_text',
                                             title: 'Ancestral groups',
                                             sortable: true
                                         }],
                                         data: data_json,

                                     });
    $('#study-table').bootstrapTable('load',data_json)
    if(data_json.length>5){
        $('#study-table').bootstrapTable('refreshOptions',{pagination: true,pageSize: pageRowLimit,pageList: [5,10,25,50,100,'All']})
    }
    hideLoadingOverLay('#study-table-loading')
}


/**
 * trigger a download from the browser.
 * @param {String} content
 * @param {String} filename - default file name to save the download
 * @param {String} contentType - default is 'application/octet-stream'
 */
function download(content, filename, contentType) {
    if(!contentType) contentType = 'application/octet-stream';
    var a = document.createElement('a');
    var blob = new Blob([content], {'type':contentType});
    a.href = window.URL.createObjectURL(blob);
    a.download = filename;
    a.click();
}

/**
 * Generate content of currently selected terms
 * @returns {Promise} - contins a string of the content
 */
generateDownloadContentForCart = function(){
    var rowSep = '\n'
    var colSep = '\t'
    var list = [];
    $('.badge.cart-item-number-badge').filter(function() {
        return this.id.match(/_noa/);
    }).map(function(){
        list = list.concat($('#'+this.id).data('efos'))
    })
    var list_unique = list.filter(function onlyUnique(value, index, self) {
        return self.indexOf(value) === index;
    })


    var rows = Promise.all(list_unique.map(OLS.getEFOInfo)).then((efoinfos) => {
        return efoinfos.map((efoinfo)=>{
            return [efoinfo.short_form,efoinfo.ontology_name,efoinfo.iri].join(colSep)
        })
    })

//        var content = encodeURIComponent(list_unique.join('\n'));
    var content = rows.then((rows)=>{
        return rows.join(rowSep);
    })
    return content;
}

/**
 * Display association table from solr search associations
 * @param {[association_doc]} solr_association
 * @param {Boolean} cleanBeforeInsert
 */
function displayEfotraitAssociations_deprecated(solr_association, cleanBeforeInsert=true) {
    //by default, we clean the table before inserting data

    var asso_count = solr_association.length;

    $(".association_count").html(asso_count);

    if (asso_count == 1) {
        $(".association_label").html("Association");
    }

    if(cleanBeforeInsert){
        $("#association-table-body tr").remove();
    }

    $.each(solr_association, function(index,asso) {

        var row = $('<tr/>');

        // Risk allele
        var riskAllele = asso.strongestAllele[0];
        var riskAlleleLabel = riskAllele;
        var riskAllele_rsid = riskAllele;
        if (riskAlleleLabel.match(/\w+-.+/)) {
            riskAlleleLabel = riskAllele.split('-').join('-<b>')+'</b>';
            riskAllele_rsid = riskAllele.split('-')[0];
        }
        // This is now linking to the variant page instead of the search page
        // riskAllele = setQueryUrl(riskAllele,riskAlleleLabel);
        riskAllele = setExternalLinkText(window.location.pathname.split('/gwas/')[0]+'/gwas/' + 'variants/' + riskAllele_rsid,riskAlleleLabel);

        row.append(newCell(riskAllele));

        // Risk allele frequency
        var riskAlleleFreq = asso.riskFrequency;
        row.append(newCell(riskAlleleFreq));

        // p-value
        var pValue = asso.pValueMantissa;
        if (pValue) {
            var pValueExp = " x 10<sup>" + asso.pValueExponent + "</sup>";
            pValue += pValueExp;
            if (asso.qualifier) {
                if (asso.qualifier[0].match(/\w/)) {
                    pValue += " " + asso.qualifier.join(',');
                }
            }
            row.append(newCell(pValue));
        } else {
            row.append(newCell('-'));
        }

        // OR
        var orValue = asso.orPerCopyNum;
        if (orValue) {
            if (asso.orDescription) {
                orValue += " " + asso.orDescription;
            }
            row.append(newCell(orValue));
        } else {
            row.append(newCell('-'));
        }

        // Beta
        var beta = asso.betaNum;
        if (beta) {
            if (asso.betaUnit) {
                beta += " " + asso.betaUnit;
            }
            if (asso.betaDirection) {
                beta += " " + asso.betaDirection;
            }
            row.append(newCell(beta));
        } else {
            row.append(newCell('-'));
        }

        // CI
        var ci = (asso.range) ? asso.range : '-';
        row.append(newCell(ci));
        // Reported genes
        var genes = [];
        var reportedGenes = asso.reportedGene;
        if (reportedGenes) {
            $.each(reportedGenes, function(index, gene) {
                genes.push(setQueryUrl(gene));
            });
            row.append(newCell(genes.join(', ')));
        } else {
            row.append(newCell('-'));
        }

        // Reported traits
        var traits = [];
        var reportedTraits = asso.traitName;
        if (reportedTraits) {
            $.each(reportedTraits, function(index, trait) {
                traits.push(setQueryUrl(trait));
            });
            row.append(newCell(traits.join(', ')));
        } else {
            row.append(newCell('-'));
        }

        // Mapped traits
        var mappedTraits = asso.mappedLabel;
        if (mappedTraits) {
            row.append(newCell(mappedTraits.join(', ')));
        } else {
            row.append(newCell('-'));
        }

        // Study
        var author = asso.author_s;
        var publicationDate = asso.publicationDate;
        var pubDate = publicationDate.split("-");
        var pubmedId = asso.pubmedId;
        var study = setQueryUrl(author, author + " - " + pubDate[0]);
        study += '<div><small>'+setExternalLink(EPMC_URL+pubmedId,'PMID:'+pubmedId)+'</small></div>';
        row.append(newCell(study));

        var studyId = asso.studyId;

        // Populate the table
        $("#association-table-body").append(row);
        hideLoadingOverLay('#association-table-loading')
    });
}


/**
 * display study table from solr search studies
 * @param {[study_doc]} solr_studies
 * @param {Boolean} cleanBeforeInsert
 */
function displayEfotraitStudies_deprecated(solr_study, cleanBeforeInsert=true) {
    //by default, we clean the table before inserting solr_study
    if (cleanBeforeInsert === undefined) {
        cleanBeforeInsert = true;
    }


    var study_ids = [];
    if(cleanBeforeInsert){
        $("#study-table-body tr").remove();
    }
    $.each(solr_study, function(index, asso) {
        var study_id = asso.id;
        if (jQuery.inArray(study_id, study_ids) == -1) {

            var row = $('<tr/>');

            study_ids.push(study_id);

            // Author
            var author = asso.author_s;
            var publicationDate = asso.publicationDate;
            var pubDate = publicationDate.split("-");
            var pubmedId = asso.pubmedId;
            var study_author = setQueryUrl(author, author);
            study_author += '<div><small>'+setExternalLink(EPMC_URL+pubmedId,'PMID:'+pubmedId)+'</small></div>';
            row.append(newCell(study_author));

            // Publication date
            var p_date = asso.publicationDate;
            var publi = p_date.split('T')[0];
            row.append(newCell(publi));

            // Journal
            row.append(newCell(asso.publication));

            // Title
            row.append(newCell(asso.title));

            // Initial sample desc
            var initial_sample_text = '-';
            if (asso.initialSampleDescription) {
                initial_sample_text = displayArrayAsList(asso.initialSampleDescription.split(', '));
            }
            row.append(newCell(initial_sample_text));

            // Replicate sample desc
            var replicate_sample_text = '-';
            if (asso.replicateSampleDescription) {
                replicate_sample_text = displayArrayAsList(asso.replicateSampleDescription.split(', '));
            }
            row.append(newCell(replicate_sample_text));

            // ancestralGroups
            var ancestral_groups_text = '-';
            if (asso.ancestralGroups) {
                ancestral_groups_text = displayArrayAsList(asso.ancestralGroups);
            }
            row.append(newCell(ancestral_groups_text));

            // Populate the table
            $("#study-table-body").append(row);
        }
    });
    // Study count //
    $(".study_count").html(study_ids.length);

    if (study_ids.length == 1) {
        $(".study_label").html("Study");
    }

    hideLoadingOverLay('#study-table-loading')
}



//************************** helper functions **************************
/**
 * Helper functions for parsing HATEOAS RESTFUL response
 */
var RESTFUL_Response = {
    hasPage : function(response){
        return response.page != undefined;
    },
    hasNext : function(response){
        return response._links.next!= undefined;
    },
    pageNumber : function(response){
        return response.page.number;
    },
    pageSize : function(response){
        return response.page.size;
    },
    totalElements : function(response){
        return response.page.totalElements;
    },
    totalPages :  function(response){
        return response.page.totalPages;
    },
    _embedded : function(response){
        return response._embedded;
    }
}

/**
 * get data asyn with promise
 * @param {String} url
 * @param {hash} params
 * @param {Boolean} debug
 * @returns {Promise}
 */
function promiseGet(url, params,debug)

{
    if(debug == undefined){
        debug = false
    }

    if (!url.startsWith("http")) {
        url = window.location.origin + url
    }

    // Return a new promise.
    return new Promise(function(resolve, reject) {
        // Do the usual XHR stuff
        var req = new XMLHttpRequest();

        params = params || {}
        if (Object.keys(params).length > 0) {
            var params_str = '';
            Object.keys(params).forEach(function(key, index) {
                params_str = params_str + '&' + key + '=' + this[key];
            }, params);
            url = url + '?' + params_str.substring(1);
        }

        req.open('GET', url);
        if(debug){
            console.log('promise get from :' + url);
        }

        req.onload = function() {
            // This is called even on 404 etc
            // so check the status
            if (req.status == 200) {
                // Resolve the promise with the response text
                resolve(req.response);
            }
            else {
                // Otherwise reject with the status text
                // which will hopefully be a meaningful error
                reject(Error(req.statusText));
            }
        };

        // Handle network errors
        req.onerror = function() {
            reject(Error("Network Error"));
        };

        // Make the request
        req.send();
    });
}

/**
 * get data asyn with promise. This is first implemented to query efo colour but turn out to be slower
 * then making multiple GET request.
 * need to refactor to change header
 * @param {String} url
 * @param {hash} params
 * @param {Boolean} debug
 * @returns {Promise}
 */
function promisePost(url, params={}, header='application/json') {

    if (!url.startsWith("http")) {
        url = window.location.origin + url
    }

    // Return a new promise.
    return new Promise(function(resolve, reject) {
        var req = new XMLHttpRequest();

        if(header == 'application/json'){
            params = JSON.stringify(params)
        }
        if(header == 'application/x-www-form-urlencoded') {
            params = Object.keys(params).map(function (key) {
                return encodeURIComponent(key) + '=' + encodeURIComponent(params[key]);
            }).join('&');
        }


        req.open('POST', url);

        req.setRequestHeader("Content-Type", header);


        req.onload = function() {
            // This is called even on 404 etc
            // so check the status
            if (req.status == 200) {
                // Resolve the promise with the response text
                resolve(req.response);
            }
            else {
                // Otherwise reject with the status text
                // which will hopefully be a meaningful error
                reject(Error(req.statusText));
            }
        };

        // Handle network errors
        req.onerror = function() {
            reject(Error("Network Error"));
        };

        // Make the request
        req.send(params);
    });


    // //testing how fast the post method works
    // //"{"1":472.7800000000002,"10":1723.4450000000002,"50":8417.69,"100":16249.485000000002,"200":30406.670000000002,"205":28694.609999999986,"208":30608.684999999823,"209":29462.99499999988,"210":30634.399999999907}"
    // var size = [1,10,50,100,200,400,800,1000]
    // var size = [220,240,260,280,300]
    // var result = {};
    // var t0 = performance.now();
    // size.forEach(function(s){
    //     getAvailableEFOs().then(function(x) {
    //         t0 = performance.now();
    //         return getColourForEFOs(Object.keys(x).slice(0, s))
    //     }).then(console.log).then(function(){
    //         var t1 = performance.now();
    //         console.warn(s + " took " + (t1 - t0) + " milliseconds.")
    //         result[s.toString()] = t1 - t0;
    //     })
    // });
    // result
    //
    // //testing using get
    // var size = [1,10,50,100,200,400,800,1000]
    // var size = [1500]
    // var result = {};
    // var t0 = performance.now();
    // size.forEach(function(s){
    //     getAvailableEFOs().then(function(x) {
    //         t0 = performance.now();
    //         return Promise.all( Object.keys(x).slice(0, s).map(getColourForEFO))
    //     }).then(console.log).then(function(){
    //         var t1 = performance.now();
    //         console.warn(s + " took " + (t1 - t0) + " milliseconds.")
    //         result[s.toString()] = t1 - t0;
    //     })
    // });
    // result
}

/**
 * get data asyn with promise, recursively querying if the response has 'next' link.
 * The return Promise contains a hash with keys coresponding to the _embedded keys.
 * @param {String} url
 * @param {hash} params
 * @returns {Promise}
 * @example promiseGetRESTFUL_HATEOAS('http://www.ebi.ac.uk/ols/api/ontologies/efo/terms/http%253A%252F%252Fwww.ebi.ac.uk%252Fefo%252FEFO_0000400/hierarchicalDescendants',{'size':5})
 * @example promiseGetRESTFUL_HATEOAS('http://www.ebi.ac.uk/ols/api/ontologies/efo/terms/http%253A%252F%252Fwww.ebi.ac.uk%252Fefo%252FEFO_0000408/hierarchicalDescendants',{'size':1000})
 *
 */
function promiseGetRESTFUL_HATEOAS(url,params){

    //following the link recursively
    var query = function(url,params,result){
        return promiseGet(url,params).then(JSON.parse).then(function(response) {
            if(!result){
                result = []
            }
            result = result.concat(RESTFUL_Response._embedded(response));
            console.debug(RESTFUL_Response.pageNumber(response)+1 + '/' + RESTFUL_Response.totalPages(response) + ' done!')
            if(RESTFUL_Response.hasNext(response)){
                var next = response._links.next.href;
                //no need to parse params, this already included in the 'next' url
                return query(next,undefined,result);
            }
            return result
        })
    }

    //merge the result into an hash
    return query(url,params).then(function(result){
        var resultArray = {};
        result.map(function(r){
            //all keys in _embedded
            var keys = Object.keys(r);
            keys.map(function(k){
                if(!resultArray[k]){
                    resultArray[k] = [];
                }
                resultArray[k] = resultArray[k].concat(r[k])
            })
        })
        return resultArray;
    })
}

/**
 * get data asyn with promise, workout the pages and query them at the same time.
 * The return Promise contains a hash with keys coresponding to the _embedded keys.
 * @param {String} url
 * @param {hash} params
 * @returns {Promise}
 * @example promiseGetRESTFUL_fast('http://www.ebi.ac.uk/ols/api/ontologies/efo/terms/http%253A%252F%252Fwww.ebi.ac.uk%252Fefo%252FEFO_0000400/hierarchicalDescendants',{'size':5})
 * @example promiseGetRESTFUL_fast('http://www.ebi.ac.uk/ols/api/ontologies/efo/terms/http%253A%252F%252Fwww.ebi.ac.uk%252Fefo%252FEFO_0000408/hierarchicalDescendants',{'size':1000})
 */
function promiseGetRESTFUL(url,params){
    if(params == undefined){
        params = {};
    }
    return promiseGet(url,params).then(JSON.parse).then(function(response) {
        var totalPages = RESTFUL_Response.totalPages(response)
        var totalElements = RESTFUL_Response.totalElements(response)
        if(params.size == undefined){
            params.size = RESTFUL_Response.pageSize(response);
        }

        if(RESTFUL_Response.hasPage(response)){
            var allurls = $.map($(Array(totalPages)), function(val, i) {
                return url + '?page=' + i  + '&size=' +params.size;
            })
            return Promise.all(allurls.map(function(url) {
                // console.log('querying ' + url);
                return promiseGet(url).then(JSON.parse).then(function(response) {
                    console.debug(RESTFUL_Response.pageNumber(response) + ' done!');
                    return RESTFUL_Response._embedded(response);
                })
            }))
        }else{
            return response.RESTFUL_Response._embedded(response);
        }
    }).then(function(result){
        console.debug('all done');
        var resultArray = {};
        result.map(function(r){
            //all keys in _embedded
            var keys = Object.keys(r);
            keys.map(function(k){
                if(!resultArray[k]){
                    resultArray[k] = [];
                }
                resultArray[k] = resultArray[k].concat(r[k])
            })
        })
        return resultArray;
    }).then(function(resultArray){
        return resultArray;
    })
}


/**
 * OLS FUNCTIONS
 * @type {{getOntologyInfo: OLS.getOntologyInfo, getPrefix2OntologyId: OLS.getPrefix2OntologyId, getOntologyIdByPrefix: OLS.getOntologyIdByPrefix, getIriByShortForm: OLS.getIriByShortForm, getOntologyByShortForm: OLS.getOntologyByShortForm, searchOLS: OLS.searchOLS, getRelatedTerms: OLS.getRelatedTerms, getEFOInfo: OLS.getEFOInfo, getOLSLinkAPI: OLS.getOLSLinkAPI, getOLSLink: OLS.getOLSLink, getHierarchicalDescendants: OLS.getHierarchicalDescendants}}
 */
var OLS = {
    /**
     * Load ontology info from ols api.
     * Lazy load.
     * @returns {Promise}
     * @example OLS.getOntologyInfo()
     * @example http://www.ebi.ac.uk/ols/api/ontologies
     */
    getOntologyInfo:function() {
        var _parseOntologies = function(response){
            var ontologyInfo = {};
            response.ontologies.forEach(function(d) {
                ontologyInfo[d.ontologyId] = d;
            })
            return ontologyInfo;
        }

        var dataPromise = getDataFromTag(global_efo_info_tag_id, 'ontologyInfo');
        if (dataPromise == undefined) {
            //lazy load
            console.log('Loading Ontology Info...')
            dataPromise = promiseGetRESTFUL(global_ols_restful_api_ontology,
                                            {'size': 1000}).then(_parseOntologies).catch(function(err) {
                console.error('Error when loading ontology info! ' + err);
            })
            //cache data
            $(global_efo_info_tag_id).data('ontologyInfo',dataPromise);
        }else{
            console.log('Loading Ontology Info from cache.')
        }
        return dataPromise;
    },

    /**
     * Mapping for ontology name and abbrvation
     * For example, 'ordo' is used for Orphanet
     * This is needed to workout the ols link
     * Lazy load.
     * @returns {Promise} - hash containing prefix2ontology mapping
     * @example getPrefix2OntologyId()
     */
    getPrefix2OntologyId:function(){
        var dataPromise = getDataFromTag(global_efo_info_tag_id, 'prefix2ontId');
        if (dataPromise == undefined) {
            //lazy load
            console.log('Loading prefix2ontId...')
            dataPromise = OLS.getOntologyInfo().then(function(ontoInfo){
                var prefix2ontid = {}
                Object.keys(ontoInfo).forEach(function(ontId){
                    prefix2ontid[ontoInfo[ontId].config.baseUris[0].split('/').slice(-1)[0].split('_')[0]] = ontId;
                })
                return prefix2ontid;
            }).catch(function(err){
                console.error('Error when loading prefix2ontId! ' + err);
            })
            //add to tag
            $(global_efo_info_tag_id).data('prefix2ontId',dataPromise);
        }else{
            console.log('Loading prefix2ontId from cache.')
        }
        return dataPromise;
    },

    /**
     * giving a prefix, for example, EFO, return the ontology name which is 'efo'
     * Lazy load.
     * @param {String} prefix - The prefix is usually the first part of an ontology id (EFO_0000400)
     * @returns {Promise} - the name of the ontology
     * @example OLS.getOntologyIdByPrefix('Orphanet')
     */
    getOntologyIdByPrefix : function(prefix) {
        return OLS.getPrefix2OntologyId().then(function(p2o){
            //ignore case
            prefix = prefix.toLowerCase()
            var index = Object.keys(p2o).map((key)=>{return key.toLowerCase()}).indexOf(prefix)
            return Object.values(p2o)[index]
            // return p2o[prefix]
        })
    },

    /**
     * Get ontology term iri from short form. The iri looks like this:
     * http://www.ebi.ac.uk/efo/EFO_0000400
     * Lazy load.
     * @param {String} shortForm - ontology id short form, like EFO_0000400.
     * @return {Promise} getIriByShortForm('EFO_0000400')
     */
    getIriByShortForm : function(shortForm){
        var prefix = shortForm.split('_')[0];
        var id = shortForm.split('_')[1];
        var ont = OLS.getOntologyByShortForm(shortForm);

        return ont.then(function(ontName){
            return OLS.getOntologyInfo().then(function(ontologies){
                return ontologies[ontName].config.baseUris[0] + id;
            })
        }).catch(function(err){
            console.error(`Error finding iri for term ${shortForm}. ${err}`);
        })
    },

    /**
     * Get ontology name by ontology term short form.
     * For example, EFO_0000400 is an 'efo' term.
     * Lazy load.
     * @param {String} shortForm - ontology id short form, like EFO_0000400.
     * @example OLS.getOntologyByShortForm('EFO_0000400')
     */
    getOntologyByShortForm : function(shortForm){
        var prefix = shortForm.split('_')[0];
        var id = shortForm.split('_')[1];
        p = OLS.getOntologyIdByPrefix(prefix);

        return p.then(function(ontology){
            return ontology;
        }).catch(function(err){
            console.log('Error finding iri for term ' + shortForm + '. ' + err);
        })
    },

    /**
     * Search the ols seach API (solr).
     * @param {String}keyword Search keyword.
     * @param {}params - paramaters in hash. use solr parameter to filter the result.
     * @returns {Promise} - search result in Json
     * @example searchOLS('EFO_0000400',{
            'ontology': 'efo',
            'fieldList': 'iri,ontology_name,ontology_prefix,short_form,description,id,label,is_defining_ontology,obo_id,type,logical_description'
        })
     */
    searchOLS : function(keyword,params){
        params = $.extend({}, params, {'q':keyword});
        return promiseGet(global_ols_seach_api,params).then(JSON.parse).then(function(data){
            console.log("data returned by ols search:");
            console.log(data);
            return data;
        })

    },

    /**
     * Find related term for a give efo term using ols solr search api. This is primarily from the 'logical_description' field.
     * Lazy load.
     * @param {String} efoid
     * @returns {Promise} - solr result in JSON
     * @example OLS.getRelatedTerms*'EFO_0000400')
     */
    getRelatedTerms : function(efoid){
        var queryRelatedTerms = function(efoid){
            console.log('Loading related terms...')
            return OLS.searchOLS(efoid,{
                'ontology': 'efo',
                'fieldList': 'iri,ontology_name,ontology_prefix,short_form,description,id,label,is_defining_ontology,obo_id,type,logical_description'
            }).then(function(data){
                var terms = {};
                data.response.docs.forEach(function(d) {
                    d.obo_id = d.obo_id.replace(":", "_")
                    terms[d.obo_id] = d
                })
                var tmp={};
                tmp[efoid] = terms;
                return tmp;
            }).catch(function(err){
                console.error(`Error when loading related terms! ${err}`);
            })
        }

        var dataPromise = getPromiseFromTag(global_efo_info_tag_id, 'relatedEFOs');

        return dataPromise.then(function(data){
            //query if data not exist
            if($.inArray(efoid,Object.keys(data)) == -1){
                dataPromise = queryRelatedTerms(efoid);
                return dataPromise.then(function(data){
                    //add to tag
                    addPromiseToTag(global_efo_info_tag_id,dataPromise,'relatedEFOs');
                    return data[efoid];
                })
            }else{
                console.debug('Loading related terms from cache.')
                return data[efoid]
            }
        })
    },

    /**
     * Query OLS for efo term information.
     * Lazy load.
     * @param efoid
     * @returns {Promise}
     * @example OLS.getEFOInfo('EFO_0000400')
     */
    getEFOInfo : function(efoid){
        var queryEFOInfo = function(efoid){
            return OLS.getOLSLinkAPI(efoid).then(function(url){
                return promiseGet(url).then(JSON.parse).then(function(response) {
                    var tmp = {};
                    tmp[efoid] = response;
                    return tmp;
                }).catch(function(err){
                    console.debug('error when loading efo info for ' + efoid + '. ' + err);
                })
            })
        }

        var dataPromise = getPromiseFromTag(global_efo_info_tag_id, 'efoInfo');
        return dataPromise.then(function(data) {
            if ($.inArray(efoid, Object.keys(data)) == -1) {
                //efo info is not currently loaded
                console.log('Loading efoInfo for ' + efoid)
                dataPromise = queryEFOInfo(efoid);
                return dataPromise.then(function(data){
                    //add to tag
                    addPromiseToTag(global_efo_info_tag_id,dataPromise,'efoInfo');
                    return data[efoid];
                })
            }else {
                //efo colour is has been loaded perviously
                console.debug('Loading efoInfo from cache for ' + efoid);
                return data[efoid]
            }
        })
    },

    /**
     * Get the OLS API link for a give efo term.
     * Lazy load.
     * @param {String} efoid
     * @returns {Promise} a link to the ols api for the queryed term
     * @example OLS.getOLSLinkAPI('EFO_0000400')
     */
    getOLSLinkAPI : function(efoid){
        var ont = OLS.getOntologyByShortForm(efoid);
        var iri = OLS.getIriByShortForm(efoid);
        return Promise.all([ont,iri]).then(function(arrayPromise) {
            var url = global_ols_api + 'ontologies/' + arrayPromise[0] + '/terms/' +
                    encodeURIComponent(encodeURIComponent(arrayPromise[1]));
            return url
        })
    },

    /**
     * Get the OLS link for a give efo term. This is return a link to the term page.
     * @param {String} efoid
     * @returns {Promise}
     */
    getOLSLink : function(efoid){
        var ont = OLS.getOntologyByShortForm(efoid);
        var iri = OLS.getIriByShortForm(efoid);
        return Promise.all([ont,iri]).then(function(arrayPromise) {
            var url = global_ols + 'ontologies/' + arrayPromise[0] + '/terms?iri=' +
                    encodeURIComponent(arrayPromise[1]);
            return url
        })
    },

    /**
     * Query ols for hierarchical descendants for a give efo term.
     * If the data is available in the cache, load it fron cache. Other wise, query from ols.
     * Currently ols query has a size limit of 1000, and it is set to 1000 to reduce the number of query needed.
     * For query of high level efo terms, for example, EFO_0000408 disease which has 9k descendant, this will be slow.
     * @param String efoid
     * @return Promise - hash contain
     * @example OLS.getHierarchicalDescendants('EFO_0000400')
     */
    getHierarchicalDescendants : function(efoid){
        var parseResponse = function(response){
            var terms = {};
            response.terms.forEach(function(d, i) {
                terms[d.short_form] = d;
            })
            var tmp = {}
            tmp[efoid] = terms;
            return tmp;
        }
        var queryDescendant = function(efoid){
            var efoinfo = OLS.getEFOInfo(efoid)
            return efoinfo.then(function(response){
                if (response.has_children) {
                    return promiseGetRESTFUL(response._links.hierarchicalDescendants.href,{'size':1000})
                            .then(parseResponse);
                }
                else {
                    console.debug('no descendant found for ' + efoid);
                    return new Promise(function(resolve, reject) {
                        var tmp = {};
                        tmp[efoid] = {};
                        resolve(tmp);
                    })
                }
            })
        }

        var dataPromise = getPromiseFromTag(global_efo_info_tag_id, 'efoDecendants');

        return dataPromise.then(function(data) {
            if ($.inArray(efoid, Object.keys(data)) == -1) {
                //efo descendant not currently loaded
                console.debug(`Loading descendant for ${efoid}`)
                dataPromise = queryDescendant(efoid);
                return dataPromise.then(function(data){
                    //add to tag
                    addPromiseToTag(global_efo_info_tag_id,dataPromise,'efoDecendants');
                    return data[efoid];
                })
            }else {
                //efo colour is has been loaded perviously
                console.debug('Loading descendant from cache for ' + efoid);
                return data[efoid]
            }
        })
    },

    hasDescendant : function(efoid){
        return OLS.getEFOInfo(efoid).then((efoInfo)=>{
            return efoInfo.has_children;
        })
    },
}


//http://www.ebi.ac.uk/europepmc/webservices/rest/search?&query=ext_id:26831199%20src:med&resulttype=core&format=json
var EPMC = {
    /**
     * Query PMC for paper info. PMC uses their own id, but can optionally accept pubmed id.
     * @param {String} pubmed_id
     * @returns {Promise}
     * @example EPMC.getFromEPMC('')
     */
    getByPumbedId : function(pubmed_id){
        return promiseGet(global_epmc_api,
                          {
                              'query': 'ext_id:'+pubmed_id + '%20src:med',
                              'resulttype' :  'core',
                              'format' : 'json'
                          }).then(JSON.parse);
    },

    searchResult : {
        paper : function(EPMCresult){
            return EPMCresult.resultList.result[0];
        },
        citedByCount : function(EPMCresult){
            return EPMC.searchResult.paper(EPMCresult).citedByCount;
        },
        firstPublicationDate : function(EPMCresult){
            return EPMC.searchResult.paper(EPMCresult).firstPublicationDate;
        },
        journalInfo : function(EPMCresult){
            return EPMC.searchResult.paper(EPMCresult).journalInfo;
        },
        abstractText : function(EPMCresult){
            return EPMC.searchResult.paper(EPMCresult).abstractText;
        },
    },
}


/**
 * Get color for a give efo term by querying the parentMapping api.
 * The give back the prefered parent term and the predefined color of that parent term.
 * The color will be use in badges and plots, and it is consistence with the digram.
 * Lazy load.
 *
 * The loaning of the colour take a long time when the number of efo term increase.
 * Disease this (using a default colour, commented out) will increase the speed.
 * Need to optimize this for better performance.
 * @param {String} efoid
 * @returns {Promise} - json result.
 */
getColourForEFO = function(efoid) {
    // return new Promise(function(resolve){
    //     resolve({
    //                 uri: "-",
    //                 trait: "-",
    //                 parentUri: "-",
    //                 parent: "?",
    //                 colour: "#808080",
    //                 colourLabel: "?",
    //                 message: null
    //             })
    // });


    var queryColour = function(efoid){
        console.debug('Loading Colour...')
        return promiseGet(global_color_url + efoid).then(JSON.parse).then(function(response) {
            var tmp = {};
            tmp[efoid] = response;
            return tmp;
        })
    }

    var dataPromise = getPromiseFromTag(global_efo_info_tag_id, 'efo2colour');

    return dataPromise.then(function(data) {
        if ($.inArray(efoid, Object.keys(data)) == -1) {
            //efo colour is not currently loaded
            console.debug('Loading Colour...')
            dataPromise = queryColour(efoid);
            return dataPromise.then(function(data){
                //add to tag
                addPromiseToTag(global_efo_info_tag_id,dataPromise,'efo2colour');
                return data[efoid];
            })
        }else {
            //efo colour is has been loaded perviously
            console.debug('Loading Colour from cache.')
            return data[efoid]
        }
    })
}

/**
 * Use POST to query colour for multiple efo terms. This turn out to be slower
 * @param {[String]}efoids
 * @returns {Promise}
 * @example getColourForEFOs(["EFO_0000249",'abdc'])
 */
getColourForEFOs = function(efoids){
    var queryColours = function(efoids){
        console.debug('Loading Colours...')
        return promisePost(global_color_url_batch, efoids).then(JSON.parse).then(function(response){
            return hashFromArrays(efoids,response);
        });
    }

    var dataPromise = getPromiseFromTag(global_efo_info_tag_id, 'efo2colour');


    return dataPromise.then(function(data) {
//            workout what is missing
        var missing = efoids.filter(function(n) {
            return Object.keys(data).indexOf(n) == -1;
        });
//            missing=efoids;
        if(missing.length >0){
            console.debug('Loading Colour...' + missing);
            var promiseMissingColour = promisePost(global_color_url_batch, missing).then(JSON.parse).then(function(response){
                return hashFromArrays(missing,response);
            });

            return promiseMissingColour.then(function(colours){
                //add new data to the tag
                return addPromiseToTag(global_efo_info_tag_id,promiseMissingColour,'efo2colour').then(function(efo2colour){
                    return subHash(efo2colour,efoids)
                })
            })
        }else{
            console.debug('Loading Colour from cache.')
            //xintodo select those efoids
            return subHash(data,efoids)
        }
    })

}

/**
 * Query SOLR for all available efo traits.
 * @return Promise
 * @example http://localhost:8280/gwas/api/search/efotrait?&q=*:*&fq=resourcename:efotrait&group.limit=99999&fl=shortForm
 */
getAvailableEFOs=function(){
    var dataPromise = getDataFromTag(global_efo_info_tag_id,'availableEFOs');
    if(dataPromise == undefined){
        //lazy load
        console.log('Loading all available EFOs in Gwas Catalog...')
        //xintodo refactor this to use post
        dataPromise =  promisePost(window.location.pathname.split('/gwas/')[0]+'/gwas/' + 'api/search/efotrait', {
            'q': '*:*',
            'fq': 'resourcename:efotrait',
            'group.limit': 99999,
            'fl' : 'shortForm'
        },'application/x-www-form-urlencoded').then(JSON.parse).then(function(data) {
            $.each(data.grouped.resourcename.groups, function(index, group) {
                switch (group.groupValue) {
                    case "efotrait":
                        available_efos_docs = group.doclist.docs;
                        break;
                    default:
                }
            });
            var tmp = {};
            available_efos_docs.forEach(function(doc) {
                if (doc.shortForm) {
                    tmp[doc.shortForm[0]] = doc;
                }
            });

            return tmp;
        }).catch(function(err){
            console.error('Error when loading all available EFOs! ' + err);
        })

        //add to tag
        $(global_efo_info_tag_id).data('availableEFOs',dataPromise);

    }else{
        console.debug('Loading all available EFOs from cache.')
    }
    return dataPromise;
}

/**
 * Add a promise to a tag's data attribute. The data attribute contains a hash.
 * If the hash key exist, the new promise data is merged to the old one.
 * @param String tagID - the html tag used to store the data, with the '#'
 * @param Promise promise - the promise, fullfilled or pending
 * @param String key - the has key for Promise
 * @param Boolean overwriteWarning - Default value is false. if true, print log to idicate overwritting.
 * @return {Promise} - Promise containing merged data
 * @example addPromiseToTag('#efoInfo',promise,'key',false)
 */
addPromiseToTag = function(tagID, promise, key, overwriteWarning=false) {
    initInfoTag(tagID);

    var oldPromise = $(tagID).data(key) || new Promise(function(resolve){
                resolve({})
            });


    var result;
    var p = Promise.all([oldPromise,promise]).then(function(arrayPromise){
        oldData = arrayPromise[0]
        newData = arrayPromise[1]

        if(overwriteWarning){
            var overlap = Object.keys(oldData).filter(function(n) {
                return Object.keys(newData).indexOf(n) > -1;
            });
            if (overlap.length > 0) {
                overlap.forEach(function(d) {
                    console.log(d + 'will be overwritten.')
                })
            }
        }
        result = $.extend({}, oldData, newData)
        return result;
    });


    p.then(function(){
        $(tagID).data(key,p);
        console.debug('adding promise to ' + key + ' tag ' + tagID);
    })
    return p;
}

/**
 * query a promise from a tag's data attribute with a key. The data attribute contains a hash.
 * If the hash key exist, the promise is returned, otherwise, a new empty resolved promise is returned.
 * @param String tagID - the html tag used to store the data, with the '#'
 * @param String key - the has key for promise
 * @return Promise
 * @example getPromiseFromTag('#efoInfo','key')
 */
getPromiseFromTag = function(tagID,key){
    initInfoTag(tagID);
    var dataPromise =  getDataFromTag(tagID,key)
    if (dataPromise == undefined) {
        dataPromise = new Promise(function(resolve){
            resolve({});
        })
    }
    return dataPromise;
}

/**
 * Add user hash data to a tag's data attribute. The data attribute contains a hash.
 * If the hash key exist, the user hash data is merged to the existing one.
 * @param String tagID - the html tag used to store the data, with the '#'
 * @param {} hash - an hash
 * @param String key - the has key for Promise
 * @param Boolean overwriteWarning - Default value is false. if true, print log to idicate overwritting.
 * @return undefined
 * @example addDataToTag('#efoInfo',{'name':'xin'},'key',false)
 */
addDataToTag = function(tagID, hash, key, overwriteWarning) {
    initInfoTag(tagID);
    var old = $(tagID).data(key) || {};
    overwriteWarning = overwriteWarning || false;
    if (overwriteWarning) {
        var result = Object.keys(old).filter(function(n) {
            return Object.keys(hash).indexOf(n) > -1;
        });
        if (result.length > 0) {
            result.forEach(function(d) {
                console.log(d + 'will be overwritten.')
            })
        }
    }
    result = $.extend({}, old, hash)
    if (key) {
        $(tagID).data(key, result)
    }
    else {
        $(tagID).data(result)
    }

    return result;
}

/**
 * create a hidden div for cache, if there isn't already one
 */
initInfoTag = function(tagID){
    if ($(tagID).length == 0) {
        var cb = $('<div />',
                   {id: tagID.substring(1)}).css({'display': 'none'}).appendTo($("body"));
    }
}

/**
 * query data from a tag's data attribute with a key. The data attribute contains a hash.
 * If the hash key exist, the date is returned, otherwise, a new empty hash is returned.
 * @param String tagID - the html tag used to store the data, with the '#'
 * @param String key - the has key for promise
 * @return {}
 * @example getDataFromTag('#efoInfo','key')
 */
getDataFromTag = function(tagID, key) {
    initInfoTag(tagID);
    key = key || ''
    var data = $(tagID).data();
    if (key == '')
        return data
    if (Object.keys(data).indexOf(key) == -1){
        console.warn('The requested data with key ' + key + ' to ' + tagID + ' does not exist!');
        return undefined;
    }
    return data[key]
}


/**
 * remove hash data from a tag's data attribute with a key. The data attribute contains a hash.
 * If the hash key exist, it is removed and return true, otherwise, return false.
 * @param {String} tagID - the html tag used to store the data, with the '#'
 * @param {String} key - the has key for date
 * @param {String} tobeDelete - the has key to be removed
 * @return {Boolean} true if success, otherwise false.
 * @example removeDataFromTag('#efoInfo','key','xin')
 */
removeDataFromTag = function(tagID, key, tobeDelete){
    var tmp = getDataFromTag(tagID,key);
    if(tmp != undefined){
        if(Object.keys(tmp).indexOf(tobeDelete) != -1){
            delete tmp[tobeDelete];
            return true;
        }
    }
    return false;
}


/**
 * When ploting with descendants, the number of efos increase dramatically.
 * This is to remove the efos that have no annotation in GWAS catalog,
 * thus querying/ploting only those have at lease one annotation
 * @param []String toBeFilter - array of efoIDs
 * @return []String - array of efoIDs that has at least one annotation in the GWAS CATALOG
 * @example filterAvailableEFOs(['EFO_0000400','EFO_1234567'])
 */
filterAvailableEFOs = function(toBeFilter) {
    return getAvailableEFOs().then(function(availableEFOs){
        if(availableEFOs)
            return toBeFilter.filter(function(n) {
                return Object.keys(availableEFOs).indexOf(n) !== -1;
            });
    })
}


/**
 * Query oxo for cross reference of an given term.
 * Note, oxo uses 'EFO:0000400' instead of 'EFO_0000400'.
 * Lazy load.
 * @param {String} efoid
 * @returns {Promise}
 */
getOXO = function(efoid){
    var queryOXO = function(efoid){
        return promiseGet(global_oxo_api + 'mappings',
                          {
                              'fromId': efoid.replace('_',':'),
                          }
        ).then(JSON.parse).then(function(data){
            var tmp = {}
            tmp[efoid] = data;
            return tmp;
        });
    }


    var dataPromise = getPromiseFromTag(global_efo_info_tag_id, 'efo2OXO');
    return dataPromise.then(function(data) {
        if ($.inArray(efoid, Object.keys(data)) == -1) {
            //efo info is not currently loaded
            console.log('Loading oxo for ' + efoid)
            dataPromise = queryOXO(efoid);
            return dataPromise.then(function(data){
                //add to tag
                addPromiseToTag(global_efo_info_tag_id,dataPromise,'efo2OXO');
                return data[efoid];
            })
        }else {
            //efo colour is has been loaded perviously
            console.debug('Loading oxo from cache for ' + efoid);
            return data[efoid]
        }
    })
}

/**
 * The main efo is defined by the url, as a main entry of the page. It is stored in 'mainEFOInfo'
 * in the date attribute of the <global_efo_info_tag_id>
 * @return String efoID - 'EFO_0000400'
 * @example getMainEFO()
 */
getMainEFO = function(){
    return $('#query').text();
}

/**
 * Return a hash object where the hash keys are all the current selected terms
 * this are the terms in the cart, not including descendants.
 * @returns {Hash}
 */
getCurrentSelected = function(){
    return $(global_efo_info_tag_id).data('selectedEfos');
}


/**
 *
 * @returns {Hash}
 */
getAllSelected = function(){
    var all_descendants = Object.assign({},getCurrentSelected());
    return Promise.all(whichDescendant().map(OLS.getHierarchicalDescendants)).then((descendants) => {
        descendants.forEach((terms) => {
            Object.keys(terms).forEach( (term) => {
                all_descendants[term] = terms[term];
            })
        })
    }).then(()=>{
        return all_descendants;
    }).catch(function(err) {
        console.warning(`Error when trying to find all descendants with error messgae. ${err}`);
    })
}


/**
 * Ruturn an array of efos that are currently checked for descendants in the cart.
 * @returns {Array} efoids
 */
whichDescendant = function(){
    // return $(".cart-item-cb:checkbox:checked").map(function() { return this.id.split("selected_cb_")[1]; }).get();
    var tmp = getDataFromTag(global_efo_info_tag_id,'whichDescendant')
    if(tmp == undefined)
        return []

    return Object.keys(tmp);
}

/**
 * A sharable link is generated dynamically as the page changes. This function is call
 * every time the page updated, to update the link so the users can share their result.
 */
updateSharableLink = function(){
    var checked;
    if(whichDescendant().length > 0){
        var tmp = isAlwaysDescendant()? isAlwaysDescendant().toString() : whichDescendant().join(',')
        checked = '&checked=' + tmp;
    }else{
        checked = '';
    }

    $("#sharable_link").attr('value', window.location.origin + window.location.pathname + '?included=' +
                             Object.keys(getCurrentSelected()).join(',') + checked);
    hideLoadingOverLay('#sharable_link_btn');
}


/**
 * Find studies for a given efoid from the solr result(global variable), return empty hash if the solr result is undefined
 * This function search the highlighting result from solr.
 * Require solr result.
 * @param {String} efoid
 * @returns {Hash} studies
 */
findStudiesForEFO = function(efoid) {
    var studies = {};
    if(data_highlighting==undefined)
        return studies;
    Object.keys(data_highlighting).forEach(function(key) {
        if (/^study/.test(key)) {
            data_highlighting[key].efoLink.forEach(function(highlightedefo) {
                if (highlightedefo.match(/<b>(\w*_\d*)<\/b>/)[1] == efoid) {
                    studies[key] = efoid;
                }
            });
        }
    })

    $.each(data_study.docs, function(index, value) {
        if ($.inArray(value.id, Object.keys(studies)) != -1) {
            studies[value.id] = value;
        }
    })
    return studies;
}


/**
 * Find associations for a given efoid from the solr result(global variable), return empty hash if the solr result is undefined
 * This function search the highlighting result from solr.
 * Require solr result.
 * @param {String} efoid
 * @returns {Hash} associations
 */
findAssociationForEFO = function(efoid){
    var associations = {};

    if(data_highlighting==undefined)
        return associations;
    Object.keys(data_highlighting).forEach(function(key) {
        if (/^association/.test(key)) {
            data_highlighting[key].efoLink.forEach(function(highlightedefo) {
                if (highlightedefo.match(/<b>(\w*_\d*)<\/b>/)[1] == efoid) {
                    associations[key] = efoid;
                }
            });
        }
    })

    $.each(data_association.docs, function(index, value) {
        if ($.inArray(value.id, Object.keys(associations)) != -1) {
            associations[value.id] = value;
        }
    })
    return associations;
}


/**
 * Find studies for an array of efoids from the solr result(global variable), return empty hash if the solr result is undefined
 * This function search the highlighting result from solr.
 * Require solr result.
 * xintodo can be refact to findStudiesForEFO? same as the findAssociationForEFOs
 * @param {String} efoid
 * @returns {Hash} studies
 */
findStudiesForEFOs = function(efoids){
    var studies = {};
    if(data_highlighting==undefined)
        return studies;
    Object.keys(data_highlighting).forEach(function(key) {
        if (/^study/.test(key)) {
            data_highlighting[key].efoLink.forEach(function(highlightedefo) {
                if (efoids.indexOf(highlightedefo.match(/<b>(\w*_\d*)<\/b>/)[1]) != -1) {
                    studies[key] = key;
                }
            });
        }
    })

    $.each(data_study.docs, function(index, value) {
        if ($.inArray(value.id, Object.keys(studies)) != -1) {
            studies[value.id] = value;
        }
    })
    return studies;
}

/**
 * Find association for an array of efoids from the solr result(global variable), return empty hash if the solr result is undefined
 * This function search the highlighting result from solr.
 * Require solr result.
 * @param {String} efoid
 * @returns {Hash} studies
 */
findAssociationForEFOs = function(efoids){
    var associations = {};

    if(data_highlighting==undefined)
        return associations;
    Object.keys(data_highlighting).forEach(function(key) {
        if (/^association/.test(key)) {
            data_highlighting[key].efoLink.forEach(function(highlightedefo) {
                if (efoids.indexOf(highlightedefo.match(/<b>(\w*_\d*)<\/b>/)[1]) != -1) {
                    associations[key] = key;
                }
            });
        }
    })

    $.each(data_association.docs, function(index, value) {
        if ($.inArray(value.id, Object.keys(associations)) != -1) {
            associations[value.id] = value;
        }
    })
    return associations;
}


/**
 * an association can be annotated to more than one efo. find them all. This is using solr highlighting where the
 * efo for an association is wrap by <b/> tag.
 * require solr result.
 * @param {String} association_id - This is the solr id, not the rsid.
 * @param {Hash} data - solr search result, group association
 * @returns {array} efoids - an array of efoids annotated to the given association
 */
var findAllEFOsforAssociation = function(association_id,data_association) {
    var association_doc = data_association.docs.filter((association) => {
        return association.id == association_id
    })
    if(association_doc.length >0){
        //"C-reactive protein measurement|EFO_0004458|http://www.ebi.ac.uk/efo/EFO_0004458"
        return association_doc[0].efoLink.map((efoLink)=>{
            return efoLink.split('|')[1]
        })
    }else{
        return []
    }
    // var terms = data.highlighting[association_id].shortForm;
    // return terms.map((termHighlighted) => {
    //     return termHighlighted.match(/<b>(\w*_\d*)<\/b>/)[1];
    // })
    // terms.forEach(function(d, i) {
    //     //xintodo only efo?
    //     terms[i] = d.match(/<b>(\w*_\d*)<\/b>/)[1];
    // })
    // return terms;
}

/**
 * Check if an assocaition has only one efo annotated to it.
 * @param association_id
 * @param data
 * @returns {boolean}
 */
// var hasOneEFO = function(association_id,data){
//     return findAllEFOsforAssociation(association_id,data).length ==1 ;
// }


/**
 * The association can link to multiple efo in the selection list. Return all hits.
 * xintodo can this be the most significant one?
 * xintodo refactor findAllEFOsforAssociation into this.
 * @param {String} associationid
 * @param {Object} solr_highlighting - solr search highlighting object
 * @returns {[]} array of efo ids
 * @private
 */
findHighlightEFOForAssociation = function(association_id,solr_highlighting) {
    var terms = solr_highlighting[association_id].shortForm;
    return terms.map((termHighlighted) => {
        return termHighlighted.match(/<b>(\w*_\d*)<\/b>/)[1];
    })
}


/**
 * Study sorting functions.
 */
var studySorting = {

    add : function (a, b){
        return parseInt(a) + parseInt(b);
    },

    sortByPublishDate : function(array){
        var publishDate = {};

        $.each(array, function(index, value) {
            publishDate[value.id] = value.publicationDate;
        })

        var keysSorted = Object.keys(publishDate).sort(function(a, b) {
            return Date.parse(publishDate[b]) - Date.parse(publishDate[a])
        })
        return keysSorted;
    },


    sortByCatalogPublishDate : function(array){
        var catalogPublishDate = {};

        $.each(array, function(index, value) {
            catalogPublishDate[value.id] = value.catalogPublishDate;
        })

        var keysSorted = Object.keys(catalogPublishDate).sort(function(a, b) {
            return Date.parse(catalogPublishDate[b]) - Date.parse(catalogPublishDate[a])
        })
        return keysSorted;
    },


    // sortBySampleSize : function(array){
    //     // studies['study:8072'].initialSampleDescription.split(',').join('').match(/(\d*)/g).filter(Number).reduce(_add,0)
    //     // "155 Korean ancestry medication non-adherent diabetes cases, 80 Korean ancestry medication non-adherent hypertensive cases, 240 Korean ancestry medication adherent diabetes cases, 827 Korean ancestry medication adherent hypertensive cases"
    //     var sampleSize = {};
    //
    //     $.each(array, function(index, value) {
    //         var init = value.initialSampleDescription
    //         var total = init.split(',').join('').match(/(\d*)/g).filter(Number).reduce(studySorting.add,0)
    //         sampleSize[value.id] = total;
    //     })
    //
    //     //desc
    //     var keysSorted = Object.keys(sampleSize).sort(function(a, b){
    //         return parseInt(sampleSize[b]) - parseInt(sampleSize[a])
    //     })
    //     return keysSorted;
    // },


    sortByInitialSampleSize : function(array){
        // "initial|NR|NR|European|11522|NA"
        // "replication|NR|NR|European|4955|NA"
        var sampleSize = {};

        var isInitial = function(ancestryLinkString){
            return ancestryLinkString.match(/^initial/) != null
        }
        var InitialSampleSize = function(ancestryLinkString){
            return ancestryLinkString.split('|')[4]
        }



        $.each(array, function(index, value) {
            var init = value.ancestryLinks
            var total = init.filter(isInitial)
                    .map(InitialSampleSize)
                    .reduce(studySorting.add,0)
            sampleSize[index] = total;
        })

        //desc
        var keysSorted = Object.keys(sampleSize).sort(function(a, b){
            return parseInt(sampleSize[b]) - parseInt(sampleSize[a])
        });
        return keysSorted;
    },
}

/**
 * work out which study is the highlighted study for an efo trait.
 * Currently find the one with largest initial sample size.
 * This require the solr search data.
 * @param String efoid
 * @return {Object} - study_solr_doc
 * @example findHighlightedStudiesForEFO('EFO_0000400')
 */
findHighlightedStudiesForEFO = function(efoid) {
    var studies = findStudiesForEFO(efoid);
    var sorted_index = studySorting.sortByInitialSampleSize(studies);
    // return studies[sorted_index[sorted_index.length - 1]];
    return studies[sorted_index[0]];
}

/**
 * return html to display information for a data point(association) in the locus plot when moveover the datapoint
 * @param {Hash} association_solr_doc
 * @returns {String} The html as string
 */
buildLocusPlotPopoverHTML = function(association){
    _addNameValuePairHTML = function(name,value){
        if(value instanceof Array){
            value = value.join(',')
        }
        return "<div>" + name + ":<strong> " +  value + "</strong></div>";
    }
    var text = $('<div/>');
    text.append(_addNameValuePairHTML('rsid',association.rsId==undefined? '' : association.rsId[0]));
    text.append(_addNameValuePairHTML('pValue in the study',association.pval));
    text.append(_addNameValuePairHTML('Catalog Publish Date',new Date(association.catalogPublishDate).toLocaleDateString()));
    text.append(_addNameValuePairHTML('Author(s)',association.author_s));
    text.append(_addNameValuePairHTML('Chromosome',association.chromLocation));
    text.append(_addNameValuePairHTML('Countries of Recruitment',association.countriesOfRecruitment));
    text.append(_addNameValuePairHTML('Initial Sample Description',association.initialSampleDescription));
    text.append(_addNameValuePairHTML('Mapped Entrez Genes',association.entrezMappedGenes));
    text.append(_addNameValuePairHTML('Mapped EFO Trait',association.mappedLabel));
    text.append(_addNameValuePairHTML('pValue Exponent',association.pValueExponent));
    text.append(_addNameValuePairHTML('Author Reported Gene',association.reportedGene));
    text.append(_addNameValuePairHTML('PubmedId',association.pubmedId));
    text.append(_addNameValuePairHTML('Title',association.title));
    return text.prop('outerHTML');
}

/**
 * Display an overlay spinner on a tag
 * https://gasparesganga.com/labs/jquery-loading-overlay/
 * @param {String} tagID
 * @returns undefined
 * @example showLoadingOverLay('#efoInfo')
 */
showLoadingOverLay = function(tagID){
    var options = {
        color: "rgba(255, 255, 255, 0.8)",   // String
        custom: "",                // String/DOM Element/jQuery Object
        fade: [100, 1500],                      // Boolean/Integer/String/Array
        fontawesome: "",                          // String
//            image: "data:image/gif;base32,...",  // String
        imagePosition: "center center",             // String
        maxSize: "100px",                  // Integer/String
        minSize: "20px",                    // Integer/String
        resizeInterval: 10,                       // Integer
        size: "20%",                       // Integer/String
        zIndex: 1000,                        // Integer
    }
    $(tagID).LoadingOverlay("show",options);
}

/**
 * Hide an overlay spinner on a tag
 * https://gasparesganga.com/labs/jquery-loading-overlay/
 * @param {String} tagID
 * @returns undefined
 * @example hideLoadingOverLay('#efoInfo')
 */
hideLoadingOverLay = function(tagID){
    return $(tagID).LoadingOverlay("hide", true);
}

/**
 * Check if an efo in the cart has been checked for descendants.
 * @param {String} efoid
 * @returns {boolean}
 */
isDescendantRequired = function(efoid){
    return whichDescendant().indexOf(efoid) != -1
}

/**
 * check if we should always include descendants for the efos in the cart
 * @returns {boolean}
 */
isAlwaysDescendant = function(){
    return $("#cb-query-include-descendants").is(":checked");
}

/**
 * check if the efo is the mainEFO. mainEFO is defined by the url.
 * @returns {boolean}
 */
isMainEFO = function(efoid){
    return efoid == getMainEFO();
}




/**
 * subset a hash on keys provided.
 * @param {String} hash
 * @param {[]} keys
 * @returns {{}}
 * @example subHash({'a':1,'b':2,'c':3},['a','c'])    //{'a':1,'c':3}
 */
var subHash = function(hash, keys){
    var tmp = {};
    var subHashKeys =  Object.keys(hash).map(function(n) {
        if(keys.indexOf(n) !== -1){
            tmp[n] = hash[n];
        }
    })
    return tmp;
}

/**
 * A helper function to re-index an array of hash, with the key being one of the hash value.
 * @param indexStr - one of the key in each of the arrayofHash
 * @param arrayofHash
 * @returns {{}}
 * @example addIndexToArrayOfHash('name',[{'name':'a','age':1},{'name':'b','age':2}])   // {a:{'name':'a','age':1},'b':{'name':'b','age':2}}
 *
 */
addIndexToArrayOfHash = function(indexStr,arrayofHash){
    var hash = {}
    arrayofHash.map(function(d){
        hash[d[indexStr]] = d;
    })
    return hash;
}


/**
 * A helper function to make a hash with two arrays, one being keys and one being values.
 *
 * @param array
 * @param keys
 * @returns {{}}
 * @example hashFromArrays(['a','b','c'],[1,2,3])       //{'a':1,'b':2,'c':3}
 */
hashFromArrays = function(arr_keys,arr_values){
    if(arr_keys.length != arr_values.length){
        console.error(`the number of keys ${arr_keys} must be the same of the number of values ${arr_values}!`)
        return undefined;
    }
    var hash = {}
    arr_keys.forEach(function(i) {
        hash[i] = arr_values.shift();
    })
    return hash;
}


// Create a popover to display content
createPopover = function(label,header,content){
    var content_text = $('<a></a>');
    content_text.html(label);
    content_text.popover({title: header, content: content, animation : true,
                             delay: {show: 100, hide: 200},
                             placement :'auto right',
                             trigger : 'hover',
                             html: true,
                             template: '<div class="popover" role="tooltip" style="width: 100%;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"><div class="data-content"></div></div></div>'
                         });
    return content_text;
}








/**
 * debug function
 */
_peak = function() {
    console.log($(global_efo_info_tag_id).data());
}

/**
 * debug function
 */
_cleanDataTag = function(tagID){
    tagID=tagID || global_efo_info_tag_id;
    Object.keys($(tagID).data()).forEach(function(i){
        $(tagID).removeData(i)
    })
}

