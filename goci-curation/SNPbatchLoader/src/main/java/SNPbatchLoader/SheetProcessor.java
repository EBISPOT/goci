package SNPbatchLoader;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;


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
			}
			else{
				String gene = row.getCell(0).getRichStringCellValue().getString();

				String strongestallele = row.getCell(1).getRichStringCellValue().getString();
				String snp	= row.getCell(2).getRichStringCellValue().getString();
	
				String riskfrequency = null;
				XSSFCell risk = row.getCell(3);
				
				switch(risk.getCellType()){
					case Cell.CELL_TYPE_STRING:
						riskfrequency = risk.getRichStringCellValue().getString();
						break;
					case Cell.CELL_TYPE_NUMERIC:
						riskfrequency = Double.toString(risk.getNumericCellValue());
						break;
				}
						
				int pvalue_mantissa	= (int)row.getCell(4).getNumericCellValue();				
				int pvalue_exponent	= (int)row.getCell(5).getNumericCellValue();
				float pval_float = (float) (pvalue_mantissa * Math.pow(10, pvalue_exponent));
				String pval_num = setpvalnum(pvalue_mantissa, pvalue_exponent);	
				
				String pvaluetxt;
				if(row.getCell(6) != null){
					pvaluetxt = row.getCell(6).getRichStringCellValue().getString();
				}
				else{
					pvaluetxt = null;
				}
				
				Double orpercopynum;
				if(row.getCell(7) != null){
					orpercopynum = row.getCell(7).getNumericCellValue();
				}
				else{
					orpercopynum = null;
				}

				Double orpercopyrecip;
				if(row.getCell(8) != null){
					orpercopyrecip = row.getCell(8).getNumericCellValue();
					
					if(orpercopyrecip == 0){
						orpercopyrecip = null;
					}
				}
				else{
					orpercopyrecip = null;
				}
				
				if(orpercopyrecip != null){
					orpercopynum = ((100/orpercopyrecip)/100);			
				}
								
				char ortype	= row.getCell(9).getRichStringCellValue().getString().charAt(0);
				
				String orpercopyrange; 
				if(row.getCell(10) != null){
					orpercopyrange = row.getCell(10).getRichStringCellValue().getString();
				}
				else{
					orpercopyrange = null;
				}
				
				String orpercopyunitdescr; 
				if(row.getCell(11) != null){
					orpercopyunitdescr = row.getCell(11).getRichStringCellValue().getString();
				}
				else{
					orpercopyunitdescr = null;
				}
				
				Double orpercopystderror;
				
				if(row.getCell(12) != null){
					orpercopystderror = row.getCell(12).getNumericCellValue();
				}
				else{
					orpercopystderror = null;
				}
				
				if((orpercopyrange == null) && (orpercopystderror !=  null)){
					orpercopyrange = setRange(orpercopystderror, orpercopynum);
				}
				
				if((orpercopyrecip != null) && (orpercopyrange != null) && (orpercopystderror == null)){
					orpercopyrange = reverseCI(orpercopyrange);				
				}
				
				
				String snptype = row.getCell(13).getRichStringCellValue().getString();
				
				SNPentry thisSNP = 
					new SNPentry(gene, strongestallele, snp, riskfrequency, pvalue_mantissa, pvalue_exponent, pval_float, pval_num, pvaluetxt, orpercopynum, orpercopyrecip, ortype, orpercopyrange, orpercopyunitdescr, orpercopystderror, snptype);
					
				allSNPs.add(thisSNP);
			}
			
			rownum++;			
		}
	}

/**
 * This method sets the String form of the p-value from the mantissa and the exponent
 * @param int mantissa the mantissa of the p-value
 * @param int exponent the exponenet of the p-value
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
 * @param double orpc_stderr The standard error
 * @param double orpc_num The odds-ratio or beta value for the SNP
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
* @param String interval The confidence interval
* @return String The reversed confidence interval  
*
* */
	public String reverseCI(String interval){
		String ci = interval.replace("[","");
		ci = ci.replace("]","");
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
		
		String newInterval = ("["+ lowval + "-" + highval + "]");
		
		return newInterval;		
	}
	
	public ArrayList<SNPentry> getSNPlist(){
		return allSNPs;
	}
}
