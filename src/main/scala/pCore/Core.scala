package pCore

import org.apache.spark._
import org.apache.hadoop.fs.Path
import org.apache.spark.mllib.linalg.distributed.MatrixEntry

import com.github.nscala_time.time.Imports._

import reactivemongo.api.{ MongoDriver, MongoConnection}
import reactivemongo.bson._

import scala.concurrent._
import scala.concurrent.duration.Duration._


import geo._
import matrix._
import org.apache.spark.graphx._

object Core extends App {{
    
    import common._
    val aconf = new ArgConfig(args)
    val alpha=aconf.alpha()
    val t_norm=aconf.transitivity()
    val alg=aconf.closure_alg()
  
    val sconf = new SparkConf().setAppName("consumption").setMaster("spark://172.17.32.100:7077")
    sconf.set("spark.driver.memory","4000m")
    sconf.set("spark.executor.memory","2000m")
    sconf.set("spark.eventLog.enabled","true")
    sconf.set("spark.eventLog.dir","file:///tmp/spark-events")
    sconf.set("spark.driver.extraJavaOptions", "-Xss40M ")
    sconf.set("spark.executor.extraJavaOptions", "-Xss40M")
    sconf.set("spark.worker.cleanup.enabled", "true")
 
    
    val sc=new SparkContext(sconf)
    val partitions=sc.wholeTextFiles("hdfs://172.17.32.100:54310/geoData/Data/00[1]/Trajectory/*",4).map(v=>{
             val path=new Path(v._1)
             val _object=path.getParent.getParent.getName.toInt
             val trajectory_id=path.getName.split('.')(0).toLong
             val points=v._2.split("\n").drop(6).map(line=>{
               val entries=line.split(",")
               val dateTime=new DateTime(s"${entries(5)}T${entries(6)}+00:00".filter(_ >= ' ')).toInstant().getMillis
               PtEntry(Array(entries(1).toDouble, entries(0).toDouble), dateTime)
             }).toVector
            TrEntry(_object, trajectory_id, points)
    }).map(t=>(t.size,t)).partitionBy(new LoadPartitioner(20)).zipWithIndex()
  
    val trajectories=partitions.map(tuple=>(tuple._2, tuple._1._2))//id,tra
    val cartesian=trajectories.cartesian(trajectories).filter{case(t1,t2)=>t1._1<=t2._1}
    val similarities=cartesian.map{
      case (tuple1, tuple2)=>{
        tuple1._2.getSimilarity(tuple2._2)(tuple1._1, tuple2._1)
      }
    }.filter(_.value>=alpha).repartition(20) ; cartesian.unpersist()

    val edges=SiMatrix.apply(similarities)(alpha, t_norm,  alg).entries ; similarities.unpersist()
    
    val groups=Graph(trajectories.map(t=>(t._1,None)),edges.map(e=>Edge(e.i, e.j, e.value))).connectedComponents().vertices.groupBy(_._2)
    val clusters= groups.zipWithIndex().flatMap(g=>{
      g._1._2.map(v=>(v._1, g._2))
    })// id, c
    
    val b_clusters=sc.broadcast(clusters.collect.toMap)
    
   trajectories.foreachPartition(p=>{
      import ExecutionContext.Implicits.global
      val driver = new MongoDriver 
      val database = for {
          uri <- Future.fromTry(MongoConnection.parseURI("mongodb://172.17.32.150:27017"))
          //guards
          con= driver.connection(uri)
          //connection
          db <- con.database("TrajectorySet")
      } yield db
      val collection=database.map(_.collection("clusters"))
      val writes=collection.map(coll=>{
          val futures=p.map(tr=>{
            val cluster=b_clusters.value.get(tr._1).get
            val bson=TrEntry.write(tr._2, cluster)
            coll.insert[BSONDocument](ordered = false).one(bson)
          })
           Await.result(Future.sequence(futures), Inf)
         }).andThen{
            case _=>driver.close()
        }
       Await.result(writes, Inf)
    })
 
   
    sc.stop()  
}}