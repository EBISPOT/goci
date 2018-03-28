/** First refactoring action: common js - DRY. From Xin original code
 *  Datatable action to rendering studies panel.
 * */

/**
 * display study table
 * @param {Object} data - study solr docs
 * @param {Boolean} cleanBeforeInsert
 */
function displayDatatableStudies(data, cleanBeforeInsert=true) {
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
        var nr_association = 0;
        if ('association_rsId' in asso) {
             var arraysize= asso.association_rsId;
             nr_association = arraysize.length;
        }
        
        // Publication date
        var p_date = asso.publicationDate;
        var publi = p_date.split('T')[0];
        tmp['publi'] = publi;
    
        // AccessionID
        tmp['study'] = '<a href="'+contextPath+'studies/'+asso.accessionId+'">'+asso.accessionId+'&nbsp;<span class="icon-GWAS_Study_2017"></span></a>';
    
    
        // Journal
        tmp['Journal'] = asso.publication;
        
        // Title
        tmp['Title'] = asso.title;
        
        // Number Associations
        tmp['nr_associations'] = nr_association.toString();
        
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
            field: 'study',
            title: 'Study',
            sortable: true
            },
            {
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
        },{
            field: 'nr_associations',
            title: 'Number Associations',
            sortable: true
        }
        ],
        data: data_json,
        
    });
    $('#study-table').bootstrapTable('load',data_json)
    if(data_json.length>5){
        $('#study-table').bootstrapTable('refreshOptions',{pagination: true,pageSize: pageRowLimit,pageList: [5,10,25,50,100,'All']})
    }
    hideLoadingOverLay('#study-table-loading')
}
