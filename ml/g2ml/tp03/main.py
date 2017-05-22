#! /usr/bin/env python3

from mnist import load_mnist
import numpy as np
import visualize as vz
import matplotlib.pyplot as plt

train_data, train_labels = load_mnist(dataset='training', path='./')
test_data, test_labels = load_mnist(dataset='testing', path='./')

train_data = np.reshape(train_data, (60000, 28 * 28)).T
test_data  = np.reshape(test_data,  (10000, 28 * 28)).T

if __name__ == '__main__':
    binarize = lambda x : x < 128/255
    vfunc = np.vectorize(binarize)
    train_data = vfunc(train_data)
    # Display mean images.
    vz.plotGroupImages(train_data[:,:10])
    # Show images.
    plt.show()
