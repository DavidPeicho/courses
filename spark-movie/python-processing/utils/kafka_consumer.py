from kafka import SimpleConsumer, KafkaClient

class KafkaConsummerWrapper:
    def __init__(self, topic, broker = "localhost:9092", group_id = 'sparkmovie-group'):
        self._client = KafkaClient(broker)
        self._consumer = SimpleConsumer(self._client, group_id, topic)

    def consume(self, callback):
        for message in self._consumer:
            callback(message)
