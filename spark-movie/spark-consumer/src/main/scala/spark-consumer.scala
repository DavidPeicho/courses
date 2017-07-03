import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.kafka.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka.ConsumerStrategies.Subscribe

object SparkConsumer {

    val kafkaParams = Map[String, Object] (
        "bootstrap.servers" -> "localhost:9092"
    )

    val topics = Array("movie-topic")

    def main(args: Array[String]) {
        System.out.println("\n[SPARK CONSUMER] Starting Spark instance...")
        if (args.length < 4) {
            System.err.println("Usage: SparkConsumer <zkQuorum><group> <topics> <numThreads>")
            System.out.println("[SPARK CONSUMER] Stoping Spark instance...")
            System.exit(1)
        }

        val sparkConf = new SparkConf().setAppName("SparkConsumer")

        val ssc = new StreamingContext(sparkConf, Seconds(2))
        ssc.checkpoint("checkpoint")

        val stream = KafkaUtils.createDirectStream[String, String] (
            ssc,
            PreferConsistent,
            Subscribe[String, String](topics, kafkaParams)
        )

    }

}
