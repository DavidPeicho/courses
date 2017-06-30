import pickle

class Index:
    def __init__(self, urlToDid, didToUrl, wordToDids):
        self.urlToDid = urlToDid
        self.didToUrl = didToUrl
        self.wordToDids = wordToDids

    def save(self, path):
        pickle.dump(self, open(path, 'wb'))

    @staticmethod
    def load(path):
        return pickle.load(open(path, 'rb'))

    @staticmethod
    def build(postings):

        # Maps url to a single Did
        urlToDid = {}
        # Maps did to a single url
        didToUrl = {}
        # Maps word to a list of Dids
        wordToDids = {}

        # Creates the mapping from url to a single Did
        for key in postings:
            value = postings[key]
            for url in value.urls:
                split_on_slash = url.rsplit('/', 1)
                dId = split_on_slash[len(split_on_slash) - 1]
                urlToDid[url] = dId
                didToUrl[dId] = url

        # Creates the mapping from word to a list of Dids
        for word in postings:
            if not(word in wordToDids):
                wordToDids[word] = []

            value = postings[word]
            for url in value.urls:
                wordToDids[word].append(urlToDid[url])

        return Index(urlToDid, didToUrl, wordToDids)
        