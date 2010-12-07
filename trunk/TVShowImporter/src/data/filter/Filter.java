package data.filter;

public interface Filter<T> {
	public boolean test(T item);
}
