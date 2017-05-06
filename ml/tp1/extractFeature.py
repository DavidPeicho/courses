from array import array as pyarray
import numpy as np;

class extractFeature:
    def __init__(self, folder = ''):
        fBase = open(folder + 'base.data', 'rb')
        rawdata = pyarray("d", fBase.read())
        self.Base = np.array(rawdata[0:10*784]).reshape((10, 784))
        self.M    = np.array(rawdata[10*784:]).reshape((784, 1))
        fBase.close()
    def process(self, data):
        return self.Base.dot(data - self.M)
