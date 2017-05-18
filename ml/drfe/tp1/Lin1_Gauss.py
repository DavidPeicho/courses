import numpy as np

class Lin1_Gauss:
    # Gaussian classifier, Sigma = indicator matrix.
    def train(self, data, labels):
        # Computes the average for each class.
        self.nbclasses = np.unique(labels).shape[0]
        self.mean = np.zeros((data.shape[0], self.nbclasses))
        for i in range(self.nbclasses):
            self.mean[:, i] = np.mean(data[:, np.where(labels == i)[0]],
                                      axis = 1)


    def process(self, data):
        # Data is labeled with the nearest class in terms of euclidian distance.
        return np.array([self._process(data[:, i])
                        for i in range(data.shape[1])])


    def _process(self, vect):
        # Computes the euclidian distance with all classes and returns the
        # smallest one.
        dist = [np.sum((self.mean[:, i] - vect) ** 2) for i in
                                                       range(self.nbclasses)]
        return np.argmin(dist)
