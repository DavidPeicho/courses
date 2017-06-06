#! /usr/bin/env python3

from mnist import load_mnist
import numpy as np
import visualize as vz
import matplotlib.pyplot as plt

train_data, train_labels = load_mnist(dataset='training', path='./')
test_data, test_labels = load_mnist(dataset='testing', path='./')

train_data = np.reshape(train_data, (60000, 28 * 28)).T
test_data  = np.reshape(test_data,  (10000, 28 * 28)).T

def EM(data):
    pass

def bernouli(x, center):
    out = np.zeros(x.shape)
    tmp = np.where(x == 0)
    out[tmp[0, :]] = 1 - center[tmp[0, :]]
    out[tmp[1, :]] = center[tmp[0, :]]
    return np.prod(out)

def etapeE(data, center, W):
    N = data.shape[1]
    D = data.shape[0]
    K = W.shape[0]
    tabl = np.zeros((N, K))

    for n in range(N):
        for k in range(K):
            tabl[n, k] = bernouli(data[:, n], center[:, k] * W[k])
        tabl[n, :] = tabl[n, :] / np.sum(tabl[n, :])

    return tabl

def etapeM(tabl, data):
    N = tabl.shape[0]
    K = tabl.shape[1]
    D = data.shape[0]
    W = np.sum(tabl, axis=0) / N
    center = np.dot(np.dot(data, tabl), np.diag(N * W))
    return W, center

if __name__ == '__main__':
    # Binarize data.
    binarize = lambda x : x < 128 / 255
    vfunc = np.vectorize(binarize)
    
    btrain_data = vfunc(train_data)
    # Display images.
    vz.plotGroupImages(btrain_data[:,:10])
    # Show images.
    plt.show()

