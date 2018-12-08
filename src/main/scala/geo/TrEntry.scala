package geo

import reactivemongo.bson._

import scala.math._
import common._

case class TrEntry(_object:Int, tr_id:Long, points:Vector[PtEntry]) extends GeoEntry{
  lazy val size=points.size
  import TrEntry._
  
  def getSimilarity(that:TrEntry)(id1:Long, id2:Long)={
      val s=if(id1==id2) 1.0 else lcss(this.points,that.points)/(max(this.points.size, that.points.size).toDouble)
      Similarity(id1, id2, s)
  }
      
  def lcss(a:Vector[PtEntry],b:Vector[PtEntry]):Int={
       var current:Array[Int]=new Array(b.size+1)
       var previous=current
       for(i<-1 to a.size){
         previous=current
         for(j<-1 to b.size){
           if(a(i-1).close(b(j-1))) current(j)=1+previous(j-1) else current(j)=max(previous(j), current(j-1))  
         }
       }
       current.last
  }   
}

object TrEntry  {

     def write(tr: TrEntry, cluster:Long=0): BSONDocument ={
            BSONDocument(
                "_object"->tr._object,
                "trajectory"->tr.tr_id,
                "cluster"->cluster,
                "features"->tr.points.map(p=>PtEntry.BsonWriter.write(p))
            )
     }

  
   
}