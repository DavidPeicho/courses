import numpy as np

class Lda:
    def train(self, data, labels):
        # Fetch number of classes.
        self.nbclasses = np.unique(labels).shape[0]
        # Compute the mean for each classes.
        self.mean = np.zeros((data.shape[0], self.nbclasses))
        for i in range(self.nbclasses):
            self.mean[:, i] = np.mean(data[:, np.where(labels == i)[0]],
                                      axis = 1)
        # self.cov = np.cov(centered)
        # Eigenvalues and eigenvectors of covariance matrix.
        # self.eigenval, self.eigenvect = np.linalg.eigh(self.cov)
        # Sort eigenvalues and vectors in decreasing order.
        # iw = np.argsort(self.eigenval)[::-1]
        # self.eigenval = self.eigenval[iw]
        # self.eigenvect = self.eigenvect[:, iw]

    # Project in n dimensions.
    def project(self, data, n):
        # centered = (data.T - np.mean(data, axis=1)).T
        # nv = self.eigenvect[:, :n]
        # return np.dot(nv.T, centered)
