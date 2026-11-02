/*
 * Copyright 2015 Olivier Grégoire <fror@users.noreply.github.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.fror.projecteuler.problem;

/**
 * <p>
 * If we list all the natural numbers below 10 that are multiples of 3 or 5, we get 3, 5, 6 and 9.
 * The sum of these multiples is 23.</p>
 * <p>
 * Find the sum of all the multiples of 3 or 5 below 1000.</p>
 *
 * @author Olivier Grégoire &lt;fror@users.noreply.github.com&gt;
 */
public class Problem1 extends AbstractProblem {

    // Volatile, so the JIT reloads them on every call and can't fold or hoist the computation.
    private volatile int a = 3;
    private volatile int b = 5;
    private volatile int limit = 1000;

    @Override
    public Object solve() {
        return sumOfMultiplesBelow(a, b, limit);
    }

    /** Sum of the multiples of {@code a} or {@code b} below {@code limit}; a and b must be coprime. */
    private static int sumOfMultiplesBelow(int a, int b, int limit) {
        var n = limit - 1;
        return sumOfMultiples(a, n) + sumOfMultiples(b, n) - sumOfMultiples(a * b, n);
    }

    /** Sum of the positive multiples of {@code x} up to {@code n}, inclusive. */
    private static int sumOfMultiples(int x, int n) {
        var i = n / x;
        return i * (i + 1) / 2 * x;
    }

}
