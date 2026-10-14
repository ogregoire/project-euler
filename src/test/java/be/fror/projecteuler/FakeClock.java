package be.fror.projecteuler;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

/** A nanosecond clock that moves when told to, and optionally by a fixed tick on every read. */
final class FakeClock implements LongSupplier {

  private final AtomicLong nanos = new AtomicLong();
  private final long tickNanos;

  FakeClock() {
    this(0);
  }

  FakeClock(long tickNanos) {
    this.tickNanos = tickNanos;
  }

  void advanceNanos(long nanos) {
    this.nanos.addAndGet(nanos);
  }

  @Override
  public long getAsLong() {
    return nanos.addAndGet(tickNanos);
  }
}
