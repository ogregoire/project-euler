/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler;

import java.util.Optional;

/**
 * @author Olivier Grégoire
 */
public class ProblemFactory {

    private static final String PROBLEMS_PACKAGE = "be.fror.projecteuler.problem";
    private static final String CLAUDE_PROBLEMS_PACKAGE = "be.fror.projecteuler.claude";

    /**
     * Gets the problem with the given ID.
     *
     * @param id the ID (problem number) of the problem.
     * @return a problem.
     * @throws ProblemNotFoundException if there is no problem with the given ID.
     */
    public Problem getProblem(int id) {
        return loadProblem(PROBLEMS_PACKAGE, id)
            .orElseThrow(() -> new ProblemNotFoundException("Problem " + id + " not found."));
    }

    /**
     * Gets Claude's alternative solution to the problem with the given ID, if there is one.
     *
     * @param id the ID (problem number) of the problem.
     * @return Claude's solution, or an empty optional if there is none for this problem.
     */
    public Optional<Problem> getClaudeProblem(int id) {
        return loadProblem(CLAUDE_PROBLEMS_PACKAGE, id);
    }

    private Optional<Problem> loadProblem(String packageName, int id) {
        try {
            Class<?> problemClass = Class.forName(packageName + ".Problem" + id);
            return Optional.of((Problem) problemClass.getDeclaredConstructor().newInstance());
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
