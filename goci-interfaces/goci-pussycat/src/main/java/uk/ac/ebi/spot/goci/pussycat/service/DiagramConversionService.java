package uk.ac.ebi.spot.goci.pussycat.service;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dwelter on 24/11/15.
 */
public class DiagramConversionService {

    private String svg;
    private Document document;

    public DiagramConversionService(String svg) {
        this.svg = svg;
        try {
            document = loadSVGFromString(svg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public Document loadSVGFromString(String svg) throws org.xml.sax.SAXException, java.io.IOException {
        return loadSVGFromString(new java.io.ByteArrayInputStream(svg.getBytes()));
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//
//        factory.setNamespaceAware(true);
//        DocumentBuilder builder = factory.newDocumentBuilder();
//
//        return builder.parse(new ByteArrayInputStream(svg.getBytes()));
    }

    public Document loadSVGFromString(InputStream is) throws org.xml.sax.SAXException, java.io.IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        }
        catch (javax.xml.parsers.ParserConfigurationException ex) {
        }
        Document doc = builder.parse(is);
        is.close();
        return doc;

    }


    public void save() throws Exception {

        // Create a JPEGTranscoder and set its quality hint.
        JPEGTranscoder t = new JPEGTranscoder();
        t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY,
                new Float(.8));

        // Set the transcoder input and output.
        TranscoderInput input = new TranscoderInput(document);
        OutputStream ostream = new FileOutputStream("out.jpg");
        TranscoderOutput output = new TranscoderOutput(ostream);

        // Perform the transcoding.
        t.transcode(input, output);
        ostream.flush();
        ostream.close();
    }


}
