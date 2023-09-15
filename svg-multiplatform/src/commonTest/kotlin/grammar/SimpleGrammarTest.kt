package grammar

import Utils.toNum
import Utils.toType
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.st.liftToSyntaxTreeGrammar
import obj.Conditional
import obj.Container
import obj.Empty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

// class SimpleGrammarTest {
//     val grammar = VariableParser(Container())
//
//     @Test
//     fun testVariableUsage() {
//         val first = grammar.parseToEnd("first=12+4232/2").first()
//         val second = grammar.parseToEnd("second=\$first+2..423").first()
//         assertEquals(first, (12 + 4232 / 2).toNum().toType())
//         assert(second.asNum() in (first.asNum() + 2)..(first.asNum() + 423))
//         println(grammar)
//     }
//
//     @Test
//     fun testParentheses() {
//         val one = grammar.parseToEnd("a = (1 + 2 - 2 ? 15 : 2)").first()
//         assert(one.asNum().toInt() == 15)
//     }
//
//     @Test
//     fun testObj() {
//         val conditional = grammar.parseToEnd("cond=[Conditional]").first()
//         assert(conditional.asObj() is Conditional)
//         val empty = grammar.parseToEnd("a=[]").first()
//         assertEquals(empty.asObj(), Empty)
//     }
//
//     @Test
//     fun testTernary() {
//         val two = grammar.parseToEnd("at = 1 ? 2 : 3").first()
//         assertEquals(two, 2.0f.toType())
//         val empty = grammar.parseToEnd("at1 = (0 ? 0 : 1) ? [] : 0").first()
//         assert(empty.asObj() is Empty)
//         val group = grammar.parseToEnd("at2 = (0 ? 0 : 1) ? [Group] : 0").first()
//         assert(group.asObj() is Container)
//         val exception = assertFails { grammar.parseToEnd("a = [C]") }
//         assert(exception.message!!.contains("Expected one of "))
//     }
//
//     @Test
//     fun testSeparators() {
//         val two = grammar.parseToEnd("""as1 = \
//             2""").first()
//         assert(two.asNum().toInt() == 2)
//         val manyVariables = grammar.parseToEnd("""
//            as = 2
//            bs = 3
//            cs = \
//            4
//         """)
//         assert(manyVariables[0].asNum().toInt() == 2)
//         assert(manyVariables[1].asNum().toInt() == 3)
//         assert(manyVariables[2].asNum().toInt() == 4)
//         val a = grammar.liftToSyntaxTreeGrammar()
//     }
// }
