package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Counting Sundays.
 *
 * <p>Tracks the weekday of each first of the month with plain integer arithmetic: the next first
 * falls {@code days in month mod 7} weekdays later. This replaces date objects and calendar
 * lookups with one addition per month.
 */
public class Problem19 extends AbstractProblem {

  private static final int[] DAYS_IN_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

  private static final int SUNDAY = 6;

  @Override
  public Object solve() {
    // 1 Jan 1900 was a Monday (0) and 1900 is not a leap year, so 1 Jan 1901 is 365 days later.
    var weekday = 365 % 7;
    var count = 0;
    for (var year = 1901; year <= 2000; year++) {
      // Within the range, every year divisible by 4 is a leap year, 2000 included.
      var leap = year % 4 == 0;
      for (var month = 0; month < 12; month++) {
        if (weekday == SUNDAY) {
          count++;
        }
        var days = month == 1 && leap ? 29 : DAYS_IN_MONTH[month];
        // Every month has 28 to 31 days, so the weekday moves forward by 0 to 3.
        weekday += days - 28;
        if (weekday >= 7) {
          weekday -= 7;
        }
      }
    }
    return count;
  }
}
