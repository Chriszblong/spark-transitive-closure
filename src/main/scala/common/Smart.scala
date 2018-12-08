package common

import org.apache.spark.rdd.RDD
import matrix._

trait Smart extends AbstractMatrix {
   import AbstractMatrix._
   override abstract def closure(Ri:RDD[Similarity])(Rx:KeyEntry=ireverse(Ri))(Rj:RDD[Similarity]=composition(Ri)(Rx)):RDD[Similarity]={
       if(Rj.subtract(Ri).isEmpty()) Ri
       else closure(Rj)()()
   }
}