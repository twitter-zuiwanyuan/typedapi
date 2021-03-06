package typedapi.server

import typedapi.dsl._
import shapeless._

import org.specs2.mutable.Specification

final class ApiToEndpointLinkSpec extends Specification {

  case class Foo(name: String)

  "link api definitions to endpoint functions" >> { 
    val Api = := :> "find" :> typedapi.dsl.Segment[String]('name) :> 
                    Query[Int]('limit) :> 
                    Client.Header('hello, 'world) :> 
                    Server.Send('foo, 'bar) :> Server.Match[String]("hi") :>
                    Get[Json, List[Foo]]

    val endpoint0 = derive[Option](Api).from((name, limit, hi) => Some(List(Foo(name)).take(limit)))
    endpoint0("john" :: 10 :: Set("whats", "up") :: HNil) === Some(List(Foo("john")))
    endpoint0.headers == Map("foo" -> "bar")
    endpoint0.method == "GET"
  }
}
