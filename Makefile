# #########################################################
# FILENAME: Makefile
# AUTHORS: Matt Welch
# SCHOOL: Arizona State University
# CLASS: CSE598: Introduction to Artificial Intelligence
# INSTRUCTOR: Dr. Joohyung Lee
# SECTION:
# TERM: Spring 2014
# DESCRIPTION: Makefile for 8-puzzle solver with various search
# methods
# #########################################################

all: 8-puzzle

8-puzzle: Board.java eightPuzzle.java
	javac $(JFLAGS) Board.java eightPuzzle.java

clean:
	rm -f *.class

tidy: clean
	rm -f *.*~ *~ *.tmp

JFLAGS = -g

run: all
	java eightPuzzle | tee output

time: run
	grep -i -e 'heuristic time' -e 'finished' -e '===' -e 'c(p)' -e 'h(n)' -e duration -e nodes output --color=auto
