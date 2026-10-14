/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.problem;

import be.fror.projecteuler.math.Primes;

/**
 *
 * @author Olivier Grégoire
 */
public class Problem37 extends AbstractProblem {

  @Override
  public Object solve() {

    int N = 1000000; // arbitrary value

    int[] powersOf10 = new int[(int) Math.log10(N) + 1];
    for (int i = 0, p = 1; i < powersOf10.length; i++, p *= 10) {
      powersOf10[i] = p;
    }

    int[] primes = Primes.sieve(N);
    int count = 11;
    int sum = 0;

    int powIndex = 0, nextPowIndex = 1;
    outer:
    for (int i = 4; count > 0 && i < primes.length; i++) { // skip 2, 3, 5 and 7
      int prime = primes[i];
      if (prime >= powersOf10[nextPowIndex]) {
        powIndex = nextPowIndex++;
      }
      // truncate right first.
      for (int n = prime / 10; n > 0; n /= 10) {
        if (!Primes.isPrime(n)) {
          continue outer;
        }
      }
      // now truncate left
      for (int pi = powIndex; pi > 0; pi--) {
        if (!Primes.isPrime(prime % powersOf10[pi])) {
          continue outer;
        }
      }
      count--;
      sum += prime;
    }
    return sum;
  }

}
