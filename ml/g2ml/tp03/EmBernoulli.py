import numpy as np
import random as rd
import sys

class EmBernoulli:
    def _init_center(self, data, nbComponents):
        center = np.zeros((data.shape[0], nbComponents))
        minimages = data.shape[1] // 4
        for i in range(nbComponents):
            mini = 0
            maxi = rd.randint(minimages, data.shape[1])
            center[:, i] = np.mean(data[:, minimages:maxi], axis=1)

        return center

    def computeEM(self, data, nbComponents):
        delta = 1e-2
        # P(k)
        W = np.repeat(1 / nbComponents, nbComponents)
        center = self._init_center(data, nbComponents)
        while True:
            tabl = self._expectationStep(data, center, W)
            newW, center = self._maximizationStep(tabl, data)
            if np.sum(np.abs(newW - W)) < delta:
                return newW, center
            W = newW

    def _bernoulli(self, x, center):
        # proba is P(X1, ..., Xn)
        proba = np.zeros(x.shape)
        # Retrieve indices of black and white pixels.
        indices = [np.array(np.where(x == i)) for i in range(2)]
        proba[indices[0]] = 1 - center[indices[0]]
        proba[indices[1]] = center[indices[1]]
        return np.prod(proba)

    def _expectationStep(self, data, center, W):
        N = data.shape[1] # Number of data.
        D = data.shape[0] # Number of pixels.
        K = W.shape[0] # Number of classes.
        tabl = np.zeros((N, K))

        for n in range(N):
            for k in range(K):
                # Probability for the image to be from K class.
                tabl[n, k] = self._bernoulli(data[:, n], center[:, k]) * W[k]
            tsum = np.sum(tabl[n, :])
            tabl[n, :] = tabl[n, :] / tsum

        return tabl

    def _maximizationStep(self, tabl, data):
        N = tabl.shape[0] # Number of data.
        K = tabl.shape[1] # Number of classes.
        D = data.shape[0] # Number of pixels.
        tsum = np.sum(tabl, axis=0)
        W =  tsum / N
        center = np.dot(data, tabl) / (tsum)
        return W, center
