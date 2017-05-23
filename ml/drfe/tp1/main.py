#! /usr/bin/env python3

from mnist import load_mnist
import numpy as np
import visualize as vz
import matplotlib.pyplot as plt

from Lin1_Gauss import Lin1_Gauss
from Pca import Pca

train_data, train_labels = load_mnist(dataset='training', path='./')
test_data, test_labels = load_mnist(dataset='testing', path='./')

train_data = np.reshape(train_data, (60000, 28 * 28)).T
test_data  = np.reshape(test_data,  (10000, 28 * 28)).T

if __name__ == '__main__':
    # Gaussian classifier, euclidian distance.
    Q1 = Lin1_Gauss()
    # Q1.train(train_data, train_labels)

    # Question 1.
    # Display mean images.
    # vz.plotGroupImages(Q1.mean[:,:])
    # Show images.
    # plt.show()

    # Question 3
    # out = Q1.process(test_data)
    # perf = (out == test_labels).sum() / test_labels.shape[0]
    # print("Lin1 perf:", perf)

    # Question 4
    # Question A.
    pca = Pca()
    pca.train(train_data)

    # Question B.
    # plt.plot(pca.eigenval)
    # plt.show()

    # Question C.
    rates = [0.75, 0.90, 0.95, 0.995]
    # nb_eigenval = pca.computecompression(rates)
    # print(nb_eigenval)

    # Question D.
    # for i in range(len(rates)):
        # if nb_eigenval[i] is None :
            # break

        # proj_train_data = pca.project(train_data, nb_eigenval[i])
        # proj_test_data = pca.project(test_data, nb_eigenval[i])
        # Q1.train(proj_train_data, train_labels)

        # out = Q1.process(proj_test_data)
        # perf = (out == test_labels).sum() / test_labels.shape[0]

        # print("Lin1 proj with ", rates[i], " of info, perf: ", perf)

    # Question E.
    # vz.plotGroupImages(pca.eigenvect[:, :10])
    # plt.show()

    # Question F.
    proj_train_data = pca.project(train_data, 2)
    proj_test_data = pca.project(test_data, 2)
    Q1.train(proj_train_data, train_labels)

    colors = ['gold', 'green', 'black', 'magenta', 'teal', 'olivedrab', 'forestgreen', 'darkmagenta', 'khaki', 'darkgray']
    # for i in range(Q1.nbclasses):
        # d = proj_train_data[:, np.where(train_labels == i)[0]]
        # plt.scatter(d[0,:], d[1,:], c=colors[i], s=0.6, marker='.', label=i)

    # plt.legend()
    # plt.show()

    # Question G.
    out = Q1.process(proj_test_data)
    for i in range(Q1.nbclasses):
        d = proj_test_data[:, np.where(out == i)[0]]
        plt.scatter(d[0,:], d[1,:], c=colors[i], s=0.6, marker='.', label=i)

    plt.legend()
    plt.show()
