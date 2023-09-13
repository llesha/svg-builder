package grammar

import Utils.toNum
import Utils.toType
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import obj.Conditional
import obj.Container
import obj.Empty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimpleGrammarTest {
    val grammar = VariableParser()

    @Test
    fun testVariableUsage() {
        val first = grammar.parseToEnd("first=12+4232/2")
        val second = grammar.parseToEnd("second=\$first+2..423")
        assertEquals(first, (12 + 4232 / 2).toNum().toType())
        assert(second.asNum() in (first.asNum() + 2)..(first.asNum() + 423))
        println(grammar)
    }

    @Test
    fun testParentheses() {
        val one = grammar.parseToEnd("a = (1 + 2 - 2 ? 15 : 2)")
        assert(one.asNum().toInt() == 15)
    }

    @Test
    fun testObj() {
        val conditional = grammar.parseToEnd("cond=[Conditional]")
        assert(conditional.asObj() is Conditional)
        val empty = grammar.parseToEnd("a=[]")
        assertEquals(empty.asObj(), Empty)
    }

    @Test
    fun testTernary() {
        val two = grammar.parseToEnd("a = 1 ? 2 : 3")
        assertEquals(two, 2.0f.toType())
        val empty = grammar.parseToEnd("a = (0 ? 0 : 1) ? [Group] : 0")
        assert(empty.asObj() is Container)
    }
}
