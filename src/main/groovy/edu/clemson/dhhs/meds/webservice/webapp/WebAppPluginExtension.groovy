package edu.clemson.dhhs.meds.webservice.webapp

import org.gradle.api.tasks.*

class WebAppPluginExtension {
    public static final NAME = "webApp"
    @Input String defaultEnv = "dev"
    @Input String productionEnv = 'prod'
    @Input String configFileName = "envConfig.groovy"
    @Input String explodeDir = "explodedWar"
    @Input String webappPluginDir = "webappTemp"
    @Input String jsFileName = "all-min.js"
    @Input String cssFileName = "all-min.css"
    @Input boolean environmentalConfig = true
    @Input boolean explode = true
    @Input def expandFiles = "**/*.xml"

    // helper methods to match gradle dsl
    def defaultEnv(String defaultEnv) { this.defaultEnv = defaultEnv }
    def productionEnv(String productionEnv) { this.productionEnv = productionEnv }
    def configFileName(String configFileName) { this.configFileName = configFileName }
    def explodeDir(String explodeDir) { this.explodeDir = explodeDir }
    def webappPluginDir(String webappPluginDir) { this.webappPluginDir = webappPluginDir }
    def jsFileName(String jsFileName) { this.jsFileName = jsFileName }
    def cssFileName(String cssFileName) { this.cssFileName = cssFileName }
    def environmentalConfig(boolean environmentalConfig) { this.environmentalConfig = environmentalConfig }
    def explode(boolean explode) { this.explode = explode }
    def expandFiles(def expandFiles) { this.expandFiles = expandFiles }
}
