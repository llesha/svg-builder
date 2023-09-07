package grammar

import MathUtils.rnd
import MathUtils.toNum
import Num
import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import kotlin.contracts.contract
import kotlin.math.roundToInt

class VariableParser() : Grammar<Any>() {
    val variables: MutableMap<String, Any> = mutableMapOf()

    private val refT by literalToken("$")
    private val assignmentT by literalToken("=")
    private val rangeT by literalToken("..")
    private val doubleRangeT by literalToken("...")

    private val mul by literalToken("*")
    private val div by literalToken("/")
    private val minus by literalToken("-")
    private val plus by literalToken("+")
    private val leftPar by literalToken("(")
    private val rightPar by literalToken(")")

    private val number by regexToken("\\d+")
    private val floatNumber by number and -literalToken(".") and number
    private val num: Parser<Num> by (number map { it.text.toNum() }) or
            (floatNumber map { (it.t1.text + "." + it.t2.text).toNum() })

    private val ident by regexToken("[a-zA-Z_]\\w*")
    @Suppress("unused")
    private val ws by regexToken("[\\t\\n ]+", ignore = true)

    private val notation = (num and (doubleRangeT or rangeT) and num map {
        if (it.t2.text == "..")
            rnd.nextInt(it.t1.roundToInt(), it.t3.roundToInt()).toNum()
        else
            rnd.nextDouble(it.t1.toDouble(), it.t3.toDouble()).toNum()
    }) or
            num or
            (ident map { it.text }) or
            (-refT and ident map {
                if (variables[it.text] == null)
                    throw GrammarError("variable ${it.text} not defined")
                variables[it.text]!!
            })

    private val term: Parser<Any> by (optional(minus) and notation map {
        checkNumeric(it.t2)
        if (it.t1 != null)
            -(it.t2 as Num)
        else it.t2
    }) or (-leftPar and parser(::arithmeticExpression) and -rightPar map { it })

    private val divMulChain: Parser<Any> by leftAssociative(
        term, div or mul
    ) { a, op, b -> doArithmeticOperation(a, op, b) }

    private val arithmeticExpression: Parser<Any> by leftAssociative(
        divMulChain, plus or minus
    ) { a, op, b -> doArithmeticOperation(a, op, b) }

    private val assignment by ident and -assignmentT and arithmeticExpression map {
        variables[it.t1.text] = it.t2
        it.t2
    }

    override val rootParser: Parser<Any>
        get() = assignment

    override fun toString(): String = variables.toString()
}

val arithmeticOperationMap = mutableMapOf<String, (Num, Num) -> Num>(
    "+" to { a, b -> a + b },
    "-" to { a, b -> a - b },
    "*" to { a, b -> a * b },
    "/" to { a, b -> a / b }
)

private fun doArithmeticOperation(a: Any, op: TokenMatch, b: Any): Num {
    checkNumeric(a)
    checkNumeric(b)
    return arithmeticOperationMap[op.text]!!(a, b)
}

private fun checkNumeric(value: Any) {
    contract {
        returns() implies (value is Num)
    }
    if (value !is Num)
        throw GrammarError("- is not applicable to Text")
}


