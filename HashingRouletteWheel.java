import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("WeakerAccess")
public final class HashingRouletteWheel<T, R> implements Function<T, R> {

  private final NumberItemMapper<? extends R> mapper;
  private final ToIntFunction<? super T> hashFunction;

  public HashingRouletteWheel(
      Iterable<? extends RouletteOption<? extends R>> iterableSchema,
      ToIntFunction<? super T> hashFunction) {

    Objects.requireNonNull(iterableSchema, "null schema!");
    Objects.requireNonNull(hashFunction, "null hash function!");

    Map<? extends R, Integer> schema =
        StreamSupport.stream(iterableSchema.spliterator(), false)
            .collect(
                Collectors.groupingBy(
                    RouletteOption::getObj, Collectors.summingInt(RouletteOption::getWeight)));
    if (schema.isEmpty()) throw new IllegalArgumentException("empty schema!");
    this.hashFunction = hashFunction;
    int weightSum = schema.values().stream().mapToInt(Integer::intValue).sum();
    if (weightSum < 1000 || (schema.size() > 10 && weightSum < 10000)) {
      this.mapper = new HashingNumberItemMapper<>(weightSum, schema);
    } else {
      this.mapper = new RangeNumberItemMapper<>(weightSum, schema);
    }
  }

  public HashingRouletteWheel(Iterable<? extends RouletteOption<? extends R>> iterableSchema) {
    this(iterableSchema, Objects::hashCode);
  }

  private static int intAbs(int hash) {
    int i = hash >> 31;
    return ((hash ^ i) - i);
  }

  public R select(T t) {
    Objects.requireNonNull(t);
    int hashCode = hashFunction.applyAsInt(t);
    return mapper.getItem(intAbs(hashCode) % mapper.weightSum());
  }

  @Override
  public R apply(T t) {
    return select(t);
  }

  private interface NumberItemMapper<T> {

    int weightSum();

    T getItem(int n);
  }

  private static final class HashingNumberItemMapper<T> implements NumberItemMapper<T> {

    private final int weightSum;
    private final Map<Integer, T> numObjMap;

    private HashingNumberItemMapper(int weightSum, Map<? extends T, Integer> schema) {
      Map<Integer, T> numObjMap = new HashMap<>(weightSum);
      int last = 0, current = 0;
      for (Entry<? extends T, Integer> entry : schema.entrySet()) {
        T item = entry.getKey();
        current += entry.getValue();
        for (int i = last; i < current; i++) numObjMap.put(i, item);
        last += entry.getValue();
      }
      this.weightSum = weightSum;
      this.numObjMap = numObjMap;
    }

    @Override
    public int weightSum() {
      return weightSum;
    }

    @Override
    public T getItem(int n) {
      assert n < weightSum;
      return numObjMap.get(n);
    }
  }

  private static final class RangeNumberItemMapper<T> implements NumberItemMapper<T> {

    private final int weightSum;
    private final ItemRange[] ranges;

    private RangeNumberItemMapper(int weightSum, Map<? extends T, Integer> schema) {

      this.weightSum = weightSum;
      ItemRange[] ranges = new ItemRange[schema.size()];
      int current = 0, index = 0;
      // 将权重高的放前面，减少查找时的循环次数；
      List<Entry<? extends T, Integer>> entryList =
          schema
              .entrySet()
              .stream()
              .sorted(Comparator.comparing(Entry::getValue, Comparator.reverseOrder()))
              .collect(Collectors.toList());
      for (Entry<? extends T, Integer> entry : entryList) {
        int len = entry.getValue();
        ItemRange<T> range = new ItemRange<>(entry.getKey(), current, current + len);
        ranges[index++] = range;
        current += len;
      }
      this.ranges = ranges;
    }

    @Override
    public int weightSum() {
      return weightSum;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getItem(int n) {
      assert n < weightSum;
      for (ItemRange<T> range : ranges) {
        if (range.accept(n)) return range.obj;
      }
      throw new IllegalStateException("unreachable code!");
    }
  }

  private static final class ItemRange<T> {
    private final T obj;
    private final int from;
    private final int to;

    ItemRange(T obj, int from, int to) {
      this.obj = obj;
      if (from < 0 || to <= from)
        throw new IllegalArgumentException("illegal range:" + from + "," + to);
      this.from = from;
      this.to = to;
    }

    boolean accept(int i) {
      return i >= from && i < to;
    }
  }
}
