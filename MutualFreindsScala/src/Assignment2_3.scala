import org.apache.spark.SparkContext
import org.apache.spark.SparkConf


object Assignment2_3 {
  
  def main(args : Array[String]){
      
    val config = new SparkConf()
                .setAppName("Mutual Friends")
                .setMaster("local[4]")
    val sparkContext = new SparkContext(config)
    
    val userA = readLine("Enter UserA : ")
    val userB = readLine("Enter UserB : ")
    
    val inputFile1 = sparkContext.textFile("hdfs://localhost:9000/input")
    
    val userAfriends = inputFile1.map(x=>x.split("\\t"))
                                           .filter(x => (x.size == 2))
                                           .filter(x=>userB==x(0))
                                           .flatMap(x=>x(1).split(","))
            
    val userBfriends = inputFile1.map(x=>x.split("\\t"))
                                           .filter(x => (x.size == 2))
                                           .filter(x=>userA==x(0))
                                           .flatMap(x=>x(1).split(","))
    
    val mutualFriends = userBfriends.intersection(userAfriends).collect()
    
    val inputFile2 = sparkContext.textFile("hdfs://localhost:9000/userdata")
    val mutualFriendsDetails = inputFile2.map(x=>x.split(","))  
                                         .filter(x=>mutualFriends.contains(x(0)))
                                         .map(x=>(x(1)+":"+x(9)))
    val answer=userA+" "+userB+"\t["+mutualFriendsDetails.collect.mkString(",")+"]"
    println(answer)
  }
}