package edu.clemson.dhhs.meds.webservice.webapp

import org.gradle.api.Project
import org.gradle.process.ExecResult

class RhinoExec {
    public static final String RHINO_DEPENDENCY = 'org.mozilla:rhino:1.7R3'
    private static final String RHINO_MAIN_CLASS = 'org.mozilla.javascript.tools.shell.Main'
    Project project

    public RhinoExec(final Project project) {
        this.project = project
    }

    int execute(final Map options = [:], final Iterable<String> execargs) {
        ExecResult result = project.javaexec {
            main = RHINO_MAIN_CLASS
            classpath = project.configurations.webApp
            args = [ "-opt", "9" ] + execargs
            workingDir = options.get('workingDir', '.')
            ignoreExitValue = options.get('ignoreExitCode', true).asBoolean()
            standardOutput = options.get('out', System.out) as OutputStream
            maxHeapSize = options.get('maxHeapSize', null)
        }

        result.exitValue
    }
}
