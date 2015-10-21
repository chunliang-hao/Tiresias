
/**
 * Created by tom on 22/9/15.
 */
class Worker(node:WorkerNode, slotCount:Int) {

  val slots = new Array[Task](slotCount)

  def tryPlaceTask(task: Task): Boolean ={

    val currentTime = System.nanoTime()
    for(i <- 0 until slotCount) {
        if(     (slots(i)==null)
            ||  (slots(i).timeFinish < currentTime)) {
          slots(i) = task
          task.setTimeExecute(currentTime)
          return true
        }
    }
    return false
  }

  //because of the existance of a worker queue, sparrow tasks will be executed no matter what
  def placeSparrowTask(task:Task): Unit ={


    if(this.getEmptySlotCount()>0){
      val currentTime = System.nanoTime()
      for(i <- 0 until slotCount) {
        task.setTimeExecute(currentTime)
        if(     (slots(i)==null)
          ||  (slots(i).timeFinish < currentTime)) {
          slots(i) = task
          return
        }
      }
    }

    else{
      //since we have no worker queue design for Tiresias, a replace strategy is used
      //for sparrow to simulate worker queue, the results should be exactly the same
      var earlist = 0L
      var num = -1
      for(i <- 0 until this.slotCount) {
        if((slots(i).timeFinish < earlist)||(earlist == 0L) ) {
          earlist = slots(i).timeFinish
          num = i
        }
      }
      if(num>=0){
        task.setTimeExecute(earlist)
        slots(num) = task
        return
      }
    }
  }


  def getEmptySlotCount(): Int ={

    val currentTime = System.nanoTime()
    var count = 0
    for(i <- 0 until this.slotCount) {
      if(     (slots(i)==null)
        ||  (slots(i).timeFinish < currentTime)) {
        count += 1
      }
    }
    return count
  }

}
