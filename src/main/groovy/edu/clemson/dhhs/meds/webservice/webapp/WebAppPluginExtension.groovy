package edu.clemson.dhhs.meds.webservice.webapp

import org.gradle.api.tasks.*

class WebAppPluginExtension {
    public static final NAME = "webApp"
    public static final EXPLODE_DIR = "explodedWar"
    @Input String defaultEnv = "dev"
    @Input String productionEnv = 'prod'
    @Input String explodeDir = EXPLODE_DIR
    @Input String webappPluginDir = NAME + "Temp"
    @Input String jsFileName = "all-min.js"
    @Input String jsSourceMap = jsFileName + ".map"
    @Input String cssFileName = "all-min.css"
    @Input List expandFiles = [ "**/web.xml" ]
    @Input String jsSourceMappingUrlPrefix = "resources/js/"

    // helper methods to match gradle dsl
    def defaultEnv(String defaultEnv) { this.defaultEnv = defaultEnv }
    def productionEnv(String productionEnv) { this.productionEnv = productionEnv }
    def webappPluginDir(String webappPluginDir) { this.webappPluginDir = webappPluginDir }
    def expandFiles(String expandFiles) { this.expandFiles << expandFiles }
    def expandFiles(String... expandFiles) { this.expandFiles.addAll(expandFiles) }

    def explodeDir(String explodeDir) { this.explodeDir = explodeDir }
    def explode(boolean explode) {
        if(!explode) explodeDir = ""
        else explodeDir = explodeDir ?: EXPLODE_DIR
    }

    def jsFileName(String jsFileName) {
        this.jsFileName = jsFileName
        this.jsSourceMap = jsFileName + ".map"
    }
    def jsSourceMap(String jsSourceMap) { this.jsSourceMap = jsSourceMap }
    def cssFileName(String cssFileName) { this.cssFileName = cssFileName }
    def jsSourceMappingUrlPrefix(String jsSourceMappingUrlPrefix) { this.jsSourceMappingUrlPrefix = jsSourceMappingUrlPrefix }
}
