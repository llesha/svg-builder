package primitive

import Properties

abstract class Shape {
    open val properties: Properties = Properties()
}

abstract class PosShape : Shape() {
    override val properties: Properties = Properties().addCoords()
}