package grammar

import com.github.h0tk3y.betterParse.st.LiftToSyntaxTreeOptions
import com.github.h0tk3y.betterParse.st.liftToSyntaxTreeParser
import obj.Container

class Parser {
    val root = Container()
    fun parseDfs(rootConfig: String) {
        val properties = VariableParser(root).liftToSyntaxTreeParser(
            LiftToSyntaxTreeOptions(
                retainSkipped = false,
                retainSeparators = false
            )
        )
    }

    fun parseBfs(rootConfig: String) {
    }
}