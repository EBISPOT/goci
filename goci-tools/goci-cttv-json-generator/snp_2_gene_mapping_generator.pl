use strict;
use warnings;


my $snp_2_gene_mapping_file = $ARGV[0];
my $failed_snp_file = $ARGV[1];


my $url = 'http://localhost:8890/sparql?default-graph-uri=&query=PREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0APREFIX+rdfs%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%0D%0APREFIX+owl%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%0D%0APREFIX+xsd%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E%0D%0APREFIX+dc%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0D%0APREFIX+dcterms%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Fterms%2F%3E%0D%0APREFIX+oban%3A+%3Chttp%3A%2F%2Fpurl.org%2Foban%2F%3E%0D%0APREFIX+ro%3A+%3Chttp%3A%2F%2Fwww.obofoundry.org%2Fro%2Fro.owl%23%3E%0D%0APREFIX+efo%3A+%3Chttp%3A%2F%2Fwww.ebi.ac.uk%2Fefo%2F%3E%0D%0APREFIX+gt%3A+%3Chttp%3A%2F%2Frdf.ebi.ac.uk%2Fterms%2Fgwas%2F%3E%0D%0APREFIX+gd%3A+%3Chttp%3A%2F%2Frdf.ebi.ac.uk%2Fdataset%2Fgwas%2F%3E%0D%0A%0D%0ASELECT+DISTINCT+%3Fsnp%0D%0AWHERE+%7B%0D%0A++%3Fassociation+a+gt%3ATraitAssociation+%3B%0D%0A+++++++++++++++gt%3Ahas_p_value+%3Fpvalue+%3B%0D%0A+++++++++++++++oban%3Ahas_subject+%3Fsnp+%3B%0D%0A+++++++++++++++oban%3Ahas_object+%3Ftrait+%3B%0D%0A+++++++++++++++ro%3Apart_of+%3Fstudy+.%0D%0A++%0D%0A++%3Fstudy+gt%3Ahas_pubmed_id+%3Fpubmed_id+.%0D%0A++%3Fstudy+gt%3Ahas_publication_date+%3Fdate+.%0D%0A%7D%0D%0A&format=text%2Ftab-separated-values&timeout=0&debug=on';

my $page = `wget -O - "$url"`;

my @lines = split /\n/, $page;
foreach my $snp_url (@lines) {
    
    $snp_url =~ tr/"//d;
    #    perl ./nearest_gene_to_snp.pl rs636864 1
    
    $snp_url =~ s/http:\/\/rdf.ebi.ac.uk\/dataset\/gwas\/SingleNucleotidePolymorphism\///;
    
    my @args = ("perl","nearest_gene_to_snp.pl",$snp_url, 0, $snp_2_gene_mapping_file, $failed_snp_file);
    system(@args);

}
