package mic.base.statemachine.gdlrewriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ggp.base.util.gdl.GdlUtils;
import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlConstant;
import org.ggp.base.util.gdl.grammar.GdlLiteral;
import org.ggp.base.util.gdl.grammar.GdlPool;
import org.ggp.base.util.gdl.grammar.GdlRule;
import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.gdl.grammar.GdlVariable;
import org.ggp.base.util.gdl.transforms.CommonTransforms;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

import mic.util.Itertools;
import mic.util.Pair;

public class RedundantSubgoalPruner implements GdlRewriter {


	@Override
	public List<Gdl> rewrite(List<Gdl> description) {
		List<Gdl> newDescription = new ArrayList<Gdl>();

		for (Gdl term : description) {
			if (term instanceof GdlRule) {
				Gdl newTerm = pruneSubgoal((GdlRule) term);
				newDescription.add(newTerm);
			} else {
				newDescription.add(term);
			}

		}

		return newDescription;
	}

	private GdlRule pruneSubgoal(GdlRule rule) {
		GdlSentence head = rule.getHead();
		List<GdlLiteral> newBody = new ArrayList<GdlLiteral>();

		for (Pair<Integer, GdlLiteral> e : Itertools.enumerate(rule.getBody())) {
			Set<GdlVariable> vl = GdlUtils.getVariablesSet(head);
			Set<GdlLiteral> sl = Sets.newHashSet(Iterators.concat(newBody.iterator(), rule.getBody().listIterator(e.first + 1)));
			if (!prunable(sl, e.second, vl)) {
				newBody.add(e.second);
			} else {
				System.out.println("Pruning: " + e.second.toString());
				System.out.println("From: " + rule.toString() + "\n");

			}
		}

		return GdlPool.getRule(head, newBody);
	}

	private boolean prunable(Set<GdlLiteral> sl, GdlLiteral p, Set<GdlVariable> vl) {
		for (GdlLiteral s : sl) {
			vl.addAll(GdlUtils.getVariablesSet(s));
		}

		Map<GdlVariable, GdlConstant> al = new HashMap<GdlVariable, GdlConstant>();
		for (Pair<Integer, GdlVariable> e : Itertools.enumerate(vl)) {
			al.put(e.second, GdlPool.getConstant("x" + e.first.toString()));
		}
		List<GdlLiteral> facts = new ArrayList<GdlLiteral>();
		for (GdlLiteral s : sl) {
			facts.add(CommonTransforms.replaceVariables(s, al));
		}

		GdlLiteral goal = CommonTransforms.replaceVariables(p, al);
		return compfindp(goal, facts);
	}

	private boolean compfindp(GdlLiteral goal, List<GdlLiteral> facts) {
		for (GdlLiteral fact : facts) {
			if (Unifier.canUnify(fact, goal) != null) {
				return true;
			}
		}
		return false;
	}

}
