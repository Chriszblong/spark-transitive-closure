package common

import scala.math.abs

class Similarity(val i:Long, val j:Long, val value:Double) extends Serializable{
  
      override def equals(that:Any):Boolean={
        that match{
            case that:Similarity=>i==that.i && j==that.j && this.~=(that)
            case _=>false
        }
      }
      private def ~=(that:Similarity):Boolean=abs(value-that.value)<.01
      override def hashCode()=(i+j).hashCode()
      override def toString()=s"Similarity(${i}, ${j}, ${value})"
}
object Similarity{
  def apply(i:Long, j:Long, value:Double)=new Similarity(i,j,value)
}