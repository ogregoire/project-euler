/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.problem;

import static be.fror.projecteuler.math.IntMath2.gcd;

/**
 * @author Olivier Grégoire
 */
public class Problem29 extends AbstractProblem {

    @Override
    public Object solve() {

        int N = 100;
        int maxPow = Integer.SIZE - Integer.numberOfLeadingZeros(N) - 1;
        int[] dupesPerPow = new int[maxPow + 1];
        int[] numOfPow = new int[maxPow + 1];
        int totDupes = 0;

        // Step one: find dupes per power.
        for (int a = 2; a <= maxPow; a++) {
            boolean[] powOverlap = new boolean[N + 1];
            for (int k = 1; k < a; k++) {
                int spacing = k / gcd(k, a);
                for (int n = 0, l = k * N / a; n <= l; n += spacing) {
                    powOverlap[n] = true;
                }
            }
            for (int j = 2; j <= N; j++) {
                if (powOverlap[j]) {
                    dupesPerPow[a]++;
                }
            }
        }

        // Step two: find actual number of powers.
        int sqrtN = (int) Math.sqrt(N);
        boolean[] powersNotToRepeat = new boolean[sqrtN + 1];
        for (int i = 2; i <= sqrtN; i++) {
            if (!powersNotToRepeat[i]) {
                for (int p = 2, pow = i * i; pow <= N; p++, pow *= i) {
                    numOfPow[p]++;
                    if (pow <= sqrtN) {
                        powersNotToRepeat[pow] = true;
                    }
                }
            }
        }

        for (int p = 2; p <= maxPow; p++) {
            totDupes += numOfPow[p] * dupesPerPow[p];
        }
        return (N - 1) * (N - 1) - totDupes;

    }
}
