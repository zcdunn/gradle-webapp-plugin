package edu.clemson.dhhs.meds.webservice.webapp

import org.gradle.api.tasks.*
import org.gradle.api.file.*

class AppcacheExtension {
    public static final String NAME = 'appcache'
    @Input String destFile = 'manifest.appcache'
    @Input String images = [dir: "images/", excludes: ""]
    @Input String fonts = [dir: "fonts/", excludes: ""]
    @Input List network = [ '*' ]

    def network(List network) { this.network = network }
}
