package expr;

import dfa.DFA;
import dfa.DFAOperations;
import nfa.NFA;
import nfa.NFAOperations;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Tim Day
 * @date 16.12.2017
 * This test has some asserts that check if certain strings are recognized or not for the reversed nfa
 * I also include toDot system prints for the normal nfa and the reversed version, for each test
 * these can be viwed using: http://sandbox.kidstrythisathome.com/erdos/
 */
public class ReverseNFATest {

    @Test
    public void reverseNFATest1_simple() {
        // a b c d -> d c b a
        Expression expr = Expr.str("abcd");
        NFA nfa = expr.compile();
        NFA rnfa = NFAOperations.reversalNFA(nfa);

        System.out.println("abcd -> dcba");
        System.out.println(nfa.toDot());
        System.out.println(rnfa.toDot());

        Assert.assertTrue(nfa.recognize("abcd"));
        Assert.assertTrue(rnfa.recognize("dcba"));
    }

    @Test
    public void reverseNFATest2_withSelfLoop() {
        // a(b*)c d -> d c b* a
        Expression expr = Expr.concat(Expr.concat(Expr.ch('a'),Expr.star(Expr.ch('b'))),Expr.str("cd"));
        NFA nfa = expr.compile();
        NFA rnfa = NFAOperations.reversalNFA(nfa);

        System.out.println("a(b*)c d -> d c b* a ");
        System.out.println(nfa.toDot());
        System.out.println(rnfa.toDot());

        Assert.assertTrue(nfa.recognize("abcd"));
        Assert.assertTrue(nfa.recognize("acd"));
        Assert.assertTrue(nfa.recognize("abbbbcd"));

        Assert.assertTrue(rnfa.recognize("dcba"));
        Assert.assertTrue(rnfa.recognize("dca"));
        Assert.assertTrue(rnfa.recognize("dcbbbba"));

        Assert.assertFalse(rnfa.recognize("dcbbbbax"));
        Assert.assertFalse(rnfa.recognize("dba"));
    }

    @Test
    public void reverseNFATest3_repeatPart() {
        // (ab)*cd -> dc(ba)*
        Expression expr = Expr.concat(Expr.star(Expr.str("ab")),Expr.str("cd"));
        NFA nfa = expr.compile();
        NFA rnfa = NFAOperations.reversalNFA(nfa);

        System.out.println("(ab)*cd -> dc(ba)*");
        System.out.println(nfa.toDot());
        System.out.println(rnfa.toDot());

        Assert.assertTrue(nfa.recognize("cd"));
        Assert.assertTrue(nfa.recognize("abcd"));
        Assert.assertTrue(nfa.recognize("ababcd"));

        Assert.assertTrue(rnfa.recognize("dc"));
        Assert.assertTrue(rnfa.recognize("dcba"));
        Assert.assertTrue(rnfa.recognize("dcbaba"));
    }

    @Test
    public void reverseNFATest4_repeatWholePhrase() {
        // (a b c)* -> (c b a)*
        Expression expr = Expr.star(Expr.str("abc"));
        NFA nfa = expr.compile();
        NFA rnfa = NFAOperations.reversalNFA(nfa);

        System.out.println("(a b c)* -> (c b a)*");
        System.out.println(nfa.toDot());
        System.out.println(rnfa.toDot());

        Assert.assertTrue(nfa.recognize(""));
        Assert.assertTrue(nfa.recognize("abc"));
        Assert.assertTrue(nfa.recognize("abcabc"));

        Assert.assertTrue(rnfa.recognize(""));
        Assert.assertTrue(rnfa.recognize("cba"));
        Assert.assertTrue(rnfa.recognize("cbacba"));
    }


    @Test
    public void reverseNFATest5_aStar() {
        // a*
        Expression expr = Expr.star(Expr.ch('a'));
        NFA nfa = expr.compile();
        NFA rnfa = NFAOperations.reversalNFA(nfa);

        System.out.println("a* -> a*");
        System.out.println(nfa.toDot());
        System.out.println(rnfa.toDot());

        Assert.assertTrue(nfa.recognize(""));
        Assert.assertTrue(nfa.recognize("a"));
        Assert.assertTrue(nfa.recognize("aaa"));

        Assert.assertTrue(rnfa.recognize(""));
        Assert.assertTrue(rnfa.recognize("a"));
        Assert.assertTrue(rnfa.recognize("aaa"));
    }

