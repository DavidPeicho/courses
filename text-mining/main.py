import argparse

from document import Document
from normalizer_processor import NormalizerProcessor
from tokenized_document import TokenizedDocument
from posting import Posting
from index import Index
from searcher import Searcher

documents = None
processors = []
tokenized_documents = []

def tokenize_all():
    for d in documents:
        tokenized_doc = TokenizedDocument.analyze(d, processors)
        tokenized_documents.append(tokenized_doc)

# def parse_args():

if __name__ == "__main__":
    documents = Document.fetch("./assets/emails/alt.atheism", True)

    processors.append(NormalizerProcessor())

    # Normalizes every loaded documents
    tokenize_all()

    # Creates the index by mapping every word
    # to all the documents that reference it
    posting_list = Posting.index(tokenized_documents)

    index = Index.build(posting_list)

    searcher = Searcher()
    print(searcher.search("anything", index))
