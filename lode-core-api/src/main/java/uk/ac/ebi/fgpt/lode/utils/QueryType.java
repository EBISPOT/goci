package uk.ac.ebi.fgpt.lode.utils;

import com.hp.hpl.jena.query.Query;

/**
 * @author Simon Jupp
 * @date 26/02/2013
 * Functional Genomics Group EMBL-EBI
 */
public enum QueryType {

       DESCRIBEQUERY,
       CONSTRUCTQUERY,
       TUPLEQUERY,
       BOOLEANQUERY,
       UNKOWN;


       public static QueryType getQueryType(Query q1) {

           if (q1.isSelectType()) {
               return QueryType.TUPLEQUERY;
           }
           else if (q1.isConstructType() ) {
               return QueryType.CONSTRUCTQUERY;
           }
           else if ( q1.isDescribeType()) {
               return QueryType.DESCRIBEQUERY;
           }
           else if (q1.isAskType()) {
               return QueryType.BOOLEANQUERY;
           }
           return QueryType.UNKOWN;  //To change body of created methods use File | Settings | File Templates.
       }
   }