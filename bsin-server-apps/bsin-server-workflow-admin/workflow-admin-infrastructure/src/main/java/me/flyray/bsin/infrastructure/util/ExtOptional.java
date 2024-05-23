package me.flyray.bsin.infrastructure.util;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>
 * 增强型ExtOptional
 * </p>
 *
 * @author zsk
 * @date 2020/11/20 下午4:34
 */
public final class ExtOptional<T> {
    /**
     * Common instance for {@code empty()}.
     */
    private static final ExtOptional<?> EMPTY = new ExtOptional<>();

    /**
     * If non-null, the value; if null, indicates no value is present
     */
    private final T value;

    /**
     * Constructs an empty instance.
     *
     * @implNote Generally only one empty instance, {@link ExtOptional#EMPTY},
     * should exist per VM.
     */
    private ExtOptional() {
        this.value = null;
    }

    /**
     * Returns an empty {@code ExtOptional} instance.  No value is present for this
     * ExtOptional.
     *
     * @param <T> Type of the non-existent value
     * @return an empty {@code ExtOptional}
     * @apiNote Though it may be tempting to do so, avoid testing if an object
     * is empty by comparing with {@code ==} against instances returned by
     * {@code Option.empty()}. There is no guarantee that it is a singleton.
     * Instead, use {@link #isPresent()}.
     */
    public static <T> ExtOptional<T> empty() {
        @SuppressWarnings("unchecked")
        ExtOptional<T> t = (ExtOptional<T>) EMPTY;
        return t;
    }

    /**
     * Constructs an instance with the value present.
     *
     * @param value the non-null value to be present
     * @throws NullPointerException if value is null
     */
    private ExtOptional(T value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Returns an {@code ExtOptional} with the specified present non-null value.
     *
     * @param <T>   the class of the value
     * @param value the value to be present, which must be non-null
     * @return an {@code ExtOptional} with the value present
     * @throws NullPointerException if value is null
     */
    public static <T> ExtOptional<T> of(T value) {
        return new ExtOptional<>(value);
    }

    /**
     * Returns an {@code ExtOptional} describing the specified value, if non-null,
     * otherwise returns an empty {@code ExtOptional}.
     *
     * @param <T>   the class of the value
     * @param value the possibly-null value to describe
     * @return an {@code ExtOptional} with a present value if the specified value
     * is non-null, otherwise an empty {@code ExtOptional}
     */
    public static <T> ExtOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * If a value is present in this {@code ExtOptional}, returns the value,
     * otherwise throws {@code NoSuchElementException}.
     *
     * @return the non-null value held by this {@code ExtOptional}
     * @throws NoSuchElementException if there is no value present
     * @see ExtOptional#isPresent()
     */
    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * If a value is present, invoke the specified consumer with the value,
     * otherwise do nothing.
     *
     * @param consumer block to be executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     *                              null
     */
    public void ifPresent(Consumer<? super T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * return an {@code ExtOptional} describing the value, otherwise return an
     * empty {@code ExtOptional}.
     *
     * @param predicate a predicate to apply to the value, if present
     * @return an {@code ExtOptional} describing the value of this {@code ExtOptional}
     * if a value is present and the value matches the given predicate,
     * otherwise an empty {@code ExtOptional}
     * @throws NullPointerException if the predicate is null
     */
    public ExtOptional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent()) {
            return this;
        } else {
            return predicate.test(value) ? this : empty();
        }
    }

