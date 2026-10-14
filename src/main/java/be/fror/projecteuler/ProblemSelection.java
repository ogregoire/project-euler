package be.fror.projecteuler;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;

/** Parses which problems to run, such as {@code 23}, {@code 1-10}, {@code 1,5,20-30} or {@code all}. */
final class ProblemSelection {

  static final String USAGE =
      "Usage: ./euler [-h | --help] [<problem> | <from>-<to> | all][,...]";

  private ProblemSelection() {}

  /**
   * Returns the selected problems, in the order given and without duplicates.
   *
   * <p>A problem named on its own must exist. Ranges and {@code all} skip unsolved problems.
   *
   * @throws IllegalArgumentException if the selection is invalid or names an unsolved problem.
   */
  static List<Integer> parse(List<String> args, SortedSet<Integer> solved) {
    var selected = new LinkedHashSet<Integer>();
    for (var arg : args) {
      // Commas or spaces, so that a line typed at the prompt reads like the arguments.
      for (var token : arg.split("[,\\s]+")) {
        if (token.isEmpty()) {
          continue;
        }
        if (token.equals("all")) {
          selected.addAll(solved);
        } else if (token.contains("-")) {
          var bounds = token.split("-", 2);
          var from = parseId(bounds[0], token);
          var to = parseId(bounds[1], token);
          if (from > to) {
            throw new IllegalArgumentException("Empty range: " + token);
          }
          selected.addAll(solved.subSet(from, to + 1));
        } else {
          var id = parseId(token, token);
          if (!solved.contains(id)) {
            throw new IllegalArgumentException("Problem " + id + " not found.");
          }
          selected.add(id);
        }
      }
    }
    if (selected.isEmpty()) {
      throw new IllegalArgumentException("No problem selected.");
    }
    return new ArrayList<>(selected);
  }

  private static int parseId(String text, String token) {
    try {
      return Integer.parseInt(text);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Not a problem, range or 'all': " + token);
    }
  }
}
