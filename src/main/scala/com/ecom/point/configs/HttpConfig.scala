package com.ecom.point.configs

import zio.ZLayer
import zio.config._

final case class HttpConfig(host: String, port: Int)

object HttpConfig {
//	ConfigSource
}
