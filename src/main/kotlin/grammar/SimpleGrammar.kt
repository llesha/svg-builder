package grammar

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.*
import com.github.h0tk3y.betterParse.parser.Parser

object SimpleGrammar : Grammar<Any>() {
    private val refT by literalToken("$")
    private val assignmentT by literalToken("=")
    private val rangeT by literalToken("..")

    private val mulT by literalToken("*")
    private val divT by literalToken("/")
    private val minusT by literalToken("-")
    private val plusT by literalToken("+")
    private val leftPar by literalToken("(")
    private val rightPar by literalToken(")")

    private val number by regexToken("\\d")
    private val floatNumber by number and -literalToken(".") and number
    private val ident by regexToken("[a-zA-Z_]\\w*")
    private val ws by regexToken("[\\t\\n ]+", ignore = true)

    private val notation = floatNumber or number or ident or (-refT and ident map { Variable(it.text) })

    private val term by (floatNumber map { (it.t1.text + "." + it.t2.text).toDouble() }) or
            (number map { it.text.toInt() }) or
            (ident map { it.text })

    override val rootParser: Parser<Any>
        get() = TODO("Not yet implemented")
}

object Num

class Negated(val value: Any)