package expr;

import nfa.NFA;
import nfa.NFAOperations;

public final class Char implements Expression {
    Char(char c) {
        this.c = c;
    }

    @Override
    public NFA compile() {
        return NFAOperations.character(c);
    }

    @Override
    public String toString() {
        return Character.toString(c);
    }

    private final char c;
}
