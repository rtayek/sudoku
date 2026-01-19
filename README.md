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

## Input format

CSV lines are `puzzle,solution`. Digits use `0` for blanks; spaces are ignored.

## Output

- Images: per-puzzle PNG/JPEG/GIF/BMP written to the working directory.
- PDFs: sent through the OS print dialog.

## Notes

- Color settings are stored in `sudoku.properties`.
- Extra generator experiments live in `q/` and `r/`.
