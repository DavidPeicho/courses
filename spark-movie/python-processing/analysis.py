#!/usr/bin/env python3

import os
import argparse

import numpy as np

from sentiment_analysis.linear_svm import LinearSVM

def parse_args():
    description =   """
                    Consummes data from a given kafka topic and makes
                    sentiment analysis on its reviews.
                    """
    
    arg_parser = argparse.ArgumentParser(description=description)
    
    # Register arguments
    arg_parser.add_argument('-c', '--consummer',
                            help='Id of the Kafka topic to consumme from. \'movie-topic\' by default.',
                            required=False)
    arg_parser.add_argument('-p', '--producer',
                            help='Id of the Kafka topic to produce to. \'movie-analyzed\' by default.',
                            required=False)
    arg_parser.add_argument('-pf', '--positive',
                            help='Path to the training positive folder.',
                            required=False)
    arg_parser.add_argument('-nf', '--negative',
                            help='Path to the training negative folder.',
                            required=False)
    arg_parser.add_argument('-s', '--save_db',
                            help='Saves the learning database after training.',
                            required=False)
    arg_parser.add_argument('-l', '--load_db',
                            help='Load the learning database previously trained.',
                            required=False)

    return arg_parser.parse_args()

if __name__ == "__main__":

    DATA_ROOT_FOLDER = "data/reviews/train/data/"
    #DATA_ROOT_FOLDER = "data/txt_sentoken/"
    POS_FOLDER = DATA_ROOT_FOLDER + "pos"
    NEG_FOLDER = DATA_ROOT_FOLDER + "neg"

    args = parse_args()
    if args.consummer is None:
        args.consummer = "movie-topic"
    if args.producer is None:
        args.producer = "movie-analyzed"
    if args.positive is None:
        args.positive = POS_FOLDER
    if args.negative is None:
        args.negative = NEG_FOLDER

    # The user did not provide a previsouly trained model,
    # the script needs to train from any possible data folder.
    svm = None
    if not(args.load_db):
        svm = LinearSVM()
        svm.train(args.positive, args.negative)

        if args.save_db:
            svm.dump(args.save_db)

    else:
        svm = LinearSVM.load(args.load_db)

    test_data = []
    test_labels = []
    for root, sub, files in os.walk("data/txt_sentoken/neg"):
        for file_name in files:
            url = os.path.join(root, file_name)
            content = open(url, 'r', encoding='utf-8', errors='ignore').read()
            test_data.append(content)
            test_labels.append('neg')

    y_test = svm.project(test_data)
    i = 0
    for a in y_test:
        if a == "neg":
            i = i + 1
    print(i)