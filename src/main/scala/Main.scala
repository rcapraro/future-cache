import scala.concurrent.Future

import scala.concurrent.ExecutionContext.global
import scala.concurrent.Promise
import scala.concurrent.ExecutionContext
import sttp.client3._
import sttp.client3.circe._
import io.circe.generic.auto._

object Main extends App {

  implicit val executionContext = scala.concurrent.ExecutionContext.global

  implicit val cache: Cache[Double, Character] = new Cache[Double, Character]

  case class Character(name: String, gender: String, height: String, mass: String)

  class Cache[K, V] {
    private final val underlyingCache = new java.util.concurrent.ConcurrentHashMap[K, Promise[V]]() // TODO use a production-ready cache like caffeine
    def get(key: K, fetchOrCache: => Future[V]): Future[V] = {
      underlyingCache.get(key) match {
        case null =>
          val p = Promise[V]()
          underlyingCache.putIfAbsent(key, p) match {
            case null =>
              p.completeWith(fetchOrCache).future // TODO cache eviction
            case existing =>
              existing.future
          }
        case existing =>
          existing.future
      }
    }
  }

  val backend = HttpClientFutureBackend()

  def getCharacter(characterId: Long): Future[Character] = {
    println(s"Fetching character $characterId by calling uri https://swapi.dev/api/people/$characterId")
    val response = basicRequest
      .get(uri"https://swapi.dev/api/people/$characterId")
      .response(asJson[Character])
      .send(backend)
    response.map(result =>
      result.body match {
        case Left(value)  => Character("Unknown", "Unknow", "Unknow", "Unknow")
        case Right(value) => value
      }
    )
  }

  val f, e, g, h, i = cache.get(1, getCharacter(1))
  f.onComplete(println)
  e.onComplete(println)
  g.onComplete(println)
  h.onComplete(println)

  val j, k = cache.get(2, getCharacter(2))
  j.onComplete(println)
  k.onComplete(println)

  Thread.sleep(2000)

  println("Finished!")

}
