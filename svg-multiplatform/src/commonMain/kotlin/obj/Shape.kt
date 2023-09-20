package obj

import Properties
import Properties.Companion.NAME
import Utils.toType
import grammar.GrammarError
import grammar.Type
import kotlin.reflect.typeOf

abstract class Obj {
    protected open val properties: Properties = Properties()

    val parent: Obj
        get() = properties["parent"]?.asObj() ?: throw GrammarError("root object has no parent")

    val root: Container
        get() {
            var parent = this
            while(parent.properties.map.containsKey("parent"))
                parent = parent.properties["parent"]!!.asObj()
            return parent as Container
        }

    open fun toSvg(level: Int = 0): String = svgHeader(level) + svgFooter(0)
    protected fun svgHeader(level: Int): String = "${"\t".repeat(level)}<${properties[NAME]}${propertiesToString()}>"
    protected fun svgFooter(level: Int): String = "${"\t".repeat(level)}</${properties[NAME]}>"

    private fun propertiesToString(): String {
        val props = properties.map.entries.filter { it.key != NAME }
        return (if (props.isEmpty()) "" else " ") + props.joinToString { it.key + "=" + it.value }
    }

    fun props(initializer: Properties.() -> Unit) {
        properties.apply(initializer)
    }

    fun addProperty(name:String, value: Type) {
        properties.add(name, value)
    }

    fun has(name: String): Boolean = properties.map.containsKey(name)
    fun get(name:String) = properties[name]
    fun clear() = properties.map.clear()
}

open class Shape(vararg args: String) : Obj() {
    override val properties: Properties = Properties(*args)

    companion object {
        fun shape(name: String, initializer: Shape.() -> Unit): Shape {
            val res = Shape()
            res.properties.add(NAME, name.toType())
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
            res.properties.add(NAME, name.toType())
            return res.apply(initializer)
        }
    }
}

/**
 * [child] has `prev` attribute for previous element
 */
class Recursive(var amount: Int = 1) : Container() {
    lateinit var child: Obj
}

object Empty : Obj()

class ContainerGroup(val containers: List<Container>): Obj()