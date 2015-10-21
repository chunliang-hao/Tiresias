import akka.actor.Actor
import akka.actor.Actor.Receive

/**
 * Created by tom on 23/9/15.
 */
class TiresiasActor(ts:TiresiasScheduler) extends Actor {
  override def receive: Receive = {
    case task: Task => ts.placeTask(task)
  }
}
