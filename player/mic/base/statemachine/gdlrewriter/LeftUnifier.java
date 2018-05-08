package mic.base.statemachine.gdlrewriter;

import org.ggp.base.util.gdl.grammar.GdlConstant;
import org.ggp.base.util.gdl.grammar.GdlDistinct;
import org.ggp.base.util.gdl.grammar.GdlFunction;
import org.ggp.base.util.gdl.grammar.GdlLiteral;
import org.ggp.base.util.gdl.grammar.GdlNot;
import org.ggp.base.util.gdl.grammar.GdlOr;
import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.gdl.grammar.GdlTerm;
import org.ggp.base.util.gdl.grammar.GdlVariable;
import org.ggp.base.util.prover.aima.substitution.Substitution;

import mic.util.Itertools;
import mic.util.Pair;

public final class LeftUnifier {

	public static Substitution canUnify(GdlLiteral x, GdlLiteral y) {
		Substitution theta = new Substitution();
		return canUnify(x, y, theta);
	}

	public static Substitution canUnify(GdlLiteral x, GdlLiteral y, Substitution theta) {
		if (unifyLiteral(x, y, theta)) {
			return theta;
		} else {
			return null;
		}
	}

	public static Substitution canUnify(GdlSentence x, GdlSentence y) {
		Substitution theta = new Substitution();
		return canUnify(x, y, theta);
	}

	public static Substitution canUnify(GdlSentence x, GdlSentence y, Substitution theta) {
		if (unifySentence(x, y, theta)) {
			return theta;
		} else {
			return null;
		}
	}

	public static Substitution canUnify(GdlTerm x, GdlTerm y) {
		Substitution theta = new Substitution();
		return canUnify(x, y, theta);
	}

	public static Substitution canUnify(GdlTerm x, GdlTerm y, Substitution theta) {
		if (unifyTerm(x, y, theta)) {
			return theta;
		} else {
			return null;
		}

	}

	private static boolean unifyLiteral(GdlLiteral x, GdlLiteral y, Substitution theta) {
		if (!x.getClass().equals(y.getClass())) {
			return false;
		}

		if (x instanceof GdlDistinct) {
			GdlDistinct xd = (GdlDistinct)x;
			GdlDistinct yd = (GdlDistinct)y;
			return unifyTerm(xd.getArg1(), yd.getArg1(), theta) && unifyTerm(xd.getArg2(), yd.getArg2(), theta);

		} else if (x instanceof GdlNot) {
			GdlNot xd = (GdlNot)x;
			GdlNot yd = (GdlNot)y;
			return unifyLiteral(xd.getBody(), yd.getBody(), theta);
		} else if (x instanceof GdlOr) {
			GdlOr xd = (GdlOr)x;
			GdlOr yd = (GdlOr)y;
			if (xd.arity() != yd.arity()) {
				return false;
			} else {
				for (Pair<GdlLiteral, GdlLiteral> xy : Itertools.zip(xd.getDisjuncts(), yd.getDisjuncts())) {
					if (!unifyLiteral(xy.first, xy.second, theta)) {
						return false;
					}
				}
				return true;
			}
		} else {
			return unifySentence((GdlSentence)x, (GdlSentence)y, theta);
		}
	}

	private static boolean unifySentence(GdlSentence x, GdlSentence y, Substitution theta) {
		return unifyTerm(x.toTerm(), y.toTerm(), theta);
	}

	private static boolean unifyTerm(GdlTerm x, GdlTerm y, Substitution theta) {
		if (x.equals(y)) {
			return true;
		} else if ((x instanceof GdlConstant)) {
			return false;
		} else if (x instanceof GdlVariable) {
			return unifyVariable((GdlVariable) x, y, theta);
		} else if ((x instanceof GdlFunction) && (y instanceof GdlFunction)) {
			GdlFunction xFunction = (GdlFunction) x;
			GdlFunction yFunction = (GdlFunction) y;

			if (xFunction.arity() != yFunction.arity()) {
				return false;
			} else if (!unifyTerm(xFunction.getName(), yFunction.getName(), theta)) {
				return false;
			}


			for (int i = 0; i < xFunction.arity(); i++) {
				if (!unifyTerm(xFunction.get(i), yFunction.get(i), theta))
					return false;
			}

			return true;
		} else {
			return false;
		}
	}

	private static boolean unifyVariable(GdlVariable var, GdlTerm x, Substitution theta) {
		if (theta.contains(var)) {
			return unifyTerm(theta.get(var), x, theta);
		} else if (x instanceof GdlVariable) {
			return false;
		} else {
			theta.put(var, x);
			return true;
		}
	}



}
