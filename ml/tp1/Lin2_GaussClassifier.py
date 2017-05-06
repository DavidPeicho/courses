import math
import numpy as np
from numpy.linalg import inv

class Lin2_GaussClassifier(object):

    def __init__(self):
        self.trained_data = None

    def train(self, data, labels):
        unique_labels = np.unique(labels)
        self.trained_data = np.zeros((unique_labels[-1] + 1, data.shape[0]))
        # Computes the covariance matrix
        self.inv_cov = inv(np.cov(data))

        # Goes through each labels to average
        # every images labeled by the same number.
        for i in unique_labels:
            mask = np.where(i == labels)[0]
            curr_labels = data[:, mask]
            # Averages every pixel of every images labeled
            # with the same value.
            self.trained_data[i] = np.average(curr_labels, axis=1)

    def process(self, data):
        nb_img = data.shape[1]

        result = np.zeros(nb_img)
        for data_idx in range(0, nb_img):
            test_data = data[:, data_idx]
            min_dist = float('Inf')
            label = 0
            for x in range(0, self.trained_data.shape[0]):
                trained_data = self.trained_data[x]
                centered_vec = test_data - trained_data
                # Uses the Mahalanobis distance
                dist = centered_vec.T.dot(self.inv_cov).dot(centered_vec)
                if dist < min_dist:
                    min_dist = dist
                    label = x
            result[data_idx] = label

        return result
