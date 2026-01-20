# sudoku

Colored Sudoku puzzle viewer/printer with optional PDF and image output.

## Build

```
./gradlew build
```

## Run

Main entry point is `p.Main`.

```
./gradlew run --args="-f=sudokus_3.csv"
```

## CLI options

- `-h` help
- `-f` CSV file (default `sudokus_3.csv`)
- `-s` start index (0-based)
- `-n` number of puzzles
- `-i` write images and exit
- `-p` print PDF via print dialog and exit
- `-l` light option (legacy toggle)
- `-t` difficulty/title to place on printed output

## Input format

CSV lines are `puzzle,solution[,extra...]`. Digits use `0` or `.` for blanks; spaces are ignored. Extra columns are preserved and printed as `info:` on the output.

## Output

- Images: per-puzzle PNG/JPEG/GIF/BMP written to the working directory.
- PDFs: sent through the OS print dialog.

## Notes

- Color settings are stored in `sudoku.properties`.
- Extra generator experiments live in `q/` and `r/`.
 - QQWing sources live under `src/main/java/com/qqwing`; use `run.bat`/`run2.bat` to regenerate `qqexpert*.csv`.
 - Helper utilities `p.Filter`, `p.RunFilter`, `p.Solver`, and `p.Histogram` let you parse/filter CSV dumps before handing them to `p.Main`.
 - "Puzzle" is ignored if it's the first line in the .csv file.
