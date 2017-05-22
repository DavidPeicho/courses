CC = g++
CXX_FLAGS = -Werror -Wall -Wextra -std=c++11 -O3
LDLIBS = -lm

MAIN = main.cpp
SRC = $(wildcard src/*.cpp)
SKLT = $(wildcard sklt/*.cpp)

OUTPUT = bin/main

all: $(MAIN) $(SRC) $(SKLT)
	$(CC) -o $(OUTPUT) $(LIB) $(CXX_FLAGS) $^

.PHONY=all check clean

