package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Amicable numbers.
 *
 * <p>The sum of divisors σ is multiplicative, so a linear sieve gets σ of every number from σ of a
 * smaller one with a single multiplication, visiting each composite exactly once. That is about
 * {@code N} steps instead of the {@code N ln N} of adding every divisor to all of its multiples.
 *
 * <p>For {@code m = i·p} with {@code p} the smallest prime factor of {@code m}:
 *
 * <ul>
 *   <li>if {@code p} does not divide {@code i}, then {@code σ(m) = σ(i)·(1 + p)};
 *   <li>otherwise only the factor {@code 1 + p + … + p^k} of σ(i) for the power of {@code p} grows,
 *       which is why that factor is kept for every number.
 * </ul>
 */
public class Problem21 extends AbstractProblem {

  private static final int LIMIT = 10000;

  @Override
  public Object solve() {
    var divisorSums = new int[LIMIT];
    // For each n: 1 + p + ... + p^k, where p^k is the largest power of n's smallest prime p.
    var smallestPrimePowerSums = new int[LIMIT];
    var primes = new int[LIMIT];
    var primeCount = 0;
    divisorSums[1] = 1;
    for (var i = 2; i < LIMIT; i++) {
      if (divisorSums[i] == 0) {
        primes[primeCount++] = i;
        divisorSums[i] = i + 1;
        smallestPrimePowerSums[i] = i + 1;
      }
      for (var j = 0; j < primeCount; j++) {
        var p = primes[j];
        var m = i * p;
        if (m >= LIMIT) {
          break;
        }
        if (i % p == 0) {
          var powerSum = smallestPrimePowerSums[i] * p + 1;
          smallestPrimePowerSums[m] = powerSum;
          divisorSums[m] = divisorSums[i] / smallestPrimePowerSums[i] * powerSum;
          // Larger primes are not the smallest prime factor of i * p: their turn comes later.
          break;
        }
        smallestPrimePowerSums[m] = p + 1;
        divisorSums[m] = divisorSums[i] * (p + 1);
      }
    }

    var sum = 0;
    for (var a = 2; a < LIMIT; a++) {
      var b = divisorSums[a] - a;
      // Counting each pair from its larger member means b's divisor sum is already known.
      if (b < a && divisorSums[b] - b == a) {
        sum += a + b;
      }
    }
    return sum;
  }
}
