import akka.actor.Actor.Receive
import akka.actor.{Actor, Props, ActorSystem}

import scala.util.Random

/**
 * Created by tom on 22/9/15.
 */

object test1 {

  def main (args: Array[String]) {


    Cluster.resetCluster(200,64,"TiresiasTen")
    val wl1 = new WorkloadHete(0.8,30000,200,30000,2)
    //println(wl1.getProperLambda())
    wl1.run()
  }
}
