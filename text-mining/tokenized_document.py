import re

class TokenizedDocument:
    
    def __init__(self, words, url):
        self.words = words
        self.url = url
    
    @staticmethod
    def analyze(document, processors):
        words = []
        url = document.url
        doc_words = re.split("[,; ()\t\n\-!?:]+", document.text)
        for word in doc_words:
            tokenized_word = word
            for p in processors:
                tokenized_word = p.process(tokenized_word)
            tokenized_word = tokenized_word.decode("utf-8")
            words.append(tokenized_word)

        return TokenizedDocument(words, url)