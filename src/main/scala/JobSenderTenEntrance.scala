import scala.util.Random

/**
  * Created by tom on 12/10/15.
  */
object JobSenderTenEntrance {

 def sendJob(job:Job):Unit={

   if(Cluster.nodeCount<10){
     println("You just cant run this simulation on such a small cluster!!!!")
     return
   }
   val rand = new Random()
   job.dispatch()
   job.timeSubmit = System.nanoTime()
       for (i <- 0 until job.taskList.size) {
         val randomchoice = rand.nextInt(10)
         Cluster.nodeList(randomchoice).scheduler.ta ! job.taskList(i)
         job.taskList(i).timeSubmit = System.nanoTime()
       }
     }

 }
