package p;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class LoadSudokusTest {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "small built-in set", "sudokus_3.csv" },
                { "qqwing edited", "qqexpert1500edited.csv" },
                //{ "qqwing full", "qqexpert1500.csv" },
        });
    }

    private final String name;
    private final Path csv;

    public LoadSudokusTest(String name, String csvName) {
        this.name = name;
        this.csv = Paths.get(csvName);
    }

    @Test
    public void loadsFromCsv() throws Exception {
        Cli options = new Cli();
        options.filename = csv.toString();
        List<Sudoku> sudokus = Main.loadSudokus(options);
        assertTrue("expected at least one puzzle for " + name, sudokus.size() > 0);
    }
}
