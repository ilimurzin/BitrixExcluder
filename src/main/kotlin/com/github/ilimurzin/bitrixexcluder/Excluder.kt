package com.github.ilimurzin.bitrixexcluder

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile

class Excluder(
    private val project: Project,
) {
    fun excludeBitrixDirectories(bitrixDirectory: VirtualFile) {
        if (!bitrixDirectory.isBitrixDirectory()) {
            throw IllegalArgumentException("Passed file is not bitrix directory")
        }

        ModuleRootModificationUtil.updateExcludedFolders(
            getModule(bitrixDirectory),
            getContentRoot(bitrixDirectory),
            emptyList(),
            getUrlsToExclude(bitrixDirectory)
        )
    }

    private fun getModule(virtualFile: VirtualFile): Module {
        return ModuleUtil.findModuleForFile(virtualFile, project)
            ?: throw RuntimeException("Module for $virtualFile not found")
    }

    private fun getContentRoot(virtualFile: VirtualFile): VirtualFile {
        return ProjectRootManager.getInstance(project).fileIndex.getContentRootForFile(virtualFile)
            ?: throw RuntimeException("Content root for $virtualFile is not found")
    }

    private fun getUrlsToExclude(bitrixDirectory: VirtualFile): Collection<String> {
        val parent = getParentOfBitrixDirectory(bitrixDirectory)

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
        )
    }

    private fun getModuleDirectories(bitrixParentDirectory: VirtualFile): List<String> {
        val modulesDirectory = bitrixParentDirectory.findFileByRelativePath("bitrix/modules")

        if (modulesDirectory == null) {
            return emptyList()
        }

        return modulesDirectory.children.filter { it.isDirectory }.map { "bitrix/modules/${it.name}/install" }
    }
}

fun VirtualFile.isBitrixDirectory(): Boolean {
    return isDirectory && name == "bitrix"
}
