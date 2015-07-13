package uk.ac.ebi.spot.goci.pussycat.session;

import org.springframework.beans.factory.annotation.Value;
import uk.ac.ebi.spot.goci.pussycat.utils.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstract implementation of a pussycat session that provides functionality for reading and writing SVG documents to
 * and from a disk-based cache.
 *
 * @author Tony Burdett
 * @date 02/08/12
 */
public abstract class AbstractSVGIOPussycatSession extends AbstractPussycatSession {
    private static final String ENCODING = "SHA-1";

    @Value("${cache.directory}")
    private File cacheDirectory;

    private Map<String, Object[]> hashArgsMap = new HashMap<String, Object[]>();

    protected AbstractSVGIOPussycatSession() {
        super();
    }

    public File getCacheDirectory() {
        return cacheDirectory;
    }

    public void setCacheDirectory(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    protected synchronized void writeSVG(String filename, String svg) throws IOException {
        if (getCacheDirectory() == null) {
            throw new IllegalStateException("Cache directory must not be null");
        }
        Writer out = null;
        try {
            File f = new File(getCacheDirectory(), filename);
            getLog().debug("Writing SVG to '" + f.getAbsolutePath() + "'...");
            if (!getCacheDirectory().exists()) {
                getLog().debug("Making parent directory '" + getCacheDirectory().getAbsolutePath() + "'...");
                getCacheDirectory().mkdirs();
                getLog().debug("Directory created!");
            }
            out = new BufferedWriter(new FileWriter(f));
            out.write(svg);
            getLog().debug("SVG written to disk successfully!");
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }

    protected synchronized String readSVG(String filename) throws IOException {
        if (getCacheDirectory() == null) {
            throw new IllegalStateException("Cache directory must not be null");
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader in = null;
        try {
            File f = new File(getCacheDirectory(), filename);
            getLog().debug("Reading SVG from '" + f.getAbsolutePath() + "'...");
            in = new BufferedReader(new FileReader(f));
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
    }

    protected synchronized boolean isInCache(String filename) {
        File f = new File(getCacheDirectory(), filename);
        return f.exists();
    }

    protected synchronized void clearCache() {
        if (getCacheDirectory() == null) {
            throw new IllegalStateException("Cache directory must not be null");
        }
        for (File f : getCacheDirectory().listFiles()) {
            if (!f.delete()) {
                getLog().warn("Failed to delete cached SVG file '" + f.getAbsolutePath() + "'");
            }
        }
    }

    protected String generateFilename(Object... objects) {
        StringBuilder hashedArgs = new StringBuilder();
        for (Object o : objects) {
            hashedArgs.append(o.hashCode());
        }
        try {
            // encode the content using configured encoding
            MessageDigest messageDigest = MessageDigest.getInstance(ENCODING);
            byte[] digest = messageDigest.digest(hashedArgs.toString().getBytes("UTF-8"));

            // now translate the resulting byte array to hex
            String hexedHashedArgs = StringUtils.getHexRepresentation(digest);
            if (hashArgsMap.containsKey(hexedHashedArgs)) {
                // key match, check contents
                Object[] matchedKeyContents = hashArgsMap.get(hexedHashedArgs);
                if (objects.length != matchedKeyContents.length) {
                    // mismatched key content length, genuine key collision
                    throw new RuntimeException(
                            "Key collision (content length mismatch) trying to generate unique key for " + hashedArgs);
                }
                else {
                    for (int i = 0; i < objects.length; i++) {
                        // mismatched key content element, genuine key collision
                        if (!objects[i].equals(matchedKeyContents[i])) {
                            throw new RuntimeException(
                                    "Key collision (content element mismatch at " + i + ") " +
                                            "trying to generate unique key for " + hashedArgs);
                        }
                    }
                }
            }
            else {
                hashArgsMap.put(hexedHashedArgs, objects);
            }

            getLog().trace("Generated new " + ENCODING + " based, hex encoded ID string: " + hexedHashedArgs);
            return hexedHashedArgs + ".svg";
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported!");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(ENCODING + " algorithm not available, this is required to generate ID");
        }
    }
}
