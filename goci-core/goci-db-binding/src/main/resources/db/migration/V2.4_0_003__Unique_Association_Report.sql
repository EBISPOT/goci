/*

################################################################################
Migration script to enforce unique association ids in association_report table
author: Jon Stewart
date:    14 Jan 2020
version: 2.4.0.003
################################################################################
*/

alter table "GWAS"."ASSOCIATION_REPORT" add constraint as_rpt_unique_id unique("ASSOCIATION_ID")