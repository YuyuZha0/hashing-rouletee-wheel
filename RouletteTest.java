package com.ifeng.dmp.ctrp.roulette;

import org.junit.Test;

import java.util.*;

public class RouletteTest {

  @Test
  public void test1() {
    List<RouletteOption<Integer>> schema = new ArrayList<>();
    schema.add(RouletteOption.of(1, 100));
    schema.add(RouletteOption.of(2, 200));
    schema.add(RouletteOption.of(3, 300));
    schema.add(RouletteOption.of(4, 400));
    schema.add(RouletteOption.of(5, 500));

    HashingRouletteWheel<String, Integer> wheel = new HashingRouletteWheel<>(schema);

    String[] ss = new String[1997];
    for (int i = 0; i < ss.length; i++) ss[i] = UUID.randomUUID().toString();

    int[] a = new int[5];
    long t1 = System.nanoTime();
    for (int i = 0; i < 1500000; i++) {
      int n = wheel.select(ss[i % ss.length]) - 1;
      a[n]++;
    }
    long t2 = System.nanoTime();
    System.out.println(Arrays.toString(a));
    System.out.println((t2 - t1) / 1500000);
  }

  @Test
  public void test2() {
    List<RouletteOption<Integer>> schema = new ArrayList<>();
    schema.add(RouletteOption.of(1, 1));
    schema.add(RouletteOption.of(2, 2));
    schema.add(RouletteOption.of(3, 3));
    schema.add(RouletteOption.of(4, 4));
    schema.add(RouletteOption.of(5, 5));

    HashingRouletteWheel<String, Integer> wheel = new HashingRouletteWheel<>(schema);
    String[] ss = new String[1997];
    for (int i = 0; i < ss.length; i++) ss[i] = UUID.randomUUID().toString();

    int[] a = new int[5];
    long t1 = System.nanoTime();
    for (int i = 0; i < 1500000; i++) {
      int n = wheel.select(ss[i % ss.length]) - 1;
      a[n]++;
    }
    long t2 = System.nanoTime();
    System.out.println(Arrays.toString(a));
    System.out.println((t2 - t1) / 1500000);
  }

  @Test
  public void test3() {
    List<RouletteOption<Integer>> schema = new ArrayList<>();
    for (int i = 1; i <= 100; i++) schema.add(RouletteOption.of(i, 1));

    HashingRouletteWheel<String, Integer> wheel = new HashingRouletteWheel<>(schema);
    String[] ss = new String[9997];
    for (int i = 0; i < ss.length; i++) ss[i] = UUID.randomUUID().toString();

    int[] a = new int[100];
    long t1 = System.nanoTime();
    for (int i = 0; i < 1500000; i++) {
      int n = wheel.select(ss[i % ss.length]) - 1;
      a[n]++;
    }
    long t2 = System.nanoTime();
    System.out.println(Arrays.toString(a));
    System.out.println((t2 - t1) / 1500000);
  }

  @Test
  public void test4() {
    List<RouletteOption<String>> scheme = new ArrayList<>();
    scheme.add(RouletteOption.of("Group0", 3));
    scheme.add(RouletteOption.of("Group1", 20));
    scheme.add(RouletteOption.of("Group2", 30));
    scheme.add(RouletteOption.of("Group3", 50));

    scheme.add(RouletteOption.of("Group0", 7));

    HashingRouletteWheel<String, String> wheel = new HashingRouletteWheel<>(scheme);
    Map<String, Integer> map = new HashMap<>();
    for (int i = 0; i < 100000; i++) {
      String userId = UUID.randomUUID().toString();
      String groupName = wheel.select(userId);
      if (map.containsKey(groupName)) {
        map.put(groupName, map.get(groupName) + 1);
      } else {
        map.put(groupName, 1);
      }
    }
    System.out.println(map);
  }
}
