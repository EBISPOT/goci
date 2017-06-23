/**
 * Created by xin on 19/04/2017.
 * From an EFOtrait, query solr, parse solr search result and display in page
 */


/**
 * Other global setting
 */
var list_min = 2;
var EPMC_URL = "http://www.europepmc.org/abstract/MED/";
var OLS  = "http://www.ebi.ac.uk/ols/search?q=";
var ENSVAR = "http://www.ensembl.org/Homo_sapiens/Variation/";
var DBSNP  = "http://www.ncbi.nlm.nih.gov/SNP/snp_ref.cgi?rs=";
var UCSC   = "https://genome.ucsc.edu/cgi-bin/hgTracks?hgFind.matches=";
var ENS_SHARE_LINK = 'Variant_specific_location_link/97NKbgkp09vPRy1xXwnqG1x6KGgQ8s7S';
var CONTEXT_RANGE = 500;


// $(document).ready(function() {
//     if (window.location.pathname.indexOf("beta") != -1) {
//         $('#beta-icon').show();
//     }
//     var searchTerm = $('#query').text();
//     console.log("Loading search module!");
//     console.log("efoTrait: " + searchTerm);
//     if (searchTerm != '') {
//         console.log("Start search for the efotrait " + searchTerm);
//         getEfoTraitData(searchTerm);
//     }
//     //load the data from html tag and render
//     // getVariantInfoFromEnsembl(searchTerm);
// });

// function getEfoTraitData(efotraitId) {
//     console.log("Solr research request received for " + efotraitId);
// //        setState(SearchState.LOADING);
//     $.getJSON('/gwas/api/search/efotrait',
//               {
//                   'q': efotraitId,
//                   'max': 1000
//               })
//             .done(function(data) {
//                 console.log(data);
// $("#hiddenresult").append(newItem(aaaa))
//                 // $("#header ul").append('<li><a href="/user/messages"><span class="tab">Message Center</span></a></li>');
//                 //
//                 // $("#queryresultJsonString").html=JSON.stringify(data, null, 0)
//
// //                    document.write(data);
// //                     document.getElementById("json").innerHTML = JSON.stringify(data, undefined, 2);
// //                    window.open("data:text/json," + encodeURIComponent(JSON.stringify(data, undefined, 2)),
// //                                "_blank");
//                 processEfotraitData(data, efotraitId);
//             });
//     //serilize the data in html tag and save it in a tag for visulization
//     console.log("Solr research done for " + efotraitId);
// }

// // Parse the Solr results and display the data on the HTML page
// function processEfotraitData(data, efotraitId) {
//     // Check if Solr returns some results
//     if (data.grouped.resourcename.matches == 0) {
//         $('#lower_container').html("<h2>The efotrait <em>" + efotraitId +
//                                    "</em> cannot be found in the GWAS Catalog database</h2>");
//     }
//     else {
//         //split the solr search by groups
//         var data_efo;
//         var data_study;
//         var data_association;
//         var data_diseasetrait;
//         var data_facet = data.facet_counts.facet_fields.resourcename;
//
//         $.each(data.grouped.resourcename.groups, function(index, group) {
//             if (group.groupValue == "efotrait") {
//                 data_efo = group.doclist;
//                 console.log(data_efo);
//             }
//             if (group.groupValue == "study") {
//                 data_study = group.doclist;
//                 console.log(data_study);
//             }
//             if (group.groupValue == "association") {
//                 data_association = group.doclist;
//                 console.log(data_association);
//             }
//             if (group.groupValue == "diseasetrait") {
//                 data_diseasetrait = group.doclist;
//                 console.log(data_diseasetrait);
//             }
//         })
//
//         //processing solr search data
//         // Variant summary panel
//         getEfoTraitInfo(data_efo.docs, efotraitId);
//         getSummary(data_study);
//         // External links panel
// //            getLinkButtons(data_facet,efotraitId);
// //            // Associations table
//         displayEfotraitAssociations(data_association.docs);
// //            // Studies table
//         displayEfotraitStudies(data_study.docs);
// //            // Traits table
// //            getEfotraitDiseasetrait(data_diseasetrait.docs);
// //
// //            //downloads link
// //            setDownloadLink(rsId);
//     }
// }


// Generate the summary sentence, at the bottom of the summary panel
// xintodo done
function getSummary(data) {
    var first_report  = getFirstReportYear(data);
    var count_studies = countStudies(data);

    if (count_studies == 0) {
        count_studies = '';
    }
    else if (count_studies == 1) {
        count_studies = '<b>1</b> study reports this efotrait';
    }
    else if (count_studies > 1) {
        count_studies = '<a class="inpage-link" onclick="toggle_and_scroll('+"'"+'#study_panel'+"'"+')"><b>'+count_studies+'</b> studies report this efotrait</a>';
    }

    if (first_report != '' && count_studies != '') {
        first_report += ', ';
    }
    $("#efotrait-summary-content").html(first_report+count_studies);
    return first_report+count_studies;
}

