package edu.clemson.dhhs.meds.webservice.webapp.tasks

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.api.file.*
import org.gradle.api.logging.*
import org.gradle.api.internal.project.IsolatedAntBuilder

class JsLintTask extends DefaultTask {
    public static final String TASK_NAME = 'jslint'
    public static final String ANT_TASK_NAME = 'com.googlecode.jslint4java.ant.JSLintTask'
    public static final String CLASSPATH = '/WEB-INF/libs/jslint4java-ant-1.4.4.jar'
    public static final String ANT_JAR = 'com.googlecode.jslint4java:jslint4java-ant:1.4.4'

    @OutputFile def dest

    File getDest() {
        project.with {
            file("${buildDir}/${webApp.webappPluginDir}/${webApp.jslint.destFilename}")
        }
    }

    @TaskAction
    def run() {
        logging.level = LogLevel.INFO
        def jslintExt = project.webApp.jslint
        ant.taskdef(name: TASK_NAME, classname: ANT_TASK_NAME, classpath: project.configurations.webApp.asPath)
        ant."$TASK_NAME"(jslintExt.optionsMap) {
            formatter(type: jslintExt.formatterType, destfile: getDest())
            jslintExt.inputDirs.each { dirName ->
                fileset(dir: dirName, includes: '**/*.js', excludes: jslintExt.excludes)
            }
        }
        if(jslintExt.displayErrors) {
            def output = getDest().text
            if(output)
                println output
        }
    }

    def runAntTask(ant, Closure command) {
        def buffer = new ByteArrayOutputStream()
        def captureStream = new PrintStream(buffer, true, "UTF-8")
        def listener = new org.apache.tools.ant.DefaultLogger(
            errorPrintStream: captureStream,
            outputPrintStream: captureStream,
            messageOutputLevel:org.apache.tools.ant.Project.MSG_INFO
        )

        ant.project.addBuildListener(listener)
        project.configure(ant, command)
        ant.project.removeBuildListener(listener)

        return buffer.toString("UTF-8")
    }
}
