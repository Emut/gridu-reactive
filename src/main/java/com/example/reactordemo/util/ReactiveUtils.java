package com.example.reactordemo.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;
import reactor.util.context.Context;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@UtilityClass
public class ReactiveUtils {

    public static final String MDC_KEY = "MDC";

    public static Context mdcContextModifier(Context context) {
        Map<String, String> currentMdc = Optional.ofNullable(MDC.getCopyOfContextMap())
                .orElse(Collections.emptyMap());

        if (context.hasKey(MDC_KEY)) {
            ((Map<String, String>) context.get(MDC_KEY)).putAll(currentMdc);
            return context;
        } else {
            return context.put(MDC_KEY, currentMdc);
        }
    }

    public static <T> void logOnEachWithMdcFromContext(Signal<T> signal, Consumer<T> doOnNext, Consumer<Throwable> doOnError) {
        log.trace("Context is:{}", signal.getContextView());

        Map<String, String> mdc = signal.getContextView().getOrDefault(MDC_KEY, Collections.emptyMap());
        MDC.setContextMap(mdc);
        try {
            if (signal.getType() == SignalType.ON_ERROR) {
                doOnError.accept(signal.getThrowable());
            } else if (signal.getType() == SignalType.ON_NEXT) {
                T value = signal.get();
                doOnNext.accept(value);
            }
        } finally {
            MDC.clear();
        }
    }
}
