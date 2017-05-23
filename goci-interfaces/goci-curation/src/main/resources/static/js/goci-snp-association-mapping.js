/**
 * Created by Laurent on 23/06/15.
 *
 * Function to switch between the SNP association and the SNP mapping tabs
 *
 */


// Main "function" waiting for a click on one of the tab button
$(document).ready(function() {

    displayTooltip();

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


// Display mouseover tooltip from bootstrap
function displayTooltip() {
    $('[data-toggle="tooltip"]').mouseover(
            function() {
                $(this).tooltip('show');
            }
    );
}