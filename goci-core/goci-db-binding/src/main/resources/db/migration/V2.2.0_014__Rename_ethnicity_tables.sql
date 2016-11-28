/*
################################################################################
Rename all ethncitity-related tables & fields in tables to ancestry

author: Dani Welter
date:    Nov 24th 2016
version: 2.2.0.013
################################################################################
*/
--------------------------------------------------------
-- Rename ETHNICITY table
--------------------------------------------------------

ALTER TABLE ethnicity
  RENAME TO ancestry;


--------------------------------------------------------
-- Rename ETHNICIC_GROUP field
--------------------------------------------------------

ALTER TABLE ancestry
  RENAME COLUMN ethnic_group to ancestral_group;



--------------------------------------------------------
-- Rename DELETED_ETHNICITY table
--------------------------------------------------------

ALTER TABLE deleted_ethnicity
  RENAME TO deleted_ancestry;


--------------------------------------------------------
-- Rename DELETED_ETHNICITY_EVENT table
--------------------------------------------------------

ALTER TABLE deleted_ethnicity_event
  RENAME TO deleted_ancestry_event;


--------------------------------------------------------
-- Rename DELETED_ETHNICITY_ID field
--------------------------------------------------------

ALTER TABLE deleted_ancestry_event
  RENAME COLUMN deleted_ethnicity_id to deleted_ancestry_id;


--------------------------------------------------------
-- Rename ETHNICITY_EVENT table
--------------------------------------------------------

ALTER TABLE ethnicity_event
  RENAME TO ancestry_event;


--------------------------------------------------------
-- Rename ETHNICITY_id field
--------------------------------------------------------

ALTER TABLE ancestry_event
  RENAME COLUMN ethnicity_id to ancestry_id;




--------------------------------------------------------
-- Rename ancestry-related fields in Housekeeping
--------------------------------------------------------

ALTER TABLE housekeeping
  RENAME COLUMN ETHNICITY_CHECKED_LEVEL_ONE TO ANCESTRY_CHECKED_LEVEL_ONE;

ALTER TABLE housekeeping
  RENAME COLUMN ETHNICITY_CHECKED_LEVEL_TWO TO ANCESTRY_CHECKED_LEVEL_TWO;

ALTER TABLE housekeeping
  RENAME COLUMN ETHNICITY_BACK_FILLED TO ANCESTRY_BACK_FILLED;
