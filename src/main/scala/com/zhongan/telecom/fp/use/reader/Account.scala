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
      no:String,
      name:String,
      openDate:java.util.Date = today, //开户时间
      quota:Quota = Quota(0)           //账户额度
);

case class Quota(balance:Amount)
