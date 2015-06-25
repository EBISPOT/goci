/**
 * Created by Laurent on 23/06/15.
 *
 * Use the Ensembl REST API to:
 * - Validate the variants
 * - Validate the reported gene(s)
 * - Load the variants mappings (cytogenetic band, chromosome, position)
 * - Load the variants genomic contexts
 */

// Ensembl REST URLs
var rest_url_root = "http://rest.ensembl.org";
var rest_variation = rest_url_root + "/variation/homo_sapiens/";
var rest_variation_post = rest_url_root + "/variation/homo_sapiens";
var rest_lookup_symbol = rest_url_root + "/lookup/symbol/homo_sapiens/";
var rest_overlap_region =  rest_url_root + "/overlap/region/homo_sapiens/";
var rest_info_assembly = rest_url_root + "/info/assembly/homo_sapiens/"; // Get chromosome length
var rest_xrefs_id = rest_url_root + "/xrefs/id/";

var ncbi_db_type = "otherfeatures";
var genomic_distance = 100000; // 100kb

// Forms
var snpMappingForms = "snpMappingForms";
var genomicContextForms = "genomicContexts";
var hidden_input = "input type=\"hidden\"";

var ok_icon = "<span class=\"glyphicon glyphicon-ok-sign\"></span> ";
var error_icon = "<span class=\"glyphicon glyphicon-remove-sign\"></span> ";


