package obj

import grammar.GrammarError

enum class Objects {
    EMPTY,
    SHAPE,
    GROUP,
    CONDITIONAL,
    RECURSIVE;

    fun make(current: Obj): Obj {
        if(current !is Container)
            throw GrammarError("Other objects are allowed only inside containers, $current is not a container")
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
            else try {
                return valueOf(string)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Expected one of [], ${Objects.values().joinToString { "[${it}]" }}")
            }
        }
    }
}