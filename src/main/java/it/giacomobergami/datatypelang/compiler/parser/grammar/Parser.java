package it.giacomobergami.datatypelang.compiler.parser.grammar;

import com.github.mrebhan.crogamp.cli.TableList;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import it.giacomobergami.datatypelang.compiler.parser.TableCase;
import it.giacomobergami.datatypelang.compiler.parser.grammar.grammar.Rule;
import it.giacomobergami.datatypelang.compiler.parser.grammar.associations.ANTerm;
import it.giacomobergami.datatypelang.compiler.parser.grammar.grammar.Grammar;
import it.giacomobergami.datatypelang.compiler.parser.grammar.grammar.State;
import it.giacomobergami.datatypelang.compiler.parser.grammar.input.OnInput;
import it.giacomobergami.datatypelang.compiler.parser.grammar.stack.OnStack;
import it.giacomobergami.datatypelang.compiler.parser.grammar.stack.ReducedStack;
import it.giacomobergami.datatypelang.compiler.parser.grammar.stack.myToken;
import it.giacomobergami.datatypelang.utils.data.LazyRead;
import it.giacomobergami.datatypelang.utils.funcs.Opt;
import it.giacomobergami.datatypelang.utils.funcs.OptErr;

import java.util.*;

/**
 * Created by vasistas on 11/12/16.
 */
public class Parser {

    public final Table<Integer, TableColumnEntry, TableCase> table;
    Set<State> visitedStates;
    Deque<Integer> stateStack;

    public Parser() {
        table = HashBasedTable.create();
        visitedStates = new HashSet<>();
    }

    public Opt<TableCase> set(Integer state, TableColumnEntry t_nt_dollar, int shift) {
        TableCase t;
        return (t = table.put(state, t_nt_dollar, new TableCase(shift))) != null ? Opt.of(t) : Opt.err();
    }

    public Opt<TableCase> set(Integer state, TableColumnEntry t_nt_dollar, Rule r) {
        TableCase t;
        return (t = table.put(state, t_nt_dollar, new TableCase(r))) != null ? Opt.of(t) : Opt.err();
    }

    public Opt<TableCase> get(Integer state, TableColumnEntry t_nt_dollar) {
        return  (table.contains(state,t_nt_dollar)) ? Opt.of(table.get(state,t_nt_dollar)) : Opt.err();
    }

    public int numberOfStates() {
        return table.rowKeySet().size();
    }

    public int numberOfTerms() {
        return table.columnKeySet().size();
    }

    public int tableSize() {
        return table.size();
    }

    public int statesNumber() {
        return visitedStates.size();
    }

    public int insertAndCheck(State kState) {
        if (visitedStates.contains(kState)) {
            return visitedStates.stream().filter(x->x.equals(kState)).findFirst().get().getNumber();
        } else {
            visitedStates.add(kState);
            return -1;
        }
    }

    public int get(State kState) {
        if (visitedStates.contains(kState)) {
            return visitedStates.stream().filter(x->x.equals(kState)).findFirst().get().getNumber();
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        ArrayList<TableColumnEntry> header = new ArrayList<>(table.columnKeySet());
        header.add(0,null);
        TableList tl = new TableList(header.size(),header.stream().map(x->x==null ? "" : x.toString()).toArray((i)-> new String[i])).sortBy(0).withUnicode(true);
        for (int i: table.rowKeySet()) {
            Map<TableColumnEntry, TableCase> row = table.row(i);
            tl.addRow(
            header.stream().map(x->{
                if (x==null)
                    return i+"";
                else if (row.containsKey(x))
                   return row.get(x).toString();
                else
                   return "";
            }).toArray((j)->new String[j]));
        }
        return tl.toString();
    }

    public  Opt<TableCase> set(int stateNo, TableColumnEntry kTableColumnEntry) {
        TableCase t;
        return (t = table.put(stateNo, kTableColumnEntry, new TableCase())) != null ? Opt.of(t) : Opt.err();
    }

    public OptErr<ANTerm,OnInput> recognize(Grammar starter, LazyRead terminals) {
        stateStack = new ArrayDeque<>();
        stateStack.push(0);
        Deque<OnStack> recognizedInput = new ArrayDeque<>();
        boolean cond = false;
        while (!cond) {
            //Return the next state position
            Integer s1 = stateStack.peek();

            //get the next terminal (or empty element, if it doesn't exist
            OnInput w = terminals.peek();
            ///System.out.println("State: "+w+" State: "+s1);

            // If the table contains the entry
            Opt<Boolean> b = get(s1,w.asTableColumnValue()).ifte(y -> y.eliminationRule2(
                    // a. If I have a shift operation, then… and save the terminal values,
                    shiftNo -> {

                        // a1. …push the state in the stack,
                        ///System.err.println("Shifting to "+shiftNo);
                        stateStack.push(shiftNo);
                        // a2. …and push the recognized text in the stack.
                        if (w instanceof myToken) recognizedInput.push((myToken) w);
                        terminals.flush();
                    },

                    // b. if I have a reduce operation,
                    x -> {
                        Grammar g;
                        // b1. …Apply the reduction: reduce the stack input
                        x.reduce(recognizedInput);
                        // b2. …Apply the reduction: backtrack to the previous state
                        for (int i = 0; i < x.tail().length; i++) stateStack.pop();
                        // b3. get the state where we have to restart from
                        Integer restartFrom = stateStack.peek();
                        // b4. checks if I can perform the goto action within the table
                        return get(restartFrom, x.header())
                                .ifte(
                                        z -> z.eliminationRule(stateStack::push, w1 -> false),
                                        () -> Opt.of(false)
                                );
                    }), () ->
                    Opt.err("ERROR: I'm reading "+w.toString()+" at elem "));

            if (b.isError()) {
                return (new OptErr<>(w));
            } else {
                cond = b.value();
            }
        }

        return OptErr.of2(new ANTerm(starter.getStarter(),(ReducedStack)recognizedInput.pop()));
    }

}
