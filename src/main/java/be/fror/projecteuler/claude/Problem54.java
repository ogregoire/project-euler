package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Poker hands.
 *
 * <p>Each hand is reduced to a single {@code long} score such that a better hand always has a
 * higher score, so deciding a deal is one comparison. The category (high card up to straight
 * flush) sits in the top bits. Below it, ties are broken by four bit sets of card values: those
 * appearing at least 4, 3, 2 and 1 times. Comparing these sets as numbers, highest first, is
 * exactly poker's rule within a category: the larger groups are compared first (the three of a
 * full house before its pair), and comparing two equally sized sets compares their values from the
 * highest down. A royal flush needs no special case: it is the best straight flush.
 *
 * <p>The sets are built without branches: adding a card moves its value one level up. The file is
 * loaded once as raw ASCII bytes and read in place, so no objects, sorting or collections are
 * involved.
 */
public class Problem54 extends AbstractProblem {

  private static final byte[] DEALS = readResource("/p054_poker.txt");

  /** Card values from lowest to highest, each mapped to its index in this string. */
  private static final String VALUES = "23456789TJQKA";

  private static final int[] VALUE_OF = new int[128];

  static {
    for (var i = 0; i < VALUES.length(); i++) {
      VALUE_OF[VALUES.charAt(i)] = i;
    }
  }

  /** Bytes taken by five cards, each written as value, suit and a separator. */
  private static final int HAND_LENGTH = 15;

  /** Bits per value set: one per card value. */
  private static final int SET_BITS = VALUES.length();

  private static final int HIGH_CARD = 0;
  private static final int ONE_PAIR = 1;
  private static final int TWO_PAIRS = 2;
  private static final int THREE_OF_A_KIND = 3;
  private static final int STRAIGHT = 4;
  private static final int FLUSH = 5;
  private static final int FULL_HOUSE = 6;
  private static final int FOUR_OF_A_KIND = 7;
  private static final int STRAIGHT_FLUSH = 8;

  private static final int LOWEST_STRAIGHT = 0b11111;
  /** A, 2, 3, 4, 5: the ace plays low, making a straight below 2 to 6. */
  private static final int WHEEL = 1 << (SET_BITS - 1) | 0b1111;

  private static byte[] readResource(String name) {
    try (var in = Problem54.class.getResourceAsStream(name)) {
      return in.readAllBytes();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public Object solve() {
    var data = DEALS;
    var wins = 0;
    var i = 0;
    while (i + 2 * HAND_LENGTH - 1 <= data.length) {
      if (score(data, i) > score(data, i + HAND_LENGTH)) {
        wins++;
      }
      i += 2 * HAND_LENGTH - 1;
      // Skip the line terminator, whatever its form.
      while (i < data.length && data[i] <= ' ') {
        i++;
      }
    }
    return wins;
  }

  /** Scores the five cards starting at {@code offset}, as described in the class Javadoc. */
  static long score(byte[] data, int offset) {
    // atLeast1 holds the values seen at least once, atLeast2 at least twice, and so on.
    var atLeast1 = 0;
    var atLeast2 = 0;
    var atLeast3 = 0;
    var atLeast4 = 0;
    var suits = 0;
    for (var card = offset; card < offset + HAND_LENGTH; card += 3) {
      var value = 1 << VALUE_OF[data[card]];
      atLeast4 |= atLeast3 & value;
      atLeast3 |= atLeast2 & value;
      atLeast2 |= atLeast1 & value;
      atLeast1 |= value;
      // The suit letters C, D, H and S differ in their low 5 bits.
      suits |= 1 << (data[card + 1] & 31);
    }

    var flush = Integer.bitCount(suits) == 1;
    int category;
    if (atLeast4 != 0) {
      category = FOUR_OF_A_KIND;
    } else if (atLeast3 != 0) {
      category = atLeast2 != atLeast3 ? FULL_HOUSE : THREE_OF_A_KIND;
    } else if (atLeast2 != 0) {
      category = Integer.bitCount(atLeast2) == 2 ? TWO_PAIRS : ONE_PAIR;
    } else {
      var wheel = atLeast1 == WHEEL;
      if (wheel) {
        // Without its ace, the wheel ranks below every other straight.
        atLeast1 = LOWEST_STRAIGHT >> 1;
      }
      var straight =
          wheel || atLeast1 >> Integer.numberOfTrailingZeros(atLeast1) == LOWEST_STRAIGHT;
      category = straight ? (flush ? STRAIGHT_FLUSH : STRAIGHT) : (flush ? FLUSH : HIGH_CARD);
    }
    return (long) category << (4 * SET_BITS)
        | (long) atLeast4 << (3 * SET_BITS)
        | (long) atLeast3 << (2 * SET_BITS)
        | (long) atLeast2 << SET_BITS
        | atLeast1;
  }
}
