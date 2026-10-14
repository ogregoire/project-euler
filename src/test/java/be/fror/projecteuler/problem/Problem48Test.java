package be.fror.projecteuler.problem;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class Problem48Test {

  @Test
  public void modPowHandlesProductsBeyondLongRange() {
    // 9_999_999_999 is -1 modulo 10^10, so its square is 1, but the plain product needs 67 bits.
    assertThat(new Problem48().modPow(9_999_999_999L, 2, 10_000_000_000L)).isEqualTo(1L);
  }
}
