:: let's see if qq generates the same stuff each time
java -cp bin/main com.qqwing.QQWingMain --count-solutions --difficulty expert --generate 2 --solution --csv
echo -----------------
java -cp bin/main com.qqwing.QQWingMain --count-solutions --difficulty expert --generate 2 --solution --csv