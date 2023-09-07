import MathUtils.defaultNum

/**
 * Probably use it instead of Any for Text and Num
 */
class Property

class Properties(vararg args: String) {
    val map: MutableMap<String, Any>

    init {
        map = (args.toList().associateWith { defaultNum() }.toMutableMap())
    }

    fun addCoords(): Properties {
        map["x"] = 0
        map["y"] = 0
        return this
    }
}
