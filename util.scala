package ljsp

import ljsp.AST._

object util {
  // fresh returns a new unique identifier that begins with s
  val fresh = (() =>  {
    // This is a map that maps identifier prefixes to their counters
    // New prefixes get added to the map automatically, it's not necessary
    // to initialize this map with all prefixes that will be used
    // FIXME this function doesn't work if the prefix it's called with isn't part of this collection
    var counters = scala.collection.mutable.Map("var" -> -1, "cont" -> -1, "env" -> -1, "func" -> -1, "conv_lambda" -> -1)

    // this is the function that fresh will be bound to
    (s: String) => {
      counters get s match {
        case Some(counter) => {
          counters(s) = counter + 1
          SIdn(s ++ "_" ++ counters(s).toString())
        }
        case None => {
          counters ++ scala.collection.mutable.Map(s -> 0)
          SIdn(s ++ "_0")
        }
      }
    }
  })()
}
