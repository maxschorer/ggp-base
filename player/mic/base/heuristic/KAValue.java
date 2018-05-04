package mic.base.heuristic;

public class KAValue implements Value {
	private final int R;
	private final boolean T;

	public KAValue(int R, boolean T) {
		this.R = R;
		this.T = T;
	}

	@Override
	public int compareTo(Value other) {
		int score = toInt();
		int otherScore = other.toInt();

		if (other instanceof KAValue) {
			boolean otherT = ((KAValue) other).T;
			if (T && !otherT) {
				if (R == 100 || score > otherScore) {
					return 1;
				} else {
					return -1;
				}
			} else if (!T && otherT) {
				if (R != 100 && score >= otherScore) {
					return 1;
				} else {
					return -1;
				}
			}
		}
		return score - otherScore;
	}

	@Override
	public int toInt() {
		return R;
	}
}
