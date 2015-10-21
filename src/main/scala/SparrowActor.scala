import akka.actor.Actor

import scala.util.Random

/**
 * Created by tom on 23/9/15.
 */
class SparrowActor(ss:SparrowScheduler) extends Actor {
  override def receive: Receive = {
    case job: Job =>{
      job.dispatch()
      job.timeSubmit = System.nanoTime()
      ss.placeJob(job)
    }
  }
}
