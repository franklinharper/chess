package com.franklinharper.chess

import kotlin.jvm.JvmInline

@JvmInline
value class Coordinate(private val i: Int) {
    init {
        require(i in 0..7) { "Chess board coordinate must be between 0 and 7" }
    }
}
