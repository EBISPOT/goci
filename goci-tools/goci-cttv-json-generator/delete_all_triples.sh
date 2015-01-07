isql << EOF
log_enable(3,1);
SPARQL CLEAR GRAPH 'http://rdf.ebi.ac.uk/gwas'; 
delete from DB.DBA.load_list;
EXIT;
EOF
