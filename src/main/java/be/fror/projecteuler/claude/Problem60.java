package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

import java.util.Arrays;

/**
 * Prime pair sets.
 *
 * <p>Two primes are compatible when both their concatenations are prime: the answer is the
 * smallest sum of five pairwise compatible primes. Primes are taken in increasing order, and the
 * search keeps a tree of the partial sets that could still grow into a set below the best sum
 * found so far: each node adds one larger prime to its parent's partial set. A new prime extends
 * every partial set it is compatible with, which explores each compatible set exactly once.
 *
 * <p>What makes it fast is how little gets tested once a first set is found. A partial set only
 * matters while it can still reach a sum below the best one, and every prime still to come is
 * larger than all the primes already in the tree. So each node remembers the smallest sum of its
 * partial sets of each size, below it in the tree, and a new prime only descends into the
 * branches that could still beat the best sum with it. Almost every branch is cut, so only a few
 * concatenations get tested per prime, and the search stops once a prime alone is too large,
 * which proves the answer minimal without an arbitrary limit.
 *
 * <p>Cheap facts skip most primality tests: 2 and 5 are never compatible (concatenations ending
 * with them aren't prime), and a concatenation is divisible by 3 when both primes have different
 * nonzero residues modulo 3, because a number and its digit sum share their residue. A memo makes
 * sure each pair is tested once. The test itself is a deterministic Miller-Rabin test, whose cheap
 * part (trial division and a first witness) runs on both concatenations before the rest runs on
 * either, since most pairs fail.
 */
public class Problem60 extends AbstractProblem {

  private static final int SET_SIZE = 5;

  /** Trial division by these rejects most composites before the costlier Miller-Rabin test. */
  private static final int[] SMALL_PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

  /** With the other witnesses, Miller-Rabin is exact below 4,759,123,141, so for any int. */
  private static final int FIRST_WITNESS = 2;

  private static final int[] OTHER_WITNESSES = {7, 61};

  @Override
  public Object solve() {
    var search = new Search();
    for (var p = 3; p < search.best; p += 2) {
      if (p != 5 && isPrime(p)) {
        search.add(p);
      }
    }
    return search.best;
  }

  /** A set of pairwise compatible primes, which may grow into a set of {@link #SET_SIZE}. */
  private static final class PartialSet {

    final int prime;
    final int sum;
    final int size;
    final PartialSet parent;

    /**
     * For each size, the smallest sum of a partial set of that size in this subtree, or
     * {@link Integer#MAX_VALUE} if there is none.
     */
    final int[] smallestSums = new int[SET_SIZE];

    /** In increasing order of their last prime, as primes are added in increasing order. */
    PartialSet[] children = new PartialSet[4];

    int childCount;

    PartialSet(int prime, int sum, int size, PartialSet parent) {
      this.prime = prime;
      this.sum = sum;
      this.size = size;
      this.parent = parent;
      Arrays.fill(smallestSums, Integer.MAX_VALUE);
    }

    void addChild(int prime) {
      var child = new PartialSet(prime, sum + prime, size + 1, this);
      if (childCount == children.length) {
        children = Arrays.copyOf(children, childCount * 2);
      }
      children[childCount++] = child;
      child.smallestSums[child.size] = child.sum;
      for (var ancestor = this; ancestor != null; ancestor = ancestor.parent) {
        ancestor.smallestSums[child.size] = Math.min(ancestor.smallestSums[child.size], child.sum);
      }
    }

    /**
     * Whether a set through this subtree and {@code p} can sum below {@code best}: one of its
     * partial sets, plus {@code p}, plus members larger than {@code p} to complete it.
     */
    boolean canBeat(int best, int p) {
      for (var size = this.size; size < SET_SIZE; size++) {
        if (smallestSums[size] < best - (SET_SIZE - size) * p) {
          return true;
        }
      }
      return false;
    }
  }

  /** The search state: the tree of partial sets, and the best sum so far. */
  private static final class Search {

