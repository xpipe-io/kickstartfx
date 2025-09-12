package io.abc_def.kickstart_fx.util;

@FunctionalInterface
public interface FailableConsumer<T, E extends Throwable> {

    void accept(T var1) throws E;
}
