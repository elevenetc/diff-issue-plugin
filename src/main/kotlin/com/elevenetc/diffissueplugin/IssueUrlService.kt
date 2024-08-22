package com.elevenetc.diffissueplugin

import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private const val CreateIssueUrlFallback =
    "https://youtrack.jetbrains.com/newIssue?summary=[TITLE]&description=[DESCRIPTION]"

fun createIssue(title: String, diff: Diff) {
    val publishUrl = readFileFromResources("issue-tracker.config") ?: CreateIssueUrlFallback
    val description = diff.toHtml()
    val url = publishUrl
        .replace("[TITLE]", title)
        .replace("[DESCRIPTION]", description)
    openUrlWithBrowser(url)
}

private fun openUrlWithBrowser(url: String) {
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        Desktop.getDesktop().browse(URI(url))
    }
}

fun Diff.toHtml(): String {
    val expectedDecoded = URLEncoder.encode(this.expected, StandardCharsets.UTF_8)
    val actualDecoded = URLEncoder.encode(this.actual, StandardCharsets.UTF_8)
    val codeWrap = URLEncoder.encode("```", StandardCharsets.UTF_8)
    val newLine = "%0A"
    val exp = "Expected$newLine$codeWrap$newLine$expectedDecoded$newLine$codeWrap"
    val act = "Actual$newLine$codeWrap$newLine$actualDecoded$newLine$codeWrap"
    return "$exp%0A$act"
}