package edu.clemson.dhhs.meds.webservice.webapp.tasks

import edu.clemson.dhhs.meds.webservice.webapp.RhinoExec
import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.api.file.*
import org.gradle.api.logging.*
import org.gradle.api.internal.project.IsolatedAntBuilder

class JsHintTask extends SourceTask {
    private static final String JSHINT_PATH = 'jshint-rhino-2.4.3.js'
    private final RhinoExec rhino = new RhinoExec(project)

    @InputFiles def inputs = source
    @OutputFile def dest

    @TaskAction
    def run() {
        def jshintExt = project.webApp.jshint
        final File jshintJsFile = extractFileToDirectory(project.file("${project.buildDir}/${project.webApp.webappPluginDir}"), JSHINT_PATH) 
        final List<String> args = [ jshintJsFile.canonicalPath ] + source.files.collect { it.canonicalPath }

        def optionsArgs = makeOptionsArg(jshintExt.options + jshintExt.predef)
        if(optionsArgs)
            args << optionsArgs

        def optionsList = [
            ignoreExitCode: true,
            out: new FileOutputStream(dest as File)
        ]
        def exitValue = rhino.execute(args, optionsList)
        try {
            if(!jshintExt.ignoreExitCode && exitValue != 0)
                throw new ResourceException("There was an error in the javascript source. See ${dest} for more info.")
        }
        finally {
            if(jshintExt.outputToStdout) 
                println project.file(dest).text
        }
    }

    private String makeOptionsArg(LinkedHashMap<String, Object> options) {
        def optionsArg = ""
        if(options) {
            options.each { key, value ->
                optionsArg = (optionsArg == "") ? "${key}=${value}" : "${optionsArg},${key}=${value}"
            }
        }
        return optionsArg
    }

    File extractFileToDirectory(final File targetDirectory, final String resourcePath) {
        if(targetDirectory.exists() && !targetDirectory.isDirectory())
            throw new IllegalArgumentException("Target directory is a file!")
        else if(!targetDirectory.exists())
            targetDirectory.mkdirs()

        final File file = new File(targetDirectory, resourcePath)
        if(!file.exists()) {
            final InputStream inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(resourcePath)
            file << inputStream
            inputStream.close()
        }
        return file
    }
}
