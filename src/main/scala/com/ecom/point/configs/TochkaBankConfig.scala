package com.ecom.point.configs

import zio.{Config, ZLayer}
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider

final case class TochkaBankConfig(url: String, clientId: String, clientSecret: String)

object TochkaBankConfig {
		val layer: ZLayer[Any, Config.Error, TochkaBankConfig] =
			ZLayer.fromZIO(
				TypesafeConfigProvider
					.fromResourcePath().load(deriveConfig[TochkaBankConfig])
			)
}
