package edu.clemson.dhhs.meds.webservice.webapp

import org.gradle.api.tasks.*

class JsHintExtension {
    public static final String NAME = 'jshint'
    @Input LinkedHashMap<String, Object> options = []
    @Input LinkedHashMap<String, Boolean> predef = []
    @Input boolean ignoreExitCode = false
    @Input boolean outputToStdout = true
    @Input String destFile = NAME + ".txt"
    @Input List<String> excludes = []

    def options(Map options) { this.options << options }
    def predef(Map predef) { this.predef << predef }
    def ignoreExitCode(boolean ignoreExitCode) { this.ignoreExitCode = ignoreExitCode }
    def outputToStdout(boolean outputToStdout) { this.outputToStdout = outputToStdout }
    def destFile(def destFile) { this.destFile = destFile }

    def excludes(String excludes) { this.excludes << excludes }
    def excludes(List excludes) { this.excludes + excludes }
}
