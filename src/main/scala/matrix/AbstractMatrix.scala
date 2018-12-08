package matrix

import org.apache.spark.rdd.RDD
import common._

abstract class AbstractMatrix {
   
   import AbstractMatrix._
   
   val Rx:KeyEntry=null
   def norm(x:Double, y:Double):Double
   def closure(Ri:RDD[Similarity])(Rx:KeyEntry)(Rj:RDD[Similarity]):RDD[Similarity]
   def composition(R:RDD[Similarity])(Rx:KeyEntry):RDD[Similarity]
   def handle(join:RDD[(Long, ((Long, Double), (Long, Double)))])(implicit alpha:Double):RDD[((Long, Long), Double)]
}

object AbstractMatrix {
  type KeyEntry=RDD[(Long, (Long, Double))]
   def ireverse(R:RDD[Similarity])={
     R.map{
       case e=>(e.i,(e.j, e.value))
     }
   }
}