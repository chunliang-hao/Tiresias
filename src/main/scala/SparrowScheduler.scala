import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
 * Created by tom on 10/10/15.
 */
class SparrowScheduler (node:WorkerNode){
  val SchedulerType = "Sparrow"
  val sample = new ArrayBuffer[Worker]
  def placeJob(job:Job): Boolean ={

    val currentTime = System.nanoTime()
    for(i <- 0 until job.taskList.size){
      job.taskList(i).timeSubmit = currentTime
    }

    val rand = new Random()
    var complete = 0
    for(i <- 0 until job.taskList.size*2){
      val randomchoice = rand.nextInt(Cluster.nodeList.size)
      val randomworker = Cluster.nodeList(randomchoice).worker
      if(randomworker.getEmptySlotCount()>0) {
        randomworker.placeSparrowTask(job.taskList(complete))
        complete += 1
        if(complete>=job.taskList.size)return true
      }
      else sample += randomworker
    }

    val k = job.taskList.size - complete
    if(k>0){
      for(i <- 0 until k ){
        if(sample(i)!=null)
          sample(i).placeSparrowTask(job.taskList(complete))
          complete += 1
      }
      return true
    }
    return false
  }


}
