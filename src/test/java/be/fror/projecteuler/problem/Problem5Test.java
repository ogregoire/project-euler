package be.fror.projecteuler.problem;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Problem5Test {

  @Test
  public void smallestMultipleKeepsBoundThatIsAPrimePower() {
    // 16 is 2^4, so the answer must keep the full 16, not stop at 8.
    assertEquals(720_720L, new Problem5().smallestMultiple(16));
  }
}
