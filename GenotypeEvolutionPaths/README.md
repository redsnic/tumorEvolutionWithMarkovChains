# CIMICE: markov Chain Inference Method to Identify Cancer Evolution

**CIMICE** is a tool to infer Markov Chain based cancer models from mutational matrices.
Mutational matrices are tables in which for each pair (sample, gene) 
it is associated a boolean value stating whether that gene was mutated in that sample.

The ouput of this tool is a clonal DAG and it is in .dot format, please refer to [http://www.graphviz.org/](http://www.graphviz.org/) for more information about the format and the visualizzation options.

## Pre-requirements

In order to compile this software a copy of the [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) must be installed. 

## Compilation and Execution

Instruction for Linux/UNIX users:
Choose a folder and fetch the data from the repository using: 

```
git clone https://github.com/redsnic/tumorEvolutionWithMarkovChains
```

Move into the newly generated folder:

```
cd tumorEvolutionWithMarkovChains
```

Then compile everything by running:

```
mkdir class ; find -name "*.java" | grep -v "Test" > sources.txt && javac @sources.txt -d class; rm sources.txt ; cd class && jar -cf ../CIMICE.jar * && cd ..;
```

now you should find CIMICE.jar in your current directory. Finally, to execute the program:

```
java -cp CIMICE.jar Main.CommandLineInterface -h
```

This should print the help message.

## Input format

There are two input formats for the input dataset:

#### Default:

```
m
n
gene_1 gene_2 ... gene_n
sample_1 1 0 ... 0
...
sample_m 1 1 ... 1
```

#### CAPRI-like format:

```
s/g    gene_1 gene_2 ... gene_n
sample_1 1 0 ... 0
...
sample_m 1 1 ... 1
```
this latter format is recommended and it can be used by setting the -c flag when calling the program.   
