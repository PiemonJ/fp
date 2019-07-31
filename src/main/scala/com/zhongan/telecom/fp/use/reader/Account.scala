package com.zhongan.telecom.fp.use.reader

import java.util.Calendar


object common {
  type Amount = BigDecimal

  def today = Calendar.getInstance.getTime
}

import common._


/**
  *
  * 个人账户
  * quota:信用额度
  */
case class Account(
      id:String,
      no:String,
      openDate:java.util.Date = today, //开户时间
      quota:Quota                      //账户额度
);

case class Quota(balance:Amount)
