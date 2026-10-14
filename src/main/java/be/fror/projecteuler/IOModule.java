/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * @author Olivier Grégoire
 */
public class IOModule extends AbstractModule {

    @Provides
    @Singleton
    PrintStream providePrintStream() {
        return System.out;
    }

    @Provides
    @Singleton
    Scanner provideScanner() {
        return new Scanner(System.in);
    }

}
