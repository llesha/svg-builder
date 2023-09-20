package grammar

import Num
import Utils.checkNumeric
import Utils.rnd
import Utils.toNum
import Utils.toType
import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import grammar.parsed.Arithmetic
import grammar.parsed.Assignment
import grammar.parsed.Comparison
import grammar.parsed.ObjDefinition
import grammar.parsed.Parseable
import grammar.parsed.Path
import grammar.parsed.Ternary
import obj.Empty
import obj.Obj
import obj.Objects
import kotlin.math.roundToInt

val slash = '\\'

class VariableParser() : Grammar<List<ObjDefinition>>() {
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
    private val geqT by literalToken(">=")
    private val leqT by literalToken("<=")
    private val equalT by literalToken("==")
    private val unequalT by literalToken("!=")

    private val mul by literalToken("*")
    private val div by literalToken("/")
    private val pathSep by literalToken(Path.SEP)
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
    private val path: Parser<Parseable> by -pathSep and separatedTerms(ident or rangeT or doubleRangeT, pathSep) map {
        Path(it.map { e -> e.text })
    }

    @Suppress("unused")
    private val ws by regexToken("[\t ]", ignore = true)

    @Suppress("unused")
    private val sep by regexToken(
        """\\
""", ignore = true
    )

    private val variableSep by regexToken("\n+")

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

    private val term: Parser<Parseable> by path or
        obj or
        (optional(minus) and notation map {
            checkNumeric(it.t2, "unary minus")
            if (it.t1 != null) (-(it.t2.asNum())).toType()
            else it.t2
        }) or // this is strange, `or` doesn't work
        (-leftPar and parser(::arithmeticExpression) and -rightPar map { it.toType() }) or
        (-leftPar and parser(::ternary) and -rightPar map { it.toType() })

    private val divMulChain: Parser<Parseable> by leftAssociative(
        term, div or mul
    ) { a, op, b -> Arithmetic(a, b, op.text) }

    private val arithmeticExpression: Parser<Parseable> by leftAssociative(
        divMulChain, plus or minus
    ) { a, op, b -> Arithmetic(a, b, op.text) }

    private val comparisonExpression: Parser<Parseable> by leftAssociative(
        arithmeticExpression, smallerT or biggerT or leqT or geqT
    ) { a, op, b -> Comparison(a, b, op.text) }

    private val equalityExpression: Parser<Parseable> by leftAssociative(
        comparisonExpression, equalT or unequalT
    ) { a, op, b -> Comparison(a, b, op.text) }

    private val ternary: Parser<Parseable> by (arithmeticExpression or parser(::ternary)) and
        -questionT and (arithmeticExpression or parser(::ternary)) and
        -colonT and (arithmeticExpression or parser(::ternary)) map {
        Ternary(it.t1, it.t2, it.t3)
    }

    private val assignment: Parser<Assignment> by ident and -assignmentT and (ternary or arithmeticExpression) map {
        Assignment(it.t1.text, it.t2)
    }

    private val block: Parser<ObjDefinition> by path and colonT and -optional(variableSep) and
        separatedTerms(assignment, variableSep) and
        -optional(variableSep) map { ObjDefinition(Path(listOf()), listOf()) }

    override val rootParser: Parser<List<ObjDefinition>> by -optional(variableSep) and
        separatedTerms(block, variableSep) and
        -optional(variableSep)

    override fun toString(): String = variables.toString()
}