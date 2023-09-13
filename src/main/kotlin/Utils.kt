import grammar.Type
import obj.Obj
import kotlin.math.roundToInt
import kotlin.random.Random

typealias Num = Float

object Utils {
    /**
     * SEED for [rnd]
     */
    var SEED = 1242352
    val rnd = Random(SEED)

    fun String.toNum(): Num {
        return this.toFloat()
    }

    fun Num.isInt(): Boolean = this.roundToInt().toNum() == this
    fun defaultNum(): Num = 0.0f
    fun oneNum(): Num = 1.0f

    fun Int.toNum() = this.toFloat()
    fun Double.toNum() = this.toFloat()

    fun Any.type(): String {
        return when (this) {
            is Type -> value.type()
            is Boolean -> "bool"
            is Num -> "num"
            is String -> "str"
            is Obj -> "obj"
            else -> throw RuntimeException("Unexpected type: ${this::class}")
        }
    }

    fun Any.toType(): Type {
        if (this is Type)
            return this
        return Type(this)
    }
}
