package com.ecom.point.configs

import zio.ZLayer
import zio.config.magnolia._
import zio.config.typesafe._

final case class HttpConfig(host: String, port: Int)

object HttpConfig {
	val layer =
		ZLayer.fromZIO(
			TypesafeConfigProvider
				.fromResourcePath().load(deriveConfig[HttpConfig])
		)
}
