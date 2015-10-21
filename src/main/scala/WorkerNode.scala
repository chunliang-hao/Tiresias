
import scala.util.Random

/**
 * Created by tom on 22/9/15.
 */
class WorkerNode(nodeID:Int, slotCount:Int) {
    val scheduler = new TiresiasScheduler(this)

  val worker = new Worker(this, slotCount)

  def getID(): Int ={
    nodeID
  }

}
