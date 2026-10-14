package be.fror.projecteuler.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reads the data files that some problems ship with, from the classpath.
 *
 * @author Olivier Grégoire
 */
public final class Resources {

  private Resources() {
  }

  /**
   * Reads a classpath resource as UTF-8 text.
   *
   * @param name the resource name, relative to the classpath root.
   * @return the content of the resource.
   * @throws IllegalArgumentException if the resource doesn't exist.
   * @throws UncheckedIOException if the resource can't be read.
   */
  public static String read(String name) {
    try (var in = Resources.class.getClassLoader().getResourceAsStream(name)) {
      if (in == null) {
        throw new IllegalArgumentException("Resource not found: " + name);
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Reads a classpath resource made of comma-separated, double-quoted words, such as
   * {@code "MARY","PATRICIA"}.
   *
   * @param name the resource name, relative to the classpath root.
   * @return the words without their quotes, in file order, as a mutable list.
   * @throws IllegalArgumentException if the resource doesn't exist.
   * @throws UncheckedIOException if the resource can't be read.
   */
  public static List<String> readWords(String name) {
    var words = new ArrayList<>(Arrays.asList(read(name).split(",", -1)));
    words.replaceAll(word -> word.replaceAll("^\"+|\"+$", ""));
    return words;
  }
}
