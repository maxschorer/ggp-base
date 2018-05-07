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

public final class Unifier {

	public static boolean canUnify(GdlLiteral x, GdlLiteral y) {
		Substitution theta = new Substitution();
		return unifyLiteral(x, y, theta);
	}

	public static boolean canUnify(GdlSentence x, GdlSentence y) {
		Substitution theta = new Substitution();
		return unifySentence(x, y, theta);
	}

	public static boolean canUnify(GdlTerm x, GdlTerm y) {
		Substitution theta = new Substitution();
		return unifyTerm(x, y, theta);

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
		if (x.equals(y))
			return true;
		if ((x instanceof GdlConstant) && (y instanceof GdlConstant)) {
			if (!x.equals(y)) {
				return false;
			}
		} else if (x instanceof GdlVariable) {
			if (!unifyVariable((GdlVariable) x, y, theta))
				return false;
		} else if (y instanceof GdlVariable) {
			if (!unifyVariable((GdlVariable) y, x, theta))
				return false;
		} else if ((x instanceof GdlFunction) && (y instanceof GdlFunction)) {
			GdlFunction xFunction = (GdlFunction) x;
			GdlFunction yFunction = (GdlFunction) y;

			if (!unifyTerm(xFunction.getName(), yFunction.getName(), theta))
				return false;

			for (int i = 0; i < xFunction.arity(); i++) {
				if (!unifyTerm(xFunction.get(i), yFunction.get(i), theta))
					return false;
			}
		} else {
			return false;
		}

		return true;
	}

	private static boolean unifyVariable(GdlVariable var, GdlTerm x, Substitution theta) {
		if (theta.contains(var)) {
			return unifyTerm(theta.get(var), x, theta);
		} else if ((x instanceof GdlVariable) && theta.contains((GdlVariable) x)) {
			return unifyTerm(var, theta.get((GdlVariable) x), theta);
		} else {
			theta.put(var, x);
			return true;
		}
	}

}
