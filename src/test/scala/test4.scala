/**
 * Created by tom on 30/9/15.
 */
object test4 extends App{


  Cluster.resetCluster(200,64,"TiresiasTen")
  val wl1 = new Workload(4.3,30000,200,2)
  //println(wl1.getProperLambda())
  wl1.run()

}
