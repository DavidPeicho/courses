# Very Large Graph : Communities

## Dependencies

You should have the following dependencies:
* g++ >= 5.3.0
* python3

You should also have the following python3 modules installed:
* matplotlib
* networkx

## Installation

### Compiling the Louvain algorithm

First of all, you should compile the Louvain code, which is written in C++.
You just have to run:
```sh
$ cd src/findcommunities
$ make all
```
This will compile the C++ code and copies an extra dll into the build folder
for Windoww users. 

Alternatively, you can use the **run.sh** script compiling the C++ code and
running the benchmark (you can have more information on this in the next section).

## Running

### By hand

You can run the benchmark by doing:
```sh
$ python3 main.py
```

This will launch the script with basic parameters. You can provided one of the
following presets: **vfast** - **fast** - **medium** - **slow** - **vslow**.

Example:
```sh
$ python3 main.py --preset medium
```

You can also specify a lot of parameters by hand:

Example:
```sh
$ python3 main.py -nmax 100 -kmax 100 -nstep 10 -kstep 10 -nstart 10 -kstart 10
```

In order to see what every option does, you can run the script like this:
Example:
```sh
$ python3 main.py -h
```

### With the script
If you only want to run the benchmark with a preset or with the default parameters,
you can run the simple bash script provided:

```sh
$ ./run.sh
```

This will compile the findcommunities code and run the benchmark with default
data.

You can also provide the preset to the script:
```sh
$ ./run.sh -p vfast
```

It is convenient but you canno't provide more parameter to the **run.sh** script.
If you want more possibilities, you will have to run the main.py python script
by hand.