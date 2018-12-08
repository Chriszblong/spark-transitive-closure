package matrix

import org.apache.spark.rdd.RDD
import common._

trait DefaultMatrix extends AbstractMatrix {
   this:MatrixImpl=>
      import AbstractMatrix._
      override def norm(x:Double, y:Double)= .0
      override def closure(Ri:RDD[Similarity])(Rx:KeyEntry=null)(Rj:RDD[Similarity]=null):RDD[Similarity]=null
      override def handle(join:RDD[(Long, ((Long, Double), (Long, Double)))])(implicit alpha:Double):RDD[((Long, Long), Double)]=null
}