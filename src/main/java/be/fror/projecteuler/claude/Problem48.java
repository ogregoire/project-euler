package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Self powers.
 *
 * <p>The last ten digits are the sum modulo {@code 10^10 = 2^10 * 5^10}. Working modulo 10^10
 * directly would overflow a {@code long} when multiplying two residues, so the sum is computed
 * separately modulo each factor and recombined once with the Chinese remainder theorem:
 *
 * <ul>
 *   <li>Modulo {@code 2^10}, plain {@code long} multiplication already keeps the low bits exact
 *       when it wraps, so the powers need no reduction at all until the end.
 *   <li>Modulo {@code 5^10}, residues are below 2^24, so their products fit in a {@code long}.
 * </ul>
 *
 * <p>Both residues of a power are computed in the same loop, as two independent dependency chains
 * that the CPU can run in parallel. Multiples of 10 are skipped: {@code n^n} is divisible by
 * {@code 10^n}, hence by 10^10.
 */
public class Problem48 extends AbstractProblem {

  private static final int N = 1000;
  private static final int DIGITS = 10;
  private static final long MOD_2 = 1L << DIGITS;
  private static final long MOD_5 = pow(5, DIGITS);

  @Override
  public Object solve() {
    var sum2 = 0L;
    var sum5 = 0L;
    for (var n = 1; n <= N; n++) {
      if (n % 10 == 0) {
        continue;
      }
      var power2 = 1L;
      var power5 = 1L;
      var base2 = (long) n;
      var base5 = (long) n;
      for (var exp = n; exp > 0; exp >>= 1) {
        if ((exp & 1) != 0) {
          power2 *= base2;
          power5 = power5 * base5 % MOD_5;
        }
        base2 *= base2;
        base5 = base5 * base5 % MOD_5;
      }
      sum2 += power2;
      sum5 += power5;
    }
    return combine(sum2 & (MOD_2 - 1), sum5 % MOD_5);
  }

  /** Returns the residue modulo 10^10 that is {@code r2} modulo 2^10 and {@code r5} modulo 5^10. */
  private static long combine(long r2, long r5) {
    // Newton's iteration for an inverse modulo a power of 2: x*x = 1 (mod 8) for any odd x, and
    // each step doubles the number of correct low bits (3, 6, 12).
    var inverse = MOD_5;
    for (var i = 0; i < 2; i++) {
      inverse *= 2 - MOD_5 * inverse;
    }
    var k = (r2 - r5) * inverse & (MOD_2 - 1);
    return r5 + MOD_5 * k;
  }

  private static long pow(long base, int exp) {
    var result = 1L;
    for (var i = 0; i < exp; i++) {
      result *= base;
    }
    return result;
  }
}
