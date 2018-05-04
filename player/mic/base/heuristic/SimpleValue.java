package mic.base.heuristic;

public class SimpleValue implements Value {
	private final int val;
	public SimpleValue(int v) {
		val = v;
	}

	@Override
	public int compareTo(Value o) {
		return val - o.toInt();
	}

	@Override
	public int toInt() {
		return val;
	}
}
