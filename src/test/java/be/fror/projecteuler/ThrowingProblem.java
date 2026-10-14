package be.fror.projecteuler;

/** A problem whose solution always throws the given failure. */
final class ThrowingProblem implements Problem {

  private final int id;
  private final Throwable failure;

  ThrowingProblem(int id, Throwable failure) {
    this.id = id;
    this.failure = failure;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public Object solve() {
    if (failure instanceof Error error) {
      throw error;
    }
    throw (RuntimeException) failure;
  }

  @Override
  public Object call() {
    return solve();
  }
}
