from collections import OrderedDict

class ClassifierTester(object):

    def compare(self, arr, train_data, train_labels, test_data, test_labels):
        rates = {}
        for key in arr:
            arr[key].train(train_data, train_labels)
            out = arr[key].process(test_data)
            rates[key] = (out == test_labels).sum() / test_labels.shape[0]

        sort_desc = OrderedDict(sorted(rates.items(), key=lambda x: x[1], reverse=True))
        print('Processing classification...')
        for key in sort_desc:
            print('\t' + key + ': ' + str(sort_desc[key] * 100.0) + '%')
        print('Ending classification process...')