    @Test
    public void reverseDFATest1_withSelfLoop() {
        // a(b*)c d -> d c b* a
        Expression expr = Expr.concat(Expr.concat(Expr.ch('a'),Expr.star(Expr.ch('b'))),Expr.str("cd"));
        NFA nfa = expr.compile();
        DFA dfa = NFAOperations.determinize(nfa);
        NFA rdfa = DFAOperations.reversalDFA(dfa);

        System.out.println("a(b*)c d -> d c b* a ");
        System.out.println(nfa.toDot());
        System.out.println(rdfa.toDot());

        Assert.assertTrue(nfa.recognize("abcd"));
        Assert.assertTrue(nfa.recognize("acd"));
        Assert.assertTrue(nfa.recognize("abbbbcd"));

        Assert.assertTrue(rdfa.recognize("dcba"));
        Assert.assertTrue(rdfa.recognize("dca"));
        Assert.assertTrue(rdfa.recognize("dcbbbba"));
    }


    @Test
    public void reverseDFATest2_withStar() {
        // (ab)* -> (ba)*
        Expression expr = Expr.star(Expr.str("ab"));
        NFA nfa = expr.compile();
        DFA dfa = NFAOperations.determinize(nfa);
        NFA rdfa = DFAOperations.reversalDFA(dfa);

        System.out.println("(ab)* -> (ba)*");
        System.out.println(nfa.toDot());
        System.out.println(rdfa.toDot());

        Assert.assertTrue(nfa.recognize(""));
        Assert.assertTrue(nfa.recognize("ab"));
        Assert.assertTrue(nfa.recognize("abab"));

        Assert.assertTrue(rdfa.recognize(""));
        Assert.assertTrue(rdfa.recognize("ba"));
        Assert.assertTrue(rdfa.recognize("baba"));
    }


    //todo bug here
    @Test
    public void reverseTest_sheepLanguage1_DFA() {
        // baa*a! -> !aa*ab
        Expression expr = Expr.concat(Expr.concat(Expr.str("ba"), Expr.star(Expr.ch('a'))), Expr.str("a!"));
        NFA nfa = expr.compile();
        DFA dfa = NFAOperations.determinize(nfa);
        NFA rdfa = DFAOperations.reversalDFA(dfa);

        System.out.println("baa*a! -> !aa*ab");
        System.out.println(nfa.toDot());
        System.out.println(rdfa.toDot());

        Assert.assertTrue(nfa.recognize("baa!"));
        Assert.assertTrue(nfa.recognize("baaaaaa!"));

        Assert.assertTrue(rdfa.recognize("!aab"));
        Assert.assertTrue(rdfa.recognize("!aaaaab"));

//        System.out.println(dfa.toDot());
    }

    //todo bug here
    @Test
    public void reverseTest_sheepLanguage2_NFA() {
        // baa*a! -> !aa*ab
        Expression expr = Expr.concat(Expr.concat(Expr.str("ba"), Expr.star(Expr.ch('a'))), Expr.str("a!"));
        NFA nfa = expr.compile();
        NFA rnfa = NFAOperations.reversalNFA(nfa);

        System.out.println("baa*a! -> !aa*ab");
        System.out.println(nfa.toDot());
        System.out.println(rnfa.toDot());

        Assert.assertTrue(nfa.recognize("baa!"));
        Assert.assertTrue(nfa.recognize("baaaaaa!"));

        Assert.assertTrue(rnfa.recognize("!aab"));
        Assert.assertTrue(rnfa.recognize("!aaaaab"));
    }

    //todo bug here
    @Test
    public void reverseTest_sheepLanguage3_finite() {
        // baa! -> !aab
        Expression expr = Expr.str("baa!");
        NFA nfa = expr.compile();
        NFA rnfa = NFAOperations.reversalNFA(nfa);

        System.out.println("baa! -> !aab");
        System.out.println(nfa.toDot());
        System.out.println(rnfa.toDot());

        Assert.assertTrue(nfa.recognize("baa!"));

        Assert.assertTrue(rnfa.recognize("!aab"));

    }



    //        // TEST 5
//        // ((abcdefg|hijklmn)d+)*
//        //should equal above
//        Expression expr5 = Expr.star(Expr.concat(Expr.or(Expr.str("abcdefg"), Expr.str("hijklmn")),
//                Expr.plus(Expr.ch('d'))));
//        System.out.println(expr5);


}