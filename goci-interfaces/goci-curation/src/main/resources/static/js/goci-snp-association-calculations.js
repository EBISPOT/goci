// Calculates various values used in association form

// Calculate OR per copy num
$(document).ready(function() {
    $("#calculate-or").click(function() {
        var orPerCopyRecip = document.forms["snp-association-form"]["orPerCopyRecip"].value;
        calculateOrPerCopyNum(orPerCopyRecip);
    });
});

function calculateOrPerCopyNum(orPerCopyRecip) {
    var orPerCopyNum;
    orPerCopyNum=Math.round(100/orPerCopyRecip)/100;
    console.log(orPerCopyNum);
    document.getElementById("orPerCopyNum").value = orPerCopyNum;
}

// Build orPerCopyRange
$(document).ready(function() {
    $("#calculate-or-range").click(function() {
        var orPerCopyStdError = document.forms["snp-association-form"]["orPerCopyStdError"].value;
        var orPerCopyNum = document.forms["snp-association-form"]["orPerCopyNum"].value;
        setRange(orPerCopyStdError, orPerCopyNum);
    });
});


/* This method calculates the confidence interval based on the standard error
 taken from Kent's Coldfusion code.*/

function setRange(orPerCopyStdError, orPerCopyNum){
    var delta = Math.round(100000 * orPerCopyStdError * 1.96) / 100000;
    var ORpercopylow = orPerCopyNum - delta;
    var ORpercopyhigh = (1 * orPerCopyNum) + delta;

    if (ORpercopylow < .001) {
        var ORpercopylow2 = ORpercopylow.toFixed(5);
        var ORpercopyhigh2 = ORpercopyhigh.toFixed(5);
    }
    else if (ORpercopylow < .01) {
        ORpercopylow2 = ORpercopylow.toFixed(4);
        ORpercopyhigh2 = ORpercopyhigh.toFixed(4);
    }
    else if (ORpercopylow < .1) {
        ORpercopylow2 = ORpercopylow.toFixed(3);
        ORpercopyhigh2 = ORpercopyhigh.toFixed(3);
    }
    else {
        ORpercopylow2 = ORpercopylow.toFixed(2);
        ORpercopyhigh2 = ORpercopyhigh.toFixed(2);
    }

    document.getElementById("orPerCopyRange").value = '[' + ORpercopylow2 + '-' + ORpercopyhigh2 + ']';
}

// Calculate reciprocal orPerCopyRange

$(document).ready(function() {
    $("#calculate-recip-range").click(function() {
        var orPerCopyRecipRange = document.forms["snp-association-form"]["orPerCopyRecipRange"].value;

        calculateOrPerCopyRange(orPerCopyRecipRange);
    });
});


function calculateOrPerCopyRange(orPerCopyRecipRange) {
    orPerCopyRecipRange = orPerCopyRecipRange.replace("[", "");
    orPerCopyRecipRange = orPerCopyRecipRange.replace("]", "");

    var ci = orPerCopyRecipRange.split("-");

    var one = parseFloat(ci[0].trim());
    var two = parseFloat(ci[1].trim());

    var high = ((100 / one) / 100);
    var low = ((100 / two) / 100);

    var lowval;
    var highval;

    if (low < 0.001) {
        lowval = parseFloat(low).toFixed(5);
        highval = parseFloat(high).toFixed(5);
    } else if (low >= 0.001 && low < 0.01) {
        lowval = parseFloat(low).toFixed(4);
        highval = parseFloat(high).toFixed(4);
    } else if (low >= 0.01 && low < 0.1) {
        lowval = parseFloat(low).toFixed(3);

        highval = parseFloat(high).toFixed(3);
    } else {
        lowval = parseFloat(low).toFixed(2);
        highval = parseFloat(high).toFixed(2);
    }

    document.getElementById("orPerCopyRange").value = '[' + lowval + '-' + highval + ']';

}

