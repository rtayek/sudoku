 Handoff Summary

  - Refactored the UI so Main is now a coordinator that plugs in SudokuCanvas (rendering via SudokuPainter) and
    MainControls, and exposed SudokuGuiLauncher for Swing entry while keeping the CLI entry intact.
  - Pulled Struct out to p.Struct with all puzzle/layout metadata so Main, painter, and controls share one data
    model; painting/image-exporting now go through SudokuPainter.
  - com.qqwing is canonical under sudoku; deleted the duplicate package from sudokuqq to avoid drift.
  - Build: ./gradlew clean build (passes with legacy warnings).

  Next actions: keep splitting Main so controls/menus live separately, and consider migrating MainControls to a panel
  with layout managers (e.g., BorderLayout) for richer resizing behavior.