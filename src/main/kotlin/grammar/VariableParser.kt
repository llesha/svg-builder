package grammar

import Num
import Utils.rnd
import Utils.toNum
import Utils.toType
import Utils.type
import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import obj.Empty
import obj.Objects
import kotlin.contracts.contract
import kotlin.math.roundToInt

class VariableParser : Grammar<Type>() {
    val variables: MutableMap<String, Type> = mutableMapOf()

    private val questionT by literalToken("?")
    private val colonT by literalToken(":")
    private val refT by literalToken("$")
    private val assignmentT by literalToken("=")
    private val rangeT by literalToken("..")
    private val doubleRangeT by literalToken("...")

    private val isT by literalToken("is")
    private val biggerT by literalToken(">")
    private val smallerT by literalToken("<")
    private val equalT by literalToken("==")
    private val unequalT by literalToken("!=")

    private val mul by literalToken("*")
    private val div by literalToken("/")
    private val minus by literalToken("-")
    private val plus by literalToken("+")
    private val leftPar by literalToken("(")
    private val rightPar by literalToken(")")
    private val leftBracket by literalToken("[")
    private val rightBracket by literalToken("]")

    private val number by regexToken("\\d+")
    private val floatNumber by number and -literalToken(".") and number
    private val num: Parser<Num> by (number map { it.text.toNum() }) or
        (floatNumber map { (it.t1.text + "." + it.t2.text).toNum() })

    private val ident by regexToken("[a-zA-Z_]\\w*")

    @Suppress("unused")
    private val ws by regexToken("[\\t\\n ]+", ignore = true)

    private val notation: Parser<Type> = (num and (doubleRangeT or rangeT) and num map {
        if (it.t2.text == "..")
            rnd.nextInt(it.t1.roundToInt(), it.t3.roundToInt()).toNum().toType()
        else
            rnd.nextDouble(it.t1.toDouble(), it.t3.toDouble()).toNum().toType()
    }) or
        (num map { it.toType() }) or
        (ident map { it.text.toType() }) or
        (-refT and ident map {
            if (variables[it.text] == null) throw GrammarError("variable ${it.text} not defined")
            variables[it.text]!!
        })

    // name = [Recursive] [Conditional] [Group] [Shape] []
    private val obj: Parser<Type> by ((leftBracket and rightBracket) or (-leftBracket and ident and -rightBracket)) map {
        if (it is TokenMatch) {
            Objects.toObject(it.text.uppercase()).make().toType()
        } else Empty.toType()
    }

    private val term: Parser<Type> by obj or (optional(minus) and notation map {
        checkNumeric(it.t2, "unary minus")
        if (it.t1 != null) (-(it.t2 as Num)).toType()
        else it.t2.toType()
    }) or
        (-leftPar and parser(::arithmeticExpression) and -rightPar map { it.toType() }) or
        (-leftPar and parser(::ternary) and -rightPar map { it.toType() })

    private val divMulChain: Parser<Type> by leftAssociative(
        term, div or mul
    ) { a, op, b ->
        doArithmeticOperation(a, op, b).toType()
    }

    private val arithmeticExpression: Parser<Type> by leftAssociative(
        divMulChain, plus or minus
    ) { a, op, b ->
        doArithmeticOperation(a, op, b).toType()
    }

    private val ternary: Parser<Type> by (arithmeticExpression or parser(::ternary)) and
        -questionT and (arithmeticExpression or parser(::ternary)) and
        -colonT and (arithmeticExpression or parser(::ternary)) map {
        if (it.t1.asBool()) {
            it.t2
        } else it.t3
    }

    private val assignment: Parser<Type> by ident and -assignmentT and (ternary or arithmeticExpression) map {
        variables[it.t1.text] = it.t2
        it.t2
    }

    override val rootParser: Parser<Type>
        get() = assignment

    override fun toString(): String = variables.toString()
}

val arithmeticOperationMap = mutableMapOf<String, (Num, Num) -> Num>("+" to { a, b -> a + b },
    "-" to { a, b -> a - b },
    "*" to { a, b -> a * b },
    "/" to { a, b -> a / b })

private fun doArithmeticOperation(a: Type, op: TokenMatch, b: Type): Num {
    checkNumeric(a, op.text)
    checkNumeric(b, op.text)
    return arithmeticOperationMap[op.text]!!(a.asNum(), b.asNum())
}

private fun checkNumeric(value: Type, operation: String) {
    if (value.value !is Num) throw GrammarError("$operation is not applicable to $value of type ${value.type()}")
}
