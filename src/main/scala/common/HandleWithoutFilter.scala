package common

import org.apache.spark.rdd.RDD
import matrix._

trait HandleWithoutFilter extends AbstractMatrix {
 abstract override def handle(join:RDD[(Long, ((Long, Double), (Long, Double)))])(implicit alpha:Double):RDD[((Long, Long), Double)]={
    join.map({
       case (_,(((i,wi)),((j,wj))))=>((i,j),(norm(wi,wj)))
     })
  }
}