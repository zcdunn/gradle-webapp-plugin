package edu.clemson.dhhs.meds.webservice.webapp

import org.gradle.api.tasks.*
import org.gradle.api.file.*
import com.google.javascript.jscomp.CompilerOptions

class ClosureCompilerExtension {
    public static final NAME = "closure"
    @Input CompilerOptions compilerOptions = new CompilerOptions()
    @Input String compilationLevel = 'SIMPLE_OPTIMIZATIONS'
    @Input String warningLevel = 'QUIET'
    @Input FileCollection externs = null
    @Input List<String> excludes = []

    def excludes(String exclude) { this.excludes << exclude }
    def excludes(List excludes) { this.excludes.addAll(excludes) }
    def excludes(String... excludes) { this.excludes.addAll(excludes) }
}
