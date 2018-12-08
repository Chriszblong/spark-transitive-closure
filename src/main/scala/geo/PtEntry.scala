package geo

import reactivemongo.bson._
import scala.math.{cos,abs}

case class PtEntry(coordinates:Array[Double], time:Long) extends GeoEntry{
   import PtEntry._
   
   val longitude=coordinates(0).toRadians
   val latitude=coordinates(1).toRadians
   
   val close=(that:PtEntry)=>compareT(that) || compareD(that)
   
   def compareD(that:PtEntry)=abs(this.latitude-that.latitude)<=lat_diff_th && abs(this.longitude-that.longitude)<=long_diff_th 
   def compareT(that:PtEntry):Boolean=abs(this.time-that.time)<=time_th
}
 
object PtEntry{
    
    val r:Int=6378137
    val time_th=60*60*1000
    val distance_th=200.0
    
    lazy val lat_diff_th=(distance_th/r) //radians
    lazy val long_diff_th=abs(distance_th/(r*Math.cos(lat_diff_th))) //
    
    
    implicit object BsonWriter extends BSONDocumentWriter[PtEntry] {
        def write(point: PtEntry): BSONDocument ={
            BSONDocument(
                "type"->"Feature",
                "geometry"->BSONDocument(
                   "type"->"Point",
                   "coordinates"->BSONArray(point.coordinates(0), point.coordinates(1))
                ),
                "properties"->BSONDocument(
                   "time"->BSONDateTime(point.time)
                )
            )
               
        }
     }
  

 }