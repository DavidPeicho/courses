import numpy as np
import random as rd
import sys

class EmGaussian:
    def _init_center_cov(self):
        self.center = np.zeros((self.data.shape[0], self.nbComponents))
        #cov = [np.zeros((data.shape[0], data.shape[0])) for i in
        #        range(nbComponents)]
        self.cov = [np.identity(self.data.shape[0]) for i in
                    range(self.nbComponents)]
        minimages = self.data.shape[1] // 4

        for i in range(self.nbComponents):
            maxi = rd.randint(minimages, self.data.shape[1])
            self.center[:, i] = np.mean(self.data[:, minimages:maxi], axis=1)
            #cov[i] = np.cov(data[:, minimages:maxi])


    def __init__(self, data, nbComponents):
        self.data = data
        self.nbComponents = nbComponents
        self.delta = 1e-2

    def computeEM(self):
        # P(k)
        self.W = np.repeat(1 / self.nbComponents, self.nbComponents)
        self._init_center_cov()

        while True:
            self._expectationStep()
            oldW = self.W
            self._maximizationStep(tabl, center, data)
            if np.sum(np.abs(W - oldW)) < self.delta:
                return self.W, self.center, self.cov

    def _gaussian(self, x, k):
        # Here, we have to use the logarithm
        # in order to store the value in a double
        D = x.shape[0]
        xcentered = x - self.center[:, k]
        covdet = np.linalg.det(self.cov[k])
        covinv = np.linalg.pinv(self.cov[k])
        #piN = (2 * np.pi) ** D
        #denom = np.sqrt(piN * covdet)
        a = -0.5 * xcentered.T.dot(covinv).dot(xcentered)
        return -0.5 * (D * np.log(2 * np.pi) * np.log(covdet)) + a
        #return np.exp(-0.5 * xcentered.T.dot(covinv.dot(xcentered))) / denom

    def _expectationStep(self):
        N = self.data.shape[1] # Number of data.
        D = self.data.shape[0] # Number of pixels.
        K = self.W.shape[0] # Number of classes.
        self.tabl = np.zeros((N, K))
        # Likelihood
        for n in range(N):
            for k in range(K):
                # print("EXPECTATION n =", n, "k =", k)
                # Probability for the image to be from K class.
                self.tabl[n, k] = self._gaussian(self.data[:, n], k) * self.W[k]
            tsum = np.sum(self.tabl[n, :])
            self.tabl[n, :] = self.tabl[n, :] / tsum

    def _maximizationStep(self):
        print("MAXIMIZATION")
        N = self.tabl.shape[0] # Number of data.
        K = self.tabl.shape[1] # Number of classes.
        D = self.data.shape[0] # Number of pixels.
        tsum = np.sum(self.tabl, axis=0)
        oldcenter = W
        # Weights
        self.W =  tsum / N
        # Means
        self.center = np.dot(self.data, self.tabl) / tsum
        # Covariances
        xcentered = np.power(self.data - oldcenter, 2)
        self.cov = np.dot(xcentered, self.tabl) / tsum
        print(self.cov.shape)
