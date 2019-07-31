package com.zhongan.telecom.fp.use.reader

import scala.util.Try

trait Repository[IDType,AggrType] {

  def query(id:IDType):Try[Option[AggrType]]

  def save(aggr:AggrType):Try[AggrType]


}
