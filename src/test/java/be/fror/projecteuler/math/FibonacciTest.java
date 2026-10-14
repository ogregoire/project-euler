/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.math;

import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Olivier Grégoire
 */
public class FibonacciTest {

    static final int[] numbers = new int[50];

    @BeforeAll
    public static void setUpClass() {
        Random random = new Random();
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = random.nextInt(1 << 20);
        }
    }

    @Test
    public void testFibonacciDoubling() {

        long sum = 0L;
        for (int n : numbers) {
            long start, end;
            start = System.nanoTime();
            Fibonacci.fibonacciDoubling(n);
            end = System.nanoTime();
            sum += end - start;
        }

        System.out.printf("testFibonacciDoubling: %.2f µs%n", sum / 1000.0 / numbers.length);
    }

    @Test
    public void testFibonacciMatrix() {

        long sum = 0L;
        for (int n : numbers) {
            long start, end;
            start = System.nanoTime();
            Fibonacci.fibonacciMatrix(n);
            end = System.nanoTime();
            sum += end - start;
        }

        System.out.printf("testFibonacciMatrix: %.2f µs%n", sum / 1000.0 / numbers.length);
    }
}
