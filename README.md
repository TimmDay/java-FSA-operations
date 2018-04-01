# FSA Streamliner for Regular Expressions

This program provides tools for streamlining finite state automata. They include :
- Recognizer: will determine if a given string is valid according to a given FSA
- determinizer: will determinize a non-deterministic FSA.
- reverser: will reverse any FSA
- a minimiser: will minimise any FSA, using the Brzozowski algorithm

This is a maven project written in java. You can use the command line to run the maven tests, but there is no kind of user interface for using the program from the command line.

To use the project:
- Open the project in a web editor such as IntelliJ 
- Go to the tests directory
- Observe how to construct a regular expression in the directory ExpressionTest
- Go to the test directory of any of the other operations to try them out
- add your code to the bottom of the list of examples
- run the code, copy/paste the console output to a [visualisation](http://www.webgraphviz.com/) tool

Example:
My favourite example is at the end of 






You can construct your clunky FSA (



The program will give you an output like this
```
digraph G {
rankdir=LR;
0 -> 1 [label="a"];
0 -> 2 [label="c"];
1 -> 3 [label="b"];
2 -> 4 [label="d"];
3 -> 1 [label="a"];
3 -> 2 [label="c"];
4 [peripheries=2];
}
```
This describes the modified finite state automaton.
To see what it looks like, use an online visulaisation tool such as [webgraphviz](http://www.webgraphviz.com/), and copy/paste the digraph description.
