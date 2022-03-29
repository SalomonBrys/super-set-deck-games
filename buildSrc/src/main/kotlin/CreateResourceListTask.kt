import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File


abstract class CreateResourceListTask : DefaultTask() {

    init {
        group = "build"
    }

    @get:Input
    val dirs = ArrayList<File>()

    @get:InputFiles
    val files get() = dirs.map { project.fileTree(it) as FileTree } .reduce { l, r -> l + r }

    @get:OutputFile
    val output = project.buildDir.resolve("resourcesList/resources.txt")

    @ExperimentalStdlibApi
    @TaskAction
    fun run() {
        output.parentFile.mkdirs()
        val files = dirs
            .flatMap { dir ->
                project.fileTree(dir).map { it.relativeTo(dir) }
            }
            .toSet()
            .minus(File("index.html"))
            .plus(File("${project.name}.js"))
            .plus(File("${project.name}.js.map"))
            .sorted()
        output.writeText(files.joinToString("\n"))
    }

}
