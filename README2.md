
• Found sudokuqq (note spelling) at C:\dfromrays8350\ray\conradapps\sudokuqq; it’s a standalone QQWing generator project and doesn’t include any of the
  printing/rendering code.

  - sudokuqq/src/com/qqwing/* is the QQWing generator source (same package later vendored into sudoku2/src/main/java/com/qqwing/*).
  - sudokuqq/run.bat runs QQWing to generate qqexpert1500.csv and notes you may need to remove header lines.
  - sudokuqq/qqexpert1500.csv and sudokuqq/qqexpert1500edited.csv are generated data files.
  - sudokuqq has no Gradle build; it’s an Eclipse-style project with bin/ outputs.

  How it relates to the other two:

  - sudoku = colored puzzle viewer/printer (simple puzzle,solution CSV).
  - sudoku2 = sudoku + QQWing generation + filtering/solver/stats + extra CSV fields/difficulty labels.
  - sudokuqq = only QQWing generation; sudoku2 effectively absorbs it.