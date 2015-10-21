import akka.actor.Actor

import scala.util.Random

/**
 * Created by tom on 24/9/15.
 */
class JobSubmitActorOneEntrance extends Actor{
  override def receive: Receive = {
    case job:Job => {
      val rand = new Random()
      job.dispatch()
      job.timeSubmit = System.nanoTime()
      for (i <- 0 until job.taskList.size) {
        Cluster.nodeList(1).scheduler.ta ! job.taskList(i)
        job.taskList(i).timeSubmit = System.nanoTime()
      }
    }
    case _ => print("JobSubmitActor: Wrong Input! expecting a Job!")
  }
}
