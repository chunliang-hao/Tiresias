/**
 * Created by tom on 28/9/15.
 */
object test3 extends App {

  Cluster.resetCluster(200,64,"TiresiasTen")
  val wl1 = new WorkloadSelfAdjust(0.8,60000,200,2)
  //println(wl1.getProperLambda())
  wl1.run()
}