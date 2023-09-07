package primitive

import Properties

class Rect: PosShape() {
    override val properties: Properties = Properties("width", "height").addCoords()
}

class Circle: Shape() {
    override val properties = Properties("cx", "cy", "r")
}
