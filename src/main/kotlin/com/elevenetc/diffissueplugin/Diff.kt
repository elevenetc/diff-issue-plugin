package com.elevenetc.diffissueplugin

import com.intellij.diff.comparison.ComparisonManager
import com.intellij.diff.comparison.ComparisonPolicy
import com.intellij.diff.comparison.DiffTooBigException
import com.intellij.diff.fragments.LineFragment
import com.intellij.openapi.progress.EmptyProgressIndicator

data class Diff(
    val expected: String,
    val actual: String
)

fun buildDiff(
    expected: String, actual: String,
    diffFragments: List<LineFragment>,
    includeLines: Boolean,
    onlyDiff: Boolean
): Diff {
    val expectedSb = StringBuilder()
    val actualSb = StringBuilder()

    if (onlyDiff) {
        diffFragments.forEach { diff ->

            expectedSb.append(toLinedCode(expected, diff, includeLines, { lines ->
                lines.subList(diff.startLine1, diff.endLine1)
            }))

            actualSb.append(toLinedCode(actual, diff, includeLines, { lines ->
                lines.subList(diff.startLine2, diff.endLine2)
            }))
        }
    } else {
        var expectIndex = 1
        var actualIndex = 1
        for (line in expected.lines()) {
            expectedSb.appendLine(buildLine(line, expectIndex, includeLines))
            expectIndex++
        }

        for (line in actual.lines()) {
            actualSb.appendLine(buildLine(line, actualIndex, includeLines))
            actualIndex++
        }
    }
    return Diff(expectedSb.trim().toString(), actualSb.trim().toString())


}

fun buildLine(line: String, index: Int, includeLine: Boolean): String {
    return if (includeLine) "$index: $line" else line
}

fun toLinedCode(
    code: String,
    diff: LineFragment,
    includeLines: Boolean,
    subListOf: (List<String>) -> List<String>
): String {
    val sb = StringBuilder()
    var index = diff.startLine1 + 1 //diff view starts with 1st line
    subListOf(code.lines()).forEach { line ->
        if (line.isNotBlank()) {
            sb.appendLine(buildLine(line, index, includeLines))
        }
        index++
    }
    return sb.toString()
}


fun getDiffFragments(expectText: String, actualText: String): List<LineFragment>? {
    try {
        return ComparisonManager.getInstance().compareLines(
            expectText,
            actualText,
            ComparisonPolicy.DEFAULT,
            EmptyProgressIndicator()
        )
    } catch (e: DiffTooBigException) {
        e.printStackTrace()
        return null
    }
}