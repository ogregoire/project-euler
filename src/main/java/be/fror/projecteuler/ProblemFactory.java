/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.SortedSet;
import java.util.TreeMap;

/**
 * @author Olivier Grégoire
 */
public class ProblemFactory {

    private static final String CLAUDE_PROBLEMS_PACKAGE = "be.fror.projecteuler.claude";

    private final NavigableMap<Integer, Problem> problems;
    private final NavigableMap<Integer, Problem> claudeProblems;

    /**
     * Creates a factory serving the given solutions.
     *
     * @param problems your solutions.
     * @param claudeProblems Claude's alternative solutions.
     * @throws IllegalArgumentException if a list has two solutions to the same problem.
     */
    public ProblemFactory(List<Problem> problems, List<Problem> claudeProblems) {
        this.problems = byId(problems);
        this.claudeProblems = byId(claudeProblems);
    }

    /**
     * Creates a factory serving every solution registered in
     * {@code META-INF/services/be.fror.projecteuler.Problem}. Claude's solutions are the ones in
     * the {@code be.fror.projecteuler.claude} package.
     *
     * @return a factory serving all registered solutions.
     * @throws IllegalArgumentException if two registered solutions of the same author solve the
     *     same problem.
     */
    public static ProblemFactory load() {
        var solutionsByClaude = ServiceLoader.load(Problem.class).stream()
            .collect(partitioningBy(
                provider -> provider.type().getPackageName().equals(CLAUDE_PROBLEMS_PACKAGE),
                mapping(ServiceLoader.Provider::get, toList())));
        return new ProblemFactory(solutionsByClaude.get(false), solutionsByClaude.get(true));
    }

    /**
     * Gets the problem with the given ID.
     *
     * @param id the ID (problem number) of the problem.
     * @return a problem.
     * @throws ProblemNotFoundException if there is no problem with the given ID.
     */
    public Problem getProblem(int id) {
        var problem = problems.get(id);
        if (problem == null) {
            throw new ProblemNotFoundException("Problem " + id + " not found.");
        }
        return problem;
    }

    /**
     * Gets Claude's alternative solution to the problem with the given ID, if there is one.
     *
     * @param id the ID (problem number) of the problem.
     * @return Claude's solution, or an empty optional if there is none for this problem.
     */
    public Optional<Problem> getClaudeProblem(int id) {
        return Optional.ofNullable(claudeProblems.get(id));
    }

    /**
     * Gets the IDs of all the problems you solved.
     *
     * @return the IDs, in increasing order.
     */
    public SortedSet<Integer> ids() {
        return Collections.unmodifiableSortedSet(problems.navigableKeySet());
    }

    private static NavigableMap<Integer, Problem> byId(List<Problem> problems) {
        return problems.stream().collect(toMap(Problem::getId, identity(), (first, second) -> {
            throw new IllegalArgumentException("Two solutions to problem " + first.getId() + ": "
                + first.getClass().getName() + " and " + second.getClass().getName());
        }, TreeMap::new));
    }

    /**
     * Exception thrown when a problem cannot be found for a given ID.
     */
    public static class ProblemNotFoundException extends RuntimeException {

        public ProblemNotFoundException(String message) {
            super(message);
        }
    }
}
