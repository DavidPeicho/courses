from mnist import load_mnist
import numpy as np

class Pca:
    def train(self, data):
        # Covariance centered matrix of train data.
        mean = np.tile(np.mean(data, axis=1), (data.shape[1], 1)).T
        centered = data - mean
        self.cov = np.cov(centered)
        # Eigenvalues and eigenvectors of covariance matrix.
        self.eigenval, self.eigenvect = np.linalg.eigh(self.cov)
        # Sort eigenvalues and vectors in decreasing order.
        iw = np.argsort(self.eigenval)[::-1]
        self.eigenval = self.eigenval[iw]
        self.eigenvect = self.eigenvect[:, iw]

    # Find number of eigenvalues needed to have at least rates[j] of info.
    def computecompression(self, rates):
        nb_eigenval = [None for i in range(len(rates))]
        seigenv = self.eigenval.sum()
        j = 0

        for i in range(len(self.eigenval)):
            if j >= len(rates):
                break
            c = self.eigenval[:i].sum() / seigenv
            if c >= rates[j]:
                nb_eigenval[j] = i
                j += 1

        return nb_eigenval

    # Project in n dimensions.
    def project(self, data, n):
        mean = np.tile(np.mean(data, axis=1), (data.shape[1], 1)).T
        centered = data - mean
        nv = self.eigenvect[:, :n]
        return np.dot(nv.T, centered)
