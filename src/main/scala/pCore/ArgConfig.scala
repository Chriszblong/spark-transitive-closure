package pCore

import org.rogach.scallop._

class ArgConfig(args:Seq[String]) extends ScallopConf(args) {
     val transitivity = opt[String](default=Some("max-min"))
     val closure_alg = opt[String](default=Some("smart"))
     val alpha = opt[Double](required = true)
     verify()
    
}