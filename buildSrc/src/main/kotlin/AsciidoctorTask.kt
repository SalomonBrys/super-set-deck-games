import org.asciidoctor.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileType
import org.gradle.api.tasks.*
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

abstract class AsciidoctorTask : DefaultTask() {

    init {
        group = "build"
    }

    private val inputDir = project.projectDir.resolve("games")

    @get:Incremental
    @get:InputFiles
    val inputs = project.fileTree(inputDir) {
        include("**/*.adoc")
    }

    @get:OutputDirectory
    val outputDir = project.buildDir.resolve("asciidoctor")

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        Asciidoctor.Factory.create().use { adoc ->
            inputChanges.getFileChanges(inputs)
                .filterNot { it.fileType == FileType.DIRECTORY }
                .forEach {
                    val output = outputDir.resolve(it.file.relativeTo(inputDir).parentFile.resolve(it.file.nameWithoutExtension + ".inc.html"))
                    output.parentFile.mkdirs()
                    adoc.convertFile(
                        it.file,
                        Options.builder()
                            .safe(SafeMode.UNSAFE)
                            .toFile(output)
                            .headerFooter(false)
                            .attributes(Attributes.builder().attribute("icons", "font").build())
                            .build()
                    )
                }
        }
    }
}
