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
}
