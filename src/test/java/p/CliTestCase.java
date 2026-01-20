package p;

import static org.junit.Assert.*;
import java.util.Arrays;
import org.junit.*;

public class CliTestCase {
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testTitle() {
        String[] arguments=new String[] {"-t=expert"}; // don't put a leading space in front of -t!
        System.out.println(Arrays.asList(arguments));
        Cli myOptions=new Cli();
        myOptions.options(arguments);
        System.out.println(myOptions);
    }
    @Test public void test() {
        String[] arguments=new String[] {"-p","-file="+"qqexpert1500edited.csv","-n=99","-t=expert"};
        System.out.println(Arrays.asList(arguments));
        Cli myOptions=new Cli();
        myOptions.options(arguments);
        System.out.println(myOptions);

    }
}
