package expr;

import dfa.DFA;
import nfa.NFA;
import nfa.NFAOperations;
import org.junit.Test;

/**
 * @author Tim Day
 * @date 17.12.2017
 * for testing the determinizer method in NFAOperations
 */
public class DeterminizerTest {
    @Test
    public void determinizeTest_or() {
        // abc | abg
        Expression abcOabg = Expr.or(Expr.str("abc"), Expr.str("abg"));
        NFA abcOabgNFA = abcOabg.compile();
//        System.out.println(abcOabgNFA.toDot());

        DFA dfa = NFAOperations.determinize(abcOabgNFA);
        System.out.println(dfa.toDot());
    }

    @Test
    public void determinizeTest_or2() {
        // (ab|ac)
        Expression a_bOc = Expr.concat(Expr.ch('a'),Expr.or(Expr.ch('b'),Expr.ch('c')));
        NFA a_bOcNFA = a_bOc.compile();
//        System.out.println(a_bOcNFA.toDot());
        DFA a_bOcDFA = NFAOperations.determinize(a_bOcNFA);
        System.out.println(a_bOcDFA.toDot());
    }

    @Test
    public void determinizeTest_kleeneStar() {
        // a*
        Expression aS = Expr.star(Expr.ch('a'));
        NFA aSNFA = aS.compile();

        System.out.println(aSNFA.toDot());

        DFA aSDFA = NFAOperations.determinize(aSNFA);
        System.out.println(aSDFA.toDot());
    }

    @Test
    public void determinizeTest_kleeneStar2() {
        // a*a
        Expression aSa = Expr.concat(Expr.star(Expr.ch('a')),Expr.ch('a'));
        NFA aSaNFA = aSa.compile();
        System.out.println(aSaNFA.toDot());
        DFA aSaDFA = NFAOperations.determinize(aSaNFA);
        System.out.println(aSaDFA.toDot());
    }

    @Test
    public void determinizeTest5() {
        // (a(b|c)d+)*

        Expression slidesEg = Expr.concat(Expr.ch('a'),Expr.or(Expr.ch('b'), Expr.ch('c')));
        Expression slidesEg2 = Expr.plus(Expr.ch('d'));
        Expression slidesEg3 = Expr.star(Expr.concat(slidesEg,slidesEg2));

        NFA slidesEgNFA = slidesEg3.compile();
//        System.out.println(slidesEgNFA.toDot());

        DFA slidesEgDFA = NFAOperations.determinize(slidesEgNFA);
        System.out.println(slidesEgDFA.toDot());
    }


    @Test
    public void determinizeTest_comboOrPlus() {
        // (ab|ac)+
        Expression expr = Expr.plus(Expr.or(Expr.str("ab"), Expr.str("ac")));

        NFA nfa = expr.compile();
//        System.out.println(nfa.toDot());
        DFA dfa = NFAOperations.determinize(nfa);
        System.out.println(dfa.toDot());
    }

    @Test
    public void determinizeTest_comboOrPlusStar() {
        // ((ab|ac)d+)*
        Expression ac = Expr.str("ac");
        Expression ab = Expr.str("ab");
        Expression or = Expr.or(ab,ac);
        Expression dP = Expr.plus(Expr.ch('d'));
        Expression expr = Expr.star(Expr.concat(or,dP));

        NFA nfa = expr.compile();
        DFA dfa = NFAOperations.determinize(nfa);
//        System.out.println(nfa.toDot());
        System.out.println(dfa.toDot());
    }

}