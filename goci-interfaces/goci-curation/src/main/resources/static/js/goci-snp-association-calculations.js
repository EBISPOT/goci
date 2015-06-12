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
    $("#validation_button").click(function() {
        if ($("#snpValidated").val() != "true" ||  $("#snp_id").val() != $("#snp").val()) {
            var rest_url = "http://rest.ensembl.org/variation/human/" + $("#snp_id").val();
            $.ajax({
                type: "GET",
                dataType: "json",
                url: rest_url,
                error: function (jqXHR, status, errorThrown) {
                    $("#validation_status").html(status+" ("+errorThrown+")");
                    //$("#validation_status").html("Error: can't find the variant "+$("#snp").val()+" in Ensembl");
                    $("#validation_status").css({color: "#F00"});
                },
                success: function(result) {
                    $("#snpValidated").val("true");
                    $("#snp").val($("#snp_id").val());
                    $("#validation_status").css({color: "#0A0"});
                    $("#validation_status").html($("#snp_id").val() + " validated");
                }
            });
            $("#snp_check_waiting").hide();
        }
    });
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