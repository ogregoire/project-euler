package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Coded triangle numbers.
 *
 * <p>The words are loaded once, unquoted, as ASCII byte arrays rather than strings: summing a
 * {@code byte[]} is a tight loop with no per-character method call. Each letter's value is its
 * code minus {@code 'A' - 1}, so that offset is subtracted once per word instead of per letter.
 *
 * <p>A value {@code t} is triangular exactly when {@code 8t + 1} is a perfect square, which needs
 * no table sized for an unknown maximum word value.
 */
public class Problem42 extends AbstractProblem {

  private static final byte[][] WORDS = readWords("/words.txt");

  /** Reads a comma-separated list of quoted words. */
  private static byte[][] readWords(String name) {
    try (var in = Problem42.class.getResourceAsStream(name)) {
      var quoted = new String(in.readAllBytes(), StandardCharsets.US_ASCII).split(",");
      var words = new byte[quoted.length][];
      for (var i = 0; i < quoted.length; i++) {
        var word = quoted[i];
        words[i] = word.substring(1, word.length() - 1).getBytes(StandardCharsets.US_ASCII);
      }
      return words;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public Object solve() {
    var count = 0;
    for (var word : WORDS) {
      var value = -word.length * ('A' - 1);
      for (var letter : word) {
        value += letter;
      }
      count += isTriangular(value) ? 1 : 0;
    }
    return count;
  }

  private static boolean isTriangular(int t) {
    var square = 8 * t + 1;
    var root = (int) Math.sqrt(square);
    return root * root == square;
  }
}
