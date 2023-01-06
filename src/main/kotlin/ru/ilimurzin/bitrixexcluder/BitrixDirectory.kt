package ru.ilimurzin.bitrixexcluder

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile

class BitrixDirectory(
    private val virtualFile: VirtualFile,
    private val project: Project,
) {
    init {
        require(virtualFile.isBitrixDirectory()) { "Passed file is not bitrix directory" }
    }

    fun excludeDirectories() {
        ModuleRootModificationUtil.updateExcludedFolders(
            getModule(),
            getContentRoot(),
            emptyList(),
            getUrlsToExclude()
        )
    }

    private fun getModule(): Module {
        return ModuleUtil.findModuleForFile(virtualFile, project)
            ?: throw RuntimeException("Module for $virtualFile not found")
    }

    private fun getContentRoot(): VirtualFile {
        return ProjectRootManager.getInstance(project).fileIndex.getContentRootForFile(virtualFile)
            ?: throw RuntimeException("Content root for $virtualFile is not found")
    }

    private fun getUrlsToExclude(): Collection<String> {
        val parent = getParentOfBitrixDirectory(virtualFile)

        val directoriesToExclude = getBaseDirectories() + getModuleDirectories(parent)

        return directoriesToExclude.mapNotNull { parent.findFileByRelativePath(it)?.url }
    }

    private fun getParentOfBitrixDirectory(bitrixDirectory: VirtualFile): VirtualFile {
        return bitrixDirectory.parent
            ?: throw RuntimeException("Bitrix directory is a root directory. This is not supported.")
    }

    private fun getBaseDirectories(): List<String> {
        return listOf(
            "upload",
            "bitrix/cache",
            "bitrix/managed_cache",
            "bitrix/stack_cache",
            "bitrix/tmp",
            "bitrix/updates",
        )
    }

    private fun getModuleDirectories(bitrixParentDirectory: VirtualFile): List<String> {
        val modulesDirectory = bitrixParentDirectory.findFileByRelativePath("bitrix/modules")

        if (modulesDirectory == null) {
            return emptyList()
        }

        return modulesDirectory.children.filter { it.isDirectory }.map { "bitrix/modules/${it.name}/install" }
    }

    fun isDirectoriesExcluded(): Boolean {
        val module = ModuleUtil.findModuleForFile(virtualFile, project)
            ?: throw RuntimeException("Module for $virtualFile not found")

        val urlsToExclude = getUrlsToExclude()

        module.rootManager.contentEntries.forEach {
            if (it.excludeFolderUrls.containsAll(urlsToExclude)) {
                return true
            }
        }

        return false
    }
}

fun VirtualFile.isBitrixDirectory(): Boolean {
    return isDirectory && name == "bitrix" && children.isNotEmpty()
}
