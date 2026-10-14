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

import be.fror.projecteuler.math.Primes;

/**
 * <p>
 * 2520 is the smallest number that can be divided by each of the numbers from 1 to 10 without any
 * remainder.</p>
 * <p>
 * What is the smallest positive number that is <dfn title="divisible with no remainder">evenly
 * divisible</dfn> by all of the numbers from 1 to 20?</p>
 *
 * @author Olivier Grégoire &lt;fror@users.noreply.github.com&gt;
 */
public class Problem5 extends AbstractProblem {

  @Override
  public Long solve() {
    return smallestMultiple(20);
  }

  long smallestMultiple(int max) {
    long n = 1;
    for (long p : Primes.basicSieve(max)) {
      long m = p;
      for (long x = p * p; x <= max; x *= p) {
        m = x;
      }
      n *= m;
    }
    return n;
  }

}
