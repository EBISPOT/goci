package SNPbatchLoader;

import java.io.File;
import java.util.ArrayList;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;*/

/**
 * @author dwelter
 * @version 1.0
 * 
 * This is a small program to upload a batch of SNPs from a .xlsx spreasheet to a database.
 * Note that the spreadsheet must be of .xlsx format! 
 *
 */
public class BatchLoader 
{
	
/**
 * Create logger for this class	
 */
	private Logger log = LoggerFactory.getLogger(getClass());

	protected Logger getLog() {
		return log;
	}

/**
 * Central controlling constructor for the entire uploader. It calls all classes and methods. File is passed in and processed, SNP uploader is called. File is deleted at the end.  	
 * @param name A string giving the absolute path to the file that needs to be processed
 * @param id Database ID of the paper that the SNPs are extracted from
 * @throws Exception
 */
	public BatchLoader(String name, int id) throws Exception
    {  
     	try{
    		
     	   	getLog().info("Starting upload...");
     	 	
        	String filename = name;
        	int paperID = id;
        	XSSFSheet sheet = null;      	
        	 
        	OPCPackage pkg = OPCPackage.open(filename);
    		XSSFWorkbook current = new XSSFWorkbook(pkg);
    		getLog().debug("Acquiring 0-index sheet...");

    		sheet = current.getSheetAt(0);
    		getLog().debug("Got sheet 0 OK!");
    		
    	  	SheetProcessor processor = new SheetProcessor(sheet);

    		ArrayList<SNPentry> SNPlist = processor.getSNPlist();
    		new SNPUploader(paperID, SNPlist);   
    		
    		pkg.close();
    		
        	getLog().info("Upload successful");

    	}
     	catch (Exception e) {
     		getLog().error("Encountered a " + e.getClass().getSimpleName() + " whilst trying to upload file '" + name + "'" +
     				" (" + new File(name).getAbsolutePath() + ")", e);
     		e.printStackTrace();
     		throw e;
     	}
     	finally{
     		File f = new File(name);
     		if(f.delete()){
     			getLog().info("File deleted");
     		}
     		else{
     			getLog().error("Failure to delete file");
     		}
     		
     	}

    }
}
