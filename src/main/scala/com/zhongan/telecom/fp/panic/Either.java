package com.zhongan.telecom.fp.panic;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

public abstract class Either<L,R>  implements Serializable{

    public Either() {
    }

    public abstract R get();

    public abstract boolean isLeft();

    public abstract boolean isRight();

    public <U> Either<L, U> map(Function<? super R, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        if (isRight()) {
            return Either.right(mapper.apply(get()));
        } else {
            return (Either<L, U>) this;
        }
    }

    public <U> Either<L, U> flatMap(Function<? super R, Either<L, ? extends U>> mapper) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        if (isRight()) {
            return (Either<L, U>) mapper.apply(get());
        } else {
            return (Either<L, U>) this;
        }
    }


    public static <L, R> Either<L, R> left(L left) { return new Either.Left(left);}

    public static <L, R> Either<L, R> right(R right) {
        return new Either.Right(right);
    }

    private static final class Right<L, R> extends Either<L, R> implements Serializable {
        private final R value;

        private Right(R value) {
            super();
            this.value = value;
        }

        public R get() {
            return this.value;
        }

        public L getLeft() {
            throw new NoSuchElementException("getLeft() on Right");
        }

        public boolean isLeft() {
            return false;
        }

        public boolean isRight() {
            return true;
        }
    }

    private static final class Left<L, R> extends Either<L, R> implements Serializable {
        private final L value;

        private Left(L value) {
            super();
            this.value = value;
        }

        public R get() {
            throw new NoSuchElementException("get() on Left");
        }

        public L getLeft() {
            return this.value;
        }

        public boolean isLeft() {
            return true;
        }

        public boolean isRight() {
            return false;
        }

    }
}
