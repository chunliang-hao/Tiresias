
import akka.actor.ActorSystem._
import akka.actor._
import akka.dispatch.{Dispatchers, Mailboxes}
import akka.event.{LoggingAdapter, EventStream}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.Duration

/**
 * Created by tom on 22/9/15.
 */
object Cluster {
  var nodeCount = 200
  var schedulerType = "Sparrow"
  var nodeStrength = 64
  val RTT = 1 //assume 1ms round time traffic
  val nodeList = new Array[WorkerNode](nodeCount)
  val tiresiasActorSystem = ActorSystem("tsystem")
  //val calcRatio = 4/3

  def resetCluster(nc:Int, ns:Int, st:String){
    this.nodeCount = nc
    this.nodeStrength = ns
    this.schedulerType = st

    for(i <- 0 until this.nodeCount)
      nodeList(i) = new WorkerNode(i,this.nodeStrength)
  }

  def getLoad(): Double ={
    var unloaded = 0d
    var emptySlotTotal = 0d
    for(i <- 0 until nodeCount){
      unloaded = this.nodeList(i).worker.getEmptySlotCount()
      emptySlotTotal += unloaded
    }

    return 1-emptySlotTotal/nodeCount/nodeStrength
  }
}
