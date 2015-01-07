use strict;
use warnings;
use Bio::EnsEMBL::Registry;

# usage : perl nearest_gene_to_snp.pl [rs_id] [1or0]
# exemple : perl nearest_gene_to_snp.pl rs9268528 1
# 
# arg 1 : a snp rs id
# arg 2 : 1 or 0, 1 if you want to get the failed snp from ensembl, 0 if you don't
# return : 1 or more line with the following format 
#           rs_id[tab]ensembl_gene_label[tab]ensembl_gene_id
#           ex : rs9268528	BTNL2	ENSG00000204290
#			    rs9268528	BTNL2	ENSG00000224770
#			    rs9268528	BTNL2	ENSG00000229741
#			    rs9268528	BTNL2	ENSG00000225412
#			    rs9268528	BTNL2	ENSG00000225845
#			    rs9268528	BTNL2	ENSG00000229597
#			    rs9268528	BTNL2	ENSG00000224242
#			    rs9268528	AC226007.1	ENSG00000275798


my $rs_id = $ARGV[0];
my $get_failed = $ARGV[1];
my $reg = 'Bio::EnsEMBL::Registry';

$reg->load_registry_from_db(
-host   => 'ensembldb.ensembl.org',
-user   => 'anonymous',
);

my $snp_2_gene_mapping_file = $ARGV[2];
open(my $mapping_fh, '>>', $snp_2_gene_mapping_file) or die "Could not open file '$snp_2_gene_mapping_file' $!";


my $not_retrieved_snp_file = $ARGV[3];
open(my $failed_snp_fh, '>', $not_retrieved_snp_file) or die "Could not open file '$not_retrieved_snp_file' $!";

my $var_adaptor = $reg->get_adaptor( 'human', 'variation', 'variation' );

my $lost = 0;
my $saved = 0;


    #print "$rs_id\n";
    
    # Variation object
    
    # Modify the include_failed_variations flag in DBAdaptor to also return variations that have been flagged as failed
    if($get_failed eq "1"){
    	$var_adaptor->db->include_failed_variations(1);
    }
    my $var = $var_adaptor->fetch_by_name($rs_id);
    
    
    #if ($var->is_failed()) {
    #my $desc = $var->failed_description();
    #print $desc . "\n";
    #	}
    
    if(! $var) {
        $lost++;
        print $failed_snp_fh "$rs_id\n";
        close $failed_snp_fh;
        close $mapping_fh;
        exit 0;
    }else{
        $saved++;
        
    }
    
    my $var_features = $var->get_all_VariationFeatures(); # ->[0]->get_nearest_Gene()}), "\n";
    foreach my $var_feature (@{$var_features}) {
        my $external_gene_name = $var_feature->get_nearest_Gene()->external_name;
        my $ensembl_gene_id = $var_feature->get_nearest_Gene()->stable_id;
        print $mapping_fh join("\t", ($rs_id, $external_gene_name, $ensembl_gene_id)) . "\n";
    }

close $failed_snp_fh;
close $mapping_fh;




