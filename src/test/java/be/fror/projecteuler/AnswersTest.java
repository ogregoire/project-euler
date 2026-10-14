package be.fror.projecteuler;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Checks every solution, yours and Claude's, against the hashed answers in {@code answers.sha256}.
 *
 * <p>Only hashes are compared, so a failure never prints an answer.
 */
public class AnswersTest {

  static List<Arguments> solutions() throws IOException {
    var factory = ProblemFactory.load();
    var solutions = new ArrayList<Arguments>();
    try (var reader = new BufferedReader(new InputStreamReader(
        AnswersTest.class.getResourceAsStream("/answers.sha256"), UTF_8))) {
      for (var line : reader.lines().filter(l -> !l.isBlank() && !l.startsWith("#")).toList()) {
        var fields = line.split(" ");
        var id = Integer.parseInt(fields[0]);
        var hash = fields[1];
        solutions.add(Arguments.of("Problem " + id + ", yours", factory.getProblem(id), hash));
        factory.getClaudeProblem(id).ifPresent(claude ->
            solutions.add(Arguments.of("Problem " + id + ", Claude's", claude, hash)));
      }
    }
    return solutions;
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("solutions")
  public void answerMatchesRecordedHash(String name, Problem problem, String expectedHash)
      throws NoSuchAlgorithmException {
    var answer = String.valueOf(problem.solve());
    var hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(answer.getBytes(UTF_8)));

    assertThat(hash).as("SHA-256 of the answer").isEqualTo(expectedHash);
  }
}
