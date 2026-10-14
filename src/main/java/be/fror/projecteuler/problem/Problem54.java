/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.problem;

import be.fror.projecteuler.util.Resources;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

/**
 * In the card game poker, a hand consists of five cards and are ranked, from lowest to highest, in
 * the following way:
 *
 * High Card: Highest value card. One Pair: Two cards of the same value. Two Pairs: Two different
 * pairs. Three of a Kind: Three cards of the same value. Straight: All cards are consecutive
 * values. Flush: All cards of the same suit. Full House: Three of a kind and a pair. Four of a
 * Kind: Four cards of the same value. Straight Flush: All cards are consecutive values of same
 * suit. Royal Flush: Ten, Jack, Queen, King, Ace, in same suit.
 *
 * The cards are valued in the order: 2, 3, 4, 5, 6, 7, 8, 9, 10, Jack, Queen, King, Ace.
 *
 * If two players have the same ranked hands then the rank made up of the highest value wins; for
 * example, a pair of eights beats a pair of fives (see example 1 below). But if two ranks tie, for
 * example, both players have a pair of queens, then highest cards in each hand are compared (see
 * example 4 below); if the highest cards tie then the next highest cards are compared, and so on.
 *
 * Consider the following five hands dealt to two players: Hand	Player 1	Player 2	Winner 1	5H 5C 6S
 * 7S KD Pair of Fives 2C 3S 8S 8D TD Pair of Eights Player 2 2	5D 8C 9S JS AC Highest card Ace 2C
 * 5C 7D 8S QH Highest card Queen Player 1 3	2D 9C AS AH AC Three Aces 3D 6D 7D TD QD Flush with
 * Diamonds Player 2 4	4D 6S 9H QH QC Pair of Queens Highest card Nine 3D 6D 7H QD QS Pair of Queens
 * Highest card Seven Player 1 5	2H 2D 4C 4D 4S Full House With Three Fours 3C 3D 3S 9S 9D Full
 * House with Three Threes Player 1
 *
 * The file, poker.txt, contains one-thousand random hands dealt to two players. Each line of the
 * file contains ten cards (separated by a single space): the first five are Player 1's cards and
 * the last five are Player 2's cards. You can assume that all hands are valid (no invalid
 * characters or repeated cards), each player's hand is in no specific order, and in each hand there
 * is a clear winner.
 *
 * How many hands does Player 1 win?
 *
 *
 * @author Olivier Grégoire
 */
public class Problem54 extends AbstractProblem {

  private static final String[] lines =
      Resources.read("p054_poker.txt").lines().toArray(String[]::new);

  @Override
  public Object solve() {
    int count = 0;
    for (String line : lines) {
      Card[] cards = (Card[]) Arrays.stream(line.split(" ")).map(Card::of).toArray();
      Hand left = new Hand(Arrays.copyOfRange(cards, 0, 5));
      Hand right = new Hand(Arrays.copyOfRange(cards, 5, 10));
//      if (handComparator().compare(left, right) > 0) {
//        count++;
//      }
    }
    return count;
  }

  enum Rank {
    TWO('2'),
    THREE('3'),
    FOUR('4'),
    FIVE('5'),
    SIX('6'),
    SEVEN('7'),
    EIGHT('8'),
    NINE('9'),
    TEN('T'),
    JACK('J'),
    QUEEN('Q'),
    KING('K'),
    ACE('A');
    char character;

    Rank(char c) {
      character = c;
    }

    static Rank of(char character) {
      for (Rank rank : values()) {
        if (rank.character == character) {
          return rank;
        }
      }
      throw new IllegalArgumentException();
    }

  }

  enum Color {
    SPADES('S'),
    HEART('H'),
    DIAMOND('D'),
    CLUB('C');
    char character;

    Color(char c) {
      character = c;
    }

    static Color of(char character) {
      for (Color color : values()) {
        if (color.character == character) {
          return color;
        }
      }
      throw new IllegalArgumentException();
    }
  }

  static class Card {

    final Rank rank;
    final Color color;

    Card(Rank rank, Color color) {
      this.rank = rank;
      this.color = color;
    }

    static Card of(String s) {
      return new Card(Rank.of(s.charAt(0)), Color.of(s.charAt(1)));
    }

    static Comparator<Card> rankComparator() {
      return Comparator.comparing(c -> c.rank);
    }
  }

  static class Hand {

    Card[] cards;
    boolean flush;
    boolean royal;
    boolean straight;
    Map<Rank, Integer> kinds = new EnumMap<>(Rank.class);

    Hand(Card[] cards) {
      Arrays.sort(cards, Card.rankComparator().reversed());
      this.cards = cards;
      flush = Arrays.stream(cards).allMatch(c -> cards[0].color == c.color);
      royal = cards[0].rank == Rank.ACE;
      straight = cards[0].rank.ordinal() - cards[cards.length - 1].rank.ordinal() == cards.length - 1
          && Arrays.stream(cards).map(c -> c.rank).distinct().count() == cards.length;
      for (Rank rank : Rank.values()) {
        kinds.put(rank, 0);
      }
      for (Card c : cards) {
        kinds.put(c.rank, kinds.get(c.rank) + 1);
      }
    }

    public int[] score() {
      if (royal && flush) {
        return new int[]{9, 0};
      }
      if (straight && flush) {
        return new int[]{8, cards[0].rank.ordinal()};
      }
      return null;
    }
  }

  static Comparator<Hand> scoreComparator() {
    return (left, right) -> {
      int[] leftScore = left.score();
      int[] rightScore = right.score();


      return 1;
    };
  }
}
