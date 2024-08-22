package com.elevenetc.diffissueplugin

import java.nio.charset.Charset

private object Plugin {
    val ClassLoader = this::class.java.classLoader
}

fun readFileFromResources(fileName: String): String? {
    return readFileFromResources("", fileName)
}

fun readFileFromResources(path: String, fileName: String): String? {
    val fullPath = if (path.isBlank()) fileName else "$path/$fileName"
    val resource = Plugin.ClassLoader.getResource(fullPath) ?: return null
    val stream = resource.openStream() ?: return null
    val content = stream.readAllBytes() ?: return null
    return content.toString(Charset.defaultCharset())
}