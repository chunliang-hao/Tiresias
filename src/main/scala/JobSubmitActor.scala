import akka.actor.Actor
import akka.actor.Actor.Receive

import scala.util.Random

/**
 * Created by tom on 24/9/15.
 */
class JobSubmitActor extends Actor{
  override def receive: Receive = {
    case job:Job => {
      val rand = new Random()
      job.dispatch()
      job.timeSubmit = System.nanoTime()
      for (i <- 0 until job.taskList.size) {
        val randomchoice = rand.nextInt(Cluster.nodeList.size)
        Cluster.nodeList(randomchoice).scheduler.ta ! job.taskList(i)
        job.taskList(i).timeSubmit = System.nanoTime()
      }
    }
    case _ => print("JobSubmitActor: Wrong Input! expecting a Job!")
  }
}
