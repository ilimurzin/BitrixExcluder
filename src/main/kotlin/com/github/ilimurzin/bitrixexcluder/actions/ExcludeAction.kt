package com.github.ilimurzin.bitrixexcluder.actions

import com.github.ilimurzin.bitrixexcluder.Excluder
import com.github.ilimurzin.bitrixexcluder.isBitrixDirectory
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile

class ExcludeAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val project = getProject(e)
        val selectedElement = getSelectedElement(e)

        if (selectedElement.isBitrixDirectory()) {
            Excluder(project).excludeBitrixDirectories(selectedElement)
        } else if (isParentOfBitrixDirectory(selectedElement)) {
            Excluder(project).excludeBitrixDirectories(selectedElement.findChild("bitrix")!!)
        } else {
            Messages.showMessageDialog(
                project,
                "Selected directory does not contain `bitrix` directory",
                "Failed to Exclude",
                Messages.getInformationIcon()
            )
        }
    }

    private fun getProject(e: AnActionEvent): Project {
        val project = e.project

        if (project == null) {
            throw RuntimeException("Project not found at context")
        }

        return project
    }

    private fun getSelectedElement(e: AnActionEvent): VirtualFile {
        val selectedElement = e.getData(CommonDataKeys.VIRTUAL_FILE)

        if (selectedElement == null) {
            throw RuntimeException("No directory selected")
        }

        if (!selectedElement.isDirectory) {
            throw RuntimeException("Selected element is not a directory")
        }

        return selectedElement
    }

    private fun isParentOfBitrixDirectory(virtualFile: VirtualFile): Boolean {
        val childVirtualFile = virtualFile.findChild("bitrix")

        if (childVirtualFile == null) {
            return false
        }

        return childVirtualFile.isBitrixDirectory()
    }
}
