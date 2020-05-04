package de.fs92.defi.gasprovider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ArrayListUtil {
  private ArrayListUtil() {
    throw new IllegalStateException("Utility class");
  }

  @NotNull
  public static <T> List<T> removeDuplicates(@NotNull List<T> list) {
    ArrayList<T> newList = new ArrayList<>();
    for (T element : list) {
      if (!newList.contains(element)) {
        newList.add(element);
      }
    }
    return newList;
  }

  public static <T> String toString(@NotNull List<T> list) {
    StringBuilder sb = new StringBuilder();
    for (T s : list) {
      sb.append(s.toString());
      sb.append("\t");
    }
    return sb.toString();
  }
}