// Create external link buttons
function getLinkButtons (data,rsId) {
    var data_sample = data[0];
    var chr = data_sample.chromosomeName[0];
    var pos = data_sample.chromosomePosition[0];
    var pos_start = pos - CONTEXT_RANGE;
    if (pos_start < 1) {
        pos_start = 1;
    }
    var pos_end = pos + CONTEXT_RANGE;
    var location = chr+':'+pos_start+'-'+pos_end;
    var ens_g_context = 'http://www.ensembl.org/Homo_sapiens/Location/View?db=core;r='+location+';v='+rsId+';share_config='+ENS_SHARE_LINK;

    // Summary panel
    $("#ensembl_button").attr('onclick',     "window.open('"+ENSVAR+"Explore?v="+rsId+"',    '_blank')");
    $("#ensembl_gc_button").attr('onclick',  "window.open('"+ens_g_context+"',               '_blank')");
    $("#ensembl_phe_button").attr('onclick', "window.open('"+ENSVAR+"Phenotype?v="+rsId+"',  '_blank')");
    $("#ensembl_gr_button").attr('onclick',  "window.open('"+ENSVAR+"Mappings?v="+rsId+"',   '_blank')");
    $("#ensembl_pg_button").attr('onclick',  "window.open('"+ENSVAR+"Population?v="+rsId+"', '_blank')");
    $("#ensembl_cit_button").attr('onclick', "window.open('"+ENSVAR+"Citations?v="+rsId+"',  '_blank')");
    $("#dbsnp_button").attr('onclick',       "window.open('"+DBSNP+rsId+"',                  '_blank')");
    $("#ucsc_button").attr('onclick',        "window.open('"+UCSC+rsId+"',                   '_blank')");
    // LD
    $("#ens_ld_button").attr('onclick',  "window.open('"+ENSVAR+"HighLD?v="+rsId+"', '_blank')");
}

// Pick up the most recent publication year
// xintodo This should be the mainEFO, not including the additional efos
function getFirstReportYear(data) {
    var study_date = '';
    $.each(data, function(index,asso) {
        // var p_date = asso.publicationDate;
        var p_date = asso.catalogPublishDate;
        var year = p_date.split('-')[0];
        if (year < study_date || study_date == '') {
            study_date = year;
        }
    });
    if (study_date != '') {
        study_date = "EFO trait first reported in GWAS Catalog in <b>" + study_date + "</b>";
    }
    return study_date;
}

// Pick up the most recent publication year
// xintodo done
function countStudies(data) {
    var study_text = '';
    var studies = [];
    $.each(data, function(index,asso) {
        var study_id = asso.id;
        if (jQuery.inArray(study_id, studies) == -1) {
            studies.push(study_id);
        }
    });
    return studies.length;
}

// Display the input array as a HTML list
function displayArrayAsList(data_array) {
    var data_text = '';

    if (data_array) {
        if (data_array.length == 1) {
            data_text = data_array[0];
        }
        else if (data_array.length > 1) {
            var list = $('<ul/>');
            list.css('padding-left', '0px');
            $.each(data_array, function(index, value) {
                list.append(newItem(value));
            });
            data_text = list;
        }
    }
    return data_text;
}

// Generate an internal link to the GWAS search page
function setQueryUrl(query, label) {
    if (!label) {
        label = query;
    }
    return '<a href="/gwas/search?query='+query+'">'+label+'</a>';
}

// Update the display of the variant functional class
function variationClassLabel(label) {
    var new_label = label.replace(/_/g, ' '); // Replace all the underscores by a space
    return new_label.charAt(0).toUpperCase() + new_label.slice(1);
}


// Generate an external link (text + icon)
function setExternalLink(url,label) {
    return '<a href="'+url+'" target="_blank">'+label+'<span class="glyphicon glyphicon-new-window external-link-smaller"></span></a>';

}

// Generate an external link (text only)
function setExternalLinkText(url,label) {
    return '<a href="'+url+'" target="_blank">'+label+'</a>';
}

// Generate an external link (icon only)
function setExternalLinkIcon(url) {
    return '<a href="'+url+'" class="glyphicon glyphicon-new-window external-link" title="External link" target="_blank"></a>';
}

// Create a cell tag (<td>) and add content in it
function newCell(content) {
    return $("<td></td>").html(content);
}

// Create a list item tag (<li>) and add content in it
function newItem(content) {
    return $("<li></li>").html(content);
}

// Create a hidden list of items - Used when we have to display a more or less long list of information
function longContentList (content_id, list, type) {

    var content_text = $('<span></span>');
    content_text.css('padding-right', '8px');
    content_text.html('<b>'+list.length+'</b> '+type);

    var content_div  = $('<div></div>');
    content_div.attr('id', content_id);
    content_div.addClass('collapse');

    var content_list = $('<ul></ul>');
    content_list.css('padding-left', '25px');
    content_list.css('padding-top', '6px');
    $.each(list, function(index, item) {
        content_list.append(newItem(item));
    });
    content_div.append(content_list);

    var container = $('<div></div>');
    container.append(content_text);
    container.append(showHideDiv(content_id));
    container.append(content_div);

    return container;
}

// Create a hidden list of items - Used when we have to display a more or less long list of information
function longContent (content_id, str, label) {

    var content_text = $('<span></span>');
    content_text.css('padding-right', '8px');
    content_text.html(label);

    var content_div  = $('<div></div>');
    content_div.attr('id', content_id);
    content_div.addClass('collapse');

    var content_list = $('<p></p>');
    content_list.css('padding-left', '25px');
    content_list.css('padding-top', '6px');
    content_list.html(str);

    content_div.append(content_list);

    var container = $('<div></div>');
    container.append(content_text);
    container.append(showHideDiv(content_id));
    container.append(content_div);

    return container;
}


// Create a button to show/hide content
function showHideDiv(div_id) {
    var div_button = $("<button></button>");
    div_button.attr('title', 'Click to show/hide more information');
    div_button.attr('id', 'button-'+div_id);
    div_button.attr('onclick', 'toggleDiv("'+div_id+'")');
    div_button.addClass("btn btn-default btn-xs btn-study");
    div_button.html('<span class="glyphicon glyphicon-plus tgb"></span>');

    return div_button;
}

// Toogle a table and scroll to it.
function toggle_and_scroll (id) {
    if ($(id +" div:first-child").find('span').hasClass('panel-collapsed')) {
        toggleSidebar(id + ' span.clickable');
    }
    $(window).scrollTop($(id).offset().top - 70);
}


