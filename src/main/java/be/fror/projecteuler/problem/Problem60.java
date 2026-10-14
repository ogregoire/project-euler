/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.problem;

import static be.fror.projecteuler.math.Primes.isPrime;
import static be.fror.projecteuler.math.Primes.sieve;

/**
 *
 * @author Olivier Grégoire
 */
public class Problem60 extends AbstractProblem {

  @Override
  public Object solve() {
    int min = Integer.MAX_VALUE;
    final int[] primes = sieve();
    for (int a = 1; a < primes.length; a++) { // skip 2
      final int primeA = primes[a];
      if (primeA * 5 >= min) {
        break;
      }
      final int[] pairs = findPairs(a, primes);
      for (int bi = 0; bi < pairs.length; bi++) {
        final int primeB = primes[pairs[bi]];
        if (primeA + primeB * 4 >= min) {
          break;
        }
        final int[] triples = common(pairs, bi, primes);
        for (int ci = 0; ci < triples.length; ci++) {
          final int primeC = primes[triples[ci]];
          if (primeA + primeB + primeC * 3 >= min) {
            break;
          }
          final int[] quadruples = common(triples, ci, primes);
          for (int di = 0; di < quadruples.length; di++) {
            final int primeD = primes[quadruples[di]];
            if (primeA + primeB + primeC + primeD * 2 >= min) {
              break;
            }
            final int[] quintuples = common(quadruples, di, primes);
            if (quintuples.length > 0) {
              // Smallest, we don't care about the other candidates
              int primeE = primes[quintuples[0]];
              int sum = primeA + primeB + primeC + primeD + primeE;
              if (sum < min) {
                min = sum;
              }
            }
          }
        }
      }
    }
    return min;
  }

  private static int[] findPairs(final int indexA, final int[] primes) {
    final int[] pairs = new int[primes.length];
    int size = 0;
    final int primeA = primes[indexA];
    int powerA = 10;
    while (primeA > powerA) {
      powerA *= 10;
    }
    int powerB = powerA;
    for (int indexB = indexA + 1; indexB < primes.length; indexB++) {
      final int primeB = primes[indexB];
      if (primeB > powerB) {
        powerB *= 10;
      }
      if (isPrime(primeA * powerB + primeB) && isPrime(primeB * powerA + primeA)) {
        pairs[size++] = indexB;
      }
    }
    final int[] result = new int[size];
    System.arraycopy(pairs, 0, result, 0, size);
    return result;
  }

  private static int[] common(final int[] pairs, final int pairIndexA, final int[] primes) {
    final int[] common = new int[pairs.length];
    int size = 0;
    final int primeA = primes[pairs[pairIndexA]];
    int powerA = 10;
    while (primeA > powerA) {
      powerA *= 10;
    }
    int powerB = powerA;
    for (int pairIndexB = pairIndexA + 1; pairIndexB < pairs.length; pairIndexB++) {
      final int primeB = primes[pairs[pairIndexB]];
      if (primeB > powerB) {
        powerB *= 10;
      }
      if (isPrime(primeA * powerB + primeB) && isPrime(primeB * powerA + primeA)) {
        common[size++] = pairs[pairIndexB];
      }
    }
    final int[] result = new int[size];
    System.arraycopy(common, 0, result, 0, size);
    return result;
  }

}
