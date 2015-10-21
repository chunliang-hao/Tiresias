
import java.util

import akka.actor.{Props, Actor}


import scala.util.Random

/**
 * Created by tom on 22/9/15.
 */
class TiresiasScheduler(node:WorkerNode) {
  val SchedulerType = "Tiresias"
  val clusterSize = Cluster.nodeList.size
  val rand = new Random()
  val ta = Cluster.tiresiasActorSystem.actorOf(Props(new TiresiasActor(this)))

  def placeTask(task:Task): Unit ={
    task.hopHistory.add(this.node.getID())
    if(!this.node.worker.tryPlaceTask(task)) oneNodeSampling(task)
  }

  def oneNodeSampling(task:Task): Int={
      val target = rand.nextInt(clusterSize)
      Cluster.nodeList(target).scheduler.ta ! task
      target
  }

}
