/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler;

import java.util.Scanner;
import java.util.concurrent.Executors;

/**
 *
 * @author Olivier Grégoire
 */
public class ProjectEuler {

  public static void main(String[] args) {
    // Daemon threads, so that an abandoned solution doesn't keep the JVM alive.
    var executor = Executors.newCachedThreadPool(
        Thread.ofPlatform().daemon().name("problem-", 0).factory());
    var problemRunner = new ProblemRunner(executor, System::nanoTime, System.out);
    var application = new Application(
        System.out, new Scanner(System.in), ProblemFactory.load(), problemRunner);
    System.exit(application.start(args));
  }

}
