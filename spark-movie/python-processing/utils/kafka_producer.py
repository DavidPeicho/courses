from kafka import SimpleProducer, KafkaClient

class KafkaProducerWrapper:
    def __init__(self, broker = "localhost:9092", every_n = 1000):
        self._client = KafkaClient(broker)
        self._producer = SimpleProducer(
            self._client, async = True,
            batch_send_every_n = every_n,
            batch_send_every_t = 10
        )

    def produce(self, data, topic):
        try:
            self._producer.send_messages(topic, data.encode('utf-8'))
        except Exception as e:
            print(e)
