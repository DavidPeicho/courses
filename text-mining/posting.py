from tokenized_document import TokenizedDocument

class Posting:
    def __init__(self, word, urls):
        self.word = word
        self.urls = urls

    @staticmethod
    def index(documents):
        posting_map = {}
        for doc in documents:
            
            for word in doc.words:
                if word in posting_map:
                    continue
                urls = []
                urls.append(doc.url)
                
                for next_doc in documents:
                    if next_doc is doc:
                        continue
                    if word in next_doc.words:
                        urls.append(next_doc.url)
                
                posting_map[word] = Posting(word, urls)
        return posting_map
