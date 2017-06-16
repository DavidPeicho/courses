from document import Document
from normalizer_processor import NormalizerProcessor
from tokenized_document import TokenizedDocument
from posting import Posting

if __name__ == "__main__":
    documents = Document.fetch("./assets/emails/alt.atheism", True)

    processors = []
    processors.append(NormalizerProcessor())
    
    # Normalizes every loaded documents
    tokenized_documents = []
    for d in documents:
        tokenized_doc = TokenizedDocument.analyze(d, processors)
        tokenized_documents.append(tokenized_doc)

    # Creates the index by mapping every word
    # to all the documents that reference it
    posting_list = Posting.index(tokenized_documents)
    print(posting_list["anything"].urls)
