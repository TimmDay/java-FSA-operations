package expr;

import dfa.DFA;
import dfa.DFAOperations;
import nfa.NFA;
import nfa.NFAOperations;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Tim Day
 * @date 17.12.2017
 * the 'test' is visually comparing the NFA and minDFA toDot pictures using:
 * http://sandbox.kidstrythisathome.com/erdos/
 */
public class MinimizeNFATest {

    @Test
    public void minimizeTest1() {
        //(xb|xx|xa|xcw)
        Expression expr = Expr.or(Expr.or(Expr.str("xb"),Expr.str("xx")), Expr.or(Expr.str("xa"),Expr.str("xcw")));
        NFA nfa = expr.compile();

        System.out.println(nfa.toDot());
        // 1. reverse NFA
        NFA NFArev_1 = NFAOperations.reversalNFA(nfa);
//        System.out.println(NFArev_1.toDot());

        // 2. determinize the reversal
        DFA detNFArev_2 = NFAOperations.determinize(NFArev_1);
//        System.out.println(detNFArev_2.toDot());

        // 3. reverse that DFA, for a NFA
        NFA revDFA_3 = DFAOperations.reversalDFA(detNFArev_2);
//        System.out.println(revDFA_3.toDot());

        // 4. determinize that second reversal
        DFA minimized_4 = NFAOperations.determinize(revDFA_3);
//        System.out.println(minimized_4.toDot());

        System.out.println("Minimized");
        System.out.println(NFAOperations.minimize(nfa).toDot());

        Assert.assertEquals(NFAOperations.minimize(nfa).toDot(),minimized_4.toDot());
    }

    //pass
    @Test
    public void minimizeTest2_plus() {
        //a+
        Expression expr = Expr.plus(Expr.ch('a'));
        NFA nfa = expr.compile();
        DFA minDFA = NFAOperations.minimize(nfa);

        System.out.println(nfa.toDot());
        System.out.println(minDFA.toDot());

    }

    //pass
    @Test
    public void minimizeTest3_star() {
        // a*
        Expression expr = Expr.star(Expr.ch('a'));
        NFA nfa = expr.compile();
        DFA minDFA = NFAOperations.minimize(nfa);

        System.out.println(nfa.toDot());
        System.out.println(minDFA.toDot());
    }

    //pass
    @Test
    public void minimizeTest4_star2() {
        //a* b
        Expression expr = Expr.concat(Expr.star(Expr.ch('a')),Expr.str("b"));
        NFA nfa = expr.compile();
        DFA minDFA = NFAOperations.minimize(nfa);

        System.out.println(nfa.toDot());
        System.out.println(minDFA.toDot());

    }

    //pass
    @Test
    public void minimizeTest5_or() {
        // (aa | bb)
        Expression expr = Expr.or(Expr.str("aa"),Expr.str("bb"));
        NFA nfa = expr.compile();
        DFA minDFA = NFAOperations.minimize(nfa);

        System.out.println(nfa.toDot());
        System.out.println(minDFA.toDot());
    }

    //pass
    @Test
    public void minimizeTest6_or2() {
        // abc | abg
        Expression expr = Expr.or(Expr.str("abc"), Expr.str("abg"));
        NFA nfa = expr.compile();
        DFA minDFA = NFAOperations.minimize(nfa);

        System.out.println(nfa.toDot());
        System.out.println(minDFA.toDot());
    }

    //pass
    @Test
    public void minimizeTest7_comboStarOr() {
        //a* b | b* a
        Expression expr = Expr.or(Expr.concat(Expr.star(Expr.ch('a')),Expr.str("b")), (Expr.concat(Expr.star(Expr.ch('b')),Expr.str("a"))));
        NFA nfa = expr.compile();
        DFA minDFA = NFAOperations.minimize(nfa);

        System.out.println(nfa.toDot());
        System.out.println(minDFA.toDot());
    }

    //pass
    @Test
    public void minimizeTest8_sheepLanguage() {
        // b a a* a !
        Expression expr = Expr.concat(Expr.concat(Expr.str("ba"), Expr.star(Expr.ch('a')) ), Expr.str("a!"));
        NFA nfa = expr.compile();
        DFA minDFA = NFAOperations.minimize(nfa);

        System.out.println(nfa.toDot());
        System.out.println(minDFA.toDot());
    }

    //pass
    @Test
    public void minimizeTest9_superCombo() {
        //((ab|ac)d+)*
        Expression expr = Expr.star(Expr.concat(Expr.or(Expr.str("ab"), Expr.str("ac")), Expr.plus(Expr.ch('d'))));
        NFA nfa = expr.compile();
        DFA minDFA = NFAOperations.minimize(nfa);

        System.out.println(nfa.toDot());
        System.out.println(minDFA.toDot());
    }
}