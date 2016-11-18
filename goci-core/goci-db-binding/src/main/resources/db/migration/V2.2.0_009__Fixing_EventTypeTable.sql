/*

################################################################################

Migration script to fix a typo error.

author: Cinzia Malangone
date:    18th Nov 2016
version: 2.2.0.009

################################################################################
*/

--------------------------------------------------------
--  FIXING typo error for Unpublished event (Event Type)
--------------------------------------------------------

UPDATE EVENT_TYPE SET ACTION = 'Unpublished from catalog' WHERE EVENT_TYPE= 'STUDY_STATUS_CHANGE_UNPUBLISHED_FROM_CATALOG';
