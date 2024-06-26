package it.giacomobergami.datatypelang.compiler.parser.grammar.input;

import it.giacomobergami.datatypelang.compiler.parser.grammar.TableColumnEntry;
import it.giacomobergami.datatypelang.compiler.parser.grammar.terms.GrammarTerm;

/**
 * Created by vasistas on 11/12/16.
 */
public interface OnInput {
    boolean isEmpty();
    TableColumnEntry asTableColumnValue();
    String getType();
    default boolean nonEmpty() {
        return !isEmpty();
    }

    GrammarTerm asGrammarTerm();
}
