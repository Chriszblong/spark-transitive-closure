package common

import org.apache.spark.rdd.RDD
import matrix._

trait HandleWithFilter  extends AbstractMatrix {
  abstract override def handle(join:RDD[(Long, ((Long, Double), (Long, Double)))])(implicit alpha:Double):RDD[((Long, Long), Double)]={
    join.map({
       case (_,(((i,wi)),((j,wj))))=>{
         val w=norm(wi,wj)
         if(w>alpha) Some((i,j),(norm(wi,wj))) else None
       }
    }).filter(_.isDefined).map(_.get)
  }
  
}