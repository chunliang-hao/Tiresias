
import java.util

/**
 * Created by tom on 22/9/15.
 */
class Task(jobID:Int,taskID:Int,taskDuration:Int) {
  var timeSubmit = 0L
  var timeExecute = 0L
  var timeFinish = 0L
  var hopHistory = new util.ArrayList[Int]()

  def setTimeExecute(currentTime:Long): Unit ={
    this.timeExecute = currentTime
    this.timeFinish = currentTime + taskDuration*1000000
  }

  def getResponse(): Long ={
    if(timeExecute==0|| timeFinish==0){

      println("Task: Damn the task is not finished yet! ")
      println("TaskInfo: jobID "+this.jobID)
      return -1L
    }

    if(timeSubmit==0)
    {
      println("Task: Damn the task is not start yet!")
      return -1L
    }

    if(Cluster.schedulerType == "Sparrow"){
      val time = this.timeFinish - this.timeSubmit + Cluster.RTT * 500000 * 3
      return time
    }

    if(this.hopHistory.size()>0) {
      val time = this.timeFinish - this.timeSubmit + Cluster.RTT * 500000 * (this.hopHistory.size() - 1)
      return time
    }
    else{
      print("Task: Damn the task is not record to go through any scheduler! ")
      return -1L
    }
  }
}
