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

def parse_args():
    description = """
                    A non-optimized (at all) Google.\n Obviously, I used python,
                    so, how could it run fast? ;)
                  """
    arg_parser = argparse.ArgumentParser(description=description)
    
    # Required path to search
    arg_parser.add_argument('-i', '--input',
                            help='Input folder to search in',
                            required=True)
    arg_parser.add_argument('-s', '--search',
                            help='Use the search to give the word to look for',
                            required=True)

    return arg_parser.parse_args()

def print_result(args, urls_found):
    
    str = '---------------------------------------\n'
    str += "Search Input Folder: {0}\n".format(args.input)
    str += "Search Input Word: {0}\n".format(args.search)
    print (str)

if __name__ == "__main__":

    args = parse_args()

    folder = args.input
    wordToSearch = args.search

    processors.append(NormalizerProcessor())

    # Fetches every documents from the input folder
    print('[FETCHING] Reading text files from \'{0}\'...'.format(folder))
    documents = Document.fetch(folder, True)

    # Normalizes every loaded documents
    print('[PROCESSING] Normalizing words from every documents...')
    tokenize_all()

    # Creates the index by mapping every word
    # to all the documents that reference it
    print('[INDEXING] Building index from words...')
    posting_list = Posting.index(tokenized_documents)
    index = Index.build(posting_list)

    print('[SEARCHING] Searching for the word \'{0}\'...', wordToSearch)
    searcher = Searcher()
    print_result(args, searcher.search(wordToSearch, index))
