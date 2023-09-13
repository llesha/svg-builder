import Utils.defaultNum
import Utils.toType
import grammar.Type

class Properties(vararg args: String) {
    val map: MutableMap<String, Type> = mutableMapOf("name" to "n".toType())

    init {
        addAllFromArray(args)
    }

    fun addCoords(): Properties {
        if (!map.containsKey("x"))
            map["x"] = defaultNum().toType()
        if (!map.containsKey("y"))
            map["y"] = defaultNum().toType()
        return this
    }

    operator fun get(key: String): Type? {
        return map[key]
    }

    fun add(key: String, value: Type = defaultNum().toType()) {
        map[key] = value
    }

    fun addAll(vararg props: String) {
        addAllFromArray(props)
    }

    private fun addAllFromArray(array: Array<out String>) {
        map.putAll(array.toList().associateWith { defaultNum().toType() }.toMutableMap())
    }
}
