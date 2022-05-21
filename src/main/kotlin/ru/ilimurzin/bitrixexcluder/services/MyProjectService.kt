package ru.ilimurzin.bitrixexcluder.services

import com.intellij.openapi.project.Project
import ru.ilimurzin.bitrixexcluder.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
