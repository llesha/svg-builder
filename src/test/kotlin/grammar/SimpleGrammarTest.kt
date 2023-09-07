package grammar

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import kotlin.test.Test


class SimpleGrammarTest
{
    val grammar = VariableParser()

    @Test
    fun testVariableUsage() {
        val first = grammar.parseToEnd("first=12+4232/2")
        val second = grammar.parseToEnd("second=\$first+2..423")
        println(first)
        println(second)
        println(grammar)
    }
}
