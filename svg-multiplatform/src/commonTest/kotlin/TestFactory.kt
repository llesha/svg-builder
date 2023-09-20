import com.github.h0tk3y.betterParse.grammar.parseToEnd
import grammar.Type
import grammar.VariableParser
import obj.Container

object TestFactory {
    val grammar = VariableParser()
    fun parseAssignment(assignment: String): Type {
        val def = """/:
""" + assignment
        return grammar.parseToEnd(def)[0].parse(Container())
    }
}
