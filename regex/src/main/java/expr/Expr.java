package expr;

import com.google.common.base.Preconditions;

/**
 * This class is used to build the Expressions that are used to compile the NFAs used in this project
 * last change:  Tim Day Dec2017, added str method
 */
public final class Expr {
    public static Expression ch(char c) {
        return new Char(c);
    }

    /**
     * Create an expression that is the concatenation of the given expressions.
     */
    public static Expression concat(Expression expr, Expression expr2) {
        Preconditions.checkNotNull(expr);
        Preconditions.checkNotNull(expr2);

        return new Concat(expr, expr2);
    }

    /**
     * Create an expression that is the union of the given expressions.
     */
    public static Expression or(Expression expr, Expression expr2) {
        Preconditions.checkNotNull(expr);
        Preconditions.checkNotNull(expr2);

        return new Or(expr, expr2);
    }

    /**
     * Create an expression that is the Kleene plus of the given expression.
     */
    public static Expression plus(Expression expr) {
        Preconditions.checkNotNull(expr);

        return concat(expr, star(expr));
    }

    /**
     * Create an expression that is the Kleene star of the given expression.
     */
    public static Expression star(Expression expr) {
        Preconditions.checkNotNull(expr);

        return new KleeneStar(expr);
    }


    /**
     * Create an expression that recognizes the given string.
     */
    public static Expression str(String s) {
        Preconditions.checkNotNull(s);
        Preconditions.checkArgument(s.length() > 0);

        // get string length
        // for each char of string, add it to the expression
        Expression joinedCharsResult =  Expr.ch(s.charAt(0)); //the first one
        for (int i=1; i<s.length(); i++){
            joinedCharsResult = Expr.concat(joinedCharsResult, Expr.ch(s.charAt(i)));
        }
        return joinedCharsResult;
    }

    // CONSTRUCTOR
    private Expr() {
    }

}
