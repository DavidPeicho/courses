import os
import argparse

from sklearn.pipeline import Pipeline
from sklearn.datasets import load_files
from sklearn.svm import LinearSVC
from sklearn.model_selection import GridSearchCV
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.model_selection import train_test_split
from sklearn import metrics

import numpy as np

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

    return arg_parser.parse_args()

if __name__ == "__main__":

    NB_DATA = 12501
    #MOVIE_REVIEWS_FOLDER = "data/reviews/train/data"
    MOVIE_REVIEWS_FOLDER = "data/txt_sentoken"

    IDX_POS = 0
    IDX_NEG = 1

    topics = {
        'consumme': "movie-topic",
        'produce': "movie-analyzed"
    }

    args = parse_args()
    if not(args.consummer is None):
        topics['consumme'] = args.consummer
    if not(args.producer is None):
        topics['produce'] = args.producer

    dataset = load_files(MOVIE_REVIEWS_FOLDER, shuffle=False)
    print("n_samples: %d" % len(dataset.data))

    docs_train, docs_test, y_train, y_test = train_test_split(
        dataset.data, dataset.target, test_size=0.25, random_state=None)

    #test = "Lucasarts have pulled yet another beauty out of a seemingly bottomless bag of great games. If any further proof was required that they rule this genre of gaming, then this is it. Before actually playing the game, there was a little concern about how the writers were going to keep up the pace of gags after the first two games. Fears were rife that it was going to wear a bit thin.<br /><br />Play the game and see how quickly those fears are allayed. From the introductory video with Guybrush in the dodgem boat (!), to the closing stages in the funfair, the jokes just keep on coming. I was a great fan of the first two games and the other Lucasarts works (Day Of The Tentacle, Sam & Max, etc) and this one does not fail to deliver the quality. You will not be disappointed. (Well, I wasn't.)"
    print(len(y_train))
    #print(docs_test[0].shape)
    #print(a.shape)

    pipeline = Pipeline([
        # Computes the word histogram
        ('vect', TfidfVectorizer(min_df=3, max_df=0.95)),
        # Builds classifier
        ('clf', LinearSVC(C=1000)),
    ])

    parameters = {
        'vect__ngram_range': [(1, 2)],
    }
    grid_search = GridSearchCV(pipeline, parameters, n_jobs=-1)
    grid_search.fit(docs_train, y_train)

    # TASK: print the mean and std for each candidate along with the parameter
    # settings for all the candidates explored by grid search.
    #n_candidates = len(grid_search.cv_results_['params'])
    #for i in range(n_candidates):
        #print(i, 'params - %s; mean - %0.2f; std - %0.2f'
                 #% (grid_search.cv_results_['params'][i],
                    #grid_search.cv_results_['mean_test_score'][i],
                    #grid_search.cv_results_['std_test_score'][i]))

    # TASK: Predict the outcome on the testing set and store it in a variable
    # named y_predicted
    #y_predicted = grid_search.predict(docs_test)

    #print(test)
    # p = grid_search.predict(test)
    #y_predicted = grid_search.predict(docs_test)
    #print([p])

    # Print the classification report
    print(metrics.classification_report(y_test, y_predicted,
                                        target_names=dataset.target_names))
