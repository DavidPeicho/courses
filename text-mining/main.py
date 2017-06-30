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

def parse_args():
    description = """
                    A non-optimized (at all) Google.\n Obviously, I used python,
                    so, how could it run fast? ;)
                  """
    arg_parser = argparse.ArgumentParser(description=description)
    
    # Optional arguments
    arg_parser.add_argument('-i', '--input',
                            help='Input folder to search in',
                            required=False)
    arg_parser.add_argument('-idx', '--index',
                            help='Specifies the index file to use for searching.',
                            required=False)
    arg_parser.add_argument('-si', '--save-index',
                            help='Saves the computed index at the given path',
                            required=False)
    arg_parser.add_argument('-s', '--search',
                            help='Use the search to give the word to look for',
                            required=False)

    return arg_parser.parse_args()

def tokenize_all(documents):
    for d in documents:
        tokenized_doc = TokenizedDocument.analyze(d, processors)
        tokenized_documents.append(tokenized_doc)

def build_index():
    """
    Builds and index from a given folder.

    Normalizes the documents, tokezine them, and create the index.

    This function is called only when the user has provided a wrong
    index file, or even when it did not provide anything at all.
    """

    processors.append(NormalizerProcessor())

    # Fetches every documents from the input folder
    print('[FETCHING]\tReading text files from \'{0}\'...'.format(folder))
    documents = Document.fetch(folder, True)

    # Normalizes every loaded documents
    print('[PROCESSING]\tNormalizing words from every documents...')
    tokenize_all(documents)

    # Creates the index by mapping every word
    # to all the documents that reference it
    print('[INDEXING]\tBuilding index from words...\n')
    posting_list = Posting.index(tokenized_documents)
    index = Index.build(posting_list)

    return index


def print_result(args, urls_found):
    """
    This function prints a summary to the user about its request

    It prints informations about the initial query, the index used,
    and the list of the results, if any have been found.
    """

    str = '---------------------------------------\n'
    str += "Input Folder:\t\t{0}\n".format(args.input)
    if not(urls_found is None):
        str += "Search Input Word:\t\t{0}\n".format(args.search) + "\n"
    str += "Results : "

    print(str)
    if not(urls_found is None):
        if len(urls_found):
            print("\n".join(urls_found))
        else:
            print('There are no results matching your query.')
    else:
        print('You did not provide any search. Use -s QUERY to search in the index.')

if __name__ == "__main__":

    args = parse_args()

    folder = args.input
    wordToSearch = args.search

    recomputed_index = False
    index = None
    # Builds the index only if
    # the user did not provide any
    if args.index is None:
        if args.input is None:
            print('[ERROR!]\tImpossible to process. You did not link any index with --index,and you did not provide an input folder with -i\n')
            exit(1)
        index = build_index()
    else:
        try:
            index = Index.load(args.index)
            print('[INDEX]\t\tLoading index...')
        except:
            print('[ERROR!]\tImpossible to load the index.\n')
            recomputed_index = True
    
    if recomputed_index:
        if args.input is None:
            print('[INDEX]\t\tImpossible to recompute, you did not provide an input folder with -i path.')
            exit(1)
        print('[INDEX]\t\tRecomputing the index on error...')
        index = build_index()

    # Saves the index if asked by the user
    if not(args.save_index is None):
        print('[SAVING]\tSaving created index...\n')
        index.save(args.save_index)

    searcher = None
    if not(args.search is None) and len(args.search):
        print('[SEARCHING]\tSearching for the word \'{0}\'...'.format(wordToSearch))
        searcher = Searcher()
    
    if searcher is None:
        print_result(args, None)
    else:
        print_result(args, searcher.search_ast(wordToSearch, index))
