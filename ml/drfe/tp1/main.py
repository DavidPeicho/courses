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

colors = ['gold', 'green', 'black', 'magenta', 'teal', 'olivedrab',
          'forestgreen', 'darkmagenta', 'khaki', 'darkgray']

def scatter_data(nbclasses, data, labels):
   for i in range(nbclasses):
        d = data[:, np.where(labels == i)[0]]
        plt.scatter(d[0,:], d[1,:], c=colors[i], s=0.6, marker='.', label=i)
   plt.legend()
   plt.show()

def perf(classifier, train, test):
    classifier.train(train, train_labels)
    out = classifier.process(test)
    print("Rate:",
          ((out == test_labels).sum() / test_labels.shape[0]))

def question1(classifier):
    # Display mean images.
    vz.plotGroupImages(classifier.mean[:,:])
    # Show images.
    plt.show()

def question3(classifier):
    print("* Lin1 performance")
    perf(classifier, train_data, test_data)

def question4_1():
    pca = Pca()
    pca.train(train_data)
    return pca

def question4_2(pca):
    plt.plot(pca.eigenval)
    plt.show()

def question4_3(pca, rates):
    nb_eigenval = pca.computecompression(rates)
    print(nb_eigenval)
    return nb_eigenval

def question4_4(classifier, pca, nb_eigenval, rates):
    for i in range(len(rates)):
        if nb_eigenval[i] is None :
            break

        pca_proj_train = pca.project(train_data, nb_eigenval[i])
        pca_proj_test = pca.project(test_data, nb_eigenval[i])

        print("* Lin1, projection with", rates[i], "of info, PCA performance")
        perf(classifier, pca_proj_train, pca_proj_test)

def question4_5(pca):
    vz.plotGroupImages(pca.eigenvect[:, :10])
    plt.show()

def question4_6(classifier, pca):
    proj_train_data = pca.project(train_data, 2)
    scatter_data(classifier.nbclasses, proj_train_data, train_labels)

def question4_7(classifier, pca):
    proj_test_data = pca.project(test_data, 2)
    scatter_data(classifier.nbclasses, proj_test_data, test_labels)

def part2_question1():
    lda = Lda()
    lda.train(train_data, train_labels)
    return lda

def part2_question2(classifier, lda):
    # Computes 1...9 dimensions
    for i in range(1, 10):
        lda_proj_train = lda.project(train_data, i)
        lda_proj_test = lda.project(test_data, i)

        print("* Lin1, projection with", i, "dimensions, LDA performance")
        perf(classifier, lda_proj_train, lda_proj_test)

def part2_question3(lda):
    vz.plotGroupImages(lda.eigenvect[:, :10].real)
    plt.show()

def part2_question4(classifier, lda):
    lda_proj_train = lda.project(train_data, 2)
    scatter_data(classifier.nbclasses, lda_proj_train, train_labels)

def part2_question5(classifier, lda):
    lda_proj_test = lda.project(test_data, 2)
    scatter_data(classifier.nbclasses, lda_proj_test, test_labels)

def part2_question7(classifier, pca, lda, pca_nb_dim):
    # Project with PCA.
    pca_proj_train = pca.project(train_data, pca_nb_dim)
    pca_proj_test = pca.project(test_data, pca_nb_dim)

    # Train LDA.
    lda.train(pca_proj_train, train_labels)

    # Computes 1...9 dimensions
    for i in range(1, 10):
        lda_proj_train = lda.project(pca_proj_train, i)
        lda_proj_test = lda.project(pca_proj_test, i)

        print("* Lin1, PCA with", pca_nb_dim, "dimensions, LDA with", i,
              "dimensions, performance")
        perf(classifier, lda_proj_train, lda_proj_test)

    scatter_data(classifier.nbclasses, lda_proj_test, test_labels)

if __name__ == '__main__':
    # Gaussian classifier, euclidian distance.
    Q1 = Lin1_Gauss()
    Q1.train(train_data, train_labels)


    ######### PART 1 ###########

    # question1(Q1)

    # question3(Q1)

    pca = question4_1()

    # question4_2(pca)

    rates = [0.75, 0.90, 0.95, 0.995]

    # nb_eigenval = question4_3(pca, rates)

    # question4_4(Q1, pca, nb_eigenval, rates)

    # question4_5(pca)

    # question4_6(Q1, pca)

    # question4_7(Q1, pca)

    ########### PART 2 ###########

    lda = part2_question1()

    # part2_question2(Q1, lda)

    # part2_question3(lda)

    # part2_question4(Q1, lda)

    # part2_question5(Q1, lda)

    part2_question7(Q1, pca, lda, 87)
