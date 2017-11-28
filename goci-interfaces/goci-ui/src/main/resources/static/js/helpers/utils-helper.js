/** DRY. From Xin original code. Helper to share between modules! */
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


// Display the input array as a HTML list
function displayAuthorsListAsList(data_array) {
    var data_text = '';
    
    if (data_array) {
        if (data_array.length == 1) {
            data_text = data_array[0];
        }
        else if (data_array.length > 1) {
            var list = "";
            $.each(data_array, function(index, value) {
                var author_orchid = value.split(" | ");
                if(typeof author_orchid[1] === 'undefined') {
                    list = list.concat(author_orchid[0] + ", ");
                }
                else {
                    var orchid = '&nbsp;<a href="https://orcid.org/'+author_orchid[1]+'" target="_blank"><img alt="Orcid profile" src="https://orcid.org/sites/default/files/images/orcid_16x16.png" width="16" height="16" hspace="4" /></a>';
                    list = list.concat(author_orchid[0] + orchid +", ");
                }
                
                
            });
            data_text = list.slice(0, -2);
        }
    }
    return data_text;
}

// Toogle a table and scroll to it.
function toggle_and_scroll (id) {
    if ($(id +" div:first-child").find('span').hasClass('panel-collapsed')) {
        toggleSidebar(id + ' span.clickable');
    }
    $(window).scrollTop($(id).offset().top - 70);
}

// Create a list item tag (<li>) and add content in it
function newItem(content) {
    return $("<li></li>").html(content);
}