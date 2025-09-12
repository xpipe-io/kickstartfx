package io.abc_def.kickstart_fx.util;

@FunctionalInterface
public interface FailableFunction<T, R, E extends Throwable> {

    R apply(T var1) throws E;
}
