
import java.util

/**
 * Created by tom on 22/8/15.
 */
class Job( taskDuration:Int,taskCount:Int, hopLimit:Int) {
  val jobID = Job.genJobID()

  var origin = 0
  var dest = 0

  var timeSubmit = 0L
  var timeExecute = 0L
  var timeFinish = 0L
  var taskList = new Array[Task](taskCount)

  /**
   * dispatch fuction is used to create tasks
   * also the submittion time is set in this function
   */
  def dispatch(): Unit ={
    for(i <- 0 until taskList.size)
      taskList(i) = new Task(this.jobID,i,this.taskDuration);
    this.timeSubmit = System.nanoTime()
  }

  def getDelay(): Long = {
    var latestTime = 0L
    for(i <- 0 until taskList.size){
      if(taskList(i).timeFinish > latestTime){
      latestTime = taskList(i).timeFinish
      }
    }
    this.timeFinish = latestTime
    return this.timeFinish - this.timeSubmit
  }

  def getResponse:Long = {
    var response = 0L
    for(i <- 0 until taskList.size){
      val restemp = taskList(i).getResponse()
      if( restemp > response){
      response = restemp
      }
    }
    return response
  }

  def getMaxHop:Int = {
    var hops = 0
    for(i <- 0 until taskList.size){
      val htemp = taskList(i).hopHistory.size()
      if( htemp > hops){
        hops = htemp
      }
    }
    return hops
  }

  def getDelay(durationMS:Long):Long=
  {
    return this.getResponse - durationMS*1000000
  }

}

object Job{

  private var number = -1;

  private def genJobID(): Int ={
    number += 1
    number
  }
}
