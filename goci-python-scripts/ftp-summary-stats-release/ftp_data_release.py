import cx_Oracle
import os
import shutil

import db_properties

ip = db_properties.ip
port = db_properties.port
SID = db_properties.SID
uname = db_properties.username
pw = db_properties.password

stagingPath = db_properties.pathSummaryStats

ftpPath = db_properties.pathFTP


def performFTPCopy(directoryName):
     ftpDir = ftpPath + '/' + directoryName

     stagingDir = stagingPath + '/' + directoryName

     # if the directory already exists on the FTP, check that all the files exist
     if os.path.exists(ftpDir):
         filesInFTPDir = os.listdir(ftpDir)
         filesInStagingDir = os.listdir(stagingDir)                                                                             

     #copy across any files that don't exist yet
         for file in filesInStagingDir:
             if file not in filesInFTPDir:
                print("Copying " + file + " to " + ftpDir + " as it is missing")
                shutil.copy(stagingDir+'/'+file, ftpDir)

     #remove any files that are no longer in the staging directory
         for file in filesInFTPDir:
             if file not in filesInStagingDir:
                print("Deleting " + file + " from " + ftpDir + " as it is no longer present in staging")
                os.remove(ftpDir+'/'+file)

     #if the directory doesn't exist on the ftp, create it and copy all the files from staging across
     else:
         print("Creating directory " + ftpDir + " on the FTP server")
         os.makedirs(ftpDir)
         filesInStagingDir = os.listdir(stagingDir)

         for file in filesInStagingDir:
             print("Copying " + file + " to newly created " + ftpDir)
             shutil.copy(stagingDir+'/'+file, ftpDir)

     return

def cleanFTP():
    # deal with any directories that are on the FTP but no longer in staging
    ftpDirs = os.listdir(ftpPath)
    stagingDirs = os.listdir(stagingPath)

    for dir in ftpDirs:
            if dir not in stagingDirs:
                print("Removing directory " + dir + " from the FTP as it is no longer present in staging")
                os.removedirs(ftpPath +'/' +dir)
    return





if __name__ == '__main__':
    dsn_tns = cx_Oracle.makedsn(ip, port, SID)
    connection = cx_Oracle.connect(uname, pw, dsn_tns)

    print(connection.version)

    cur = connection.cursor()

    cur.execute("select distinct s.id, replace(s.author, ' ', ''), s.pubmed_id, s.accession_id "
                + " from study s"
                + " join housekeeping h on h.id = s.housekeeping_id"
                + " where s.full_pvalue_set = 1"
                + " and h.is_published = 1")

    studies = cur.fetchall()

    for result in studies:

        studyId = str(result[0])
        author = result[1]
        pubmedId = str(result[2])
        accession = result[3]

        directoryName = author+'_'+pubmedId+'_'+accession

        fullpath = stagingPath + '/' + directoryName

        if not os.path.exists(fullpath):
            prePublicationDirectory = author+'_'+pubmedId+'_'+studyId

            if os.path.exists(prePublicationDirectory):
                 print('Pre-publication directory ' + prePublicationDirectory + ' has not yet been renamed for the FTP')
            else:
                print('Warning! Directory ' + fullpath + ' does not exist')

        else:
            performFTPCopy(directoryName)


    cleanFTP()

    connection.close()
    print 'End process'