package com.tp.spark.utils
	
object TweetUtils {
	case class Tweet (
		id : String,
		user : String,
		text : String,
		place : String,
		country : String,
		lang : String
		)
}
