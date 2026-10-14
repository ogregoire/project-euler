package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Number letter counts.
 *
 * <p>Counts how often each word occurs when writing 1 to 1000 in British English, then multiplies
 * by the word lengths. The lengths of the literals are compile-time friendly, so the whole
 * computation folds to a handful of integer operations.
 */
public class Problem17 extends AbstractProblem {

  @Override
  public Object solve() {
    var units = "onetwothreefourfivesixseveneightnine".length();
    var teens = "teneleventwelvethirteenfourteenfifteensixteenseventeeneighteennineteen".length();
    var tens = "twentythirtyfortyfiftysixtyseventyeightyninety".length();
    var hundred = "hundred".length();
    var and = "and".length();

    // Below 100: units appear 9 times (alone, and after each of twenty to ninety),
    // teens once each, and each tens word ten times.
    var belowHundred = 9 * units + teens + 10 * tens;
    // Each of the ten hundreds blocks repeats 1-99. The nine "X hundred" blocks add the unit
    // name and "hundred" to all 100 numbers, and "and" to the 99 that are not round.
    var belowThousand = 10 * belowHundred + 100 * (units + 9 * hundred) + 9 * 99 * and;
    return belowThousand + "onethousand".length();
  }
}
