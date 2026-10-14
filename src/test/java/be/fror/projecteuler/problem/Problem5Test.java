package be.fror.projecteuler.problem;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class Problem5Test {

  @Test
  public void smallestMultipleKeepsBoundThatIsAPrimePower() {
    // 16 is 2^4, so the answer must keep the full 16, not stop at 8.
    assertThat(new Problem5().smallestMultiple(16)).isEqualTo(720_720L);
  }
}
