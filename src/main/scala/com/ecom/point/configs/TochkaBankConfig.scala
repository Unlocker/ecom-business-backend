package com.ecom.point.configs

import zio.ZLayer
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider

final case class TochkaBankConfig(url: String, clientId: String, clientSecret: String)

object TochkaBankConfig {
		val layer =
			ZLayer.fromZIO(
				TypesafeConfigProvider
					.fromResourcePath().load(deriveConfig[TochkaBankConfig])
			)
}
