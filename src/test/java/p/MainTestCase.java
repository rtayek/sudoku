package p;
import static org.junit.Assert.*;
import static p.Main.*;
import org.junit.*;
import java.awt.Color;
import java.io.*;
public class MainTestCase {
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testFromString() {
        //"java.awt.Color[r=1,g=12,b=123]";
        Color expected=new Color(1,12,123);
        String string=expected.toString();
        Color actual=fromString(string);
        assertEquals(expected,actual);
    }
}
