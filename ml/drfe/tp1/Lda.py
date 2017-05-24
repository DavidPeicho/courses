import numpy as np

class Lda:
    def train(self, data, labels):
        # Fetch number of classes.
        self.nbclasses = np.unique(labels).shape[0]
        # Compute the mean for each classes.
        self.mean = np.zeros((data.shape[0], self.nbclasses))
        self.classes = [data[:, np.where(labels == i)[0]]
                        for i in range(self.nbclasses)]


        for i in range(self.nbclasses):
            self.mean[:, i] = np.mean(self.classes[i], axis = 1)

        # Withing-class scatter matrix
        self.sw = np.zeros((data.shape[0], data.shape[0]))
        for i in range(self.nbclasses):
            vectors = self.classes[i]
            mean = self.mean[:, i].reshape((self.mean.shape[0], 1))
            meanMatrix = np.tile(mean, (1, vectors.shape[1]))
            centered = (vectors - meanMatrix)
            self.sw += centered.dot(centered.T)

        # Overall mean computation
        self.overallMean = np.mean(data,axis = 1)

        # Between-class scatter matrix
        self.sb = np.zeros((data.shape[0], data.shape[0]))
        for i in range(self.nbclasses):
            nbElts = self.classes[i].shape[1]
            mean = self.mean[:, i].reshape((self.mean.shape[0], 1))
            overallMean = self.overallMean.reshape((self.overallMean.shape[0], 1))
            centered = mean - overallMean
            self.sb += nbElts * centered.dot(centered.T)

        inv_sw = np.linalg.pinv(self.sw)
        self.eigenval, self.eigenvect = np.linalg.eig(inv_sw.dot(self.sb))
        iw = np.argsort(self.eigenval)[::-1]
        self.eigenval = self.eigenval[iw]
        self.eigenvect = self.eigenvect[:, iw]

    # Project in n dimensions.
    def project(self, data, n):
        centered = (data.T - np.mean(data, axis=1)).T
        nv = self.eigenvect[:, :n]
        return np.dot(nv.T, data)
