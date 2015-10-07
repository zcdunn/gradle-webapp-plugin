package edu.clemson.dhhs.meds.webservice.webapp

import org.gradle.api.tasks.*
import org.gradle.api.file.*

class AppcacheExtension {
    public static final String NAME = 'appcache'
    @Input String destFile = 'manifest.appcache'
    @Input String js = 'all-min.js'
    @Input String css = 'all-min.css'
    @Input String images = [dir: "images/", excludes: ""]
    @Input String fonts = [dir: "fonts/", excludes: ""]
    @Input List network = [ '*' ]

    def js(def js) { this.js = js }
    def css(def css) { this.css = css }
    def network(List network) { this.network = network }
}
