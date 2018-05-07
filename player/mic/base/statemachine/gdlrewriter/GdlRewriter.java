package mic.base.statemachine.gdlrewriter;

import java.util.List;

import org.ggp.base.util.gdl.grammar.Gdl;

public interface GdlRewriter {
	public List<Gdl> rewrite(List<Gdl> description);
}
