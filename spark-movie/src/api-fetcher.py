import argparse
import json
import requests
import time

class Crawler:
    
    def __init__(self, routes, token, output_file_path, min_year, max_year):
        self._token = token
        self._routes = routes
        self._file_path = output_file_path
        
        self._min_year = min_year
        self._max_year = max_year

        self.nb_queries = 0

        # Opens the output file directly and keep it open
        # to avoid another process to use it and to reduce
        # I/O.
        self._file = open(self._file_path, 'a')

    def crawl_all(self):
        for y in range(self._min_year, self._max_year + 1):
            print('Starting to crawl year ' + str(y))
            self._crawl_year(y)
        
        self._file.close()

    def _crawl_year(self, year):
        page_data = self._request_page(year, 1)
        if (not("total_pages" in page_data)
            or page_data["total_pages"] is None):
            return

        # Processes the first page
        if not("results" in page_data) or (page_data["results"] is None)\
            or len(page_data["results"]) == 0:
            return

        movies = self._filter_movies(page_data)
        self._write_page(movies)

        nb_pages = page_data["total_pages"] + 1
        for p in range(2, nb_pages):
            print(p)
            page_data = self._request_page(year, p)
            if not("results" in page_data) or page_data["results"] is None:
                continue
            
            movies = self._filter_movies(page_data)
            self._write_page(movies)

    def _filter_movies(self, page_data):
        data_movies = page_data["results"]
        
        movies = []

        nb_movies = 0
        for movie in data_movies:
            movie_id = movie["id"]
            data_reviews = self._request_reviews(movie_id)
            if not("results" in data_reviews)\
                or data_reviews["results"] is None or len(data_reviews["results"]) == 0:
                continue

            movie["reviews"] = []
            
            # Removes unecessary fields from the object
            del movie["poster_path"]
            del movie["backdrop_path"]
            del movie["adult"]
            # Adds reviews to movie
            movie["reviews"] = movie["reviews"] + data_reviews["results"]

            if not("total_pages" in data_reviews) or data_reviews["total_pages"] is None:
                continue

            nb_page_reviews = data_reviews["total_pages"] + 1
            for r in range(2, nb_page_reviews):
                data_reviews = self._request_reviews(movie_id)
                movie["reviews"] = movie["reviews"] + data_reviews["results"]
            
            # Add movies to the resulting computed page
            movies.append(movie)

        return movies

    def _write_page(self, movies):
        if (len(movies) == 0):
            return

        for m in movies:
            self._file.write("{}\n".format(json.dumps(m)))

    def _request_page(self, year, page_nb):
        print('Requesting page {} for year {}...'.format(page_nb, year))
        self._wait_and_reset_queries()

        params = dict(
            api_key=self._token,
            year=year,
            page=page_nb
        )
        resp = requests.get(url=self._routes["movies_list"], params=params)
        data = json.loads(resp.text)

        self.nb_queries = self.nb_queries + 1
        return data

    def _request_reviews(self, id):
        print('Requesting reviews for movie {}...'.format(id))
        self._wait_and_reset_queries()

        params = dict(
            api_key=self._token
        )
        url = self._routes["review"] + str(id) + '/reviews'
        resp = requests.get(url=url, params=params)
        data = json.loads(resp.text)

        self.nb_queries = self.nb_queries + 1
        return data

    def _wait_and_reset_queries(self):
        if self.nb_queries >= 4:
            time.sleep(1)
            self.nb_queries = 0


def parse_args():
    description = "Simple crawler for movies."
    arg_parser = argparse.ArgumentParser(description=description)
    
    # Register arguments
    arg_parser.add_argument('-o', '--output',
                            help='Output file in which data are written.',
                            required=True)

    return arg_parser.parse_args()

if __name__ == "__main__":
    THEMOVIEDB_API_TOKEN = "2cde1ceaa291c9271e32272dc26200fe"
    
    MOVIES_ROUTE_API = "https://api.themoviedb.org/3/discover/movie"
    REVIEW_ROUTE_API = "https://api.themoviedb.org/3/movie/"
    routes = {
        "movies_list": MOVIES_ROUTE_API,
        "review": REVIEW_ROUTE_API
    }

    MIN_YEAR = 1950
    MAX_YEAR = 2017

    args = parse_args()
    if (args.output is None):
        print('Error: you have to provide an output file using --output/-o FILE')
        exit(1)

    crawler = Crawler(routes, THEMOVIEDB_API_TOKEN, args.output, MIN_YEAR, MAX_YEAR)
    crawler.crawl_all()
