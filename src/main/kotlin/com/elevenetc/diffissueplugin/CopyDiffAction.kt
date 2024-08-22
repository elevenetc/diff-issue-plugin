package com.elevenetc.diffissueplugin

import com.intellij.diff.contents.FileDocumentContentImpl
import com.intellij.diff.requests.ContentDiffRequest
import com.intellij.diff.tools.util.DiffDataKeys
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection


class CopyDiffAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val diffRequest = e.getData(DiffDataKeys.DIFF_REQUEST)


        if (diffRequest is ContentDiffRequest) {

            if (e.actionManager.getAction("diff.issue.copy.actual") == this) {
                copyToClipboard(
                    code = (diffRequest.contents[0] as? FileDocumentContentImpl)?.document?.text ?: "",
                    project = e.project,
                    actual = true
                )
            } else {
                copyToClipboard(
                    code = (diffRequest.contents[1] as? FileDocumentContentImpl)?.document?.text ?: "",
                    project = e.project,
                    actual = false
                )
            }
        }
    }

    private fun copyToClipboard(
        code: String,
        project: Project?,
        actual: Boolean
    ) {
        val toolkit = Toolkit.getDefaultToolkit()
        val clipboard = toolkit.systemClipboard
        val stringSelection = StringSelection(code)
        clipboard.setContents(stringSelection, null)

        val notification = Notification(
            "diff.issue.copy",
            "Diff",
            "${if (actual) "Actual" else "Expected"} copied to clipboard",
            NotificationType.INFORMATION
        )

        Notifications.Bus.notify(notification, project)
    }
}