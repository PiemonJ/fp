package com.zhongan.telecom.fp;

import io.vavr.control.Either;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.*;
public class Test {
    public static void main(String[] args) {

        String str = "1";
        Match(str).of(
                Case($("1"), "one"),
                Case($("2"), "two"),
                Case($(""), "?")
        );
        Either<String, Object> mark = Either.left("Marks not acceptable");
    }
}
