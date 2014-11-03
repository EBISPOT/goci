package SNPbatchLoader;

public class SNPentry {
	
	private String gene, strongest_allele, snp, riskfreq, pval_txt, orpc_range, orpc_unit, snp_type, pval_num; 
	private int pval_mant, pval_exp, or_type;
	private float pval_float;
	private Float orpc_num, orpc_recip, orpc_stderr;

	public SNPentry(String gene, String allele, String snp, String risk, int pmant, int pexp, float pfloat, String pnum, String ptxt,
			Double ornum, Double orrecip, char ortype, String orrange, String orunit, Double sterr, String snptype){
		
		this.gene = gene;
		strongest_allele = allele;
		this.snp = snp;
		riskfreq = risk;
		pval_mant = pmant;
		pval_exp = pexp;
		pval_float = pfloat;
		pval_num = pnum;
		pval_txt = ptxt;
		
		if(ornum == null){
			orpc_num = null;
		}
		else{
			orpc_num = ornum.floatValue();
		}
		if(orrecip == null){
			orpc_recip = null;
		}
		else{
			orpc_recip = orrecip.floatValue();
		}
		orpc_range = orrange;
		orpc_unit = orunit;
		
		if(sterr == null){
			orpc_stderr = null;
		}
		else{
			orpc_stderr = sterr.floatValue();
		}
		snp_type = snptype;
/*or type is entered as y/n in the spreadsheet but the database requires 0/1 format*/		
		if(ortype == 'Y'){
			or_type = 1;
		}
		else{
			or_type = 0;
		}
	}
	
	public String getGene(){
		return gene;
	}	
	public String getAllele(){
		return strongest_allele;
	}
	public String getSNP(){
		return snp;
	}
	public String getRiskFreq(){
		return riskfreq;
	}
	public String getpnum(){
		return pval_num;
	}
	public String getptxt(){
		return pval_txt;
	}
	public String getRange(){
		return orpc_range;
	}
	public String getUnit(){
		return orpc_unit;
	}
	public String getSNPtype(){
		return snp_type;
	}
	public int getMant(){
		return pval_mant;
	}
	public int getExp(){
		return pval_exp;
	}
	public int getORtype(){
		return or_type;
	}
	public float getpval(){
		return pval_float;
	}
	public Float getORnum(){
		return orpc_num;
	}
	public Float getORrecip(){
		return orpc_recip;
	}
	public Float getORstderr(){
		return orpc_stderr;
	}
}
