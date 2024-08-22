package com.elevenetc.diffissueplugin

import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.event.ActionListener
import javax.swing.*

class PublishIssueDialog(
    val expectText: String,
    val actualText: String,
    val publishIssue: (title: String, diff: Diff) -> Unit
) : DialogWrapper(true) {

    val diffFragments = getDiffFragments(expectText, actualText) ?: emptyList()
    var resultDiff: Diff? = null

    init {
        title = "Create new Issue from Diff"

        init()
    }

    override fun createActions(): Array<Action> {
        return arrayOf(okAction, cancelAction)
    }

    override fun doOKAction() {
        resultDiff?.run {
            publishIssue("", this)
        }

        super.doOKAction()
    }

    override fun createCancelAction(): ActionListener? {
        return null
    }

    override fun createCenterPanel(): JComponent {

        var includeLines = true//from cache
        var onlyDiff = true
        var html = ""
        val diffLabel = JLabel("")

        fun rebuildHtml() {
            val diff = buildDiff(expectText, actualText, diffFragments, includeLines, onlyDiff)
            html = buildHtml(diff.expected, diff.actual)
            resultDiff = diff
            diffLabel.text = html
        }

        rebuildHtml()


        val panel = JPanel(BorderLayout())

        val includeLinesCheckbox = JCheckBox("Include lines").apply {
            isSelected = includeLines
            addActionListener {
                includeLines = isSelected
                rebuildHtml()
                diffLabel.text = html
            }
        }

        val onlyDiffCheckbox = JCheckBox("Only diff").apply {
            isSelected = onlyDiff
            addActionListener {
                onlyDiff = isSelected
                rebuildHtml()
            }
        }

        val checkBoxPanel = JPanel()
        checkBoxPanel.layout = BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS) // Arrange components vertically
        checkBoxPanel.add(includeLinesCheckbox)
        checkBoxPanel.add(onlyDiffCheckbox)

        panel.add(checkBoxPanel, BorderLayout.SOUTH)
        panel.add(diffLabel, BorderLayout.CENTER)
        return panel
    }
}

private fun buildHtml(expected: String, actual: String): String {
    val htmlBuilder = StringBuilder("<html><body>")
    htmlBuilder.append("<p style='color: green;'>Expected</p>")
    htmlBuilder.append("<div>")
    htmlBuilder.append(expected.colorCodeLines())
    htmlBuilder.append("<div>")

    htmlBuilder.append("<p style='color: red;'>Actual</p>")
    htmlBuilder.append("<div>")
    htmlBuilder.append(actual.colorCodeLines())
    htmlBuilder.append("<div>")
    htmlBuilder.append("</body></html>")
    return htmlBuilder.toString()
}

private fun String.colorCodeLines(): String {
    return this.lines().joinToString("<br>") { line ->
        line.replace(Regex("""^(\d+:\s)""")) { matchResult ->
            "<span style=\"color:black;\">${matchResult.value}</span>"
        }
    }
}