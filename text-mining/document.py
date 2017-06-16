import os

class Document:
    
    def __init__(self, url, text):
        self.url = url
        self.text = text

    @staticmethod
    def fetch(path, recursive = True):
        documents = []
        for root, sub, files in os.walk(path):
            for email_file in files:
                url = os.path.join(root, email_file)
                content = open(url, 'r', encoding='utf-8', errors='ignore').read()
                documents.append(Document(url, content))
        return documents
