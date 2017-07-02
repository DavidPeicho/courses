import argparse

from kafka import SimpleProducer, KafkaClient

# from api_fetcher import Crawler

class KafkaHandler:
    def __init__(self):
        self._client = KafkaClient("localhost:9092")
        self._producer = SimpleProducer(
            self._client, async = True,
            batch_send_every_n = 1000,
            batch_send_every_t = 10
        )

    def produce(self, movie_data, topic):
        try:
            self._producer.send_messages(topic, movie_data.encode('utf-8'))
        except Exception as e:
            print(e)

def parse_args():
    description =   """
                    Fetches data from a file or from an API and stream the
                    Json response to kafka
                    """
    
    arg_parser = argparse.ArgumentParser(description=description)
    
    # Register arguments
    arg_parser.add_argument('-i', '--input',
                            help='Input file used to stream content in kafka',
                            required=True)

    return arg_parser.parse_args()

kafka_handler = KafkaHandler()

def stream_file(file_path):
    with open(file_path, "r") as f:
        for line in f:
            kafka_handler.produce(line, 'movie-topic')

if __name__ == "__main__":
    args = parse_args()
    if not(args.input is None):
        stream_file(args.input)
