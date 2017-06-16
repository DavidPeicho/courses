import unicodedata

from text_processor import TextProcessor

class NormalizerProcessor(TextProcessor):
    def process(self, word):
        unicode_str = unicodedata.normalize("NFKD", word).encode("ascii", "ignore")
        return unicode_str.lower()
