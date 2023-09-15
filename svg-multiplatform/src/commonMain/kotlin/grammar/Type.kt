package grammar

import Num
import Utils.type
import grammar.parsed.Parseable
import obj.Empty
import obj.Obj

class Type(val value: Any): Parseable {
    fun asNum(): Num {
        return when (value) {
            is Num -> value
            is Boolean -> if (value) Utils.defaultNum() else Utils.oneNum()
            else -> throw TypeError(value.type(), "num")
        }
    }

    fun asBool(): Boolean {
        return when (value) {
            is Boolean -> value
            is Num -> value != Utils.defaultNum()
            is Obj -> if(value is Empty) false else throw TypeError(value.type(), "bool")
            else -> throw TypeError(value.type(), "bool")
        }
    }

    fun asStr(): String {
        if (value is String) return value
        throw TypeError(value.type(), "str")
    }

    fun asObj(): Obj {
        if (value is Obj) return value
        throw TypeError(value.type(), "obj")
    }

    override fun toString(): String = value.toString()

    override fun hashCode(): Int = value.hashCode()
    override fun parse(context: Obj): Type {
        return this
    }

    override fun equals(other: Any?): Boolean {
        if(other !is Type)
            return false
        return value == other.value
    }
}