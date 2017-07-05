import argparse
import json

from kafka import SimpleProducer, KafkaClient

from utils.kafka_producer import KafkaProducerWrapper
from fetcher_lib.crawler import imdb_crawler

def parse_args():
    description =   """
                    Fetches data from a file or from an API and stream the
                    Json response to kafka
                    """
    
    arg_parser = argparse.ArgumentParser(description=description)
    
    # Register arguments
    arg_parser.add_argument('-i', '--input',
                            help='Input file used to stream content in kafka',
                            required=False)
    arg_parser.add_argument('-o', '--output',
                            help='Write crawling ouput to the given file',
                            required=False)

    return arg_parser.parse_args()

def stream_file(file_path, kafka_handler):
    with open(file_path, "r") as f:
        for line in f:
            kafka_handler.produce(line, 'movie-topic')

def stream_from_api(kafka_handler):
    THEMOVIEDB_API_TOKEN = "2cde1ceaa291c9271e32272dc26200fe"
    
    MOVIES_ROUTE_API = "https://api.themoviedb.org/3/discover/movie"
    REVIEW_ROUTE_API = "https://api.themoviedb.org/3/movie/{}/reviews"
    routes = {
        "movies_list": MOVIES_ROUTE_API,
        "review": REVIEW_ROUTE_API
    }

    # Defines callback triggered each time a movie is
    # fetched from the api
    def callback(movie):
        string = json.dumps(movie)
        print("[KAFKA] Movie pushed to topic \'{}\'".format('movie-topic'))
        kafka_handler.produce(string, 'movie-topic')
    
    MIN_YEAR = 2001
    MAX_YEAR = 2017

    crawler = imdb_crawler.IMDBCrawler(routes, THEMOVIEDB_API_TOKEN, args.output)
    crawler.setMinMaxYear(MIN_YEAR, MAX_YEAR)

    crawler.start()
    crawler.crawl_sync(callback)
    crawler.stop()

if __name__ == "__main__":
    kafka_handler = KafkaProducerWrapper()

    args = parse_args()
    if not(args.input is None):
        stream_file(args.input, kafka_handler)
    else:
        stream_from_api(kafka_handler)
