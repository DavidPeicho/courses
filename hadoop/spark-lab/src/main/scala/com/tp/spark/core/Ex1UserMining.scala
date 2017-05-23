package com.tp.spark.core


import org.apache.spark.{SparkContext, SparkConf}

import org.apache.spark.rdd._
import com.tp.spark.utils.TweetUtils._
import play.api.libs.json._

/**
 *  We still use the dataset with the 8198 reduced tweets. The data are reduced tweets as the example below:
 *
 *  {"id":"572692378957430785",
 *  "user":"Srkian_nishu :)",
 *  "text":"@always_nidhi @YouTube no i dnt understand bt i loved of this mve is rocking",
 *  "place":"Orissa",
 *  "country":"India"}
 *
 *  We want to make some computations on the users:
 *  - find all the tweets by user
 *  - find how many tweets each user has
 */
object Ex1UserMining {

  val pathToFile = "data/reduced-tweets.json"

  case class JSONTweet(id: String, user: String, text: String, place: String, country: String)

  implicit val jsonTweetReads = Json.reads[JSONTweet]
  /**
   *  Load the data from the json file and return an RDD of Tweet
   */
  def loadData(): RDD[Tweet] = {
    // Create the spark configuration and spark context
    val conf = new SparkConf()
        .setAppName("User mining")
        .setMaster("local[*]")

    val sc = SparkContext.getOrCreate(conf)

    // Load the data and parse it into a Tweet.
    val jsonStr = sc.textFile(pathToFile)
    jsonStr.map((s : String) => {
      val obj = Json.parse(s)
      val jsontweet = obj.as[JSONTweet](jsonTweetReads)
      Tweet.apply(jsontweet.id, jsontweet.user, jsontweet.text, jsontweet.place, jsontweet.country, "")
    })

  }

  /**
   *   For each user return all his tweets
   */
  def tweetsByUser(): RDD[(String, Iterable[Tweet])] = {

    loadData().map(x => (x.user, x)).groupByKey()

  }

  /**
   *  Compute the number of tweets by user
   */
  def tweetByUserNumber(): RDD[(String, Int)] = {

    tweetsByUser().mapValues(list => {
      list.map(tweet => 1).sum
    })

  }


  /**
   *  Top 10 twitterers
   */
  def topTenTwitterers(): Array[(String, Int)] = {

    tweetByUserNumber().sortBy(_._2, false).take(10)

  }

}

