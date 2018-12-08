package matrix

import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.linalg.distributed.MatrixEntry

import org.apache.spark.graphx.Edge

import matrix._
import common._

class SiMatrix(R:RDD[Similarity])(implicit alpha:Double) extends DefaultMatrix with MatrixImpl with Serializable{
   import SiMatrix._
   import AbstractMatrix._
     
   override val Rx=R.map{
     case e=>(e.i,(e.j,e.value))
   }
   
   override def composition(R:RDD[Similarity])(Rx:KeyEntry):RDD[Similarity]={
        val join=R.map(e=>(e.j,(e.i, e.value))).join(Rx)
        handle(join).reduceByKey(_ max _).map({
           case ((i,j),w)=>Similarity(i,j,w)
        })
   }
   lazy val entries:RDD[Similarity]={
     closure(R)()()
   }
 
}

object SiMatrix {

  def flatten(R:RDD[Similarity])=R.flatMap(s=>{     
      if(s.i!=s.j) Iterator(s, Similarity(s.j,s.i,s.value))
      else Iterator(s)
  })  
  implicit def DoubleToOption(alpha:Double):Option[Double]=Some(alpha)
  
  def apply(similarities:RDD[Similarity])(implicit alpha:Double,  t_norm:String, alg:String)={
    (t_norm,alg) match {
       case ("max-delta", "smart")=> new SiMatrix(flatten(similarities)) with HandleWithFilter with  MaxDeltaNorm with Smart
       case ("max-prod", "smart")=> new SiMatrix(flatten(similarities)) with HandleWithFilter with  MaxProductNorm with Smart
       case ("max-delta", "semi-naive")=> new SiMatrix(flatten(similarities)) with HandleWithFilter with  MaxDeltaNorm with SemiNaive
       case ("max-prod", "semi-naive")=>  new SiMatrix(flatten(similarities)) with HandleWithFilter with MaxProductNorm  with SemiNaive
       case ("max-min", "semi-naive")=>  new SiMatrix(flatten(similarities)) with HandleWithoutFilter with MaxMinNorm  with SemiNaive
       case ("max-min", "smart")=> new SiMatrix(flatten(similarities)) with HandleWithoutFilter with  MaxMinNorm with Smart
    }
  }
}