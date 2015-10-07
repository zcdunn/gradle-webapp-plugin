package edu.clemson.dhhs.meds.webservice.webapp.tasks

import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.SourceTask

class CombineJsTask extends SourceTask {
    @OutputFile def dest

    @Input encoding = System.properties['file.encoding']

    File getDest() {
        project.file(dest)
    }

    @TaskAction
    def run() {
        ant.concat(destfile: (dest as File).canonicalPath, fixlastline: 'yes', encoding: encoding) {
            source.files.each { fileset(file: it) }
        }
    }
}
