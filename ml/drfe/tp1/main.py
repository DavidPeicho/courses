#! /usr/bin/env python3

from mnist import load_mnist
import numpy as np
import visualize as vz
import matplotlib.pyplot as plt

from Lin1_Gauss import Lin1_Gauss
from Pca import Pca
from Lda import Lda

train_data, train_labels = load_mnist(dataset='training', path='./')
test_data, test_labels = load_mnist(dataset='testing', path='./')

train_data = np.reshape(train_data, (60000, 28 * 28)).T
test_data  = np.reshape(test_data,  (10000, 28 * 28)).T

def question1(classifier):
    # Display mean images.
    vz.plotGroupImages(classifier.mean[:,:])
    # Show images.
    plt.show()

def qquestion3(classifier):
    out = classifier.process(test_data)
    perf = (out == test_labels).sum() / test_labels.shape[0]
    print("Lin1 perf:", perf)

def question4_1():
    pca = Pca()
    pca.train(train_data)
    return pca

def question4_2(pca):
    plt.plot(pca.eigenval)
    plt.show()

def question4_3(pca):
    rates = [0.75, 0.90, 0.95, 0.995]
    nb_eigenval = pca.computecompression(rates)
    print(nb_eigenval)
    return nb_eigenval

def question4_4(nb_eigenval, rates):
    for i in range(len(rates)):
        if nb_eigenval[i] is None :
            break

        proj_train_data = pca.project(train_data, nb_eigenval[i])
        proj_test_data = pca.project(test_data, nb_eigenval[i])
        Q1.train(proj_train_data, train_labels)

        out = Q1.process(proj_test_data)
        perf = (out == test_labels).sum() / test_labels.shape[0]

        print("Lin1 proj with ", rates[i], " of info, perf: ", perf)

def question4_5(pca):
    vz.plotGroupImages(pca.eigenvect[:, :10])
    plt.show()

def question4_6(classifier, pca):
    proj_train_data = pca.project(train_data, 2)
    proj_test_data = pca.project(test_data, 2)
    classifier.train(proj_train_data, train_labels)

    colors = ['gold', 'green', 'black', 'magenta', 'teal', 'olivedrab', 'forestgreen', 'darkmagenta', 'khaki', 'darkgray']
    for i in range(classifier.nbclasses):
        d = proj_train_data[:, np.where(train_labels == i)[0]]
        plt.scatter(d[0,:], d[1,:], c=colors[i], s=0.6, marker='.', label=i)

    plt.legend()
    plt.show()

    return proj_test_data

def question4_7(classifier, proj_test_data):
    out = classifier.process(proj_test_data)
    for i in range(classifier.nbclasses):
        d = proj_test_data[:, np.where(out == i)[0]]
        plt.scatter(d[0,:], d[1,:], c=colors[i], s=0.6, marker='.', label=i)

    plt.legend()
    plt.show()

def part2_question1():
    lda = Lda()
    lda.train(train_data, train_labels)
    return lda

def part2_question3(lda):
    vz.plotGroupImages(lda.eigenvect[:, :10].real)
    plt.show()

def part2_question4(classifier, lda):
    proj_train_data = lda.project(train_data, 2)
    proj_test_data = lda.project(test_data, 2)
    classifier.train(proj_train_data, train_labels)
    colors = ['gold', 'green', 'black', 'magenta', 'teal', 'olivedrab', 'forestgreen', 'darkmagenta', 'khaki', 'darkgray']
    for i in range(classifier.nbclasses):
        d = proj_train_data[:, np.where(train_labels == i)[0]]
        plt.scatter(d[0,:], d[1,:], c=colors[i], s=0.6, marker='.', label=i)

    plt.legend()
    plt.show()

