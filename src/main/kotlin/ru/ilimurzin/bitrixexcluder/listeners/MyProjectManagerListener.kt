package ru.ilimurzin.bitrixexcluder.listeners

import ru.ilimurzin.bitrixexcluder.Excluder
import ru.ilimurzin.bitrixexcluder.isBitrixDirectory
import ru.ilimurzin.bitrixexcluder.services.MyProjectService
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.openapi.components.service
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.vfs.VirtualFile

internal class MyProjectManagerListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        project.service<MyProjectService>()

        val bitrixDirectory = guessBitrixDirectory(project)

        if (bitrixDirectory != null && !isBitrixDirectoriesExcluded(bitrixDirectory, project)) {
            askToExcludeBitrixDirectories(bitrixDirectory, project)
        }
    }

    private fun guessBitrixDirectory(project: Project): VirtualFile? {
        val projectDirectory = getProjectDirectory(project)

        return findBitrixChild(projectDirectory, searchLimit = 2)
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

    private fun isBitrixDirectoriesExcluded(bitrixDirectory: VirtualFile, project: Project): Boolean {
        val module = ModuleUtil.findModuleForFile(bitrixDirectory, project)
            ?: throw RuntimeException("Module for $bitrixDirectory not found")

        val urlsToExclude = Excluder(project).getUrlsToExclude(bitrixDirectory)

        module.rootManager.contentEntries.forEach {
            if (it.excludeFolderUrls.containsAll(urlsToExclude)) {
                return true
            }
        }

        return false
    }

    private fun askToExcludeBitrixDirectories(bitrixDirectory: VirtualFile, project: Project) {
        NotificationGroupManager.getInstance().getNotificationGroup("Excluder Notification Group")
            .createNotification(
                "Bitrix directory found",
                "Do you want to exclude Bitrix directories? This will speed up indexing.",
            )
            .addAction(NotificationAction.createSimpleExpiring("Exclude Bitrix directories") {
                Excluder(project).excludeBitrixDirectories(bitrixDirectory)
            })
            .notify(project)
    }
}
