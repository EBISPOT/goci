isql << EOF
ld_dir ('/Users/catherineleroy/Documents/virtuoso_accessible/','gwas.owl','http://rdf.ebi.ac.uk/gwas');
rdf_loader_run();
EXIT;
EOF
