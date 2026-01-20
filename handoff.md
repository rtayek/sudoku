• Hand Off

  - UI refactor: Main now orchestrates SudokuCanvas, SudokuPainter, and the new MainControls panel, which hosts all
    buttons/color controls, while MainMenu builds the JMenuBar so Main just wires pieces together (p/Main.java, p/
    MainControls.java, p/MainMenu.java, p/SudokuCanvas.java, p/SudokuPainter.java).
  - Logic extraction: Struct moved into its own class to hold layout/puzzle metadata and delegate image/print helpers
    (p/Struct.java), and SudokuGuiLauncher keeps Swing startup separate from the CLI entry point.
  - Tests: Added parameterized LoadSudokusTest (reads sudokus_3.csv, qqexpert1500edited.csv, qqexpert1500.csv) to
    exercise Main.loadSudokus against real datasets (p/LoadSudokusTest.java).
  - Build status: ./gradlew clean test passes (Gradle warns about deprecated APIs but succeeds).

  Next steps: Continue chipping away at Main by splitting the controls panel into components (e.g., separate color
  chooser from navigation) and consider turning the painter/struct into smaller service classes so CLI runners can
  reuse them easily.
  