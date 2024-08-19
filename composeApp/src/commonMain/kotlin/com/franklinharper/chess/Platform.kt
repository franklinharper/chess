package com.franklinharper.chess

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform