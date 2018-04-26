/** DRY. From Xin original code. We must refactor all these 'action'result.js in a common way! */

var EPMC_URL = "http://www.europepmc.org/abstract/MED/";
var NCBI_URL = "https://www.ncbi.nlm.nih.gov/pubmed/?term=";
var global_fl;
var global_raw;

global_fl = 'pubmedId,title,author_s,orcid_s,publication,publicationDate,catalogPublishDate,authorsList,' +
    'initialSampleDescription,replicateSampleDescription,ancestralGroups,countriesOfRecruitment,' +
    'ancestryLinks,' +
    'traitName,mappedLabel,mappedUri,traitUri,shortForm,' +
    'label,' + 'efoLink,parent,id,resourcename,';
global_fl = global_fl + 'riskFrequency,qualifier,pValueMantissa,pValueExponent,snpInteraction,multiSnpHaplotype,rsId,strongestAllele,context,region,entrezMappedGenes,reportedGene,merged,currentSnp,studyId,chromosomeName,chromosomePosition,chromLocation,positionLinks,author_s,publication,publicationDate,catalogPublishDate,publicationLink,accessionId,initialSampleDescription,replicateSampleDescription,ancestralGroups,countriesOfRecruitment,numberOfIndividuals,traitName_s,mappedLabel,mappedUri,traitUri,shortForm,labelda,synonym,efoLink,id,resourcename'
global_raw = 'fq:resourcename:association or resourcename:study'


/**
 * Other global setting
 */
var pageRowLimit = 5;

$(document).ready(() => {

//jump to the top of the page
$('html,body').scrollTop(0);

var searchTerm = getTextToSearch('#query');

console.log("Loading search module!");
if (searchTerm != '') {
    console.log("Start search for the text " + searchTerm);
    var elements = {};
    searchTerm.split(',').forEach((term) => {
        elements[term] = term;
});
    //first load
    console.log(elements);
    executeQuery(elements, true);
}
});

/**
 * The elem to search is defined by the url, as a main entry of the page. It is stored in the div id
 * in the date attribute of the <global_elem_info_tag_id>`
 * @return Eg. String efoID - 'EFO_0000400'
 * @example getElemToSearch()
 */

getTextToSearch = function(divId) {
    return $(divId).text();
}

executeQuery = function(data = {}, initLoad = false) {
    console.log("executeQuery");
    updatePage(initLoad);
}



updatePage = function(initLoad = false) {
    
    //start spinner. The spinner will be stoped whenever the data is ready, thus closed by the coresponding data loading function.
    if (initLoad) {
        showLoadingOverLay('#summary-panel-loading');
        //            showLoadingOverLay('#highlight-study-panel-loading');
    }
    showLoadingOverLay('#association-table-loading');
    
    var main = getTextToSearch('#query');
    
    //******************************
    // when solr data ready, process solr data and update badges in the selection cart
    //******************************
    var solrPromise = getDataSolr(main, initLoad);
    
}


/**
 * Make solr query.
 * @param {String} mainEFO
 * @param {[]String} additionalEFO
 * @param {[]String} descendants
 * @param {Boolean} initLoad
 * @returns {Promise}
 */
function getDataSolr(main, initLoad = false) {
    // initLoad will be pass to processEfotraitData, controlling whether to upload the triat information(initload)
    // or just reload the tables(adding another efo term)
    
    var searchQuery = main;
    console.log("Solr research request received for " + searchQuery);
    //Please use the contextPath !
    var URLService = contextPath+'api/search/advancefilter';
    return promisePost(URLService, {
        'q': searchQuery,
        'max': 99999,
        'group.limit': 99999,
        'group.field': 'resourcename',
        'facet.field': 'resourcename',
        'hl.fl': 'shortForm,efoLink',
        'hl.snippets': 100,
        'fl': global_fl == undefined ? '*' : global_fl,
        // 'fq' : global_fq == undefined ? '*:*':global_fq,
        'raw': global_raw == undefined ? '' : global_raw,
    }, 'application/x-www-form-urlencoded').then(JSON.parse).then(function(data) {
        processSolrData(data, initLoad);
    
        displayDatatableAssociations(data_association.docs);
        displaySummaryStudy(data_study.docs);

        //work out highlight study
        //var highlightedStudy = findHighlightedStudiesForEFO(getMainEFO());
        //displayHighlightedStudy(highlightedStudy);
        //display summary information like 'EFO trait first reported in GWAS Catalog in 2007, 5 studies report this efotrait'
        //getSummary(findStudiesForEFO(getMainEFO()));
        initOrchidClaimData(data_study.docs);
        initOrcidClaiming(data_study.docs);
        console.log("Solr research done for " + searchQuery);
        return data;
    }).catch(function(err) {
        console.error('Error when seaching solr for' + searchQuery + '. ' + err);
        throw (err);
    })
}

/**
 * Parse the Solr results and display the data on the HTML page
 * @param {{}} data - solr result
 * @param {Boolean} initLoad
 */

/**
 * Parse the Solr results and display the data on the HTML page
 * @param {{}} data - solr result
 * @param {Boolean} initLoad
 */
function processSolrData(data, initLoad = false) {
    var isInCatalog = true;
    
    data_association = [];
    data_study = [];
    data_association.docs = [];
    data_study.docs = [];
    
    if (data.grouped.resourcename.matches == 0) {
        isInCatalog = false;
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
            //Xin: not sure we need this!
           case "diseasetrait":
               data_diseasetrait = group.doclist;
               break;
           default:
        }
    });
    
    //remove association that annotated with efos which are not in the list
    var remove = Promise.resolve();
    
    remove.then(() => {
        //If no solr return,greate a fake empyt array so tables/plot are empty
        if (!isInCatalog) {
            data_association.docs = []
            data_study.docs = []
        }
    })

}


function initOrchidClaimData(study_info) {
   // var global --- TO REFACTOR
    
    //ORCID Data Object
    //var orchidDescriptionArr = [
    //    "Method: ",
    //    "Deposited: 09 Feb 2015",
    //   "Released: 27 Jan 2016"
    //]
    var year_publication = "";
    if (study_info[0] != undefined) {
        year_publication = study_info[0].publicationDate.substring(0, 4);
    }
    orcidClaimData = {
        title : study_info[0].title,
        workType: 'data-set',
        publicationYear: year_publication,
        url : 'http://www.ebi.ac.uk'+contextPath+'studies/'+study_info[0].accessionId,
        workExternalIdentifiers : [ {
            workExternalIdentifierType : "other-id",
            workExternalIdentifierId : study_info[0].accessionId,
        }],
        shortDescription: 'Approved by user using GWAS CATALOG',
        //   shortDescription: orchidDescriptionArr.join(", "),
        clientDbName : 'GWAS-CATALOG'
   }
    

   

}
/**
 * display study summary
 * @param {Object} data - study solr docs
 * @param {Boolean} cleanBeforeInsert
 */
function displaySummaryStudy(data, clearBeforeInsert) {
    var study_count = data.length;
    var study = data[0];
    var first_author = study.author_s;
    if ('orcid_s' in study) {
        // the variable is defined
        var orchid = create_orcid_link(study.orcid_s, 16);
        first_author = first_author + orchid;
    }
    $("#study-author").html(first_author);
    $("#study-title").html(study.title);
    $("#study-journal").html(study.publication);
    var pubmedIdLink = '<a href="'+contextPath+'publications/'+study.pubmedId+'"><span class="gwas-icon-GWAS_Publication_2017"></span>&nbsp;'+study.pubmedId+'</a>';
    $("#study-pubmedid").html(pubmedIdLink);
    $("#study-datepublication").html(study.publicationDate.split('T')[0]);
    if ('authorsList' in study) {
        console.log(study.authorsList);
        $("#study-authors-list").html(displayAuthorsListAsList(study.authorsList));
    }
    $("#pubmedid_button").attr('onclick', "window.open('" + NCBI_URL + study.pubmedId + "',    '_blank')");
    hideLoadingOverLay('#summary-panel-loading');
    
}

function initOrcidClaiming() {
    console.log("initOrcidClaiming");
    //Orchid Data claiming details
    try {
        thorApplicationNamespace.createWorkOrcId(orcidClaimData.title, orcidClaimData.workType, orcidClaimData.publicationYear, orcidClaimData.url, orcidClaimData.shortDescription, orcidClaimData.clientDbName);
        thorApplicationNamespace.addWorkIdentifier(orcidClaimData.workExternalIdentifiers[0].workExternalIdentifierType, orcidClaimData.workExternalIdentifiers[0].workExternalIdentifierId);
        thorApplicationNamespace.loadClaimingInfo();
        thorApplicationNamespace.loadLinks();
    } catch (ignore) {}
    
    
}