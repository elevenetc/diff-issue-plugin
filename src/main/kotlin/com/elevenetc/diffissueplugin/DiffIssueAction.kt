package com.elevenetc.diffissueplugin

import com.intellij.diff.contents.FileDocumentContentImpl
import com.intellij.diff.requests.ContentDiffRequest
import com.intellij.diff.tools.util.DiffDataKeys
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class DiffIssueAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val diffRequest = e.getData(DiffDataKeys.DIFF_REQUEST)
        if (diffRequest is ContentDiffRequest) {

            val leftContent = (diffRequest.contents[0] as? FileDocumentContentImpl)?.document?.text ?: ""
            val rightContent = (diffRequest.contents[1] as? FileDocumentContentImpl)?.document?.text ?: ""

            PublishIssueDialog(
                expectText = leftContent,
                actualText = rightContent,
                publishIssue = { title, description ->
                    createIssue(title, description)

                }
            ).show()
        }
    }
}

