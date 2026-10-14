package be.fror.projecteuler.problem;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Problem48Test {

  @Test
  public void modPowHandlesProductsBeyondLongRange() {
    // 9_999_999_999 is -1 modulo 10^10, so its square is 1, but the plain product needs 67 bits.
    assertEquals(1L, new Problem48().modPow(9_999_999_999L, 2, 10_000_000_000L));
  }
}
