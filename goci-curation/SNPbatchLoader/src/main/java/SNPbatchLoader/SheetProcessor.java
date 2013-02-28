package SNPbatchLoader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * @author dwelter
 * 
 * This classes takes an Excel spreadsheet sheet and extracts all the SNP records
 * For each SNP, a SNPentry object is created
 * 
 * */

public class SheetProcessor {

	private XSSFSheet sheet;
	private ArrayList<SNPentry> allSNPs;
    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public SheetProcessor(XSSFSheet sheet){
		
		this.sheet = sheet;
			
		allSNPs = new ArrayList<SNPentry>();
		readSNPs();
	}
	
	public void readSNPs(){
		boolean done = false;
		int rownum = 1;
		
		while(!done){
			XSSFRow row = sheet.getRow(rownum);

			if(row == null){
				done = true;
                getLog().debug("Last row read");
			}
			else{
                String gene, strongestallele, snp;
                if(row.getCell(0, row.RETURN_BLANK_AS_NULL) != null){
				    gene = row.getCell(0).getRichStringCellValue().getString();
                }
                else {
                    gene = null;
                    getLog().debug("Gene is null in row " + row.getRowNum());
                }

                if(row.getCell(1, row.RETURN_BLANK_AS_NULL) != null){
                    strongestallele = row.getCell(1).getRichStringCellValue().getString();
                }
                else {
                    strongestallele = null;
                    getLog().debug("Risk allele is null in row " + row.getRowNum());
                }

                if(row.getCell(2, row.RETURN_BLANK_AS_NULL) != null){
                    snp	= row.getCell(2).getRichStringCellValue().getString();
                }
                else {
                    snp = null;
                    getLog().debug("SNP is null in row " + row.getRowNum());
                }

				String riskfrequency = null;

                if(row.getCell(3, row.RETURN_BLANK_AS_NULL) != null){
                    XSSFCell risk = row.getCell(3);
				    switch(risk.getCellType()){
					case Cell.CELL_TYPE_STRING:
						riskfrequency = risk.getRichStringCellValue().getString();
						break;
					case Cell.CELL_TYPE_NUMERIC:
						riskfrequency = Double.toString(risk.getNumericCellValue());
						break;
				    }
                }
                else{
                    getLog().debug("RF is null in row " + row.getRowNum());
                }

                Integer pvalue_mantissa, pvalue_exponent;

                if(row.getCell(4, row.RETURN_BLANK_AS_NULL) != null){
                    pvalue_mantissa	= (int)row.getCell(4).getNumericCellValue();
                }
                else{
                    pvalue_mantissa = null;
                    getLog().debug("pvalue mantissa is null in row " + row.getRowNum());
                }

                if(row.getCell(5, row.RETURN_BLANK_AS_NULL) != null){
                    pvalue_exponent	= (int)row.getCell(5).getNumericCellValue();
                }
                else{
                    pvalue_exponent = null;
                    getLog().debug("pvalue exponent is null in row " + row.getRowNum());
                }

                float pval_float;
                String pval_num;

                if(pvalue_exponent != null && pvalue_mantissa == null){
                    pval_float = (float) (pvalue_mantissa * Math.pow(10, pvalue_exponent));
                    pval_num = setpvalnum(pvalue_mantissa, pvalue_exponent);
                }
                else{
                    pval_float = 0;
                    pval_num = null;
                }

				
				String pvaluetxt;
				if(row.getCell(6, row.RETURN_BLANK_AS_NULL) != null){
					pvaluetxt = row.getCell(6).getRichStringCellValue().getString();
				}
				else{
					pvaluetxt = null;
                    getLog().debug("pvalue text is null in row " + row.getRowNum());
                }
				
				Double orpercopynum;
				if(row.getCell(7, row.RETURN_BLANK_AS_NULL) != null){
					orpercopynum = row.getCell(7).getNumericCellValue();
				}
				else{
					orpercopynum = null;
                    getLog().debug("OR is null in row " + row.getRowNum());
                }

				Double orpercopyrecip;
				if(row.getCell(8, row.RETURN_BLANK_AS_NULL) != null){
					orpercopyrecip = row.getCell(8).getNumericCellValue();
					
					if(orpercopyrecip == 0){
						orpercopyrecip = null;
					}
				}
				else{
					orpercopyrecip = null;
                    getLog().debug("OR recip is null in row " + row.getRowNum());
                }
				
				if(orpercopyrecip != null){
					orpercopynum = ((100/orpercopyrecip)/100);			
				}

                char ortype;
                if(row.getCell(9, row.RETURN_BLANK_AS_NULL) != null){
                    ortype	= row.getCell(9).getRichStringCellValue().getString().charAt(0);
                }
                else{
                    ortype = Character.UNASSIGNED;
                    getLog().debug("OR type is null in row " + row.getRowNum());
                }
				
				String orpercopyrange; 
				if(row.getCell(10, row.RETURN_BLANK_AS_NULL) != null){
					orpercopyrange = row.getCell(10).getRichStringCellValue().getString();
				}
				else{
					orpercopyrange = null;
                    getLog().debug("CI is null in row " + row.getRowNum());
                }
				
				String orpercopyunitdescr; 
				if(row.getCell(11) != null){
					orpercopyunitdescr = row.getCell(11).getRichStringCellValue().getString();
				}
				else{
					orpercopyunitdescr = null;
                    getLog().debug("OR direction is null in row " + row.getRowNum());
                }
				
				Double orpercopystderror;
				
				if(row.getCell(12, row.RETURN_BLANK_AS_NULL) != null){
					orpercopystderror = row.getCell(12).getNumericCellValue();
				}
				else{
					orpercopystderror = null;
                    getLog().debug("SE is null in row " + row.getRowNum());
                }
				
				if((orpercopyrange == null) && (orpercopystderror !=  null)){
					orpercopyrange = setRange(orpercopystderror, orpercopynum);
				}
				
				if((orpercopyrecip != null) && (orpercopyrange != null) && (orpercopystderror == null)){
					orpercopyrange = reverseCI(orpercopyrange);
				}

                String snptype;
                if(row.getCell(13, row.RETURN_BLANK_AS_NULL) != null){
                    snptype = row.getCell(13).getRichStringCellValue().getString();
                }
                else{
                    snptype = null;
                    getLog().debug("SNP type is null in row " + row.getRowNum());
                }


                if(gene==null && strongestallele==null && snp == null && riskfrequency == null){
                    done = true;
                    getLog().debug("Empty row that wasn't caught via 'row = null'");
                }
                else{
				    SNPentry thisSNP =
					new SNPentry(gene, strongestallele, snp, riskfrequency, pvalue_mantissa, pvalue_exponent, pval_float, pval_num, pvaluetxt, orpercopynum, orpercopyrecip, ortype, orpercopyrange, orpercopyunitdescr, orpercopystderror, snptype);
					
				    allSNPs.add(thisSNP);
                }


			}
			
			rownum++;			
		}
	}

/**
 * This method sets the String form of the p-value from the mantissa and the exponent
 * @param mantissa the mantissa of the p-value
 * @param exponent the exponenet of the p-value
 * @return String the p-value in String form
 * 
 * */	
	public String setpvalnum(int mantissa, int exponent){
		String mant = Integer.toString(mantissa);
		String exp = Integer.toString(exponent);
		String pval_num = mant.concat(" x 10").concat(exp);
		return pval_num;
	}
	
/**
 * This method calculates the confidence interval based on the standard error - formatting code taken from Kent's Coldfusion code.
 * @param orpc_stderr The standard error
 * @param orpc_num The odds-ratio or beta value for the SNP
 * @return String The confidence interval for the odds-ratio or beta value  
 * 
 * */	
	public String setRange(double orpc_stderr, double orpc_num){
		double delta = (100000 * orpc_stderr * 1.96)/100000;
		double low = orpc_num - delta;
		double high = orpc_num + delta;
		String lowval, highval;
		
		if(low < 0.001){
			DecimalFormat df = new DecimalFormat("#.#####");
			lowval = df.format(low);
			highval = df.format(high);
		}
		else if(low >= 0.001 && low < 0.01){
			DecimalFormat df = new DecimalFormat("#.####");
			lowval = df.format(low);
			highval = df.format(high);
		}
		else if(low >= 0.01 && low < 0.1){
			DecimalFormat df = new DecimalFormat("#.###");
			lowval = df.format(low);
			highval = df.format(high);
		}
		else {
			DecimalFormat df = new DecimalFormat("#.##");
			lowval = df.format(low);
			highval = df.format(high);
		}
		
		String orpc_range = ("["+ lowval + "-" + highval + "]");
		
		return orpc_range;
	}
	
/**
* This method reverses the confidence interval for SNPs where the reciprocal odds-ratio was provided.
* @param interval The confidence interval
* @return String The reversed confidence interval  
*
* */
	public String reverseCI(String interval){
		String newInterval;
        String ci = interval.replace("[","");
		ci = ci.replace("]","");

        if(ci.equals("NR")){
             newInterval = interval;
        }

        else{
            String[] num = ci.split("-");
            double one = Double.parseDouble(num[0]);
            double two = Double.parseDouble(num[1]);

            double high = ((100/one)/100);
            double low = ((100/two)/100);

            String lowval, highval;

            if(low < 0.001){
                DecimalFormat df = new DecimalFormat("#.#####");
                lowval = df.format(low);
                highval = df.format(high);
            }
            else if(low >= 0.001 && low < 0.01){
                DecimalFormat df = new DecimalFormat("#.####");
                lowval = df.format(low);
                highval = df.format(high);
            }
            else if(low >= 0.01 && low < 0.1){
                DecimalFormat df = new DecimalFormat("#.###");
                lowval = df.format(low);
                highval = df.format(high);
            }
            else {
                DecimalFormat df = new DecimalFormat("#.##");
                lowval = df.format(low);
                highval = df.format(high);
            }

            newInterval = ("["+ lowval + "-" + highval + "]");
        }
		
		return newInterval;		
	}
	
	public ArrayList<SNPentry> getSNPlist(){
		return allSNPs;
	}
}
