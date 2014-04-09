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
    private String logMessage;

    protected Logger getLog() {
        return log;
    }

    public SheetProcessor(XSSFSheet sheet){
		
		this.sheet = sheet;
			
		allSNPs = new ArrayList<SNPentry>();
        logMessage = "";
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
                logMessage = "All spreadsheet data processed successfully";
			}
			else{
                String gene, strongestallele, snp;
                if(row.getCell(0, row.RETURN_BLANK_AS_NULL) != null){
				    gene = row.getCell(0).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Gene' in row " + rownum+1 + "\n";

                }
                else {
                    gene = null;
                    getLog().debug("Gene is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Gene' in row " + rownum+1 + "\n";
                }

                if(row.getCell(1, row.RETURN_BLANK_AS_NULL) != null){
                    strongestallele = row.getCell(1).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Risk allele' in row " + rownum+1 + "\n";

                }
                else {
                    strongestallele = null;
                    getLog().debug("Risk allele is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Risk allele' in row " + rownum+1 + "\n";
                }

                if(row.getCell(2, row.RETURN_BLANK_AS_NULL) != null){
                    snp	= row.getCell(2).getRichStringCellValue().getString();
                    logMessage = "Error in field 'SNP' in row " + rownum+1 + "\n";

                }
                else {
                    snp = null;
                    getLog().debug("SNP is null in row " + row.getRowNum());
                    logMessage = "Error in field 'SNP' in row " + rownum+1 + "\n";

                }

				String riskfrequency = null;

                if(row.getCell(3, row.RETURN_BLANK_AS_NULL) != null){
                    XSSFCell risk = row.getCell(3);
				    switch(risk.getCellType()){
					case Cell.CELL_TYPE_STRING:
						riskfrequency = risk.getRichStringCellValue().getString();
                        logMessage = "Error in field 'Risk Frequency' in row " + rownum+1 + "\n";
                        break;
					case Cell.CELL_TYPE_NUMERIC:
						riskfrequency = Double.toString(risk.getNumericCellValue());
                        logMessage = "Error in field 'Risk Frequency' in row " + rownum+1 + "\n";

                        break;
				    }
                }
                else{
                    getLog().debug("RF is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Risk Frequency' in row " + rownum+1 + "\n";
                }

                Integer pvalue_mantissa = null;
                Integer pvalue_exponent = null;

                if(row.getCell(4, row.RETURN_BLANK_AS_NULL) != null){
                    XSSFCell mant = row.getCell(4);
                    switch(mant.getCellType()){
                        case Cell.CELL_TYPE_STRING:
                            pvalue_mantissa = null;
                            logMessage = "Error in field 'pvalue mantissa' in row " + rownum+1 + "\n";

                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            pvalue_mantissa = (int)mant.getNumericCellValue();
                            logMessage = "Error in field 'pvalue mantissa' in row " + rownum+1 + "\n";

                            break;
                    }
                }
                else{
                    pvalue_mantissa = null;
                    getLog().debug("pvalue mantissa is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvalue mantissa' in row " + rownum+1 + "\n";

                }

                if(row.getCell(5, row.RETURN_BLANK_AS_NULL) != null){
                    XSSFCell expo = row.getCell(5);
                    switch(expo.getCellType()){
                        case Cell.CELL_TYPE_STRING:
                            pvalue_exponent = null;
                            logMessage = "Error in field 'pvalue exponent' in row " + rownum+1 + "\n";

                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            pvalue_exponent = (int)expo.getNumericCellValue();
                            logMessage = "Error in field 'pvalue exponent' in row " + rownum+1 + "\n";

                            break;
                    }
                }
                else{
                    pvalue_exponent = null;
                    getLog().debug("pvalue exponent is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvalue exponent' in row " + rownum+1 + "\n";
                }

                float pval_float;
                String pval_num;

                if(pvalue_exponent != null && pvalue_mantissa != null){
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
                    logMessage = "Error in field 'pvaluetxt' in row " + rownum+1 + "\n";

                }
				else{
					pvaluetxt = null;
                    getLog().debug("pvalue text is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvaluetxt' in row " + rownum+1 + "\n";
                }
				
				Double orpercopynum = null;
				if(row.getCell(7, row.RETURN_BLANK_AS_NULL) != null){
                    XSSFCell or = row.getCell(7);
                    switch(or.getCellType()){
                        case Cell.CELL_TYPE_STRING:
                            orpercopynum = null;
                            logMessage = "Error in field 'OR' in row " + rownum+1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orpercopynum = or.getNumericCellValue();
                            logMessage = "Error in field 'OR' in row " + rownum+1 + "\n";
                            break;
                    }
				}
				else{
					orpercopynum = null;
                    getLog().debug("OR is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR' in row " + rownum+1 + "\n";
                }

				Double orpercopyrecip = null;
				if(row.getCell(8, row.RETURN_BLANK_AS_NULL) != null){
                    XSSFCell recip = row.getCell(8);
                    switch(recip.getCellType()){
                        case Cell.CELL_TYPE_STRING:
                            orpercopyrecip = null;
                            logMessage = "Error in field 'OR recip' in row " + rownum+1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orpercopyrecip = recip.getNumericCellValue();
                            logMessage = "Error in field 'OR recip' in row " + rownum+1 + "\n";
                            break;
                    }
				}
				else{
					orpercopyrecip = null;
                    getLog().debug("OR recip is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR recip' in row " + rownum+1 + "\n";

                }

                boolean recipReverse = false;

				if((orpercopyrecip != null) && (orpercopynum == null)){
					orpercopynum = ((100/orpercopyrecip)/100);
                    recipReverse = true;
				}

                char ortype;
                if(row.getCell(9, row.RETURN_BLANK_AS_NULL) != null){
                    ortype	= row.getCell(9).getRichStringCellValue().getString().charAt(0);
                    logMessage = "Error in field 'OR type' in row " + rownum+1 + "\n";
                }
                else{
                    ortype = Character.UNASSIGNED;
                    getLog().debug("OR type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR type' in row " + rownum+1 + "\n";
                }
				
				String orpercopyrange; 
				if(row.getCell(10, row.RETURN_BLANK_AS_NULL) != null){
					orpercopyrange = row.getCell(10).getRichStringCellValue().getString();
                    logMessage = "Error in field 'CI' in row " + rownum+1 + "\n";
                }
				else{
					orpercopyrange = null;
                    getLog().debug("CI is null in row " + row.getRowNum());
                    logMessage = "Error in field 'CI' in row " + rownum+1 + "\n";
                }
				
				String orpercopyunitdescr; 
				if(row.getCell(11) != null){
					orpercopyunitdescr = row.getCell(11).getRichStringCellValue().getString();
                    logMessage = "Error in field 'OR direction' in row " + rownum+1 + "\n";
                }
				else{
					orpercopyunitdescr = null;
                    getLog().debug("OR direction is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR direction' in row " + rownum+1 + "\n";
                }
				
				Double orpercopystderror = null;
				
				if(row.getCell(12, row.RETURN_BLANK_AS_NULL) != null){
                    XSSFCell std = row.getCell(12);
                    switch(std.getCellType()){
                        case Cell.CELL_TYPE_STRING:
                            orpercopystderror = null;
                            logMessage = "Error in field 'Standard Error' in row " + rownum+1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orpercopystderror = std.getNumericCellValue();
                            logMessage = "Error in field 'Standard Error' in row " + rownum+1 + "\n";
                            break;
                    }
				}
				else{
					orpercopystderror = null;
                    getLog().debug("SE is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Standard Error' in row " + rownum+1 + "\n";
                }

				
				if((orpercopyrecip != null) && (orpercopyrange != null) && recipReverse){
					orpercopyrange = reverseCI(orpercopyrange);
				}

                if((orpercopyrange == null) && (orpercopystderror !=  null)){
                    orpercopyrange = setRange(orpercopystderror, orpercopynum);
                }

                String snptype;
                if(row.getCell(13, row.RETURN_BLANK_AS_NULL) != null){
                    snptype = row.getCell(13).getRichStringCellValue().getString();
                    logMessage = "Error in field 'SNP type' in row " + rownum+1 + "\n";
                }
                else{
                    snptype = null;
                    getLog().debug("SNP type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'SNP type' in row " + rownum+1 + "\n";
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

        else if(ci.matches("[\\d\\s.-]+")){
            String[] num = ci.split("-");
            double one = Double.parseDouble(num[0].trim());
            double two = Double.parseDouble(num[1].trim());

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
        else {
             newInterval = null;
            getLog().info("Unprocessable character in the CI");
        }
		
		return newInterval;		
	}
	
	public ArrayList<SNPentry> getSNPlist(){
		return allSNPs;
	}

    public String getLogMessage(){
        return logMessage;
    }
}