// Main "function" waiting for a click on one of the button in the form
$(document).ready(function() {

    // Single variant validation (+ variant mapping and genomic context)
    $("#snp_validation_button").click(function() {
        var snp_row_id = 1; // Standard association (only 1 variant association)
        if (($("#snpValidated_"+snp_row_id).val() != "true" || $("#snp_id_"+snp_row_id).val() != $("#snp_"+snp_row_id).val()) && $("#snp_id_"+snp_row_id).val()) {
            $("#snpValidationStatusOk").html("");
            $("#snpValidationStatusFailed").html("");
            $("#snpValidationStatusOk").hide();
            $("#snpValidationStatusFailed").hide();

            var btn = $(this).button('loading');

            var rest_url = rest_variation + $("#snp_id_"+snp_row_id).val();
            $.ajax({
                method: "GET",
                dataType: "json",
                url: rest_url,
                error: function(jqXHR, status, errorThrown) {
                    $("#snpValidationStatusFailed").append("<div>" + error_icon + $("#snp_id_"+snp_row_id).val() + ": " + status + " (" + errorThrown + ")" + "</div>");
                    $("#snpValidationStatusFailed").show();
                    btn.button('reset');
                },
                success: function(result) {
                    $("#snpValidated_" + snp_row_id).val("true");
                    $("#snp_" + snp_row_id).val($("#snp_id_" + snp_row_id).val());

                    // SNP has been merged
                    if (result.name == $("#snp_id_" + snp_row_id).val()) {
                        $("#merged_" + snp_row_id).val(0);
                    }
                    else {
                        $("#merged_" + snp_row_id).val(1);
                    }

                    // Get mapping(s) and genomic context
                    if (result.mappings) {
                        $("#mapping_table > tbody").html(""); // Empty the mapping table
                        $("#context_table > tbody").html(""); // Empty the genomic context table
                        $("#contextValidationStatus").html("");

                        getMappings(result.mappings, snp_row_id);
                    }
                    else {
                        $("#snpValidationStatusFailed").append("<div>" + error_icon + $("#snp_id_" + snp_row_id).val() + ": no mapping found</div>");
                    }
                    btn.button('reset');
                    $("#snpValidationStatusOk").append("<div>" + ok_icon + $("#snp_id_" + snp_row_id).val() + ": validated</div>");
                    $("#snpValidationStatusOk").show();
                }
            });
        }
    });


    // Multiple variants validation (+ variants mappings and genomic contexts)
    $("#multi_snp_validation_button").click(function() {
        var change = 0;
        var variants_ids = [];

        var btn = $(this).button('loading');

        // Get the list of variant rsIDs
        $(".snp_row").each(function() {
            var snp_row_id = $(this).html();
            var snp = $("#snp_id_"+snp_row_id).val();
            if (($("#snpValidated_" + snp_row_id).val() != "true" || $("#snp_id_" + snp_row_id).val() != $("#snp_" + snp_row_id).val()) && $("#snp_id_"+snp_row_id).val()) {
                change = 1;
                variants_ids.push(snp);
            }
        });

        if (change == 1) {
            $("#snpValidationStatusOk").html("");
            $("#snpValidationStatusFailed").html("");
            $("#snpValidationStatusOk").hide();
            $("#snpValidationStatusFailed").hide();
        }

        if (variants_ids.length > 1) { // Check that there are at least 2 variants provided

            var rest_url = rest_variation_post;

            var json_variants_ids = JSON.stringify({"ids": variants_ids});

            $.ajax({
                method: "POST",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                url: rest_url,
                data: json_variants_ids,
                error: function(jqXHR, status, errorThrown) {
                    $("#snpValidationStatusFailed").append("<div>" + error_icon + "Variants validation: " + status +
                                                           " (" + errorThrown + ")" + "</div>");
                    $("#snpValidationStatusFailed").show();
                    btn.button('reset');
                },
                success: function(results) {

                    $("#mapping_table > tbody").html(""); // Empty the mapping table
                    $("#context_table > tbody").html(""); // Empty the genomic context table
                    $("#contextValidationStatus").html("");

                    var snp_row_id = 1; // Line number starts at 1 and array index starts at 0
                    // TODO: check that the snp_row_id corresponds to the correct SNP
                    $.each(results, function(key, data) {
                        //var snp_row_id = i + 1; // Line number starts at 1 and array index starts at 0
                        $("#snpValidated_" + snp_row_id).val("true");
                        $("#snp_" + snp_row_id).val($("#snp_id_" + snp_row_id).val());
                        $("#snpValidationStatusOk").append("<div>" + ok_icon + $("#snp_id_" + snp_row_id).val() +
                                                           ": validated</div>");
                        $("#snpValidationStatusOk").show();

                        // SNP has been merged
                        if (key == $("#snp_id_" + snp_row_id).val()) {
                            $("#merged_" + snp_row_id).val(0);
                        }
                        else {
                            $("#merged_" + snp_row_id).val(1);
                        }

                        // Get mapping(s) and genomic context
                        if (data.mappings) {
                            getMappings(data.mappings, snp_row_id);
                        }
                        else {
                            $("#snpValidationStatusFailed").append("<div>" + error_icon +
                                                                   $("#snp_id_" + snp_row_id).val() +
                                                                   ": no mapping found</div>");
                            $("#snpValidationStatusFailed").show();
                        }
                        snp_row_id++;
                    });
                    btn.button('reset');
                }
            });
        }
        else {
            $("#snpValidationStatusFailed").append("<div>Please, provide at least 2 variants</div>");
            $("#snpValidationStatusFailed").show();
        }
    });


    // Reported genes validation: check genes symbols and if the reported genes are on the same chromosome as the variant(s)
    $("#gene_validation_button").click(function() {

        if ($("#mapping_table > tbody > tr").length > 0) {

            $("#geneValidationStatus").html("");
            $("#geneValidationStatus").hide();

            var btn = $("#gene_validation_button").button('loading');

            // Get the distinct list of reported genes
            var genes = [];
            $("[id^='authorgenes']").each(function() {
                var gene_string = $(this).val();
                var genes_list = gene_string.split(",");
                for (i in genes_list) {
                    if (jQuery.inArray(genes_list[i], gene) == -1) {
                        genes.push(genes_list[i]);
                    }
                }
            });

            for (i in genes) {
                var gene = genes[i];
                var rest_full_url = rest_lookup_symbol + gene;
                $.ajax({
                    method: "GET",
                    dataType: "json",
                    async: false, // Avoid weird results
                    url: rest_full_url,
                    error: function(jqXHR, status, errorThrown) {
                        $(".tag:contains('" + gene + "')").css({backgroundColor: "#A00"});
                        $("#geneValidationStatus").append("<div>" + error_icon +"Gene "+ gene.toUpperCase() + " is not valid</div>");

                    },
                    success: function(result) {
                        var gene_chr = result.seq_region_name;
                        var same_chr = 0;
                        $(".chromosomeName").each(function() { // Inputs containing the chromosome name of the variant location
                            if ($(this).val() == gene_chr) {
                                same_chr = 1;
                            }
                        });

                        if (same_chr == 1) {
                            $(".tag:contains('" + gene + "')").css({backgroundColor: "#0A0"});
                        }
                        else {
                            $(".tag:contains('" + gene + "')").css({backgroundColor: "#A00"});
                            $("#geneValidationStatus").append("<div>" + error_icon + "Gene "+ gene.toUpperCase() + " is on a different chromosome (chr"+gene_chr+")</div>");
                        }
                    }
                });
            }
            btn.button('reset');
            if ($("#geneValidationStatus").html() != "") {
                $("#geneValidationStatus").show();
            }
        }
        else {
            alert("Please validate the variant(s) before checking the genes (need to compare gene(s) and variant(s) coordinates)");
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


/*function delete_row(row_id) {
 $("#"+row_id).remove();
 }*/


// Get the variant mapping(s) and genomic context
function getMappings(mappings,snp_row_id) {
    var row_prefix = "mapping_tr_";
    var row_id = $("[id^=row_prefix]").length + 1;

    // Populate the mapping table
    for (i in mappings) {
        var location = mappings[i].location.split(":");
        var chr = location[0];
        var position = location[1].split("-")[0];
        var band = "Unknown";
        /*var rest_cytogenytic = rest_overlap_region+chr+":"+position+"-"+position;
         $.ajax({
         method: "GET",
         dataType: "json",
         async: false, // Result needs to be get before going further in the method
         data: {"feature" : "band"},
         url: rest_cytogenytic,
         error: function(jqXHR, status, errorThrown) {
         $("#snpValidationStatus").append("<div>" + error_icon + $("#snp_id").val() + ": " +status+" - Cytogenetic band not found ("+errorThrown+")"+"</div>");
         },
         success: function(result_band) {
         band = chr+result_band[0].id;
         }
         });*/


        var snpMappingId = snpMappingForms+i;
        var snpMappingName = snpMappingForms+"["+i+"]";

        var newrow = "<tr id=\"" + row_prefix + row_id + "\">";
        // SNP
        newrow = newrow + "<td><span>" + $("#snp_id_"+snp_row_id).val() + "</span>"+
                 "<"+hidden_input+" id=\""+snpMappingId+".snp\" name=\""+snpMappingName+".snp\" value=\""+$("#snp_id_"+snp_row_id).val()+"\"></td>";
        // Region
        newrow = newrow + "<td><span>" + band + "</span>"+
                "<"+hidden_input+" id=\""+snpMappingId+".location.region.name\" name=\""+snpMappingName+".location.region.name\" value=\""+band+"\"></td>";
        // Chromosome
        newrow = newrow + "<td><span class=\"chromosomeName\">" + chr + "</span>"+
                "<"+hidden_input+" id=\""+snpMappingId+".location.chromosomeName\" name=\""+snpMappingName+".location.chromosomeName\" value=\""+chr+"\"></td>";
        // Position
        newrow = newrow + "<td><span>" + position + "</span>"+
                "<"+hidden_input+" id=\""+snpMappingId+".location.chromosomePosition\" name=\""+snpMappingName+".location.chromosomePosition\" value=\""+position+"\"></td>";
        //newrow = newrow + "<td><div class=\"btn btn-danger\" onclick=\"javascript:delete_row(\'" + row_prefix + row_id + "\')\">Delete</div></td>";
        newrow = newrow + "</tr>";
        $("#mapping_table > tbody").append(newrow);
        row_id++;

        // Populate Genomic context
        getAllGenomicContext(chr, position, snp_row_id);
    }
}


// Load the genomic context of a variant (overlap, 100kb upstream, 100kb downstream)
function getAllGenomicContext(chr,position,snp_row_id) {

    getGenomicContext(chr, position, snp_row_id, "ensembl", false); // Ensembl genes
    getGenomicContext(chr, position, snp_row_id, "refseq",  false); // NCBI genes
}


// Load the Ensembl or NCBI genomic context of a variant (overlap, 100kb upstream, 100kb downstream)
function getGenomicContext(chr,position,snp_row_id,source,clear) {

    if (clear) {
        $("#context_table > tbody").html("");
        $("#contextValidationStatus").html("");
    }

    var rest_opt = {"feature" : "gene"}; // By default the db_type is 'core' (i.e. Ensembl)

    if (source != "ensembl") {
        rest_opt = {"feature" : "gene", "source" : source, "db_type" : ncbi_db_type};
    }

    // Check if overlap gene
    var rest_full_url_1 = rest_overlap_region + chr + ':'+ position + '-' + position;
    var overlap_list = []; // Array to avoid having several occurence of the overlapping genes

    $.ajax({
        method: "GET",
        dataType: "json",
        async: false, // Result needs to be get before going further in the method
        data: rest_opt,
        url: rest_full_url_1,
        error: function(jqXHR, status, errorThrown) {
            $("#contextValidationStatus").append("<div>" + error_icon + status + ": Issue with the gene overlap call ("+errorThrown+")</div>");
        },
        success: function(result) {
            if (result.length > 0 && result != []) {
                overlap_list = addGenomicContextRow(result,position,snp_row_id,[]);
            }
        }
    });

    // Upstream
    var position_up = parseInt(position) - genomic_distance;
    if (position_up < 0) {
        position_up = 1;
    }
    var rest_full_url_2 = rest_overlap_region + chr + ':'+ position_up + '-' + position; // 100kb upstream
    $.ajax({
        method: "GET",
        dataType: "json",
        async: false, // Result needs to be get before going further in the method
        data: rest_opt,
        url: rest_full_url_2,
        error: function(jqXHR, status, errorThrown) {
            $("#contextValidationStatus").append("<div>" + error_icon + status + ": Issue with the gene upstream overlap call ("+errorThrown+")</div>");
        },
        success: function(result) {
            if (result.length > 0 && result != []) {
                addGenomicContextRow(result,position,snp_row_id,overlap_list,"upstream");
            }
        }
    });

    // Downstream
    var position_down = parseInt(position) + genomic_distance;

    // Check the downstream position to avoid having a position over the 3' end of the chromosome
    var rest_url_2 = rest_info_assembly + chr;
    $.ajax({
        method: "GET",
        dataType: "json",
        async: false, // Result needs to be get before going further in the method
        url: rest_url_2,
        error: function(jqXHR, status, errorThrown) {
            $("#contextValidationStatus").append("<div>" + error_icon + status + ": Issue getting the chromosome '"+chr+"' end coordinates ("+errorThrown+")</div>");

        },
        success: function(result) {
            if (position_down > result.length) {
                position_down = result.length;
            }
        }
    });

    var rest_full_url_3 = rest_overlap_region + chr + ':'+ position + '-' + position_down; // 100kb downstream
    $.ajax({
        method: "GET",
        dataType: "json",
        async: false, // Result needs to be get before going further in the method
        data: rest_opt,
        url: rest_full_url_3,
        error: function(jqXHR, status, errorThrown) {
            $("#contextValidationStatus").append("<div>" + error_icon + status + ": Issue with the gene downstream overlap call ("+errorThrown+")</div>");

        },
        success: function(result) {
            if (result.length > 0 && result != []) {
                addGenomicContextRow(result,position,snp_row_id,overlap_list,"downstream");
            }
        }
    });

    if ($("#contextValidationStatus").html() != "") {
        $("#contextValidationStatus").show();
    }
}


// Generate the genomic context rows
function addGenomicContextRow(json_result,position,snp_row_id,overlap,type) {

    var intergenic = false;
    var upstream = false;
    var downstream = false;
    var overlap_list = [];

    var checked = "checked=\"checked\"";
    var interchecked = "";
    var upchecked = "";
    var downchecked = "";

    var row_id = 1;
    var row_prefix = "context_tr_";

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

        var gene_name = json_result[i].external_name;
        var gene_id = json_result[i].id;

        var distance = 0;

        // NCBI gene
        if (gene_name == null) {
            var rest_url = rest_xrefs_id + gene_id;
            $.ajax({
                method: "GET",
                dataType: "json",
                async: false, // Result needs to be get before going further in the method
                data: {"external_db": "RefSeq_gene_name"},
                url: rest_url,
                error: function(jqXHR, status, errorThrown) {
                    $("#contextValidationStatus").append("<div>" + error_icon + status +
                                                         ": Issue to fetch the gene name of the GeneID " + gene_id +
                                                         " (" + errorThrown + ")</div>");
                },
                success: function(result) {
                    if (result.length > 0 && result != []) {
                        gene_name = result[0].display_id;
                    }
                }
            });
            if (gene_name == null) {
                continue; // Skip gene if no display name found after running the REST call above
            }
        }

        if (type) {
            if (jQuery.inArray(gene_name,overlap) != -1) { // Skip overlapping genes which also overlap upstream and/or downstream of the variant
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
            overlap_list.push(gene_name);
        }

        var genomicContextId = genomicContextForms+i;
        var genomicContextName = genomicContextForms+"["+i+"]";

        var hidden_intergenic_val = "";
        var hidden_upstream_val = "";
        var hidden_downstream_val = "";

        var newrow = "<tr id=\""+row_prefix + row_id + "\">";
        // SNP
        newrow = newrow + "<td><span>" + $("#snp_id_"+snp_row_id).val() + "</span>"+
                "<"+hidden_input+" id=\""+genomicContextId+".snp.rsId\" name=\""+genomicContextName+".snp.rsId\" value=\""+$("#snp_id_"+snp_row_id).val()+"\"></td>";
        // Intergenic
        if (interchecked != "") {
            hidden_intergenic_val = "<"+hidden_input+" name=\"_"+genomicContextName+".isIntergenic\" value=\"on\"/>";
        }
        newrow = newrow + "<td><input type=\"checkbox\" id=\""+genomicContextId+".isIntergenic1\" name=\""+genomicContextName+".isIntergenic\" value=\"" + intergenic + "\"" + interchecked + "\>"+
                hidden_intergenic_val+"</td>";
        // Gene name
        newrow = newrow + "<td><span>" + gene_name + " (" + gene_id + ")</span>"+
                "<"+hidden_input+" id=\""+genomicContextId+".gene.geneName\" name=\""+genomicContextName+".gene.geneName\" value=\""+gene_name+"\"></td>";
        // Upstream
        if (upchecked != "") {
            hidden_upstream_val = "<"+hidden_input+" name=\"_"+genomicContextName+".isUpstream\" value=\"on\"/>";
        }
        newrow = newrow + "<td><input type=\"checkbox\" id=\""+genomicContextId+".isUpstream1\" name=\""+genomicContextName+".isUpstream\" value=\"" + upstream + "\"" + upchecked + "\>"+
                hidden_upstream_val+"</td>";
        // Downstream
        if (downchecked != "") {
            hidden_downstream_val = "<"+hidden_input+" name=\"_"+genomicContextName+".isDownstream\" value=\"on\"/>";
        }
        newrow = newrow + "<td><input type=\"checkbox\" id=\""+genomicContextId+".isDownstream1\" name=\""+genomicContextName+".isDownstream\" value=\"" + downstream + "\"" + downchecked + "\>"+
                hidden_downstream_val+"</td>";
        // Distance
        newrow = newrow + "<td><span>" + distance + "</span>"+
                "<"+hidden_input+" id=\""+genomicContextId+".distance\" name=\""+genomicContextName+".distance\" value=\""+distance+"\"></td>";
        //newrow = newrow + "<td><div class=\"btn btn-danger\" onclick=\"javascript:delete_row(\'"+row_prefix+row_id+"\')\">Delete</div></td>";
        newrow = newrow + "</tr>";
        $("#context_table > tbody").append(newrow);
        row_id++;
    }

    if (!type) {
        return overlap_list;
    }
}