package com.murshid.ingestor.utils;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FunctionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionUtil.class);

    public static <T> T retryFn(@Nonnull final Supplier<T> fn,
                                @Nonnull final Predicate<Throwable> shouldRetry,
                                long initialDelay,
                                @Nullable String retryMessage) {

        return retryFn(fn, shouldRetry, new RetryOptions(initialDelay).withRetryMessage(retryMessage));
    }

    public static <T> T retryFn(@Nonnull final Supplier<T> fn,
                                @Nonnull final Predicate<Throwable> shouldRetry,
                                long initialDelay,
                                @Nullable String retryMessage,
                                @Nullable String failureMessage) {

        return retryFn(fn, shouldRetry,
                new RetryOptions(initialDelay).withRetryMessage(retryMessage).withFailureMessage(failureMessage));
    }

    public static <T> T retryFn(@Nonnull final Supplier<T> fn,
                                @Nonnull final Predicate<Throwable> shouldRetry,
                                long initialDelay) {
        return retryFn(fn, shouldRetry, new RetryOptions(initialDelay));
    }

    /**
     * Automatically retry the Supplier (no-arg function) if the shouldRetry condition evaluates to true.
     * The time between successive calls for each retry will be doubled, starting at the provided delay (ms).
     *
     * @param fn           A no-arg function that will be automatically retried if shouldRetry is true
     * @param shouldRetry  Predicate condition that returns true if we should retry based off the Throwable
     * @param options      additional options
     */
    public static <T> T retryFn(@Nonnull final Supplier<T> fn,
                                @Nonnull final Predicate<Throwable> shouldRetry,
                                RetryOptions options) {
        int retryCount = 0;

        while (true) {
            try {
                performRetryAction(options, retryCount);
                T value = fn.get();
                performSuccessAction(options, retryCount);
                return value;
            } catch (Throwable throwable) {
                if (retryCount + 1 >= options.maxRetries || !shouldRetry.test(throwable)) {
                    if (options.failureMessage != null) {
                        options.logger.warn(options.failureMessage, throwable);
                    }

                    throw throwable;
                }

                if (options.retryMessage != null) {
                    options.logger.warn(options.retryMessage, retryCount, throwable);
                }

                long sleep = getSleepMillis(retryCount, options.initialDelay);

                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    // ok
                }

                retryCount++;
            }
        }
    }

    /**
     * Gets the sleep delay in millis using Equal Jitter
     *
     * @see <a href="https://www.awsarchitectureblog.com/2015/03/backoff.html">aws backoff</a>
     */
    @VisibleForTesting
    public static long getSleepMillis(int retryCount, long base) {
        if (base == 0) {
            return 0;
        }

        double temp = (base * Math.pow(2, retryCount)) / 2;
        double jitter = ThreadLocalRandom.current().nextDouble(0.0, temp);
        return (long) (temp + jitter);
    }

    private static void performSuccessAction(RetryOptions options, int retryCount) {
        if (options.onSuccessAction != null) {
            try {
                options.onSuccessAction.accept(retryCount);
            } catch (RuntimeException e) {
                options.logger.error("Error handling success...", e);
            }
        }
    }

    private static void performRetryAction(RetryOptions options, int retryCount) {
        if (retryCount > 0 && options.onRetryAction != null) {
            try {
                options.onRetryAction.accept(retryCount);
            } catch (RuntimeException e) {
                options.logger.error("Error handling retry...", e);
            }
        }
    }

    public static class RetryOptions {

        private final long initialDelay;
        private String retryMessage;
        private String failureMessage;
        private int maxRetries = 5;
        private Consumer<Integer> onSuccessAction;
        private Consumer<Integer> onRetryAction;

        private Logger logger = LOGGER;

        /**
         * @param initialDelay The initial amount of time (millis) to wait before retrying
         */
        public RetryOptions(long initialDelay) {
            this.initialDelay = initialDelay;
        }

        /**
         * @param retryMessage A message to log if the function is been retried (WARN)
         * @return this
         */
        public RetryOptions withRetryMessage(String retryMessage) {
            this.retryMessage = retryMessage;
            return this;
        }

        /**
         * @param failureMessage A message to log if the predicate test failed or max retries occurred (ERROR)
         * @return this
         */
        public RetryOptions withFailureMessage(String failureMessage) {
            this.failureMessage = failureMessage;
            return this;
        }

        /**
         * @param maxRetries The maximum number of times to retry (default = 5)
         * @return this
         */
        public RetryOptions withMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        /**
         * @param onSuccessAction A {@code Consumer} of the number retries required for a success
         * @return this
         */
        public RetryOptions withOnSuccessAction(Consumer<Integer> onSuccessAction) {
            this.onSuccessAction = onSuccessAction;
            return this;
        }

        /**
         * @param onRetryAction A {@code Consumer} called before a retry. Use it to change the retry actions parameters
         * @return this
         */
        public RetryOptions withOnRetryAction(Consumer<Integer> onRetryAction) {
            this.onRetryAction = onRetryAction;
            return this;
        }

        /**
         * @param logger A {@code Logger} to use when logging messages, defaults to FunctionUtil.LOGGER
         * @return this
         */
        public RetryOptions withLogger(@Nonnull Logger logger) {
            this.logger = logger;
            return this;
        }

        public String getRetryMessage() {
            return retryMessage;
        }

        public String getFailureMessage() {
            return failureMessage;
        }
    }
}
