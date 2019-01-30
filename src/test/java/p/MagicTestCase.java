package p;
import static org.junit.Assert.*;
import org.junit.*;
import static p.Magic.*;
import static p.Sudoku.*;
import static p.Main.*;
import java.io.*;
public class MagicTestCase {
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testMagic() {}
    @Test public void testRoundTrip() {
        Magic expected=create(EasyPuzzle4254328020);
        StringWriter stringWriter=new StringWriter();
        stringWriter.write(expected.toString());
        Magic actual=Magic.fromString(stringWriter.toString());
        assertArrayEquals(expected.magic,actual.magic);
    }
    @Test public void testRoundTrip2() {
        Magic expected=create(EasyPuzzle4254328020);
        Magic actual=fromString2(expected.toString2());
        assertArrayEquals(expected.magic,actual.magic);
    }
    @Test public void testRoundTrip3() {
        Magic expected=create(EasyPuzzle4254328020);
        Magic actual=fromString3(expected.toString3());
        assertArrayEquals(expected.magic,actual.magic);
    }
    @Test public void testSudoku1Spaces() {
        Sudoku sudoku=Sudoku.fromString(sudoku1Spaces);
        assertTrue(sudoku.isConsistent());
        
    }
}
