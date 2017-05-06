from tp1.mnist import load_mnist
import numpy as np

from ClassifierTester import ClassifierTester

# Classifiers
from tp1.RandomClassifier import RandomClassifier
from tp1.MajorityClassifier import MajorityClassifier
from tp1.Lin1_GaussClassifier import Lin1_GaussClassifier

if __name__ == "__main__":

    # Loads train and test data that will later be
    # given to the classifiers.
    train_data, train_labels = load_mnist(dataset='training', path='./tp1/')
    test_data, test_labels = load_mnist(dataset='testing', path='./tp1/')

    train_data = np.reshape(train_data, (60000, 28 * 28)).T
    test_data  = np.reshape(test_data,  (10000, 28 * 28)).T

    classifiers = {
        "Random" : RandomClassifier(),
        "Majority" : MajorityClassifier(),
        "Lin1" : Lin1_GaussClassifier()
    }
    classifier_tester = ClassifierTester()
    classifier_tester.compare(classifiers, train_data, train_labels, test_data, test_labels)
