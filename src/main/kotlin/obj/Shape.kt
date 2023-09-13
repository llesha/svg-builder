package obj

import Properties
import Utils.toType
import grammar.Type

abstract class Obj {
    open val properties: Properties = Properties()

    open fun toSvg(level: Int = 0): String = svgHeader(level) + svgFooter(0)
    protected fun svgHeader(level: Int): String = "${"\t".repeat(level)}<${properties["name"]}${propertiesToString()}>"
    protected fun svgFooter(level: Int): String = "${"\t".repeat(level)}</${properties["name"]}>"

    private fun propertiesToString(): String {
        val props = properties.map.entries.filter { it.key != "name" }
        return (if (props.isEmpty()) "" else " ") + props.joinToString { it.key + "=" + it.value }
    }

    fun props(initializer: Properties.() -> Unit) {
        properties.apply(initializer)
    }
}

open class Shape(vararg args: String) : Obj() {
    override val properties: Properties = Properties(*args)

    companion object {
        fun shape(name: String, initializer: Shape.() -> Unit): Shape {
            val res = Shape()
            res.properties.add("name", name.toType())
            return res.apply(initializer)
        }
    }
}

abstract class PosShape(vararg args: String) : Shape("x", "y", *args)

open class Container(
    val elements: MutableList<Obj> = mutableListOf(),
    vararg args: String
) : Obj() {
    override val properties: Properties = Properties(*args)

    override fun toSvg(level: Int): String {
        val separator = if (elements.isEmpty()) "" else "\n"
        return svgHeader(level) +
            separator +
            elements.joinToString(separator = "\n") { it.toSvg(level + 1) } +
            separator +
            svgFooter(if (elements.isEmpty()) 0 else level)
    }

    fun elements(initializer: MutableList<Obj>.() -> Unit) {
        elements.apply(initializer)
    }

    companion object {
        fun cont(name: String, initializer: Container.() -> Unit): Container {
            val res = Container()
            res.properties.add("name", name.toType())
            return res.apply(initializer)
        }
    }
}

class Conditional(
    elements: MutableList<Obj> = mutableListOf(),
    val conditions: MutableList<Boolean> = mutableListOf()
) : Container(elements)

/**
 * [child] has `prev` attribute for previous element
 */
class Recursive(var amount: Int = 1) : Container() {
    lateinit var child: Obj
}

object Empty : Obj()

enum class Objects {
    EMPTY,
    SHAPE,
    GROUP,
    CONDITIONAL,
    RECURSIVE;

    fun make(): Obj {
        return when (this) {
            EMPTY -> Empty
            SHAPE -> Shape()
            GROUP -> Container()
            CONDITIONAL -> Conditional()
            RECURSIVE -> Recursive()
        }
    }

    companion object {
        fun toObject(string: String): Objects {
            if (string.isEmpty())
                return EMPTY
            else return valueOf(string)
        }
    }
}