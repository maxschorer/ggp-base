package mic.util;

import java.util.Iterator;

public class Itertools {
	public static<T> Iterable<T> makeIterable(Iterator<T> iter) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return iter;
			}

		};
	}

	public static<T> Iterable<Pair<Integer, T>> enumerate(Iterable<T> i) {
		return new Iterable<Pair<Integer, T>>() {
			@Override
			public Iterator<Pair<Integer, T>> iterator() {
				return new Iterator<Pair<Integer, T>>() {
					private final Iterator<T> iter = i.iterator();
					private Integer idx = 0;

					@Override
					public boolean hasNext() {
						return iter.hasNext();
					}

					@Override
					public Pair<Integer, T> next() {
						return new Pair<Integer, T>(idx++, iter.next());
					}

				};
			}
		};
	}

	public static<T1, T2> Iterable<Pair<T1, T2>> zip(Iterable<T1> i1, Iterable<T2> i2) {
		return new Iterable<Pair<T1, T2>>() {
			@Override
			public Iterator<Pair<T1, T2>> iterator() {
				return new Iterator<Pair<T1, T2>>() {
					private final Iterator<T1> iter1 = i1.iterator();
					private final Iterator<T2> iter2 = i2.iterator();

					@Override
					public boolean hasNext() {
						return (iter1.hasNext() && iter2.hasNext());
					}

					@Override
					public Pair<T1, T2> next() {
						return new Pair<T1, T2>(iter1.next(), iter2.next());
					}

				};
			}
		};
	}
}
