package grammar

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.st.LiftToSyntaxTreeOptions
import com.github.h0tk3y.betterParse.st.SyntaxTree
import com.github.h0tk3y.betterParse.st.liftToSyntaxTreeGrammar
import com.github.h0tk3y.betterParse.st.liftToSyntaxTreeParser
import grammar.parsed.ObjDefinition
import grammar.parsed.Path
import obj.Container
import obj.Obj

class SvgParser {
    val root = Container()
    val parser = VariableParser()
    fun parseDfs(rootConfig: String) {
        val properties = VariableParser().liftToSyntaxTreeParser(
            LiftToSyntaxTreeOptions(
                retainSkipped = false,
                retainSeparators = false
            )
        )
    }

    fun parseBfs(rootConfig: String, element: Obj) {
        val parsed = parse(rootConfig)
        val syntaxTree = parsed.children
        val assignments = parsed.item
        for ((i, assignment) in assignments.withIndex()) {
            try {
                val variable = assignment.parse(element)
                if (variable.value is Obj) {

                }
            } catch (e: Exception) {
                throw ParseError(e.message!!, syntaxTree[i].range)
            }
        }
    }

    private fun parse(code: String): SyntaxTree<List<ObjDefinition>> {
        return parser.liftToSyntaxTreeGrammar(LiftToSyntaxTreeOptions(retainSeparators = false)).parseToEnd(code)
    }

    fun makeTree(code: String): Container {
        val tree = parse(code)
        for (def in tree.item) {
            val current = def.path.findByPath(root)
            for (a in def.assignments) {
                val objects = a.match { it.value is Obj }
                objects.withIndex().forEach { current.addProperty(a.name + Path.SEP + it.index, it.value) }
            }
        }
        return root
    }

    class ParseError(override val message: String, val errorRange: IntRange) : Exception()
}

/*
update tree structure button: add all current nodes

[...]
 |-length
 [group]
  |-ab
 */
