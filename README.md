# project-euler
A set of implementations of problems from
[Project Euler][1] where algorithm speed matters.

## Important note

If you haven't solved a specific problem, you are entirely discouraged to use
this repository to solve that problem.

This repository intentionally violates the "don't publish solutions" *rule* of
Project Euler and is meant for people who already solved the problems. The
solutions to the problems are not written here, but if you run the problems
themselves, you *will* get the solutions.

Just play fair, there is nothing to win with the Project Euler except for
self-esteem and bragging about having solved so many problems won't get you far
at all. Also, it's sad if you actually use this for two reasons. The first is
that you're bragging for nothing and the second you're bragging using the
solution of another person.

So play fair, go solve the problem then come back here and help us find a
lightning fast solution.

## Running the application

So far, run it from an IDE, I haven't taken the time to make a proper runnable
application yet. Anyways, you can get it compiled like this:

    $ git clone https://github.com/fror/project-euler.git
    $ cd project-euler
    $ mvn clean package

When running the application, you'll be asked which problem you want to solve.
Just enter the number as is and press `Enter`. The problem will be loaded and
run.

## The framework

Currently, the framework is taken quite directly from
[Colin Decker's framework][2].

Each solution first runs once, "cold", and must finish within a minute. It is
then warmed up for about a second so the JIT compiles it, and timed for about
another second. The median and minimum of the timed runs are reported along
with the cold time.

## Checking answers

`src/test/resources/answers.sha256` holds the SHA-256 of each known answer, so
the tests can check every solution without publishing the answers. To add one:

    $ printf %s <answer> | shasum -a 256

  [1]: https://projecteuler.net/
  [2]: https://github.com/cgdecker/project-euler

