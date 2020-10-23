/*
################################################################################
Migration script to change columns in ASSOCIATION_REPORT from VARCHAR2 to CLOB

author: Tudor Groza
date:    9 October 2020
version: 2.6.0.001
################################################################################
*/

  ALTER TABLE "NOTE"
    ADD (TMP_TEXT_NOTE  CLOB);

  UPDATE "NOTE" SET TMP_TEXT_NOTE=TEXT_NOTE;
  COMMIT;

  ALTER TABLE "NOTE" DROP COLUMN TEXT_NOTE;

  ALTER TABLE "NOTE"
    RENAME COLUMN TMP_TEXT_NOTE TO TEXT_NOTE;
