package com.github.ilimurzin.bitrixexcluder.services

import com.intellij.openapi.project.Project
import com.github.ilimurzin.bitrixexcluder.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
