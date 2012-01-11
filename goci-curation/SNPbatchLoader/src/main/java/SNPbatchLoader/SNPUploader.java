package SNPbatchLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

public class SNPUploader {
	
	private ArrayList<SNPentry> snpList;
	private Connection conn;
	private Statement stmt;
	private int paperid, snpcheck;

	
	public SNPUploader(int id, ArrayList<SNPentry> list) throws Exception{
		
		paperid = id;
		snpList = list;
		snpcheck = 1;
		
		try{
			conn = DBConnection.getConnection();
			stmt = conn.createStatement();
		}
		
		catch (SQLException ex) {
				throw new Exception("Attempting database access has generated an SQL exception", ex);
		}
		catch (IOException ex){
			throw new Exception("Attempting database access has resulted in an IO exception", ex);
		}
		
		uploadSnps();
		
		conn.close();
		
	}

/**
 * This method uploads all the SNPs to the database using prepared statements
 * 
 * */	
	public void uploadSnps() throws Exception{
		try{
			 PreparedStatement writeToDB;			 	
			 String query = "INSERT INTO GWASStudiesSNP (gwasID, gene, strongestAllele, SNP, riskFrequency, pValueFloat, pValueNum,	pValueTxt, ORperCopyNum, ORpercopyRange, ORpercopyUnitDescr, ORType, SNPtype," +
			 		" SNPpending, lastUpdateDate, ORpercopyRecip, ORpercopyStdError, pvalue_mantissa, pvalue_exponent) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			 writeToDB = conn.prepareStatement(query); 

			 for(SNPentry current : snpList){
				 writeToDB.setInt(1, paperid); 
				 writeToDB.setString(2, current.getGene());
				 writeToDB.setString(3, current.getAllele());
				 writeToDB.setString(4, current.getSNP());
				 writeToDB.setString(5, current.getRiskFreq());
				 writeToDB.setFloat(6, current.getpval());
				 writeToDB.setString(7, current.getpnum());
				 
				 if(current.getptxt() != null){
					 writeToDB.setString(8, current.getptxt());
				 }
				 else{
					 writeToDB.setString(8, null);
				 }
				 
				 if(current.getORnum() != null){
					 writeToDB.setFloat(9, current.getORnum());
				 }
				 else{
					 writeToDB.setNull(9, Types.NUMERIC);
				 }
				 
				 if(current.getRange() != null){
					 writeToDB.setString(10, current.getRange());
				 }
				 else{
					 writeToDB.setString(10, null);
				 }	
				 
				 if(current.getUnit() != null){
					 writeToDB.setString(11, current.getUnit());
				 }
				 else{
					 writeToDB.setString(11, null);
				 }
				 writeToDB.setInt(12, current.getORtype());
				 writeToDB.setString(13, current.getSNPtype());
				 writeToDB.setInt(14, snpcheck);
				 
				Calendar rightnow = Calendar.getInstance();
				Timestamp dbdate = new Timestamp(rightnow.getTimeInMillis());

				 writeToDB.setTimestamp(15, dbdate);
				 
				 if(current.getORrecip() != null){
					 writeToDB.setFloat(16, current.getORrecip());
				 }
				 else{
					 writeToDB.setNull(16, Types.NUMERIC);
				 }
				 
				 if(current.getORstderr() != null){
					 writeToDB.setFloat(17, current.getORstderr());
				 }
				 else{
					 writeToDB.setNull(17, Types.NUMERIC);
				 }
				 writeToDB.setInt(18, current.getMant());
				 writeToDB.setInt(19, current.getExp());
				 
				 writeToDB.executeUpdate();
				 
/*get the ID of the SNP entry*/
				 ResultSet rs = stmt.executeQuery("Select max(id) from GWASSTUDIESSNP");
				 rs = stmt.getResultSet();
				 rs.next();
				 int snpID = rs.getInt(1);
				 
/*update the lastupdate time stamp of the GWAS paper entry	- this fails at the moment because of a problem with the SQL query so isn't done 
				 stmt.executeUpdate("Update GWASSTUDIES SET lastupdatedate = " + dbdate + " where id = '" + gwasid + "'");*/
				 
/*update gene list*/
				 if(current.getGene().indexOf(',') != -1){
					 StringTokenizer stn = new StringTokenizer(current.getGene(), ",");
					 while(stn.hasMoreTokens()){
						 String newgene = stn.nextToken();
						 ResultSet rs2 = stmt.executeQuery("Select * from GWASGENE where gene = '" + newgene + "'");
						 rs2 = stmt.getResultSet();
						 if(!rs.next()){
							 stmt.executeUpdate("Insert into GWASGENE(gene) values('" + newgene + "')");
						 }
						 rs2 = stmt.executeQuery("Select id from GWASGENE where gene = '" + newgene + "'");
						 rs2 = stmt.getResultSet();
						 rs2.next();
						 int newid = rs2.getInt(1);
						 stmt.executeUpdate("Insert into GWASGENEXREF(geneid, gwasstudiessnpid) values(" + newid + "," + snpID + ")");
					 }					 
				 }
				 else{
					 ResultSet rs2 = stmt.executeQuery("Select * from GWASGENE where gene = '" + current.getGene() + "'");
					 rs2 = stmt.getResultSet();
					 if(!rs.next()){ 
						 stmt.executeUpdate("Insert into GWASGENE(gene) values('" + current.getGene() + "')");
					 }
					 rs2 = stmt.executeQuery("Select id from GWASGENE where gene = '" + current.getGene() + "'");
					 rs2 = stmt.getResultSet();
					 rs2.next();
					 int newid = rs2.getInt(1);
					 stmt.executeUpdate("Insert into GWASGENEXREF(geneid, gwasstudiessnpid) values(" + newid + "," + snpID + ")");
				 }
/*update SNP list*/
				 ResultSet rs3 = stmt.executeQuery("Select * from GWASSNP where snp = '" + current.getSNP() + "'");
				 rs3 = stmt.getResultSet();
				 if(!rs.next()){
					 stmt.executeUpdate("Insert into GWASSNP(snp) values('" + current.getSNP() + "')");
				 }
				 rs3 = stmt.executeQuery("Select id from GWASSNP where snp = '" + current.getSNP() + "'");
				 rs3 = stmt.getResultSet();
				 rs3.next();
				 int newid = rs3.getInt(1);
				 stmt.executeUpdate("Insert into GWASSNPXREF(snpid, gwasstudiessnpid) values(" + newid + "," + snpID + ")");
				 
			 
			 } 
		 }
		catch (SQLException ex) {
				throw new Exception("Attempting to write to the database has generated an SQL exception", ex);
		}

	}
	
}
