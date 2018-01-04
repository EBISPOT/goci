// Cinzia - Jan 2018 - JS Query
(function($) {
    $.fn.gociformset = function(opts) {
        var options = $.extend({}, $.fn.gociformset.defaults, opts),
            $$ = $(this);
        options.form = $$.selector;
        
        updateElementIndex = function(formsetCssClass) {
            var forms = $('.' + formsetCssClass);
            
            forms.each(function(index, elem) {
                var curIndex = $(elem).attr('id').match(/\d+/)[0];
                
                $(elem).attr('id', $(elem).attr('id').replace(curIndex, index));
                
                var inputs = $(elem).find('.' + formsetCssClass + 'Input');
                
                inputs.each(function(j, input) {
                    $(input).attr('name', $(input).attr('name').replace(curIndex, index));
                });
            });
        };
        
        
        var addButton = $('#' + options.addbuttonid);
        addButton.click(function() {
            var formCount = parseInt($('#id_' + options.prefix + '-TOTAL_FORMS').val());
            var count = $('#' + options.form).children().length;
            var tmplMarkup = $('#' + options.prefix + '-template').html();
            var compiledTmpl = tmplMarkup.replace(/__prefix__/g, count);
            $('div#' + options.form).append(compiledTmpl);
            
            $('#' + options.prefix + "-" + count).find("[class$='" + options.prefix + "']").click(function() {
                var row = $(this).parents('.' + options.formCssClass);
                row.remove();
                var count_item_form = $('#' + options.form).children().length;
                $('#id_' + options.prefix + '-TOTAL_FORMS').attr('value', count_item_form);
                updateElementIndex(options.formCssClass);
                
                return false;
            });
            
            // Added an element.
            $('#id_' + options.prefix + '-TOTAL_FORMS').attr('value', count + 1);
            return false;
        });
        
        $(".remove-" + options.prefix).click(function() {
            var row = $(this).parents('.' + options.formCssClass);
            row.remove();
            var count_item_form = $('#' + options.form).children().length;
            $('#id_' + options.prefix + '-TOTAL_FORMS').attr('value', count_item_form);
            updateElementIndex(options.formCssClass);
            
            return false;
        });
        
        return $$;
    }
    
    /* Setup plugin defaults */
    $.fn.gociformset.defaults = {
        prefix: 'form', // The form prefix for your django formset
        formCssClass: 'ItemForm', // CSS class applied to each form in a formset
        added: null, // Function called each time a new form is added
        removed: null // Function called each time a form is deleted
    };
})(jQuery)