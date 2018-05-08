package mic.base.statemachine.gdlrewriter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlLiteral;
import org.ggp.base.util.gdl.grammar.GdlRule;
import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.prover.aima.substitution.Substitution;

import mic.util.Itertools;
import mic.util.Pair;

public class RedundantRulePruner implements GdlRewriter {

	@Override
	public List<Gdl> rewrite(List<Gdl> description) {

		List<GdlRule> rules = new ArrayList<GdlRule>();
		List<Gdl> newDescription = new ArrayList<Gdl>();

		for (Gdl term : description) {
			if (term instanceof GdlRule) {
				rules.add((GdlRule) term);
			} else {
				newDescription.add(term);
			}

		}

		List<GdlRule> newRules = pruneRules(rules);
		newDescription.addAll(newRules);
		return newDescription;
	}

	private List<GdlRule> pruneRules(List<GdlRule> rules) {
		List<GdlRule> newRules = new ArrayList<GdlRule>();

		for (Pair<Integer, GdlRule> e : Itertools.enumerate(rules)) {
			if (!subsumedp(e.second, newRules.listIterator()) && !subsumedp(e.second, rules.listIterator(e.first + 1)) ) { //
				newRules.add(e.second);
			} else {
				System.out.println("Pruning: " + e.second.toString() + "\n");
			}
		}

		return newRules;
	}

	private boolean subsumedp(GdlRule rule, ListIterator<GdlRule> rules) {
		for (GdlRule r : Itertools.makeIterable(rules)) {
			if (subsumesp(r, rule)) {
				System.out.println("s:\t" + r.toString() + "\ns:\t" + rule.toString());
				return true;
			}
		}
		return false;
	}

	private boolean subsumesp(GdlRule p, GdlRule q) {
		if (p.equals(q)) {
			return true;
		}

		Substitution al = matcher(p.getHead(),q.getHead());
		if (al != null && subsumesexp(p.getBody(), q.getBody(), al)) {
			return true;
		}
		return false;
	}

	private boolean subsumesexp(List<GdlLiteral> pl, List<GdlLiteral> ql, Substitution al) {
		if (pl.isEmpty()) {
			return true;
		}

		for (GdlLiteral q : ql) {
			if (match(pl.get(0), q, al) != null && subsumesexp(pl.subList(1, pl.size()), ql, al)) {
				return true;
			}
		}

		return false;
	}


	private Substitution matcher(GdlSentence p, GdlSentence q) {
		return LeftUnifier.canUnify(p, q);
	}

	private Substitution match(GdlLiteral p, GdlLiteral q, Substitution al) {
		return LeftUnifier.canUnify(p, q, al);
	}



}
