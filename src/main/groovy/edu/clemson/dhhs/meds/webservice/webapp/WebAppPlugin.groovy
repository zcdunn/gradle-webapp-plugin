package edu.clemson.dhhs.meds.webservice.webapp

import edu.clemson.dhhs.meds.webservice.webapp.tasks.*
import org.gradle.api.*
import org.gradle.api.plugins.*
import org.gradle.api.artifacts.*
import org.gradle.api.tasks.*
import org.gradle.api.file.*

/*
 * Maybe add jsp compilation 
 * https://discuss.gradle.org/t/can-i-compile-jsp-using-gradle/3523
 */

class WebAppPlugin implements Plugin<Project> {
    private Project project

    void apply(Project project) {
        this.project = project
        configureDependencies()
        setup()
        project.afterEvaluate(configure)
        project.afterEvaluate { proj ->
            if(proj.webApp.environmentalConfig && proj.ext.environment == proj.webApp.productionEnv)
                configureForProd()
        }
    }

    void setup() {
        project.with {
            apply(plugin: 'war')

            task('explodeWar', type: Copy, description: "Creates a directory with the exploded war content") {}
            task('combineJs', type: CombineJsTask, group: 'Build', description: "Combine javascript files into single file") {}
            task('minifyJs', type: MinifyJsTask, group: 'Build', description: "Minify javascript using Closure Compiler") {}
            task('combineCss', type: CombineCssTask, group: 'Build', description: "Combine css files into single file") {}
            task('minifyCss', type: MinifyCssTask, group: 'Build', description: "Minify css using YUI Minifier") {}
            task('jslint', type: JsLintTask, group: 'Verification', description: "Analyze javascript sources with JsLint") {}

            extensions.create(WebAppPluginExtension.NAME, WebAppPluginExtension)
            webApp.extensions.create(ClosureCompilerExtension.NAME, ClosureCompilerExtension)
            webApp.extensions.create(YuiCompressorExtension.NAME, YuiCompressorExtension)
            webApp.extensions.create(JsLintExtension.NAME, JsLintExtension)

            // include any jars the users drops in the lib directory
            repositories.flatDir(dirs: 'lib')
            dependencies.add('compile', fileTree(dir: 'src/main/webapp/WEB-INF/lib', include: '*.jar'))

            ext.spring = [ groupID: "org.springframework", version: "4.1.6.RELEASE" ]
        }
    }

    void loadConfiguration() {
        def env = project.hasProperty('env') ? project.env : project.webApp.defaultEnv
        def configFile = project.file(project.webApp.configFileName)
        if(project.webApp.environmentalConfig)
            println "Environment is set to $env"

        project.ext.with {
            environment = project.webApp.environmentalConfig ? env : "none"
            config = project.webApp.environmentalConfig ? new ConfigSlurper(env).parse(configFile.toURL()) : [:]
        }
    }

    void configureForProd() {
        project.with {
            def webappDir = getWebappDir()

            combineJs {
                source = fileTree(dir: "${webAppDirName}/js", include: "**/*.js")
                dest = file("${webappDir}/all.js")
            }

            minifyJs {
                dependsOn jslint
                source = combineJs
                dest = file("${webappDir}/${webApp.jsFileName}")
            }

            combineCss {
                source = fileTree(dir: "${webAppDirName}/css", include: "**/*.css")
                dest = file("${webappDir}/all.css")
            }

            minifyCss {
                source = combineCss
                dest = file("${webappDir}/${webApp.cssFileName}")
            }

            processResources {
                dependsOn minifyJs
                dependsOn minifyCss
                outputs.files "${webappDir}/${webApp.jsFileName}", "${webappDir}/${webApp.cssFileName}"
            }

            war {
                include "manifest.appcache"
                exclude "js/*.js", "css/*.css"
                from("${webappDir}/${webApp.jsFileName}") {
                    into "js"
                }
                from("${webappDir}/${webApp.cssFileName}") {
                    into "css"
                }
                filesMatching("*.appcache") {
                    expand("date": new Date())
                }
            }
        }
    }

    def configure = { project ->
        project.with {
            loadConfiguration()
            def webappDir = getWebappDir()
            explodeWar {
                into "${buildDir}/${webApp.explodeDir}"
                with war
            }

            processResources {
                dependsOn jslint
                inputs.property "env", environment
                outputs.files "${webappDir}/${webApp.jslint.destFilename}"
            }

            war {
                exclude "**/*.swp"      // vim creates swp files, but they're not needed in a build
                exclude "manifest.appcache"

                if(webApp.explode)
                    dependsOn explodeWar

                filesMatching("**/*.xml") {
                    expand(config)
                }
            }

            test.testLogging.exceptionFormat "full"
        }
    }

    def configureDependencies() {
        Configuration config = project.configurations.findByName('webApp')
        if(!config) {
            project.configurations {
                webApp
            }
        }
        if(project.configurations.webApp.dependencies.empty) {
            project.dependencies {
                webApp(JsLintTask.ANT_JAR)
            }
        }
    }

    def getWebappDir() {
        "${project.buildDir}/${project.webApp.webappPluginDir}"
    }
}
