package common

import org.apache.spark._
import scala.collection.mutable.HashMap

class LoadPartitioner(partitions:Int) extends Partitioner {
  
  val map:HashMap[Int,Int]=HashMap() //partition, size
    
  def initializeMap{ 
    for(i<-0 until partitions){map.put(i,0)}
  }

  def numPartitions: Int = partitions
  def getPartition(key:Any):Int={
    key match{
      case size:Int=>{
        if(map.isEmpty){
          initializeMap
        }  
        val min_elt=map.minBy(_._2)
        map.put(min_elt._1,min_elt._2+size)
        min_elt._1
      }
    }
  }
}