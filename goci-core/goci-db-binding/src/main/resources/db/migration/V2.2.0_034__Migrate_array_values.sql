/*
################################################################################
Migration script to migrate current targeted_array and genomewide_array fields
into the new xref table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:   June 21st 2017
version: 2.2.0.034
################################################################################

#######################################
#  CREATE NEW TABLES AND CONSTRAINTS
#######################################
*/


--------------------------------------------------------
-- Migrate genomewide_array values
--------------------------------------------------------
    INSERT INTO STUDY_GENOTYPING_TECHNOLOGY (STUDY_ID, GENOTYPING_TECHNOLOGY_ID)
        SELECT DISTINCT S.ID, G.ID
        FROM STUDY S, GENOTYPING_TECHNOLOGY G
        WHERE S.GENOMEWIDE_ARRAY = 1
        AND G.GENOTYPING_TECHNOLOGY = 'Genome-wide genotyping array';


--------------------------------------------------------
-- Migrate targeted_array values
--------------------------------------------------------
    INSERT INTO STUDY_GENOTYPING_TECHNOLOGY (STUDY_ID, GENOTYPING_TECHNOLOGY_ID)
        SELECT DISTINCT S.ID, G.ID
        FROM STUDY S, GENOTYPING_TECHNOLOGY G
        WHERE S.TARGETED_ARRAY = 1
        AND G.GENOTYPING_TECHNOLOGY = 'Targeted genotyping array';


