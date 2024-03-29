package com.ecom.point.banks.services

import com.linecorp.armeria.common.HttpStatus
import sttp.capabilities
import zio._
import zio.prelude._
import zio.test._
import zio.json._
import sttp.client3._
import sttp.model._
import sttp.client3.ziojson._
import sttp.client3.httpclient._
import sttp.client3.httpclient.zio._
import sttp.client3.impl.zio.RIOMonadAsyncError
import sttp.client3.testing.SttpBackendStub

object TochkaBankServiceSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("suite") {
    test("test1") {
      case class User(username: String)
      implicit val userJsonCodec: JsonCodec[User] = DeriveJsonCodec.gen[User]

      val testBackend = SttpBackendStub(new RIOMonadAsyncError[Any])
        .whenRequestMatches(_ => true)
        .thenRespond(""" {"username":"John"} """)

      for {
        response <- basicRequest
          .get(uri"http://example.org/a/b/c")
          .response(asJson[User])
          .send(testBackend)
      } yield {
        assertTrue(StatusCode.Ok.equals(response.code))
      }
    }
  }
}
