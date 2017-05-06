import math
import numpy as np
from numpy.linalg import inv
from numpy.linalg import det

class Gen_GaussClassifier(object):

    def __init__(self):
        self.trained_data = None
        self.cov_matrices = None
        self.inv_cov_matrices = None
        self.class_proba = None

    def train(self, data, labels):
        nb_vectors = data.shape[1]

        unique_labels = np.unique(labels)
        self.trained_data = np.zeros((unique_labels[-1] + 1, data.shape[0]))
        self.cov_matrices = []
        self.inv_cov_matrices = []
        self.class_proba = []

        # Goes through each labels to average
        # every images labeled by the same number.
        for i in unique_labels:
            mask = np.where(i == labels)[0]
            curr_labels = data[:, mask]

            # Computes the covariance matrix for each class
            cov = np.cov(curr_labels)
            self.cov_matrices.append(cov)
            self.inv_cov_matrices.append(inv(cov))

            # Saves probability of each class
            self.class_proba.append(curr_labels.shape[1] / nb_vectors)

            # Averages every pixel of every images labeled
            # with the same value.
            self.trained_data[i] = np.average(curr_labels, axis=1)

    def process(self, data):
        nb_img = data.shape[1]

        result = np.zeros(nb_img)
        for data_idx in range(0, nb_img):
            test_data = data[:, data_idx]
            max_val = - float('Inf')
            label = 0
            for x in range(0, self.trained_data.shape[0]):
                trained_data = self.trained_data[x]
                centered_vec = test_data - trained_data
                cov = self.cov_matrices[x]
                inv_cov = self.inv_cov_matrices[x]
                proba = self.class_proba[x]

                # Computes the discriminant function
                val = ((- 0.5 * centered_vec.T).dot(inv_cov).dot(centered_vec)) + (- 0.5 * np.log(det(cov))) + np.log(proba)
                if val > max_val:
                    max_val = val
                    label = x
            result[data_idx] = label

        return result
