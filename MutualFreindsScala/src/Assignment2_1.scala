
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
object Assignment2_1 {
  
      def main(args : Array[String]){
      
      val config = new SparkConf()
                  .setAppName("Mutual Friends")
                  .setMaster("local[4]")
      
      val sparkContext = new SparkContext(config)

      val inputFile = sparkContext.textFile("hdfs://localhost:9000/input1")
      
   
      val allUsers = inputFile.map(li=>li.split("\\t"))
                              .filter(li => (li.size == 2))
                              .flatMap(li=>Array(li(0))).collect()//0
      //println(user1(user1.length - 1))
     
      var answer = ""
     // var z = new Array[String]()
     for(a <-0 to (allUsers.length - 1)){
        
        for(b<-(a + 1) to (allUsers.length - 1)){
          if(allUsers(a) < allUsers(b)){
               val userAfriends = inputFile.map(x=>x.split("\\t"))
                                           .filter(x => (x.size == 2))
                                           .filter(x=>(allUsers(b).toString()==x(0)))
                                           .flatMap(x=>x(1).split(","))
            
               val userBfriends = inputFile.map(x=>x.split("\\t"))
                                           .filter(x => (x.size == 2))
                                           .filter(x=>(allUsers(a).toString()==x(0)))
                                           .flatMap(x=>x(1).split(","))
            
              
               val mutualFriends = userBfriends.intersection(userAfriends).collect()
               
               if(!mutualFriends.isEmpty){
                   answer = answer + allUsers(a)+", "+allUsers(b)+"\t"+mutualFriends.mkString(",") + "\n"
                   //println(answer)
               }
               
          }
        }
        
      }
  
      
      println(answer)
      
    }
      
}