package com.ilynehdev.core.network.plants.util


fun readResource(path: String): String =
    checkNotNull(Thread.currentThread().contextClassLoader.getResource(path)) {
        "Resource not found: $path"
    }.readText()

