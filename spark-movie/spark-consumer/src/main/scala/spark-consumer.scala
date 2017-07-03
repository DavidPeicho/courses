import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object SparkConsumer {

    def main(args: Array[String]) {

        val requiredOptions = List(
            "adress",
            "group-id"
        )
        val options = parseCmdLine(Map(), args.toList)

        System.out.println("\n[SPARK CONSUMER] Starting Spark instance...")
        for (x <- requiredOptions) {
            if (!options.contains(x)) {
                System.err.println("[SPARK CONSUMER] Missing argument: <" + x + ">")
                System.out.println("[SPARK CONSUMER] Stoping Spark instance...")
                System.exit(1)
            }
        }

        val kafkaParams = Map[String, Object] (
            "bootstrap.servers" -> options.get("adress").get,
            "group.id" -> options.get("group-id").get,
            "auto.offset.reset" -> "latest",
            "enable.auto.commit" -> (false: java.lang.Boolean),
            "key.deserializer" -> classOf[StringDeserializer],
            "value.deserializer" -> classOf[StringDeserializer]
        )

        val topics = Array("movie-topic")
        val sparkConf = new SparkConf().setAppName("SparkConsumer").setMaster("local[*]")

        val ssc = new StreamingContext(sparkConf, Seconds(1))
        ssc.checkpoint("checkpoint")

        val stream = KafkaUtils.createDirectStream[String, String] (
            ssc, PreferConsistent,
            Subscribe[String, String](topics, kafkaParams)
        )

        stream.map(record => (record.key, record.value))
        stream.foreachRDD { rdd =>
            println("Printing everything:")
            rdd.map(x => x.value()).foreach(x => print(x))
        }

        ssc.start() 
        ssc.awaitTermination()
    }

    def parseCmdLine(map : Map[String, String], args: List[String]) : Map[String, String] = {
        def isSwitch(s : String) = (s(0) == '-')
        args match {
            case Nil => {
                map
            }
            case ("--adress" | "-a") :: value :: tail => {
                parseCmdLine(map ++ Map("adress" -> value), tail)
            }
            case ("--group-id" | "-gid") :: value :: tail => {
                parseCmdLine(map ++ Map("group-id" -> value), tail)
            }
            case string :: tail  => {
                parseCmdLine(map, tail)
            }
        }
    }

}
