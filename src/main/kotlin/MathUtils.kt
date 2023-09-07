import kotlin.math.roundToInt
import kotlin.random.Random

typealias Num = Float

object MathUtils {
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

    fun Int.toNum() = this.toFloat()
    fun Double.toNum() = this.toFloat()
}
