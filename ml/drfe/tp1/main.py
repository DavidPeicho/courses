#! /usr/bin/env python3

from mnist import load_mnist
import numpy as np
import visualize as vz
import matplotlib.pyplot as plt

from Lin1_Gauss import Lin1_Gauss

train_data, train_labels = load_mnist(dataset='training', path='./')
test_data, test_labels = load_mnist(dataset='testing', path='./')

train_data = np.reshape(train_data, (60000, 28 * 28)).T
test_data  = np.reshape(test_data,  (10000, 28 * 28)).T

if __name__ == '__main__':
    # Display train data.
    # vz.plotImage(train_data[:,0])
    # vz.plotGroupImages(train_data[:,0:10])

    # Euclidian distance.
    Q1 = Lin1_Gauss()
    Q1.train(train_data, train_labels)

    # Display mean images.
    # vz.plotGroupImages(Q1.mean[:,:])
    # Show images.
    # plt.show()

    out = Q1.process(test_data)
    perf = (out == test_labels).sum() / test_labels.shape[0]

    print(perf)
