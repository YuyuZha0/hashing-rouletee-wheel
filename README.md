# hashing-rouletee-wheel
a java implemention of roulette wheel,for usage such as weighted grouping

## Example:

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
