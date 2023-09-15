package obj

import Properties
import Utils.toType
import obj.Container.Companion.cont
import obj.Shape.Companion.shape
import kotlin.test.Test

class ObjTest {
    @Test
    fun testSvgCreation() {
        val root = cont("root") {
            elements {
                add(shape("rect") {
                    props {
                        add("width", 12f.toType())
                    }
                })
                add(cont("inner") {
                    elements { shape("circle") {} }
                })
            }
            props {
                addAll("x", "y")
            }
        }
        println(root.toSvg())
    }
}
