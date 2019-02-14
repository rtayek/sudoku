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
    @Test public void testPages1_3() {
        int expected=1;
        int actual=pages(1,3);
        assertEquals(expected,actual);
    }
    @Test public void testPages2_3() {
        int expected=1;
        int actual=pages(2,3);
        assertEquals(expected,actual);
    }
    @Test public void testPages3_3() {
        int expected=1;
        int actual=pages(3,3);
        assertEquals(expected,actual);
    }
    @Test public void testPages4_3() {
        int expected=2;
        int actual=pages(4,3);
        assertEquals(expected,actual);
    }
    @Test public void testPages5_3() {
        int expected=2;
        int actual=pages(5,3);
        assertEquals(expected,actual);
    }
    @Test public void testPages6_3() {
        int expected=2;
        int actual=pages(6,3);
        assertEquals(expected,actual);
    }
    @Test public void testFromString() {
        //"java.awt.Color[r=1,g=12,b=123]";
        Color expected=new Color(1,12,123);
        String string=expected.toString();
        Color actual=fromString(string);
        assertEquals(expected,actual);
    }
}
