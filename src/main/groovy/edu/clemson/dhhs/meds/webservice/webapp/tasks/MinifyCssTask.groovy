package edu.clemson.dhhs.meds.webservice.webapp.tasks

import edu.clemson.dhhs.meds.webservice.webapp.CssMinifier
import org.gradle.api.tasks.*

class MinifyCssTask extends SourceTask {
    private static final CssMinifier MINIFIER = new CssMinifier()

    @OutputFile def dest

    File getDest() {
        project.file(dest)
    }

    @TaskAction
    def run() {
        def lineBreakPos = project.webApp.yuicompressor.lineBreakPos
        MINIFIER.minifyCssFile(source.singleFile, dest as File, lineBreakPos)
    }
    
}
