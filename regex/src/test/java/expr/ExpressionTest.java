package expr;

import nfa.NFAOperations;
import org.junit.Test;

/**
 * @author Daniël de Kok &lt;me@danieldk.eu&gt;
 */
public class ExpressionTest {
    @Test
    public void expressionTest() {
        // (ab|bc)+
        Expression ab = Expr.concat(Expr.ch('a'), Expr.ch('b'));
        Expression bc = Expr.concat(Expr.ch('b'), Expr.ch('c'));
        Expression expr = Expr.plus(Expr.or(ab, bc));

        System.out.println(expr);
        System.out.println(expr.compile().toDot());

        // ((ab|ac)d+)*
        Expression ac = Expr.concat(Expr.ch('a'), Expr.ch('c'));
        expr = Expr.star(Expr.concat(Expr.or(ab, ac), Expr.plus(Expr.ch('d'))));

        System.out.println(expr);
        System.out.println(expr.compile().toDot());
    }
}