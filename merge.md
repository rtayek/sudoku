
• Here’s a focused duplicate inventory and cleanup suggestions across sudoku, sudoku2, and sudokuqq.

  Identical files (sudoku vs sudoku2)

  - sudoku/src/main/java/p/Grid.java == sudoku2/src/main/java/p/Grid.java
  - sudoku/src/main/java/p/Hack.java == sudoku2/src/main/java/p/Hack.java
  - sudoku/src/main/java/p/ReadMe.java == sudoku2/src/main/java/p/ReadMe.java
  - sudoku/src/main/java/p/SortedProperties.java == sudoku2/src/main/java/p/SortedProperties.java
  - sudoku/src/main/java/p/WriteImageType.java == sudoku2/src/main/java/p/WriteImageType.java
  - sudoku/src/test/java/p/MainTestCase.java == sudoku2/src/test/java/p/MainTestCase.java
  - sudoku/src/test/java/p/MagicTestCase.java == sudoku2/src/test/java/p/MagicTestCase.java

  QQWing duplicates (sudoku2 vs sudokuqq)

  - Identical:
    sudoku2/src/main/java/com/qqwing/Action.java == sudokuqq/src/com/qqwing/Action.java
    sudoku2/src/main/java/com/qqwing/Difficulty.java == sudokuqq/src/com/qqwing/Difficulty.java
    sudoku2/src/main/java/com/qqwing/LogItem.java == sudokuqq/src/com/qqwing/LogItem.java
    sudoku2/src/main/java/com/qqwing/LogType.java == sudokuqq/src/com/qqwing/LogType.java
    sudoku2/src/main/java/com/qqwing/PrintStyle.java == sudokuqq/src/com/qqwing/PrintStyle.java
    sudoku2/src/main/java/com/qqwing/QQWing.java == sudokuqq/src/com/qqwing/QQWing.java
    sudoku2/src/main/java/com/qqwing/Symmetry.java == sudokuqq/src/com/qqwing/Symmetry.java
  - Different:
    sudoku2/src/main/java/com/qqwing/QQWingMain.java (final System.exit(0) commented out)
    sudokuqq/src/com/qqwing/QQWingMain.java (final System.exit(0) active)
    sudoku2/src/main/java/com/qqwing/RunQQWingMain.java (writes CSV files, 12 puzzles)

  Identical outputs

  - sudoku/yourImageName.GIF == sudoku2/yourImageName.GIF
  Not duplicates (good to keep separate)

  - sudoku/sudokus_3.csv and sudoku2/sudokus_3.csv differ.
  - sudoku/sudoku.properties and sudoku2/sudoku.properties differ.
  - The main logic files differ:
    sudoku/src/main/java/p/Base.java, sudoku/src/main/java/p/Cli.java, sudoku/src/main/java/p/Main.java, sudoku/src/
    main/java/p/RunMain.java vs their sudoku2 counterparts.

  Suggestions (pick one direction)

  1. Keep sudoku2 as the canonical project, and archive/delete sudokuqq after copying any CSVs you still want (like
     sudokuqq/qqexpert1500.csv or sudokuqq/qqexpert1500edited.csv) into sudoku2.
  2. Keep both sudoku and sudoku2, but extract the identical p/* utility classes and the identical tests into a shared
     module so they aren’t duplicated.
  3. If you want to keep both as-is, at least remove the duplicate yourImageName.* outputs from one project to reduce
     clutter.