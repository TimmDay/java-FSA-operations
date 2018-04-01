package expr;

import nfa.NFA;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Tim Day
 * @date 12.12.2017
 * test that the recognize method is able to recognize the strings that it should,
 * and that it doesn't recognize those that it shouldn't
 * these tests also test and confirm the correct operation of the implementations of:
 * - concatenate
 * - union
 * - kleeneStar
 * i included system prints for the nfa (using http://sandbox.kidstrythisathome.com/erdos/)
 * which can be viewed if they are uncommented
 */
public class RegonizeNFATest {
    @Test
    public void recognizeTest1_simple() {
        Expression expr = Expr.str("abcd");
        NFA nfa = expr.compile();
//        System.out.println(nfa.toDot());

        Assert.assertTrue(nfa.recognize("abcd"));

        Assert.assertFalse(nfa.recognize("abc"));
        Assert.assertFalse(nfa.recognize("abcde"));
    }

    @Test
    public void recognizeTest2_plus() {
        //a+
        Expression aP = Expr.plus(Expr.ch('a'));
        NFA nfa = aP.compile();

        Assert.assertTrue(nfa.recognize("a"));
        Assert.assertTrue(nfa.recognize("aaaaa"));

        Assert.assertFalse(nfa.recognize("aaaaab"));
        Assert.assertFalse(nfa.recognize(""));
    }

    @Test
    public void recognizeTest3_star() {
        // a*
        Expression aX = Expr.star(Expr.ch('a'));
        NFA nfa = aX.compile();
//        System.out.println(nfa.compile().toDot());

        Assert.assertTrue(nfa.recognize(""));
        Assert.assertTrue(nfa.recognize("a"));
        Assert.assertTrue(nfa.recognize("aaa"));

        Assert.assertFalse(nfa.recognize("b"));
        Assert.assertFalse(nfa.recognize("aaaab"));
    }

    @Test
    public void recognizeTest4_star2() {
        //a* b
        Expression expr = Expr.concat(Expr.star(Expr.ch('a')),Expr.str("b"));
        NFA nfa = expr.compile();
//        System.out.println(nfa.toDot());

        Assert.assertTrue(nfa.recognize("b"));
        Assert.assertTrue(nfa.recognize("ab"));
        Assert.assertTrue(nfa.recognize("aab"));
        Assert.assertTrue(nfa.recognize("aaaaaaaaaaaaab"));

        Assert.assertFalse(nfa.recognize(""));
        Assert.assertFalse(nfa.recognize("a"));
        Assert.assertFalse(nfa.recognize("aaabx"));
        Assert.assertFalse(nfa.recognize("ba"));
    }

    @Test
    public void recognizeTest5_or() {
        // (aa | bb)
        Expression expr = Expr.or(Expr.str("aa"),Expr.str("bb"));
        NFA nfa = expr.compile();
//        System.out.println(nfa.toDot());

        Assert.assertTrue(nfa.recognize("aa"));
        Assert.assertTrue(nfa.recognize("bb"));

        Assert.assertFalse(nfa.recognize(""));
        Assert.assertFalse(nfa.recognize("a"));
        Assert.assertFalse(nfa.recognize("aaa"));
        Assert.assertFalse(nfa.recognize("bbb"));
    }


    @Test
    public void recognizeTest6_or2() {
        // abc | abg
        Expression expr = Expr.or(Expr.str("abc"), Expr.str("abg"));
        NFA nfa = expr.compile();
//        System.out.println(nfa.toDot());

        Assert.assertTrue(nfa.recognize("abc"));
        Assert.assertTrue(nfa.recognize("abg"));

        Assert.assertFalse(nfa.recognize("abx"));
        Assert.assertFalse(nfa.recognize(""));
        Assert.assertFalse(nfa.recognize("abcabc"));
        Assert.assertFalse(nfa.recognize("abga"));
    }

    @Test
    public void recognizeTest7_comboStarOr() {
        //a* b | b* a
        Expression expr = Expr.or(Expr.concat(Expr.star(Expr.ch('a')),Expr.str("b")), (Expr.concat(Expr.star(Expr.ch('b')),Expr.str("a"))));
        NFA nfa = expr.compile();
//        System.out.println(aXbNFA.toDot());

        Assert.assertTrue(nfa.recognize("b"));
        Assert.assertTrue(nfa.recognize("ab"));
        Assert.assertTrue(nfa.recognize("a"));
        Assert.assertTrue(nfa.recognize("ba"));
        Assert.assertTrue(nfa.recognize("aab"));
        Assert.assertTrue(nfa.recognize("bba"));
        Assert.assertTrue(nfa.recognize("aaaaaaaaaaab"));
        Assert.assertTrue(nfa.recognize("bbbbbbbbbbba"));

        Assert.assertFalse(nfa.recognize(""));
        Assert.assertFalse(nfa.recognize("x"));
        Assert.assertFalse(nfa.recognize("abab"));
        Assert.assertFalse(nfa.recognize("baba"));
    }

    @Test
    public void recognizeTest8_sheepLanguage() {
        // b a a* a !
        Expression expr = Expr.concat(Expr.concat(Expr.str("ba"), Expr.star(Expr.ch('a')) ), Expr.str("a!"));
        NFA nfa = expr.compile();
//        System.out.println(nfa.toDot());

        Assert.assertTrue(nfa.recognize("baaa!"));
        Assert.assertTrue(nfa.recognize("baaaaaaaaa!"));
        Assert.assertTrue(nfa.recognize("baa!"));

        Assert.assertFalse(nfa.recognize("baa"));
        Assert.assertFalse(nfa.recognize("baaa!!"));
        Assert.assertFalse(nfa.recognize("aaa!"));
        Assert.assertFalse(nfa.recognize(""));
    }

    @Test
    public void recognizeTest9_superCombo() {
        //((ab|ac)d+)*
        Expression expr = Expr.star(Expr.concat(Expr.or(Expr.str("ab"), Expr.str("ac")), Expr.plus(Expr.ch('d'))));
        NFA nfa = expr.compile();
//        System.out.println(nfa.toDot());

        Assert.assertTrue(nfa.recognize(""));
        Assert.assertTrue(nfa.recognize("abd"));
        Assert.assertTrue(nfa.recognize("acd"));
        Assert.assertTrue(nfa.recognize("abdabddabddd"));
        Assert.assertTrue(nfa.recognize("abdacdabddacddacddddddddddd"));

        Assert.assertFalse(nfa.recognize("ab"));
        Assert.assertFalse(nfa.recognize("abcd!!"));
        Assert.assertFalse(nfa.recognize("acdabdac!"));
    }
}