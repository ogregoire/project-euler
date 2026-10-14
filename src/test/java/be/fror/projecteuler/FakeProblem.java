package be.fror.projecteuler;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Arrays;

/**
 * A problem whose runs take the given durations on a fake clock, in order. Once they're used up,
 * every further run takes as long as the last one.
 */
final class FakeProblem implements Problem {

  private final int id;
  private final Object answer;
  private final FakeClock clock;
  private final long[] runNanos;
  private int runs;

  private FakeProblem(int id, Object answer, FakeClock clock, long[] runNanos) {
    this.id = id;
    this.answer = answer;
    this.clock = clock;
    this.runNanos = runNanos;
  }

  static FakeProblem takingMillis(Object answer, FakeClock clock, long... runMillis) {
    return new FakeProblem(
        7, answer, clock, Arrays.stream(runMillis).map(MILLISECONDS::toNanos).toArray());
  }

  static FakeProblem takingNanos(Object answer, FakeClock clock, long... runNanos) {
    return new FakeProblem(7, answer, clock, runNanos);
  }

  /** Returns a copy of this problem, with the given ID instead of 7. */
  FakeProblem withId(int id) {
    return new FakeProblem(id, answer, clock, runNanos);
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public Object solve() {
    clock.advanceNanos(runNanos[Math.min(runs++, runNanos.length - 1)]);
    return answer;
  }
}
