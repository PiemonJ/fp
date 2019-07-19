package com.zhongan.telecom.fp.typeclass;

/**
 *
 * 类型类 Type class
 *
 * TypeClass模式有三个重要组件：
 *   1.Type Class本身
 *   2.特定类型的Type Class实例
 *   3.我们暴露给用户的接口方法
 */

/**
 * JsonWriter即是一个类型类
 */
public interface JsonWriter<A> {

    public void write(A message);
}
