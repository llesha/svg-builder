package grammar

import Utils.toNum
import Utils.toType
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.st.liftToSyntaxTreeGrammar
import obj.*
import kotlin.test.*

class SimpleGrammarTest {
    val grammar = VariableParser(Container())
    val root = Container()

    @AfterTest
    fun clear() {
        root.clear()
    }

    @Test
    fun testVariableUsage() {
        val first = grammar.parseToEnd("first=12+4232/2").first().parse(root)
        val second = grammar.parseToEnd("second=\$first+2..423").first().parse(root)
        assertEquals(first, (12 + 4232 / 2).toNum().toType())
        assertTrue(second.asNum() in (first.asNum() + 2)..(first.asNum() + 423))
        println(grammar)
    }

    @Test
    fun testParentheses() {
        val one = grammar.parseToEnd("a = (1 + 2 - 2 ? 15 : 2)").first().parse(root)
        assertTrue(one.asNum().toInt() == 15)
    }

    @Test
    fun testObj() {
        val recursive = grammar.parseToEnd("cond=[Recursive]").first().parse(root)
        assertTrue(recursive.asObj() is Recursive)
        val empty = grammar.parseToEnd("a=[]").first().parse(root)
        assertEquals(empty.asObj(), Empty)
    }

    @Test
    fun testTernary() {
        val two = grammar.parseToEnd("at = 1 ? 2 : 3").first().parse(root)
        assertEquals(two, 2.0f.toType())
        val empty = grammar.parseToEnd("at1 = (0 ? 0 : 1) ? [] : 0").first().parse(root)
        assertTrue(empty.asObj() is Empty)
        val group = grammar.parseToEnd("at2 = (0 ? 0 : 1) ? [Group] : 0").first().parse(root)
        assertTrue(group.asObj() is Container)
        val exception = assertFails { grammar.parseToEnd("a = [C]") }
        assertTrue(exception.message!!.contains("Expected one of "))
    }

    @Test
    fun testSeparators() {
        val two = grammar.parseToEnd(
            """as1 = \
            2"""
        ).first().parse(root)
        assertTrue(two.asNum().toInt() == 2)
        val manyVariables = grammar.parseToEnd(
            """
           as = 2
           bs = 3
           cs = \
           4
        """
        ).map { it.parse(root) }
        assertTrue(manyVariables[0].asNum().toInt() == 2)
        assertTrue(manyVariables[1].asNum().toInt() == 3)
        assertTrue(manyVariables[2].asNum().toInt() == 4)
        val a = grammar.liftToSyntaxTreeGrammar()
    }
}
