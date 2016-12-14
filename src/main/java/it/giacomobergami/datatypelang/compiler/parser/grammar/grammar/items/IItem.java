package it.giacomobergami.datatypelang.compiler.parser.grammar.grammar.items;

import it.giacomobergami.datatypelang.compiler.parser.grammar.TableColumnEntry;
import it.giacomobergami.datatypelang.compiler.parser.grammar.grammar.Grammar;
import it.giacomobergami.datatypelang.compiler.parser.grammar.terms.GrammarTerm;
import it.giacomobergami.datatypelang.compiler.parser.grammar.terms.NonTerminal;
import it.giacomobergami.datatypelang.compiler.parser.grammar.utils.Array;
import it.giacomobergami.datatypelang.utils.funcs.Opt;

import java.util.Collection;
import java.util.List;

/**
 * Created by vasistas on 12/12/16.
 */
public interface IItem<K extends Enum, T extends IItem<K,T>> extends Iterable<T> {
    NonTerminal<K> getHead();
    GrammarTerm<K>[] getCore();
    int getItemPos();
    GrammarTerm<K>[] getElementsNextToCore();
    List<GrammarTerm<K>> getElementsNextToCore(GrammarTerm<K> withLookahead);
    boolean hasNextitemMove();
    T getNextItemMove();
    Opt<GrammarTerm<K>> elementAtCurrentPosition();
}