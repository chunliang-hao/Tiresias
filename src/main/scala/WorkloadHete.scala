import java.util

import akka.actor.Props

/**
 * Created by tom on 25/9/15.
 */
class WorkloadHete(loadavg:Double,lifeSpan:Int,taskDur1:Int,taskDur2:Int,taskCount:Int) extends Runnable{

  val jobList1 = new util.ArrayList[Job]()
  val jobList2 = new util.ArrayList[Job]()
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
      do {

        val currentload = Cluster.getLoad()
        this.loadList.add(currentload)
        this.adjustLambda(currentload,this.loadavg)

        submitOfThisFrame = ExperimentFunctions.getPossionVariable(lambda)
        println("Workload: current cluster load is: " + currentload+" and submit is: "+submitOfThisFrame)

        if(submitOfThisFrame<0){
          println("Workload: damn somehow your lambda calculation is wrong!")
        }

        if(submitOfThisFrame>0){
          for(i <- 1 to submitOfThisFrame){
            val jtemp1 = new Job(taskDur1,taskCount,1)
            val jtemp2 = new Job(taskDur2,taskCount,1)
            jobList1.add(jtemp1)
            jsa ! jtemp1
            jobList2.add(jtemp2)
            jsa ! jtemp2
          }
        }

        timeleft -= this.timeFrame
        Thread.sleep(this.timeFrame)
      }while(timeleft>0)

      val endPoint = System.nanoTime()
      val timeDuration = endPoint - startpoint

      println("SUMMARY -- TIME SPEND: "+timeDuration)

    Thread.sleep(3000)

    println("For job type1 with " + taskDur1+"ms:")
    ExperimentFunctions.getAvgJobResponse(jobList1)
    ExperimentFunctions.getAvgHopsPerJob(jobList1)

    println("For job type2 with " + taskDur2+"ms:")
    ExperimentFunctions.getAvgJobResponse(jobList2)
    ExperimentFunctions.getAvgHopsPerJob(jobList2)
    //ExperimentFunctions.getHops(jobList1)
    ExperimentFunctions.getAvgLoad(loadList)

    sys.exit()
  }

  def adjustLambda(latestload:Double,expectedLoad:Double) ={
    val ratio = 10D * (expectedLoad - latestload) / expectedLoad
    if(ratio>0)
      this.lambda += 0.1*ratio
    else this.lambda -= 0.1
  }



}
