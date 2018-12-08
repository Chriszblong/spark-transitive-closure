package common

import org.apache.spark.rdd.RDD
import matrix._

trait SemiNaive extends AbstractMatrix {
  import AbstractMatrix._
  
   override abstract def closure(Ri:RDD[Similarity])(Rx:KeyEntry=Rx)(Rj:RDD[Similarity]=composition(Ri)(Rx)):RDD[Similarity]={
    if(Rj.subtract(Ri).isEmpty()) Ri
       else closure(Rj)()()
   }
}