$(document).ready(function() {

    var success_class = "alert alert-success col-md-offset-2 col-md-10";
    var error_class = "alert alert-danger col-md-offset-2 col-md-10";

    // Variant validation
    $("#validation_button").click(function() {
        if ($("#snpValidated").val() != "true" ||  $("#snp_id").val() != $("#snp").val()) {
            var rest_url = "http://rest.ensembl.org/variation/human/" + $("#snp_id").val();
            $("#snpValidationStatus").html("");
            $.ajax({
                type: "GET",
                dataType: "json",
                url: rest_url,
                error: function (jqXHR, status, errorThrown) {
                    $("#snpValidationStatus").append("<div class=\""+error_class+"\">"+$("#snp_id").val() + ": " +status+" ("+errorThrown+")"+"</div>");
                },
                success: function(result) {
                    $("#snpValidated").val("true");
                    $("#snp").val($("#snp_id").val());
                    $("#snpValidationStatus").append("<div class=\""+success_class+"\">" + $("#snp_id").val() + ": validated</div>");

                    // SNP has been merged
                    if (result.name==$("#snp_id").val()) {
                        $("#merged").val(0);
                    }
                    else {
                        $("#merged").val(1);
                    }

                    if (result.mappings) {
                        // Empty the mapping table
                        $("#mapping_table > tbody").html("");
                        var row_id = 1;
                        var row_prefix = "mapping_tr_";

                        // Populate the mapping table
                        for (i in result.mappings) {
                            var location = result.mappings[i].location.split(":");
                            var chr = location[0];
                            var position = location[1].split("-")[0];
                            var band = "Unknown";
                            /*var rest_cytogenytic = "http://rest.ensembl.org/overlap/region/human/"+chr+":"+position+"-"+position;
                            $.ajax({
                                type: "GET",
                                dataType: "json",
                                async: false, // Result needs to be get before going further in the method
                                data: {"feature" : "band"},
                                url: rest_cytogenytic,
                                error: function(jqXHR, status, errorThrown) {
                                    $("#snpValidationStatus").append("<div class=\""+error_class+"\">"+$("#snp_id").val() + ": " +status+" - Cytogenetic band not found ("+errorThrown+")"+"</div>");
                                },
                                success: function(result_band) {
                                    band = chr+result_band[0].id;
                                }
                            });*/

                            var newrow = "<tr id=\"mapping_tr_"+row_id+"\">";
                            newrow = newrow + "<td><span>" + $("#snp_id").val() + "</span></td>";           // SNP
                            newrow = newrow + "<td><input type=\"text\" value=\"" + band + "\"\></td>";     // Region
                            newrow = newrow + "<td><input type=\"text\" value=\"" + chr + "\"\></td>";      // Chromosome
                            newrow = newrow + "<td><input type=\"text\" value=\"" + position + "\"\></td>"; // Position
                            newrow = newrow + "<td><div class=\"btn btn-danger\">Delete</div></td>";
                            //newrow = newrow + "<td><div class=\"btn btn-danger\" onclick=\"javascript:delete_row(\'"+row_prefix+"\',\'"+row_id+"\')\">Delete</div></td>";
                            newrow = newrow + "</tr>";
                            $("#mapping_table > tbody").append(newrow);
                            row_id++;

                            // Populate Genomic context
                            getGenomicContext(chr,position);
                        }
                    }
                }
            });
            $("#snpValidationStatus").show();
        }
    });

    // Gene validation
    $("#gene_validation_button").click(function() {
        var gene_string = $("#authorgenes").val();
        var genes = gene_string.split(",");

        var rest_url = "http://rest.ensembl.org/xrefs/symbol/homo_sapiens/";

        for (i in genes) {
            var gene = genes[i];
            var rest_full_url = rest_url + gene;
            $.ajax({
                type: "GET",
                dataType: "json",
                async: false, // Avoid weird results
                url: rest_full_url,
                error: function(jqXHR, status, errorThrown) {
                    $(".tag:contains('"+gene+"')").css({backgroundColor: "#A00"});
                },
                success: function(result) {
                    if (result.length > 0 && result != []) {
                        $(".tag:contains('"+gene+"')").css({backgroundColor: "#0A0"});
                    }
                    else {
                        $(".tag:contains('"+gene+"')").css({backgroundColor: "#A00"});
                    }
                }
            });
        }
    });

    // Switch between variant association form and variant mapping form
    $("#mapping_tab").click(function() {
        var mapping_parent = $("#mapping_tab").parent();
        var association_parent = $("#association_tab").parent();
        var active_class = "active";
        if (association_parent.hasClass(active_class)) {
            association_parent.removeClass(active_class);
            mapping_parent.addClass(active_class);

            $("#association_div").hide();
            $("#mapping_div").show();
        }
    });
    $("#association_tab").click(function() {
        var association_parent = $("#association_tab").parent();
        var mapping_parent = $("#mapping_tab").parent();
        var active_class = "active";
        if (mapping_parent.hasClass(active_class)) {
            mapping_parent.removeClass(active_class);
            association_parent.addClass(active_class);

            $("#mapping_div").hide();
            $("#association_div").show();
        }
    });
});

/*function delete_row(prefix,suffix) {
    $("#"+prefix+suffix).remove();
}*/


