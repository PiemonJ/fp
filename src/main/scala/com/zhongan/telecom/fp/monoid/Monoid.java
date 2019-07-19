package com.zhongan.telecom.fp.monoid;

public interface Monoid<A> {

    public A op(A a1, A a2);

    public A unit();
}