if __name__ == '__main__':
    # Gaussian classifier, euclidian distance.
    Q1 = Lin1_Gauss()
    Q1.train(train_data, train_labels)

    # Question 1.
    # Display mean images.
    # vz.plotGroupImages(Q1.mean[:,:])
    # Show images.
    # plt.show()

    # question1(Q1)

    # Question 3
    # out = Q1.process(test_data)
    # perf = (out == test_labels).sum() / test_labels.shape[0]
    # print("Lin1 perf:", perf)

    # question3(Q1)

    # Question 4
    # Question A.
    #pca = Pca()
    #pca.train(train_data)
    pca = question4_1()

    # Question B.
    # plt.plot(pca.eigenval)
    # plt.show()
    # question4_2(pca)

    # Question C.
    # rates = [0.75, 0.90, 0.95, 0.995]
    # nb_eigenval = pca.computecompression(rates)
    # print(nb_eigenval)
    # nb_eigenval = question4_3(pca)

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
    # question4_4(nb_eigenval, rates)

    # Question E.
    # vz.plotGroupImages(pca.eigenvect[:, :10])
    # plt.show()
    # question4_5(pca)

    # Question F.
    # proj_train_data = pca.project(train_data, 2)
    # proj_test_data = pca.project(test_data, 2)
    # Q1.train(proj_train_data, train_labels)

    # colors = ['gold', 'green', 'black', 'magenta', 'teal', 'olivedrab', 'forestgreen', 'darkmagenta', 'khaki', 'darkgray']
    # for i in range(Q1.nbclasses):
        # d = proj_train_data[:, np.where(train_labels == i)[0]]
        # plt.scatter(d[0,:], d[1,:], c=colors[i], s=0.6, marker='.', label=i)

    # plt.legend()
    # plt.show()
    # proj_test_data = question4_6(Q1, pca)

    # Question G.
    # out = Q1.process(proj_test_data)
    # for i in range(Q1.nbclasses):
        # d = proj_test_data[:, np.where(out == i)[0]]
        # plt.scatter(d[0,:], d[1,:], c=colors[i], s=0.6, marker='.', label=i)

    # plt.legend()
    # plt.show()
    # question4_7(Q1, proj_test_data)

    ########### PART 2 ###########

    # Question 2.1 et 2.2
    # lda = Lda()
    # lda.train(train_data, train_labels)
    lda = part2_question1()

    # Computes 1...9 dimensions
    # for i in range(1, 10):
    #     lda_projected_train = lda.project(train_data, i)
    #     lda_projected_data = lda.project(test_data, i)
    #     Q1.train(lda_projected_train, train_labels)
    #     out = Q1.process(lda_projected_data)
    #     print(i, " Rate = ", ((out == test_labels).sum() / test_labels.shape[0]))

    # Question 2.3
    # vz.plotGroupImages(lda.eigenvect[:, :10].real)
    # plt.show()
    # part2_question1(lda)

    # Question 2.4
    # proj_train_data = lda.project(train_data, 2)
    # proj_test_data = lda.project(test_data, 2)
    # Q1.train(proj_train_data, train_labels)
    # colors = ['gold', 'green', 'black', 'magenta', 'teal', 'olivedrab', 'forestgreen', 'darkmagenta', 'khaki', 'darkgray']
    # for i in range(Q1.nbclasses):
    #     d = proj_train_data[:, np.where(train_labels == i)[0]]
    #     plt.scatter(d[0,:], d[1,:], c=colors[i], s=0.6, marker='.', label=i)

    # plt.legend()
    # plt.show()
    # part2_question4(classifier, lda)

    # Question 2.5

    # Question 2.7
    pc_nb_dim = 87
    pca_proj_train_data = pca.project(train_data, pc_nb_dim)
    pca_proj_test_data = pca.project(test_data, pc_nb_dim)
    # Processes train data obtained from PCA
    lda.train(pca_proj_train_data, train_labels)
    lda_projected_train = lda.project(pca_proj_train_data, 2)
    lda_projected_data = lda.project(pca_proj_test_data, 2)
    Q1.train(lda_projected_train, train_labels)

    # Computes 1...9 dimensions
    for i in range(1, 10):
        lda_projected_train = lda.project(pca_proj_train_data, i)
        lda_projected_data = lda.project(pca_proj_test_data, i)
        Q1.train(lda_projected_train, train_labels)
        out = Q1.process(lda_projected_data)
        print(i, " Rate = ", ((out == test_labels).sum() / test_labels.shape[0]))

    # colors = ['gold', 'green', 'black', 'magenta', 'teal', 'olivedrab', 'forestgreen', 'darkmagenta', 'khaki', 'darkgray']
    # for i in range(Q1.nbclasses):
    #     d = lda_projected_data[:, np.where(train_labels == i)[0]]
    #     plt.scatter(d[0,:], d[1,:], c=colors[i], s=0.6, marker='.', label=i)

    # plt.legend()
    # plt.show()