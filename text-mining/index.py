import pickle

class Index:
    """
    Index built from a list of documents.

    Save mapping from url to document ids, from documents ids to url,
    and from words to documents ids.
    """

    def __init__(self, urlToDid, didToUrl, wordToDids):
        self.urlToDid = urlToDid
        self.didToUrl = didToUrl
        self.wordToDids = wordToDids

    def save(self, path):
        """
        Saves the index at the given path.

        @params
        path: the location to save the index
        """
        pickle.dump(self, open(path, 'wb'))

    @staticmethod
    def load(path):
        return pickle.load(open(path, 'rb'))

    @staticmethod
    def build(postings):
        """
        Builds an index from postings and return it.
        """
        # Maps url to a single Did
        urlToDid = {}
        # Maps did to a single url
        didToUrl = {}

        # Creates the mapping from url to a single Did
        for key in postings:
            value = postings[key]
            for url in value.urls:
                split_on_slash = url.rsplit('/', 1)
                dId = split_on_slash[len(split_on_slash) - 1]
                urlToDid[url] = dId
                didToUrl[dId] = url

        # Creates the mapping from word to a list of Dids
        wordToDids = {w : set(map(lambda u: urlToDid[u], p.urls))
                      for w, p in postings.items()}

        return Index(urlToDid, didToUrl, wordToDids)
