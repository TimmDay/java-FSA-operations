package expr;

import nfa.NFA;

/**
 * This interface is used to tag expression types.
 */
public interface Expression {
    /**
     * Compile the expression to a non-deterministic fine state automaton.
     * @return The automaton.
     */
    NFA compile();
}
