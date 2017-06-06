import numpy as np
import random as rd
import sys

class EmBernoulli:
    def _init_centers(self, data, nbClasses):
        centers = np.zeros((data.shape[0], nbClasses))

        for i in range(nbClasses):
            centers[:, i] = np.mean(data[:, rd.randint(0, data.shape[1])])

        return centers

    def computeEM(self, data, nbClasses):
        # FIXME
        maxIt = 30
        # P(k)
        W = np.repeat(1 / nbClasses, nbClasses)
        center = self._init_centers(data, nbClasses)
        for it in range(maxIt):
            tabl = self._expectationStep(data, center, W)
            W, center = self._maximizationStep(tabl, data)

        return W, center

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
