/*

################################################################################
Drop the catalog_summary_view table as it is broken and causing errors and no
longer used anywhere

Designed for execution with Flyway database migrations tool.

author:  Dani Welter
date:    May 12th 2016
version: 2.1.2.024
################################################################################

*/
--------------------------------------------------------
-- Drop view
--------------------------------------------------------

DROP VIEW CATALOG_SUMMARY_VIEW;