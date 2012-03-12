package uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 06/03/12
 * Time: 10:37
 * To change this template use File | Settings | File Templates.
 */


public class FileContentTest extends TestCase{
    
    @Test
    public void testFileContent(){
        
        String content;

        ChromosomeRenderlet chrom = new ChrOne();

        content = chrom.render(null, null, null);
        
        System.out.println(content);
        
        assertNotNull(content);
    }
    
}
