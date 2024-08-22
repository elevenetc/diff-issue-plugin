package com.elevenetc.diffissueplugin

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction


class ToggleLinesActions : ToggleAction("Enable Feature", "Enable or disable a feature", null) {

    private var isSelected = false

    override fun isSelected(e: AnActionEvent): Boolean {
        return isSelected
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        isSelected = state
        // Add your code here to handle the toggle action state change
    }
}