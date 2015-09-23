/*

################################################################################
Updating any previously published studies (those with a PUBLISH_DATE NOT NULL)
to have status 'Publish study' if the existing status is 'Send to NCBI'

Designed for execution with Flyway database migrations tool.

author:  Tony Burdett
date:    March 30th 2015
version: 1.9.9.034 (pre 2.0)
################################################################################

*/

--------------------------------------------------------
--  Update status of studies
--------------------------------------------------------

UPDATE HOUSEKEEPING SET CURATION_STATUS_ID = (SELECT ID FROM CURATION_STATUS WHERE STATUS = 'Publish study')
WHERE ID IN (
  SELECT DISTINCT h.ID
  FROM STUDY s
  JOIN HOUSEKEEPING h ON h.ID = s.HOUSEKEEPING_ID
  JOIN CURATION_STATUS cs ON cs.ID = h.CURATION_STATUS_ID
  WHERE h.PUBLISH_DATE IS NOT NULL AND cs.STATUS = 'Send to NCBI');