    /**
     * If a value is present, apply the provided mapping function to it,
     * and if the result is non-null, return an {@code ExtOptional} describing the
     * result.  Otherwise return an empty {@code ExtOptional}.
     *
     * @param <U>    The type of the result of the mapping function
     * @param mapper a mapping function to apply to the value, if present
     * @return an {@code ExtOptional} describing the result of applying a mapping
     * function to the value of this {@code ExtOptional}, if a value is present,
     * otherwise an empty {@code ExtOptional}
     * @throws NullPointerException if the mapping function is null
     * @apiNote This method supports post-processing on ExtOptional values, without
     * the need to explicitly check for a return status.  For example, the
     * following code traverses a stream of file names, selects one that has
     * not yet been processed, and then opens that file, returning an
     * {@code ExtOptional<FileInputStream>}:
     *
     * <pre>{@code
     *     ExtOptional<FileInputStream> fis =
     *         names.stream().filter(name -> !isProcessedYet(name))
     *                       .findFirst()
     *                       .map(name -> new FileInputStream(name));
     * }</pre>
     * <p>
     * Here, {@code findFirst} returns an {@code ExtOptional<String>}, and then
     * {@code map} returns an {@code ExtOptional<FileInputStream>} for the desired
     * file if one exists.
     */
    public <U> ExtOptional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        } else {
            return ExtOptional.ofNullable(mapper.apply(value));
        }
    }

    /**
     * If a value is present, apply the provided {@code ExtOptional}-bearing
     * mapping function to it, return that result, otherwise return an empty
     * {@code ExtOptional}.  This method is similar to {@link #map(Function)},
     * but the provided mapper is one whose result is already an {@code ExtOptional},
     * and if invoked, {@code flatMap} does not wrap it with an additional
     * {@code ExtOptional}.
     *
     * @param <U>    The type parameter to the {@code ExtOptional} returned by
     * @param mapper a mapping function to apply to the value, if present
     *               the mapping function
     * @return the result of applying an {@code ExtOptional}-bearing mapping
     * function to the value of this {@code ExtOptional}, if a value is present,
     * otherwise an empty {@code ExtOptional}
     * @throws NullPointerException if the mapping function is null or returns
     *                              a null result
     */
    public <U> ExtOptional<U> flatMap(Function<? super T, ExtOptional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        } else {
            return Objects.requireNonNull(mapper.apply(value));
        }
    }

    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other the value to be returned if there is no value present, may
     *              be null
     * @return the value, if present, otherwise {@code other}
     */
    public T orElse(T other) {
        return value != null ? value : other;
    }

    /**
     * Return the value if present, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param other a {@code Supplier} whose result is returned if no value
     *              is present
     * @return the value if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if value is not present and {@code other} is
     *                              null
     */
    public T orElseGet(Supplier<? extends T> other) {
        return value != null ? value : other.get();
    }

    /**
     * Return the contained value, if present, otherwise throw an exception
     * to be created by the provided supplier.
     *
     * @param <X>               Type of the exception to be thrown
     * @param exceptionSupplier The supplier which will return the exception to
     *                          be thrown
     * @return the present value
     * @throws X                    if there is no value present
     * @throws NullPointerException if no value is present and
     *                              {@code exceptionSupplier} is null
     * @apiNote A method reference to the exception constructor with an empty
     * argument list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Indicates whether some other object is "equal to" this ExtOptional. The
     * other object is considered equal if:
     * <ul>
     * <li>it is also an {@code ExtOptional} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {code true} if the other object is "equal to" this object
     * otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ExtOptional)) {
            return false;
        }

        ExtOptional<?> other = (ExtOptional<?>) obj;
        return Objects.equals(value, other.value);
    }

    /**
     * Returns the hash code value of the present value, if any, or 0 (zero) if
     * no value is present.
     *
     * @return hash code value of the present value or 0 if no value is present
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Returns a non-empty string representation of this ExtOptional suitable for
     * debugging. The exact presentation format is unspecified and may vary
     * between implementations and versions.
     *
     * @return the string representation of this instance
     * @implSpec If a value is present the result must include its string
     * representation in the result. Empty and present ExtOptionals must be
     * unambiguously differentiable.
     */
    @Override
    public String toString() {
        return value != null ? String.format("ExtOptional[%s]", value) : "ExtOptional.empty";
    }

    /**
     * Returns an {@code ExtOptional} describing the specified value, if non-empty,
     * otherwise returns an empty {@code ExtOptional}.
     *
     * @param value the possibly-empty value to describe
     * @param <T>   the class of the value
     * @return an {@code ExtOptional} with a present value if the specified value
     * is non-empty, otherwise an empty {@code ExtOptional}
     */
    public static <T> ExtOptional<T> ofEmptyLogical(T value) {
        return ExtOptional.isLogicalEmptyObject(value) ? empty() : of(value);
    }

    /**
     * 判断对象逻辑空
     *
     * @param object 任意对象
     * @return 是否逻辑空
     */
    public static boolean isLogicalEmptyObject(Object object) {
        if (Objects.isNull(object)) {
            return true;
        } else if (object instanceof String) {
            return isBlank((String) object);
        } else if (object instanceof Collection) {
            return ((Collection<?>) object).isEmpty();
        } else if (object instanceof Map) {
            return ((Map<?, ?>) object).isEmpty();
        }
        return false;
    }

    /**
     * 判断字符序列是否为空白值
     *
     * @param cs 字符串序列
     * @return 若空白值返回true，否则返回false
     */
    private static boolean isBlank(CharSequence cs) {
        int strLen;
        if ((strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }
}
