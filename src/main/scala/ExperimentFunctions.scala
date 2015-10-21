import java.util

/**
 * Created by tom on 30/9/15.
 */
object ExperimentFunctions {

  def getAvgDelay(jobList:util.ArrayList[Job],taskDur:Int): Long ={
    var totalDelay = 0L
    for(i <- jobList.size()/2 until jobList.size()){
      totalDelay += jobList.get(i).getDelay()
    }
    return totalDelay*2/jobList.size()-taskDur*1000000
  }

  def getPossionVariable(lambda:Double):Int ={
    var x = 0
    val y = Math.random()
    var cdf = getPossionProbability(x, lambda)
    while(cdf < y){
      x += 1
      cdf += getPossionProbability(x,lambda)
    }
    return x
  }

  def getPossionProbability(k:Int, lambda:Double): Double ={
    val c = Math.exp(-lambda)
    var sum = 1d
    for(i <- 1 to k){
      sum *= lambda/i
    }
    return sum * c
  }

  def getHops(jobList:util.ArrayList[Job]):util.ArrayList[Int]={
    var hopCountList = new util.ArrayList[Int](10000)
    for(i <- 0 until 10000)
      hopCountList.add(0)

    var currenthop = 0;

    for(i <- jobList.size()/2 until jobList.size()) {
      val thejob = jobList.get(i)
      for(j <- 0 until thejob.taskList.size)
      {
        if(thejob.taskList(j).hopHistory == null)
          currenthop = 0
        else
          currenthop = thejob.taskList(j).hopHistory.size()
        if(currenthop>9999 || currenthop<0)
          print("WorkloadSelfAdjust: OMG! THERE ARE TOO MANY HOPS!!!!")
        else {
          val value = hopCountList.get(currenthop)+1
          hopCountList.set(currenthop,value)
        }
      }
    }

    println()

    var total = 0L;
    for(k <- 0 until hopCountList.size() ){
      total += hopCountList.get(k)
    }

    var pct = 0D;
    for(k <- 0 until 10 ) {
      pct = 1D*hopCountList.get(10-k)/total
      println(pct)
    }

    var avghop = 0D
    for(k <- 1 until hopCountList.size() ) {
      pct = 1D*hopCountList.get(k)/total
      avghop += (k-1)*pct
    }
    println("avghop per task for this test is: " + avghop)

    return hopCountList
  }

  def printMaxMinAvgHops(jobList:util.ArrayList[Job]):Unit={
    var hopCountList = new util.ArrayList[Int](10000)
    for(i <- 0 until 10000)
      hopCountList.add(0)

    var currenthop = 0;

    for(i <- jobList.size()/2 until jobList.size()) {
      val thejob = jobList.get(i)
      for(j <- 0 until thejob.taskList.size)
      {
        if(thejob.taskList(j).hopHistory == null)
          currenthop = 0
        else
          currenthop = thejob.taskList(j).hopHistory.size()
        if(currenthop>9999 || currenthop<0)
          print("WorkloadSelfAdjust: OMG! THERE ARE TOO MANY HOPS!!!!")
        else {
          val value = hopCountList.get(currenthop)+1
          hopCountList.set(currenthop,value)
        }
      }
    }

    println()

    var totalHops = 0L;
    var totalCount = 0L
    var avg = 0D;
    var max = 0
    var failed = 0;

    for(k <- 0 until hopCountList.size() ){
      if(hopCountList.get(k)>0) {
        max = k
        totalCount += hopCountList.get(k)
        totalHops += hopCountList.get(k) * k
        if(k>Cluster.nodeCount)
          failed += hopCountList.get(k)
      }

    }

    val avghops = 1D*totalHops/totalCount
    val failedRate = 1D*failed/totalCount

    println("AVG HOPS:" + avghops + " MAX HOPS:" + max + "FAILED" + failedRate)

  }

  def getHopsCDF(jobList:util.ArrayList[Job]):util.ArrayList[Int]={
    var hopCountList = new util.ArrayList[Int](10000)
    for(i <- 0 until 10000)
      hopCountList.add(0)

    var currenthop = 0;

    for(i <- jobList.size()/2 until jobList.size()) {
      val thejob = jobList.get(i)
      for(j <- 0 until thejob.taskList.size)
      {
        if(thejob.taskList(j).hopHistory == null)
          currenthop = 0
        else
          currenthop = thejob.taskList(j).hopHistory.size()
        if(currenthop>9999 || currenthop<0)
          print("WorkloadSelfAdjust: OMG! THERE ARE TOO MANY HOPS!!!!")
        else {
          val value = hopCountList.get(currenthop)+1
          hopCountList.set(currenthop,value)
        }
      }
    }

    println()

    var total = 0L;
    for(k <- 0 until hopCountList.size() ){
      total += hopCountList.get(k)
    }

    var acc = 0D;
    var pct = 0D;
    for(k <- 0 until hopCountList.size() ) {
      acc+=hopCountList.get(k)
      pct = acc/total
      println(pct)
    }
    return hopCountList
  }

  def getAvgJobResponse(jobList:util.ArrayList[Job]):Double={

    var total = 0D
    var count = 0D
    for(i <- jobList.size()/2 until jobList.size()) {
      total += 0.000001D * jobList.get(i).getResponse
      count += 1
      //println(jobList.get(i).getResponse)
    }

    val avg = total / count

    println("AVG RESPONSE IS "+ avg + " MS")
    return 1D*total/count/1000000
  }

  def getAvgHopsPerJob(jobList:util.ArrayList[Job]):Double={
    if(Cluster.schedulerType == "Sparrow")
      return -1D

    var total = 0D
    var count = 0D
    for(i <- jobList.size()/2 until jobList.size()) {
      total += jobList.get(i).getMaxHop
      count += 1
      //println(jobList.get(i).getResponse)
    }

    val avg = (total / count - 1)* 0.5

    println("Average communication time for job is "+ avg)
    return 1D*total/count
  }

  def getAvgLoad(loadList:util.ArrayList[Double]):Double={

    var total = 0D
    var count = 0D
    for(i <- loadList.size()/2 until loadList.size()) {
      total += loadList.get(i)
      count += 1
      //println(jobList.get(i).getResponse)
    }

    val avg = total / count

    println("Average stable load for this experiment is "+ avg)
    return avg
  }


}
