package com.zhongan.telecom.fp.monoid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表示没有任何完整单词的抽象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stub implements WordCounter{

    String chars;
}
