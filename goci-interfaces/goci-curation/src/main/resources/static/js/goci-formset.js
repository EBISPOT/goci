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
                index += 1;
                
                $(elem).attr('id', $(elem).attr('id').replace(curIndex, index));
                
                var inputs = $(elem).find('.' + formsetCssClass + 'Input');
                
                inputs.each(function(j, input) {
                    $(input).attr('name', $(input).attr('name').replace(curIndex, index));
                });
    
                var rows = $(elem).find('.' + formsetCssClass + 'Row');
    
                rows.each(function(j, row) {
                    $(row).html(index);
                });
    
            });
        };
        
        addElementIndex = function() {
            //var formCount = parseInt($('#id_' + options.prefix + '-TOTAL_FORMS').val());
            var count = $('#' + options.form).children().length;
            count += 1;
            var tmplMarkup = $('#' + options.prefix + '-template').html();
            var compiledTmpl = tmplMarkup.replace(/__prefix__/g, count);
            $('div#' + options.form).append(compiledTmpl);
    
            $('#' + options.prefix + "-" + count).find("[class$='" + options.prefix + "']").click(function() {
                var row = $(this).parents('.' + options.formCssClass);
                row.remove();
                var count_item_form = $('#' + options.form).children().length;
                //$('#id_' + options.prefix + '-TOTAL_FORMS').attr('value', count_item_form);
                updateElementIndex(options.formCssClass);
        
                return false;
            });
    
            // Added an element.
            $('#id_' + options.prefix + '-TOTAL_FORMS').attr('value', count);
            return false;
        }
        
        
        var multiAddButton = $('#' + options.multiaddbuttonid);
        multiAddButton.click(function(){
            // convert to number. Check copy and paste and return 0 if the user is able to insert a string.
            var nRowToAdd = parseInt($('#multi-add-study-duplications').val()) || 0;
            if (nRowToAdd == 0) { alert("Invalid input, please insert a valid positive number.");}
            for (i = 0; i < nRowToAdd; i++) {
                addElementIndex();
            }
            return false;
        });
        
        var addButton = $('#' + options.addbuttonid);
        addButton.click(function() {
            addElementIndex();
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