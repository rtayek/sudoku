:: generate the .csv file
:: this file needs to be edited before using with RunMain (probably in other project)
:: iirc, the first line or two needs to be deleted.
:: this was copied from sudokuqq/ - let's see if it works here
:: needed to change class path to bin/main/ 
:: so now we can just use this project and trash sudokuqq?
java -cp bin/main com.qqwing.QQWingMain --count-solutions --difficulty expert --generate 5000 --solution --csv