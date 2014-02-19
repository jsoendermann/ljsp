package ljsp

import ljsp.AST._

object util {
  // TODO turn this into an object
  // fresh returns a new unique identifier that begins with s
  val fresh = (() =>  {
    // This is a map that maps identifier prefixes to their counters
    // New prefixes get added to the map automatically, it's not necessary
    // to initialize this map with all prefixes that will be used
    var counters = scala.collection.mutable.Map[String, Int]()

    // this is the function that fresh will be bound to
    (s: String) => {
      counters get s match {
        case Some(counter) => {
          counters(s) = counter + 1
          s ++ "_" ++ counters(s).toString()
        }
        case None => {
          counters = counters ++ scala.collection.mutable.Map(s -> 0)
          s ++ "_0"
        }
      }
    }
  })()
  
  def find_next_power_of_2(i: Int) : Int = {
    var r = 1
    while (r < i)
      r <<= 1
    return r
  }
}
