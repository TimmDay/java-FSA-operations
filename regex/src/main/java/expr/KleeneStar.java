package expr;

import nfa.NFA;
import nfa.NFAOperations;

public final class KleeneStar implements Expression {
    KleeneStar(Expression expr) {
        this.expr = expr;
    }

    @Override
    public NFA compile() {
        return NFAOperations.kleeneStar(expr.compile());
    }

    @Override
    public String toString() {
        return String.format("(%s)*", expr);
    }

    private final Expression expr;
}
