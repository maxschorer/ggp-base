package mic.util;

public class Pair<T1, T2> {
	public final T1 first;
	public final T2 second;
	public Pair(T1 firstIn, T2 secondIn) {
		first = firstIn;
		second = secondIn;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Pair) {
			Pair<T1, T2> o = (Pair<T1 , T2>) other;
			return first.equals(o.first) && second.equals(o.second);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return first.hashCode() + 3*second.hashCode();

	}
}
