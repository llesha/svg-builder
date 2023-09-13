package grammar

class GrammarError(override val message: String?) : Throwable()

class TypeError(type: String, expected: String): Throwable()
{
    override val message: String = "Expected $expected, got $type"
}
