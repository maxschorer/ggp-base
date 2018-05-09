package mic.base.statemachine.gdlrewriter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ggp.base.util.gdl.GdlUtils;
import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlLiteral;
import org.ggp.base.util.gdl.grammar.GdlPool;
import org.ggp.base.util.gdl.grammar.GdlRule;
import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.gdl.grammar.GdlVariable;

public class SubgoalReordering implements GdlRewriter {

	@Override
	public List<Gdl> rewrite(List<Gdl> description) {
		List<Gdl> newDescription = new ArrayList<Gdl>();


		for (Gdl term : description) {
			if (term instanceof GdlRule) {
				Gdl newTerm = reorderSubgoal((GdlRule) term);
				newDescription.add(newTerm);
			} else {
				newDescription.add(term);
			}

		}
		return newDescription;
	}

	private GdlRule reorderSubgoal(GdlRule rule) {
		GdlSentence head = rule.getHead();
		List<GdlLiteral> newBody = new ArrayList<GdlLiteral>();

		Set<GdlVariable> vl = new HashSet<GdlVariable>();
		List<GdlLiteral> sl = new LinkedList<GdlLiteral>(rule.getBody());
		while (!sl.isEmpty()) {
			GdlLiteral ans = getBest(sl,vl);
			newBody.add(ans);
			Set<GdlVariable> vars = GdlUtils.getVariablesSet(ans);
			for (GdlVariable v : vars){
				vl.add(v);
			}
		}


		return GdlPool.getRule(head, newBody);
	}

	private GdlLiteral getBest(List<GdlLiteral> sl, Set<GdlVariable> vl) {
		int varNum = 10000;
		int best = 0;

		for (int i = 0; i < sl.size(); i++) {
			int dum = unboundVarNum(sl.get(i), vl);
			if (dum < varNum) {
				varNum = dum;
				best = i;
			}
			if (varNum == 0) break; //can't beat
		}
		GdlLiteral ans = sl.get(best);
		sl.remove(best);
		return ans;
	}

	private int unboundVarNum(GdlLiteral gdlLiteral, Set<GdlVariable> vl) {
		Set<GdlVariable> vars = GdlUtils.getVariablesSet(gdlLiteral);
		vars.removeAll(vl);
		return vars.size();
	}



}
