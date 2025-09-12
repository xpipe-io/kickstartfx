package io.abc_def.kickstart_fx.util;

@FunctionalInterface
public interface FailableRunnable<E extends Throwable> {

    void run() throws E;
}
