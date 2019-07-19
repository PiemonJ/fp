package com.zhongan.telecom.fp.monoid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Part implements WordCounter{


    //字符串中剔除完整单词之外的左侧字符串
    public String lStub;

    //字符串中完整单词的数量
    public int words;

    //字符串中剔除完整单词之外的右侧侧字符串
    public String rStub;
}
