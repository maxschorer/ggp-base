package mic.base.heuristic;

public class TwoStateValue implements Value {
	private final int R;
	private final boolean T;

	public TwoStateValue(int R, boolean T) {
		this.R = R;
		this.T = T;
	}

	@Override
	public int compareTo(Value other) {
		int score = toInt();
		int otherScore = other.toInt();

		if (other instanceof TwoStateValue) {
			boolean otherT = ((TwoStateValue) other).T;
			if (T && !otherT) {
				if (R == 100) {
					return 1;
				} else {
					return -1;
				}
			} else if (!T && otherT) {
				if (otherScore == 100) {
					return -1;
				} else {
					return 1;
				}
			}
		}

		return score - otherScore;
	}

	@Override
	public int toInt() {
		if (T) {
			return R;
		} else {
			return -1;
		}
	}
}
