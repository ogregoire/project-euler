/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.problem;

/**
 *
 * @author Olivier Grégoire
 */
public class Problem48 extends AbstractProblem {

  @Override
  public Object solve() {
    final long mod = 10_000_000_000L;
    long sumMod = 0L;
    for (int n = 1; n <= 1000; n++) {
      sumMod = (sumMod + modPow(n, n, mod)) % mod;
    }
    return sumMod;
  }

  long modPow(long base, int exp, long mod) {
    base %= mod;
    long result = 1;
    while (exp > 0) {
      if ((exp & 1) == 1) {
        result = mulMod(result, base, mod);
      }
      base = mulMod(base, base, mod);
      exp >>= 1;
    }
    return result;
  }

  /**
   * Returns {@code a * b % mod} for {@code a, b < mod < 2^41}. The full product can need over 64
   * bits, so {@code b} is split at bit 20 into two parts whose products with {@code a} still fit.
   */
  long mulMod(long a, long b, long mod) {
    long high = a * (b >>> 20) % mod;
    return ((high << 20) + a * (b & 0xF_FFFF)) % mod;
  }
}
