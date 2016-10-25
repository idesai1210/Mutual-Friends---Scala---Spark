import java.text.SimpleDateFormat
import org.apache.spark.sql.SQLContext
import java.util.Calendar
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.rdd.RDDFunctions._

object Assignment2_4 {
   def main(args : Array[String]){
      
        val config = new SparkConf()
                    .setAppName("Mutual Friends")
                    .setMaster("local[4]")
        val sparkContext = new SparkContext(config)
        var cal = Calendar.getInstance();
       
        def calculateAge(dob: String) ={
           val date=dob.split("/")
           val Month = cal.get(Calendar.MONTH)
           val Year = cal.get(Calendar.YEAR)
           var age = Year - date(2).toInt
           if(date(0).toInt>Month)
               age-= 1
           else if(date(0).toInt==Month){
               val currentDay=cal.get(Calendar.DAY_OF_MONTH);
               if(date(1).toInt>currentDay)
                     age-= 1
               
           }
           age
        }  
      
      
      val inputFile1 = sparkContext.textFile("hdfs://localhost:9000/input")
      val friends = inputFile1.map(x=>x.split("\\t"))
                             .filter(x => (x.size == 2))
                             .map(x=>(x(0),x(1).split(",")))
                             .flatMap(x=>x._2.flatMap(z=>Array((z,x._1))))
      
   //   println("cc" + friends(0).toString)
      
      val inputFIle2 = sparkContext.textFile("hdfs://localhost:9000/userdata")
      
      val value1 = inputFIle2.map(x=>x.split(","))
                             .map(x=>(x(0),calculateAge(x(9))))
      
      val value2 = friends.join(value1)
      //println(value2)
      
      def calculateMax(Max_Age: Iterable[Int]) = Max_Age.max
      
      
      val Max_Age = value2.groupBy(_._2._1)
                            .mapValues(Max_Age=>calculateMax(Max_Age.map(_._2._2)))
               
    //  val arrayRDD: RDD[Array[String]] = Average_Age.map(x => Array(x._1, x._2))
      val afterSort = Max_Age.sortBy(x => -x._2).collect()
      
      val topTen = afterSort.take(10)
      
      val topTenSparkContext = sparkContext.parallelize(topTen)
      val value3 = inputFIle2.map(x=>x.split(","))
                             .map(x=>(x(0),x(1),x(3),x(4),x(5)))
      val key = value3.map({case(first,second,third,fourth,fifth) => first->(second,third,fourth,fifth)})
      val Join = topTenSparkContext.join(key).sortBy(_._2,false)
      val answer = Join.map(columns=>(columns._2._2._1,columns._2._2._2,columns._2._2._3,columns._2._2._4,columns._2._1))
                       .collect.mkString("\n")
                       .replace("(","").replace(")","")
      println(answer)
   }
}