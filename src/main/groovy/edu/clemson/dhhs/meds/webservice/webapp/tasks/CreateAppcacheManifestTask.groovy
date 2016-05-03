package edu.clemson.dhhs.meds.webservice.webapp

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.api.file.*

class CreateAppcacheManifestTask extends DefaultTask {
    final String explodeDir = "${project.buildDir}/${project.webApp.explodeDir}"

    @Input def js = project.files("${explodeDir}/js/${project.webApp.jsFileName}")
    @Input def css = project.files("${explodeDir}/css/${project.webApp.cssFileName}")
    @Input def sourceMap = project.files("${explodeDir}/js/${project.webApp.jsSourceMap}")
    @Input def images = project.fileTree("${explodeDir}/images/")
    @Input def fonts = project.fileTree("${explodeDir}/fonts/")
    @Input def network = project.webApp.appcache.network

    @OutputFile def dest = project.file("${project.buildDir}/${project.webApp.webappPluginDir}/${project.webApp.appcache.destFile}")

    @TaskAction
    def run() {
        def date = new Date()
        def cacheFiles = project.files(js, css, sourceMap, images, fonts)

        dest.text = """CACHE MANIFEST
# ${date}

CACHE:
"""
        cacheFiles.each {
            def relPath = (it =~ /^.*\/([a-zA-Z]*\/.*)$/)[0][1]
            dest << "${relPath}\n"
        }

        dest << "\nNETWORK:\n"
        network.each { dest << "${it}" }
    }
}
