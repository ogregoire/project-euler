package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Names scores.
 *
 * <p>Each name is packed into a single {@code long} key: the name as a base-27 number, padded on
 * the right so that comparing two keys as numbers compares the names alphabetically, followed by
 * the name's alphabetical value in the low bits. Sorting numbers is much cheaper than sorting
 * strings: no indirection and no character-by-character comparison. And since the low bits only
 * break ties between equal names, each name's value is simply read back after sorting.
 *
 * <p>The keys are sorted in two linear steps rather than with a general-purpose sort: a counting
 * sort on their top bits, which leaves only a few keys per bucket, and then an insertion sort,
 * which costs almost nothing on such a nearly sorted array.
 */
public class Problem22 extends AbstractProblem {

  /** Letters are 1 to 26, so that 0 can pad a name: a prefix then sorts first. */
  private static final int RADIX = 27;

  /** As many letters as fit, along with the value, in a positive {@code long}. */
  private static final int MAX_LETTERS = 11;

  /** Enough for MAX_LETTERS letters of value 26. */
  private static final int VALUE_BITS = 9;

  /** 27^11 < 2^53, so a packed name takes 53 bits, and a whole key 62. */
  private static final int KEY_BITS = 53 + VALUE_BITS;

  private static final int BUCKET_BITS = 12;
  private static final int BUCKET_SHIFT = KEY_BITS - BUCKET_BITS;

  private static final long[] KEYS = readKeys();

  @Override
  public Object solve() {
    var keys = sort(KEYS);
    var total = 0L;
    for (var i = 0; i < keys.length; i++) {
      total += (i + 1L) * (keys[i] & ((1 << VALUE_BITS) - 1));
    }
    return total;
  }

  private static long[] sort(long[] keys) {
    // Bucket starts are offset by one so that after the prefix sum, starts[b] is where b begins.
    var starts = new int[(1 << BUCKET_BITS) + 1];
    for (var key : keys) {
      starts[(int) (key >>> BUCKET_SHIFT) + 1]++;
    }
    for (var b = 1; b < starts.length; b++) {
      starts[b] += starts[b - 1];
    }
    var sorted = new long[keys.length];
    for (var key : keys) {
      sorted[starts[(int) (key >>> BUCKET_SHIFT)]++] = key;
    }

    for (var i = 1; i < sorted.length; i++) {
      var key = sorted[i];
      var j = i - 1;
      while (j >= 0 && sorted[j] > key) {
        sorted[j + 1] = sorted[j];
        j--;
      }
      sorted[j + 1] = key;
    }
    return sorted;
  }

  private static long[] readKeys() {
    String text;
    try (var in = Problem22.class.getResourceAsStream("/names.txt")) {
      text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    var names = text.split(",");
    var keys = new long[names.length];
    for (var i = 0; i < names.length; i++) {
      keys[i] = key(names[i].substring(1, names[i].length() - 1));
    }
    return keys;
  }

  private static long key(String name) {
    if (name.length() > MAX_LETTERS) {
      throw new IllegalStateException("Name too long to pack: " + name);
    }
    var packed = 0L;
    var value = 0;
    for (var i = 0; i < MAX_LETTERS; i++) {
      var letter = i < name.length() ? name.charAt(i) - 'A' + 1 : 0;
      packed = packed * RADIX + letter;
      value += letter;
    }
    return packed << VALUE_BITS | value;
  }
}
