package expr;

import nfa.NFA;
import nfa.NFAOperations;

public final class Or implements Expression {
    public Or(Expression expr, Expression expr2) {
        this.expr = expr;
        this.expr2 = expr2;
    }

    @Override
    public NFA compile() {
        return NFAOperations.union(expr.compile(), expr2.compile());
    }

    @Override
    public String toString() {
        return String.format("(%s|%s)", expr, expr2);
    }

    private final Expression expr;
    private final Expression expr2;
}
