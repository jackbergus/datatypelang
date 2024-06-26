package it.giacomobergami.datatypelang.compiler.parser.grammar.associations;

import it.giacomobergami.datatypelang.compiler.parser.grammar.stack.ReducedStack;
import it.giacomobergami.datatypelang.compiler.parser.grammar.terms.GrammarTerm;
import it.giacomobergami.datatypelang.compiler.parser.grammar.terms.Terminal;
import it.giacomobergami.datatypelang.compiler.parser.grammar.stack.myToken;
import it.giacomobergami.datatypelang.utils.funcs.Opt;

/**
 * Created by vasistas on 11/12/16.
 */
public class ATerm extends Association {

    private final Terminal term;
    private final myToken token;

    ATerm(Terminal term, myToken token) {
        this.term = term;
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ATerm)) return false;

        ATerm aTerm = (ATerm) o;

        if (term != null ? !term.equals(aTerm.term) : aTerm.term != null) return false;
        return token != null ? token.equals(aTerm.token) : aTerm.token == null;
    }

    @Override
    public int hashCode() {
        int result = term != null ? term.hashCode() : 0;
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }

    @Override
    public GrammarTerm getGrammarMatchedElement() {
        return term;
    }

    @Override
    public boolean hasSubtree() {
        return false;
    }

    @Override
    public String getUnderlyingString() {
        return token.data;
    }

    @Override
    public Opt<ReducedStack> getSubTree() {
        return Opt.err();
    }


}
