import numpy as np

class MajorityClassifier:

    def __init__(self):
        self.histogram = None

    def train(self, data, labels):
        self.histogram = np.bincount(labels)

    def process(self, data):
        return np.argmax(self.histogram)