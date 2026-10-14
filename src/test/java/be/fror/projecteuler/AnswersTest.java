package be.fror.projecteuler;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Checks every solution, yours and Claude's, against the hashed answers in {@code answers.sha256}.
 *
 * <p>Only hashes are compared, so a failure never prints an answer.
 */
@RunWith(Parameterized.class)
public class AnswersTest {

  @Parameters(name = "{0}")
  public static List<Object[]> solutions() throws IOException {
    var factory = new ProblemFactory();
    var solutions = new ArrayList<Object[]>();
    try (var reader = new BufferedReader(new InputStreamReader(
        AnswersTest.class.getResourceAsStream("/answers.sha256"), UTF_8))) {
      for (var line : reader.lines().filter(l -> !l.isBlank() && !l.startsWith("#")).toList()) {
        var fields = line.split(" ");
        var id = Integer.parseInt(fields[0]);
        var hash = fields[1];
        solutions.add(new Object[] {"Problem " + id + ", yours", factory.getProblem(id), hash});
        factory.getClaudeProblem(id).ifPresent(claude ->
            solutions.add(new Object[] {"Problem " + id + ", Claude's", claude, hash}));
      }
    }
    return solutions;
  }

  @Parameter(0)
  public String name;

  @Parameter(1)
  public Problem problem;

  @Parameter(2)
  public String expectedHash;

  @Test
  public void answerMatchesRecordedHash() throws NoSuchAlgorithmException {
    var answer = String.valueOf(problem.solve());
    var hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(answer.getBytes(UTF_8)));

    assertEquals("SHA-256 of the answer", expectedHash, hash);
  }
}
