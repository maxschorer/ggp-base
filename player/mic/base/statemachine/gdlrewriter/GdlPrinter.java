package mic.base.statemachine.gdlrewriter;

import java.util.List;

import org.ggp.base.util.gdl.grammar.Gdl;

public class GdlPrinter implements GdlRewriter {

	@Override
	public List<Gdl> rewrite(List<Gdl> description) {
		for (Gdl term : description) {
			System.out.println(term);
		}
		System.out.println();
		return description;
	}
}
