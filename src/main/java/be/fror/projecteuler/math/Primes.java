/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.math;

import static java.util.Arrays.*;

import be.fror.projecteuler.collect.IntList;


/**
 *
 * @author Olivier Grégoire
 */
public final class Primes {

  private Primes() {
  }

  public static int[] basicSieve(final int upperBound) {
    return doSieve(upperBound);
  }

  public static int[] sieve() {
    return PrimesUntilLimit.PRIMES;
  }

  public static int[] sieve(final int upperBound) {
    if (upperBound <= LIMIT) {
      int[] sieve = PrimesUntilLimit.PRIMES;
      int i = binarySearch(sieve, upperBound);
      return copyOf(sieve, i < 0 ? ~i : i);
    } else {
      return doSieve(upperBound);
    }
  }

  // For upperBound <= 500_000, eratosthenes beats atkins. See benchmarks
  private static int[] doSieve(final int upperBound) {
    if (upperBound < 2) {
      return new int[0];
    }
    final int upperBoundSquareRoot = (int) Math.sqrt(upperBound);
    final boolean[] sieveBool = new boolean[upperBound + 1];
    final IntList primes = new IntList(upperBound);
    for (int candidate = 2; candidate <= upperBoundSquareRoot; candidate++) {
      if (!sieveBool[candidate]) {
        primes.add(candidate);
        for (int composite = candidate * candidate; composite <= upperBound; composite += candidate) {
          sieveBool[composite] = true;
        }
      }
    }
    for (int candidate = upperBoundSquareRoot + 1; candidate <= upperBound; candidate++) {
      if (!sieveBool[candidate]) {
        primes.add(candidate);
      }
    }
    return primes.toArray();
  }

  // Currently, limit is sqrt(Integer.MAX_VALUE)
  private static final int LIMIT = (int) Math.sqrt(Integer.MAX_VALUE);

  // The holder class, and so the sieve, is only initialized on first use.
  private static final class PrimesUntilLimit {

    static final int[] PRIMES = doSieve(LIMIT);
  }

  public static boolean isPrime(final int n) {
    final int[] primesUntilLimit = PrimesUntilLimit.PRIMES;
    if (n <= LIMIT) {
      return binarySearch(primesUntilLimit, n) >= 0;
    }
    final int limit = (int) Math.sqrt(n);
    for (final int p : primesUntilLimit) {
      if (p > limit) {
        return true;
      }
      if (n % p == 0) {
        return false;
      }
    }
    return true;
  }

  public static int[] factorize(int n) {
    int[] primes = Primes.basicSieve((int) Math.sqrt(n) + 1);
    int[] factors = new int[Integer.SIZE - Integer.numberOfLeadingZeros(n)];
    int size = 0;
    for (final int p : primes) {
      for (int q = n / p, r = n - (p * q); r == 0; n = q, q = n / p, r = n - (p * q)) {
        factors[size++] = p;
      }
    }
    if (size == 0) {
      return new int[]{n};
    }
    int[] result = new int[size];
    System.arraycopy(factors, 0, result, 0, size);
    return result;
  }

  public static long[] factorize(long n) {
    int[] primes = Primes.basicSieve((int) Math.sqrt(n) + 1);
    long[] factors = new long[Long.SIZE - Long.numberOfLeadingZeros(n)];
    int size = 0;
    for (final long p : primes) {
      for (long q = n / p, r = n - (p * q); r == 0L; n = q, q = n / p, r = n - (p * q)) {
        factors[size++] = p;
      }
    }
    if (size == 0) {
      return new long[]{n};
    }
    long[] result = new long[size];
    System.arraycopy(factors, 0, result, 0, size);
    return result;
  }
}
