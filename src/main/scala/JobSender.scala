import scala.util.Random

/**
 * Created by tom on 12/10/15.
 */
object JobSender {

def sendJob(job:Job):Unit={
  val rand = new Random()
  job.dispatch()
  job.timeSubmit = System.nanoTime()
      for (i <- 0 until job.taskList.size) {
        val randomchoice = rand.nextInt(Cluster.nodeList.size)
        Cluster.nodeList(randomchoice).scheduler.ta ! job.taskList(i)
        job.taskList(i).timeSubmit = System.nanoTime()
      }
    }

}
