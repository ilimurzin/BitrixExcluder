package ru.ilimurzin.bitrixexcluder

import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFile

internal class Startup : StartupActivity.Background {
    override fun runActivity(project: Project) {
        val bitrixDirectory = guessBitrixDirectory(project)

        if (bitrixDirectory != null && !bitrixDirectory.isDirectoriesExcluded()) {
            askToExcludeBitrixDirectories(bitrixDirectory, project)
        }
    }

    private fun guessBitrixDirectory(project: Project): BitrixDirectory? {
        val projectDirectory = getProjectDirectory(project)

        val virtualFile = findBitrixChild(projectDirectory, searchLimit = 2)

        if (virtualFile == null) {
            return null
        }

        return BitrixDirectory(virtualFile, project)
    }

    private fun getProjectDirectory(project: Project): VirtualFile {
        val projectDir = project.guessProjectDir()

        if (projectDir == null || !projectDir.isDirectory) {
            throw RuntimeException("Project directory is not found")
        }

        return projectDir
    }

    private fun findBitrixChild(parentDirectory: VirtualFile, searchLimit: Int): VirtualFile? {
        if (searchLimit < 1) {
            return null
        }

        val virtualFile = parentDirectory.findChild("bitrix")

        if (virtualFile != null && virtualFile.isBitrixDirectory()) {
            return virtualFile
        }

        for (child in parentDirectory.children) {
            val bitrixChild = findBitrixChild(child, searchLimit - 1)

            if (bitrixChild != null) {
                return bitrixChild
            }
        }

        return null
    }

    private fun askToExcludeBitrixDirectories(bitrixDirectory: BitrixDirectory, project: Project) {
        NotificationGroupManager.getInstance().getNotificationGroup("Excluder Notification Group")
            .createNotification(
                "Bitrix directory found",
                buildString {
                    append("Do you want to exclude Bitrix directories? This will speed up indexing.")
                    append("<br><br>Directories that will be excluded: ")
                    val baseDirectoriesToExclude = bitrixDirectory.getBaseDirectoriesToExclude()
                    if (!baseDirectoriesToExclude.isEmpty()) {
                        append(baseDirectoriesToExclude.joinToString() + " and ")
                    }
                    append("modules `install` directories.")
                },
                NotificationType.INFORMATION
            )
            .addAction(NotificationAction.createSimpleExpiring("Exclude Bitrix directories") {
                bitrixDirectory.excludeDirectories()
            })
            .notify(project)
    }
}
