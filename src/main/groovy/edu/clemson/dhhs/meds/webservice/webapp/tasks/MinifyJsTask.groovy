package edu.clemson.dhhs.meds.webservice.webapp.tasks

import edu.clemson.dhhs.meds.webservice.webapp.JsMinifier
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.Optional

class MinifyJsTask extends SourceTask {
    private static final JsMinifier MINIFIER = new JsMinifier()

    @OutputFile def dest
    @Optional @OutputFile def sourceMap

    File getDest() {
        project.file(dest)
    }

    @TaskAction
    def run() {
        def closureCompiler = project.webApp.closure
        Set<File> externsFiles = closureCompiler.externs ? closureCompiler.externs.files : [] as Set<File>
        MINIFIER.minifyJsFile(source.files, externsFiles, dest as File, sourceMap as File,
                              closureCompiler.compilerOptions, closureCompiler.warningLevel, closureCompiler.compilationLevel)
    }
}
