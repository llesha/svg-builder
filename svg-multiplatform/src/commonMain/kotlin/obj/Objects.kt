package obj

import grammar.GrammarError

enum class Objects {
    EMPTY,
    SHAPE,
    GROUP,
    RECURSIVE;

    fun make(): Obj {
        return when (this) {
            EMPTY -> Empty
            SHAPE -> Shape()
            GROUP -> Container()
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