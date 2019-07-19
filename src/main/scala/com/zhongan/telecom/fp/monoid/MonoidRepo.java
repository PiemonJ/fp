package com.zhongan.telecom.fp.monoid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonoidRepo {

    //整数加法的Monoid
    public static final Monoid<Integer> INT_ADDITION_MONOID = new Monoid<Integer>() {
        @Override
        public Integer op(Integer a1, Integer a2) {
            return a1 + a2;
        }

        @Override
        public Integer unit() {
            return 0;
        }
    };

    public static <A> Monoid<List<A>> listMonoid(){
        return new Monoid<List<A>>() {

            @Override
            public List<A> op(List<A> a1, List<A> a2) {
                return Stream.concat(a1.stream(), a2.stream())
                        .collect(Collectors.toList());
            }

            @Override
            public List<A> unit() {
                return Arrays.asList();
            }
        };
    };
    //自函数
    public static <A> Monoid<Function<A,A>> endoMonoid(){
        return new Monoid<Function<A, A>>() {
            @Override
            public Function<A, A> op(Function<A, A> a1, Function<A, A> a2) {
                // a1(a2(x)) 可以与andThen对比
                return a1.compose(a2);
            }

            @Override
            public Function<A, A> unit() {
                //恒等函数
                return x -> x;
            }
        };
    }


    //交换律：Commutative a + b = b + a
    //集合率：associative (a + b ) + c = a + (b + c)

    public static <A> Monoid<A> dual(Monoid<A> monoid){
        return new Monoid<A>() {
            @Override
            public A op(A a1, A a2) {
                return monoid.op(a2,a1);
            }

            @Override
            public A unit() {
                return monoid.unit();
            }
        };
    }




    //包含折叠与映射的函数
    public static <A,B> B foldMap(List<A> as,Function<A,B> func,Monoid<B> monoid){


        return as.stream()
                .map(func)
                .reduce(monoid.unit(),monoid::op);

    }

    public static <A,B> B foldRight(List<A> as, B unit, BiFunction<A,B,B> func){

        Monoid<Function<B, B>> endoMonoid = MonoidRepo.<B>endoMonoid();

        return foldMap(as,(A a) -> (B b) -> func.apply(a,b),endoMonoid).apply(unit);
    }


    public static <A,B> B foldLeft(List<A> as,B unit,BiFunction<B,A,B> func){

        Monoid<Function<B, B>> endoMonoid = MonoidRepo.<B>endoMonoid();

        Monoid<Function<B, B>> dualEndoMonoid = dual(endoMonoid);

        return foldMap(as,(A a) -> (B b) -> func.apply(b,a),dualEndoMonoid).apply(unit);
    }


    /**
     * 单词统计
     * @param args
     */

    public static final Monoid<WordCounter> WORD_COUNTER_MONOID = new Monoid<WordCounter>() {
        @Override
        public WordCounter op(WordCounter a1, WordCounter a2) {

            if (a1 instanceof Stub){
                if (a2 instanceof Stub){
                    return new Stub(((Stub) a1).chars + ((Stub) a2).chars);
                } else if (a2 instanceof Part){
                    return new Part(
                            ((Stub) a1).chars + ((Part) a2).lStub,
                            ((Part) a2).words,
                            ((Part) a2).rStub);
                }
            } else if (a1 instanceof Part){
                if (a2 instanceof Stub){
                    return new Part(
                                ((Part) a1).lStub ,
                                ((Part) a1).words,
                            ((Part) a1).rStub + ((Stub) a2).chars);
                } else if (a2 instanceof Part){
                    return new Part(
                            ((Part) a1).lStub + ((Part) a2).lStub,
                            ((Part) a1).words + ((Part) a2).words + counter(((Part) a1).rStub + ((Part) a2).lStub),
                            ((Part) a1).rStub + ((Part) a2).rStub);

                }

            }
            return null;

        }

        @Override
        public WordCounter unit() {
            return new Stub("");
        }
    };


    public static void main(String[] args) {

        String s = "hello world piemon";

        List<Character> characters = s.chars().mapToObj(c -> (char) c).collect(Collectors.toList());

        WordCounter wordCounter = foldMap(characters, MonoidRepo::count, WORD_COUNTER_MONOID);

        Integer wordCount = unstub(wordCounter);

        System.out.println(wordCount);

//        CharSequence seq = java.nio.CharBuffer.wrap(s);



    }

    public static Integer unstub(WordCounter wordCounter){

        if (wordCounter instanceof Stub)
            return counter(((Stub) wordCounter).chars);
        else
            return counter(((Part) wordCounter).lStub) + ((Part) wordCounter).words + counter(((Part) wordCounter).rStub);
    }

    public static Integer counter(String cases){
        return cases.isEmpty() ? 0 : 1;
    }

    public static WordCounter count(Character chars){
        if (chars == ' ')
            return new Part("",0,"");
        else
            return new Stub(Character.toString(chars));
    }


}