// Load the genomic context of a variant (overlap, 100kb upstream, 100kb downstream)
function getGenomicContext(chr,position) {

    var rest_url = "http://rest.ensembl.org/overlap/region/homo_sapiens/";
    var rest_opt = {"feature" : "gene"};

    // Check if overlap gene
    var rest_full_url_1 = rest_url + chr + ':'+ position + '-' + position;
    var overlap_list = [];
    $("#context_table > tbody").html("");
    $("#contextValidationStatus").html("");
    $.ajax({
        type: "GET",
        dataType: "json",
        async: false, // Result needs to be get before going further in the method
        data: rest_opt,
        url: rest_full_url_1,
        error: function(jqXHR, status, errorThrown) {
            $("#contextValidationStatus").append("<div class=\""+error_class+"\">" + status + ": Issue with the gene overlap call ("+errorThrown+")</div>");
        },
        success: function(result) {
            if (result.length > 0 && result != []) {
                overlap_list = addGenomicContextRow(result,position,[]);
            }
        }
    });

    // Upstream
    var position_up = parseInt(position) - 100000;
    if (position_up < 0) {
        position_up = 1;
    }
    var rest_full_url_2 = rest_url + chr + ':'+ position_up + '-' + position; // 100kb upstream
    $.ajax({
        type: "GET",
        dataType: "json",
        async: false, // Result needs to be get before going further in the method
        data: rest_opt,
        url: rest_full_url_2,
        error: function(jqXHR, status, errorThrown) {
            $("#contextValidationStatus").append("<div class=\""+error_class+"\">" + status + ": Issue with the gene upstream overlap call ("+errorThrown+")</div>");
        },
        success: function(result) {
            if (result.length > 0 && result != []) {
                addGenomicContextRow(result,position,overlap_list,"upstream");
            }
        }
    });

    // Downstream
    var position_down = parseInt(position) + 100000;

    // Check the downstream position to avoid having a position over the 3' end of the chromosome
    var rest_url_2 = "http://rest.ensembl.org/info/assembly/homo_sapiens/" + chr;
    $.ajax({
        type: "GET",
        dataType: "json",
        async: false, // Result needs to be get before going further in the method
        url: rest_url_2,
        error: function(jqXHR, status, errorThrown) {
            $("#contextValidationStatus").append("<div class=\""+error_class+"\">" + status + ": Issue getting the chromosome '"+chr+"' end coordinates ("+errorThrown+")</div>");

        },
        success: function(result) {
            if (position_down > result.length) {
                position_down = result.length;
            }
        }
    });

    var rest_full_url_3 = rest_url + chr + ':'+ position + '-' + position_down; // 100kb downstream
    $.ajax({
        type: "GET",
        dataType: "json",
        async: false, // Result needs to be get before going further in the method
        data: rest_opt,
        url: rest_full_url_3,
        error: function(jqXHR, status, errorThrown) {
            $("#contextValidationStatus").append("<div class=\""+error_class+"\">" + status + ": Issue with the gene downstream overlap call ("+errorThrown+")</div>");

        },
        success: function(result) {
            if (result.length > 0 && result != []) {
                addGenomicContextRow(result,position,overlap_list,"downstream");
            }
        }
    });

    if ($("#contextValidationStatus").html() != "") {
        $("#contextValidationStatus").show();
    }
}


// Generate the genomic context table
function addGenomicContextRow(json_result,position,overlap,type) {

    var row_id = 0;
    var intergenic = false;
    var upstream = false;
    var downstream = false;
    var overlap_list = [];

    var checked = " checked";
    var interchecked = "";
    var upchecked = "";
    var downchecked = "";

    if (type) {
        intergenic = true;
        interchecked = checked;
        if (type == 'upstream')   {
            upstream  = true;
            upchecked = checked;
        }
        if (type == 'downstream') {
            downstream  = true;
            downchecked = checked;
        }
    }

    for (i in json_result) {

        var gene = json_result[i].external_name;
        var distance = 0;


        if (type) {
            if (jQuery.inArray(gene,overlap) != -1) { // Skip overlapping genes which also overlap upstream and/or downstream of the variant
                continue;
            }

            if (type == "upstream") {
                distance = position - json_result[i].end;
            }
            else if (type == 'downstream') {
                distance = json_result[i].start - position;
            }
        }
        else {
            overlap_list.push(gene);
        }


        var newrow = "<tr id=\"context_tr_" + row_id + "\">";
        newrow = newrow + "<td><span>" + $("#snp_id").val() + "</span></td>";                                      // SNP
        newrow = newrow + "<td><input type=\"checkbox\"  value=\"" + intergenic + "\"" + interchecked + "\></td>"; // Intergenic
        newrow = newrow + "<td><span>" + gene + "</span></td>";                                                    // Gene
        newrow = newrow + "<td><input type=\"checkbox\" value=\"" + upstream + "\"" + upchecked + "\></td>";       // Upstream
        newrow = newrow + "<td><input type=\"checkbox\" value=\"" + downstream + "\"" + downchecked + "\></td>";   // Downstream
        newrow = newrow + "<td><input type=\"text\" value=\"" + distance + "\"\></td>";                            // Distance
        newrow = newrow + "<td><div class=\"btn btn-danger\">Delete</div></td>";
        //newrow = newrow + "<td><div class=\"btn btn-danger\" onclick=\"javascript:delete_row(\'"+row_prefix+"\',\'"+row_id+"\')\">Delete</div></td>";
        newrow = newrow + "</tr>";
        $("#context_table > tbody").append(newrow);
        row_id++;
    }

    if (!type) {
        return overlap_list;
    }
}