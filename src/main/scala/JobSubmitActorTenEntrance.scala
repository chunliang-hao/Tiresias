import akka.actor.Actor

import scala.util.Random

/**
 * Created by tom on 24/9/15.
 */
class JobSubmitActorTenEntrance extends Actor{
  override def receive: Receive = {
    case job:Job => {
      val rand = new Random()
      job.dispatch()
      job.timeSubmit = System.nanoTime()
      val randomchoice = rand.nextInt(10)
      for (i <- 0 until job.taskList.size) {
        Cluster.nodeList(randomchoice).scheduler.ta ! job.taskList(i)
        job.taskList(i).timeSubmit = System.nanoTime()
      }
    }
    case _ => print("JobSubmitActor: Wrong Input! expecting a Job!")
  }
}
