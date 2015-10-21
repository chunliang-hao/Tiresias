import java.util

import akka.actor.Props

import scala.collection.mutable.ArrayBuffer

/**
 * Created by tom on 25/9/15.
 */
class WorkloadSelfAdjust(loadavg:Double,lifeSpan:Int,taskDur:Int,taskCount:Int) extends Runnable{

  val jobList = new util.ArrayList[Job]()
  val loadList = new util.ArrayList[Double]()

  //simulation properties
  val timeFrame = 10
  var lambda = 0d

  override def run(): Unit = {

      val startpoint = System.nanoTime()

      var timeleft = this.lifeSpan

      var  jsa = Cluster.tiresiasActorSystem.actorOf(Props[JobSubmitActor])

      if(Cluster.schedulerType == "TiresiasTen")
           jsa = Cluster.tiresiasActorSystem.actorOf(Props[JobSubmitActorTenEntrance])

    if(Cluster.schedulerType == "TiresiasOne")
      jsa = Cluster.tiresiasActorSystem.actorOf(Props[JobSubmitActorOneEntrance])


    if(Cluster.schedulerType == "Sparrow") {
        val ts0 = new SparrowScheduler(Cluster.nodeList(0))
        jsa = Cluster.tiresiasActorSystem.actorOf(Props(new SparrowActor(ts0)))
        println("WorkloadSelfAdjust: Sparrow Scheduler Enabled")
      }

      var submitOfThisFrame = 0
      var jtemp = new Job(0,0,0)

      do {

        val currentload = Cluster.getLoad()
        //this.loadList.add(currentload)
        this.adjustLambda(currentload,this.loadavg)

        submitOfThisFrame = ExperimentFunctions.getPossionVariable(lambda)
        println("Workload: current cluster load is: " + currentload+" and submit is: "+submitOfThisFrame)

        if(submitOfThisFrame<0){
          println("Workload: damn somehow your lambda calculation is wrong!")
        }

        if(submitOfThisFrame>0){
          for(i <- 1 to submitOfThisFrame){
            jtemp = new Job(taskDur,taskCount,1)
            jobList.add(jtemp)
            jsa ! jtemp
          }
        }

        timeleft -= this.timeFrame
        Thread.sleep(this.timeFrame)
      }while(timeleft>0)

      val endPoint = System.nanoTime()
      val timeDuration = endPoint - startpoint

      println("SUMMARY -- TIME SPEND: "+timeDuration+" Avarage Delay: "+ExperimentFunctions.getAvgDelay(jobList,taskDur))

    Thread.sleep(3000)

    ExperimentFunctions.getAvgJobResponse(jobList)
    ExperimentFunctions.getAvgHopsPerJob(jobList)
    ExperimentFunctions.getAvgLoad(loadList)
    ExperimentFunctions.getHops(jobList)

    sys.exit()
  }

  def adjustLambda(latestload:Double,expectedLoad:Double) ={
    val ratio = 10D * (expectedLoad - latestload) / expectedLoad
    if(ratio>0)
      this.lambda += 0.1*ratio
    else this.lambda -= 0.1
  }



}
