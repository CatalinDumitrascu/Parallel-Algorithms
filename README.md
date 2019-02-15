# Parallel-Algorithms

Let there be Miners and Wizards. The goal of the miners is to explore all the tunnels in their cave system.
The cave system has many rooms, each with its puzzle to solve. The miners can solve the puzzles and with
the solutions, the wizards can break the magic barriers protecting the cave and gain access to the adjacent
rooms.
The Wizards are not always available, as sometimes they sleep. When a wizard is awake, he takes puzzle
solutions from the miners, and does wizardly things to unlock the paths to all adjacent rooms. If the puzzles
are solved incorrectly, when the wizard tries to unlock the paths, a great explosion kills everyone, ends the
program, and the student is given 0 points.
The solution should be implemented in Java using threads. You are only responsible for implementing the
miners and the communication channel between them and the wizards.

There are miners, wizards, puzzles, a communication channel amd a cave system. The whole problem is beautifully outlined in the .pdf file in this repository. In summary, i solved it using java's threaded solutions, such as "synchronize" and concurrentHashMap.
