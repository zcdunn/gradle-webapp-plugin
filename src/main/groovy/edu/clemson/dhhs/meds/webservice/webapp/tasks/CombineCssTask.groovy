package edu.clemson.dhhs.meds.webservice.webapp.tasks

import org.gradle.api.tasks.*

class CombineCssTask extends SourceTask {
    @OutputFile def dest

    File getDest() {
        project.file(dest)
    }
    
    @TaskAction
    def run() {
        ant.concat(destfile: (dest as File).canonicalPath, fixlastline: 'yes') {
            source.files.each {
                fileset(file: it)
            }
        }
    }
    
}
