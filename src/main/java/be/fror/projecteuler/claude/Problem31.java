package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Coin sums.
 *
 * <p>The classic dynamic programming over amounts, made about seven times smaller by two facts:
 *
 * <ul>
 *   <li>With only 1p and 2p coins, an amount n can be paid in {@code n / 2 + 1} ways (any number of
 *       2p coins from 0 to n / 2), so these coins need no pass at all.
 *   <li>Every other coin is a multiple of 5p. Once they are involved, only amounts that differ from
 *       the target by a multiple of 5p can matter, which is one amount in five.
 * </ul>
 */
public class Problem31 extends AbstractProblem {

  private static final int TARGET = 200;

  /** The coins above 5p, all multiples of 5p. */
  private static final int[] LARGE_COINS = {10, 20, 50, 100, 200};

  @Override
  public Object solve() {
    // ways[k] counts the ways to pay the amount offset + 5k.
    var offset = TARGET % 5;
    var ways = new int[TARGET / 5 + 1];
    ways[0] = offset / 2 + 1;
    for (var k = 1; k < ways.length; k++) {
      // Either no 5p coin at all, or one 5p coin plus any way to pay the rest.
      ways[k] = (offset + 5 * k) / 2 + 1 + ways[k - 1];
    }
    for (var coin : LARGE_COINS) {
      var step = coin / 5;
      for (var k = step; k < ways.length; k++) {
        ways[k] += ways[k - step];
      }
    }
    return ways[ways.length - 1];
  }
}
