package com.sparkmovie.core

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

import play.api.libs.json._

import com.sparkmovie.utils.CommandLineParser
import com.sparkmovie.utils.MovieUtils

object SparkConsumer {

    def main(args: Array[String]) {

        val requiredOptions = List(
            "adress",
            "group-id"
        )
        val topics = Array(
            "movie-topic"
        )
        
        System.out.println("\n[SPARK CONSUMER] Starting Spark instance...")
        val options = CommandLineParser.parseCmdLine(Map(), args.toList)
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

        val sparkConf = new SparkConf()
                                .setAppName("SparkConsumer")
                                .setMaster("local[*]")

        // Creates the streaming context allowing to connect to Kafka.
        val ssc = new StreamingContext(sparkConf, Seconds(1))
        ssc.checkpoint("checkpoint")

        // Creates the stream connecting the streaming context
        // to several Kafka topics.
        val stream = KafkaUtils.createDirectStream[String, String] (
            ssc, PreferConsistent,
            Subscribe[String, String](topics, kafkaParams)
        )

        stream.map(record => (record.key, record.value))
        stream.foreachRDD { rdd =>
            println("Printing everything:")
            /*rdd.foreachPartition { partitionOfRecords =>

            }*/
            rdd.map(x => x.value()).foreach(x => {
                    //print(x)
                    Json.parse(x).validate[MovieUtils.Movie] match {
                        case JsSuccess(movie, _) => {    
                            movie
                        }
                        case JsError(_) => println("parsing failed")
                    }
                }
            )
        }

        ssc.start()
        ssc.awaitTermination()
    }

}