    final PartialSet root = new PartialSet(0, 0, 0, null);
    int best = Integer.MAX_VALUE;

    /**
     * The memo of compatibility with the prime being added, indexed by the other prime: valid
     * where {@code testedWith} holds the prime being added, so it never needs clearing.
     */
    int[] testedWith = new int[1024];
    boolean[] compatible = new boolean[1024];

    void add(int p) {
      if (p >= testedWith.length) {
        testedWith = Arrays.copyOf(testedWith, testedWith.length * 2);
        compatible = Arrays.copyOf(compatible, compatible.length * 2);
      }
      extend(root, p);
    }

    /** Extends {@code partialSet}, whose primes are all compatible with p, and its subtree. */
    private void extend(PartialSet partialSet, int p) {
      if (partialSet.size == SET_SIZE - 1) {
        best = Math.min(best, partialSet.sum + p);
        return;
      }
      // Children added below have p as their last prime: they must not be extended with p.
      var childCount = partialSet.childCount;
      for (var i = 0; i < childCount; i++) {
        var child = partialSet.children[i];
        var q = child.prime;
        // A set through child and p holds the partial set, q, p, and others larger than q.
        if (partialSet.sum + (SET_SIZE - 1 - partialSet.size) * q + p >= best) {
          break;
        }
        if (child.canBeat(best, p) && isCompatible(p, q)) {
          extend(child, p);
        }
      }
      if (partialSet.sum + (SET_SIZE - partialSet.size) * p < best) {
        partialSet.addChild(p);
      }
    }

    private boolean isCompatible(int p, int q) {
      if (testedWith[q] != p) {
        testedWith[q] = p;
        if (q != 3 && q % 3 != p % 3) {
          compatible[q] = false;
        } else {
          var left = concat(p, q);
          var right = concat(q, p);
          compatible[q] = isProbablePrime(left) && isProbablePrime(right)
              && isConfirmedPrime(left) && isConfirmedPrime(right);
        }
      }
      return compatible[q];
    }
  }

  /**
   * Throws if the concatenation overflows, rather than testing a wrong number. It doesn't happen
   * here: by the time primes have 5 digits, the best sum only lets them pair with smaller ones.
   */
  private static int concat(int left, int right) {
    var power = 10;
    while (power <= right) {
      power *= 10;
    }
    return Math.addExact(Math.multiplyExact(left, power), right);
  }

  private static boolean isPrime(int n) {
    return isProbablePrime(n) && isConfirmedPrime(n);
  }

  /** Rejects almost every composite, with trial division and the first witness only. */
  private static boolean isProbablePrime(int n) {
    for (var p : SMALL_PRIMES) {
      if (n % p == 0) {
        return n == p;
      }
    }
    // Without a factor below 41, any composite is at least 41².
    if (n < 41 * 41) {
      return n > 1;
    }
    return isStrongProbablePrime(n, FIRST_WITNESS);
  }

  /** Makes the test exact for a probable prime, with the other witnesses. */
  private static boolean isConfirmedPrime(int n) {
    if (n < 41 * 41) {
      return true;
    }
    for (var witness : OTHER_WITNESSES) {
      if (!isStrongProbablePrime(n, witness)) {
        return false;
      }
    }
    return true;
  }

  private static boolean isStrongProbablePrime(int n, int witness) {
    var shift = Integer.numberOfTrailingZeros(n - 1);
    var x = powMod(witness, (n - 1) >>> shift, n);
    if (x == 1 || x == n - 1) {
      return true;
    }
    for (var i = 1; i < shift; i++) {
      x = x * x % n;
      if (x == n - 1) {
        return true;
      }
    }
    return false;
  }

  private static long powMod(long base, int exponent, int modulus) {
    var result = 1L;
    for (; exponent > 0; exponent >>>= 1) {
      if ((exponent & 1) != 0) {
        result = result * base % modulus;
      }
      base = base * base % modulus;
    }
    return result;
  }
}
