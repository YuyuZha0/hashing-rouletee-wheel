import java.util.Map;
import java.util.Objects;

public final class RouletteOption<T> implements Map.Entry<T, Integer> {

  private final T obj;
  private final int weight;

  @SuppressWarnings("WeakerAccess")
  public RouletteOption(T obj, int weight) {
    Objects.requireNonNull(obj, "null roulette obj!");
    if (weight <= 0) throw new IllegalArgumentException("weight must be a positive number!");
    this.obj = obj;
    this.weight = weight;
  }

  public static <T> RouletteOption<T> of(T obj, int weight) {
    return new RouletteOption<>(obj, weight);
  }

  @Override
  public String toString() {
    return obj.toString() + ":" + weight;
  }

  @Override
  public T getKey() {
    return obj;
  }

  @Override
  public Integer getValue() {
    return weight;
  }

  @Override
  public Integer setValue(Integer value) {
    throw new UnsupportedOperationException();
  }

  public T getObj() {
    return obj;
  }

  public int getWeight() {
    return weight;
  }
}
