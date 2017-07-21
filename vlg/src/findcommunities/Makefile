#!/bin/bash

CC=g++
CFLAGS= -std=c++11 -static-libstdc++ -U__STRICT_ANSI__ -O5 -Wall
LDFLAGS= -lm -Wall
TO_CP=libstdc++-6.dll
BUILD_FLDER=build
EXEC=community convert hierarchy
OBJ1= graph_binary.o community.o
OBJ2= graph.o

all: $(EXEC)

community : $(OBJ1) main_community.o
	mkdir -p $(BUILD_FLDER)
	$(CC) -o $(BUILD_FLDER)/$@ $^ $(LDFLAGS)
	cp $(TO_CP) $(BUILD_FLDER)

convert : $(OBJ2) main_convert.o
	mkdir -p $(BUILD_FLDER)
	$(CC) -o $(BUILD_FLDER)/$@ $^ $(LDFLAGS)
	cp $(TO_CP) $(BUILD_FLDER)

hierarchy : main_hierarchy.o
	mkdir -p $(BUILD_FLDER)
	$(CC) -o $(BUILD_FLDER)/$@ $^ $(LDFLAGS)
	cp $(TO_CP) $(BUILD_FLDER)

##########################################
# Generic rules
##########################################

%.o: %.cpp %.h
	$(CC) -o $@ -c $< $(CFLAGS)

%.o: %.cpp
	$(CC) -o $@ -c $< $(CFLAGS)

clean:
	rm -f *.o *~ $(EXEC)
