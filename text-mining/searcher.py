class Searcher:
    def search(self, str, index):

        if not(str in index.wordToDids):
            return None

        dIds = index.wordToDids[str]
        urls = []
        for d in dIds:
            urls.append(index.didToUrl[d])

        return urls
