/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.math;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Olivier Grégoire
 */
public class ToolsTest {

  @Test
  public void testIsPalindromic() {
    for (int i = 1; i < 100; i++) {
      assertThat(Tools.isPalindromic(i)).isEqualTo(i < 10 || i % 11 == 0);
    }
    for (int i = 101; i < 1000; i++) {
      assertThat(Tools.isPalindromic(i)).isEqualTo(i / 100 == i % 10);
    }
  }

  @Test
  public void zeroIsPalindromic() {
    assertThat(Tools.isPalindromic(0)).isTrue();
  }

  @Test
  public void negativeNumbersAreNotPalindromic() {
    assertThat(Tools.isPalindromic(-1)).isFalse();
    assertThat(Tools.isPalindromic(-121)).isFalse();
    assertThat(Tools.isPalindromic(Integer.MIN_VALUE)).isFalse();
  }

  @Test
  public void handlesTenDigitNumbers() {
    assertThat(Tools.isPalindromic(2_147_447_412)).isTrue();
    assertThat(Tools.isPalindromic(1_000_000_001)).isTrue();
    assertThat(Tools.isPalindromic(1_000_000_000)).isFalse();
    assertThat(Tools.isPalindromic(Integer.MAX_VALUE)).isFalse();
  }

  @Test
  public void handlesEvenAndOddDigitCounts() {
    assertThat(Tools.isPalindromic(1221)).isTrue();
    assertThat(Tools.isPalindromic(12321)).isTrue();
    assertThat(Tools.isPalindromic(1231)).isFalse();
    assertThat(Tools.isPalindromic(12331)).isFalse();
    assertThat(Tools.isPalindromic(100_001)).isTrue();
    assertThat(Tools.isPalindromic(100_010)).isFalse();
  }

  @Test
  public void longVersionAgreesWithIntVersion() {
    for (int i = -1_000; i < 1_000_000; i++) {
      assertThat(Tools.isPalindromic((long) i)).as("%d", i).isEqualTo(Tools.isPalindromic(i));
    }
  }

  @Test
  public void longVersionHandlesNineteenDigitNumbers() {
    assertThat(Tools.isPalindromic(9_223_372_036_302_733_229L)).isTrue();
    assertThat(Tools.isPalindromic(1_000_000_000_000_000_001L)).isTrue();
    assertThat(Tools.isPalindromic(1_000_000_000_000_000_000L)).isFalse();
    assertThat(Tools.isPalindromic(Long.MAX_VALUE)).isFalse();
    assertThat(Tools.isPalindromic(Long.MIN_VALUE)).isFalse();
  }

  @Test
  public void binaryCheckMatchesBinaryString() {
    for (int i = 0; i < 1_000_000; i++) {
      var binary = Integer.toBinaryString(i);
      assertThat(Tools.isBinaryPalindromic(i)).as(binary)
          .isEqualTo(binary.equals(new StringBuilder(binary).reverse().toString()));
    }
  }

  @Test
  public void binaryCheckHandlesExtremes() {
    assertThat(Tools.isBinaryPalindromic(Integer.MAX_VALUE)).isTrue();
    assertThat(Tools.isBinaryPalindromic(1 << 30)).isFalse();
    assertThat(Tools.isBinaryPalindromic(-1)).isFalse();
    assertThat(Tools.isBinaryPalindromic(Integer.MIN_VALUE)).isFalse();
  }

  @Test
  public void digitArrayCheck() {
    assertThat(Tools.isPalindromic(new int[] {})).isTrue();
    assertThat(Tools.isPalindromic(new int[] {7})).isTrue();
    assertThat(Tools.isPalindromic(new int[] {1, 2, 1})).isTrue();
    assertThat(Tools.isPalindromic(new int[] {1, 2, 2, 1})).isTrue();
    assertThat(Tools.isPalindromic(new int[] {1, 2})).isFalse();
    assertThat(Tools.isPalindromic(new int[] {1, 2, 3, 1})).isFalse();
  }
}
