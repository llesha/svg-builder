package grammar.parsed

import Num
import Utils.checkNumeric
import Utils.toType
import grammar.GrammarError
import grammar.Type
import obj.Obj
import kotlin.math.min

interface Parseable {
    fun parse(context: Obj): Type
    fun match(condition: (Type) -> Boolean): List<Type>
}

class ObjDefinition(val path: Path, val assignments: List<Assignment>) : Parseable {
    override fun parse(context: Obj): Type {
        for (a in assignments) {
            a.parse(context)
        }
        return context.toType()
    }

    override fun match(condition: (Type) -> Boolean): List<Type> {
        return assignments.flatMap { it.match(condition) }
    }
}

class Ternary(val condition: Parseable, val first: Parseable, val second: Parseable) : Parseable {
    override fun parse(context: Obj): Type {
        return if (condition.parse(context).asBool()) first.parse(context) else second.parse(context)
    }

    override fun match(condition: (Type) -> Boolean): List<Type> {
        return first.match(condition) + second.match(condition)
    }
}

class Arithmetic(val first: Parseable, val second: Parseable, val op: String) : Parseable {
    override fun parse(context: Obj): Type {
        val parsedFirst = first.parse(context)
        val parsedSecond = second.parse(context)
        checkNumeric(parsedFirst, op)
        checkNumeric(parsedSecond, op)
        return doArithmeticOperation(parsedFirst, op, parsedSecond).toType()
    }

    override fun match(condition: (Type) -> Boolean): List<Type> {
        return first.match(condition) + second.match(condition)
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

class Comparison(val first: Parseable, val second: Parseable, val op: String): Parseable {
    override fun parse(context: Obj): Type {
        val a = first.parse(context).asNum()
        val b = second.parse(context).asNum()
        return when(op) {
            ">" -> a > b
            ">=" -> a >= b
            "<" -> a < b
            "<=" -> a < b
            "==" -> a == b
            "!=" -> a != b
            else -> throw Exception("Comparison operator $op not found")
        }.toType()
    }

    override fun match(condition: (Type) -> Boolean): List<Type> {
        TODO("Not yet implemented")
    }
}

class Assignment(val name: String, val value: Parseable) : Parseable {
    override fun parse(context: Obj): Type {
        val res = value.parse(context)
        context.addProperty(name, res)
        return res
    }

    override fun match(condition: (Type) -> Boolean): List<Type> = value.match(condition)

}

class Path(private val properties: List<String>) : Parseable {
    companion object {
        /**
         * Might be `.`
         */
        var SEP = "/"
    }

    override fun parse(context: Obj): Type {
        var start = context
        for (prop in properties) {
            start = when (prop) {
                "..." -> start.root
                ".." -> start.parent
                else -> start.get(prop)?.asObj() ?: throw GrammarError("property $prop not found in $start")
            }
        }
        return start.toType()
    }

    override fun match(condition: (Type) -> Boolean): List<Type> {
        return listOf()
    }

    fun findByPath(root: Obj): Obj {
        if (properties.isEmpty())
            return root
        var current = root
        for ((i, e) in properties.withIndex()) {
            current = current.get(e)?.asObj() ?: throw GrammarError("No $e in ${repr(i)}")
        }
        return current
    }

    fun repr(lastI: Int): String {
        val res = StringBuilder(SEP)

        for(i in 0 until min(properties.size, lastI)) {
            res.append(properties[i])
            res.append(SEP)
        }

        return res.toString()
    }

    override fun toString(): String {
        return properties.joinToString(SEP)
    }
}
