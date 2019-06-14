package uk.ac.ebi.spot.goci.curation.service;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by emma on 19/03/15.
 *
 * @author emma
 *         <p>
 *         Service class to deal with NCBI ftp interactions Code is based on this extremely helpful example:
 *         http://www.codejava.net/java-se/networking/ftp/java-ftp-file-upload-tutorial-and-example
 */
@Service
public class FtpFileService {

    // Reading these from application.properties
    @Value("${ftp.server}")
    private String server;

    @Value("${ftp.username}")
    private String userName;

    @Value("${ftp.password}")
    private String password;

    private FTPClient ftpClient;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    // Constructor
    public FtpFileService() {
        this.ftpClient = new FTPClient();
    }

    // Upload file to NCBI
    public void ftpFileUpload(File outputFile) throws IOException {
        int maxAttempt = 7;
        boolean connected = false;
        int attempt =0;

        while (attempt < maxAttempt && !connected) {
            // Connect to FTP
            try {
                connected = connect();
                getLog().info("*** Connection: success ***");
            } catch (Exception exception) {
                attempt = attempt + 1;
                getLog().error("Attempt number " + Integer.toString(attempt) + " failed.");
                try {
                    Thread.sleep(1500); //delay
                } catch (Exception exceptionDelay) {
                }
            }
        }

        // Max attempt exceeded- Throw an exception.
        if (!connected) {
            throw new RuntimeException(
                    "Unable to connect to FTP. Max attempts exceeded.");
        }


        InputStream inputStream = new FileInputStream(outputFile);
        String remoteFile = "gwas.txt";

        boolean done = ftpClient.storeFile(remoteFile, inputStream);
        inputStream.close();
        if (done) {
            getLog().info(remoteFile + " uploaded successfully.");
        }

        else {
            getLog().error("Failed to upload file " + remoteFile + " to FTP");
        }


        // Close FTP connection
        disconnect();
    }

    // Connect to FTP server
    public Boolean connect() throws IOException {

        // Connect to server
        int reply;
        try {

            ftpClient.connect(server);
            getLog().info("Connecting to " + server);

            ftpClient.login(userName, password);
            ftpClient.enterLocalPassiveMode();

            reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                getLog().error("FTP server refused connection");
                System.exit(1);
            }

        }
        catch (Exception exception){
            throw new RuntimeException(
                    "Unable to connect to FTP ", exception);
        }

        return true;
    }


    // Disconnect from FTP server
    public void disconnect() {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(
                    "Unable to disconnect from FTP ", e);
        }
    }
}
