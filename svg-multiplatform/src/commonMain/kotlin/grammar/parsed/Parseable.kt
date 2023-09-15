package grammar.parsed

import Num
import Utils.checkNumeric
import Utils.toType
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import grammar.GrammarError
import grammar.Type
import obj.Obj

interface Parseable {
    fun parse(context: Obj): Type
}

class Ternary(val condition: Parseable, val first: Parseable, val second: Parseable) : Parseable {
    override fun parse(context: Obj): Type {
        return if (condition.parse(context).asBool()) first.parse(context) else second.parse(context)
    }
}

class Arithmetic(val first: Parseable, val second: Parseable, val op: String): Parseable {
    override fun parse(context: Obj): Type {
        val parsedFirst = first.parse(context)
        val parsedSecond = second.parse(context)
        checkNumeric(parsedFirst, op)
        checkNumeric(parsedSecond, op)
        return doArithmeticOperation(parsedFirst, op, parsedSecond).toType()
    }

    companion object {
        val arithmeticOperationMap = mutableMapOf<String, (Num, Num) -> Num>(
            "+" to { a, b -> a + b },
            "-" to { a, b -> a - b },
            "*" to { a, b -> a * b },
            "/" to { a, b -> a / b })
    }
    private fun doArithmeticOperation(a: Type, op: String, b: Type): Num {
        checkNumeric(a, op)
        checkNumeric(b, op)
        return arithmeticOperationMap[op]!!(a.asNum(), b.asNum())
    }

}

class Assignment(val name: String, val value: Parseable) : Parseable {
    override fun parse(context: Obj): Type {
        val res = value.parse(context)
        context.properties.add(name, res)
        return res
    }
}

class Path(private val properties: List<String>) : Parseable {
    override fun parse(context: Obj): Type {
        var start = context
        for (prop in properties) {
            start = when (prop) {
                "..." -> start.root
                ".." -> start.parent
                else -> start.properties[prop]?.asObj() ?: throw GrammarError("property $prop not found in $start")
            }
        }
        return start.toType()
    }
}
