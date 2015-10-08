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
    private static final def SPRING = [ groupID: "org.springframework", version: "4.1.6.RELEASE" ]
    private Project project
    String pluginOutputDir

    void apply(Project project) {
        this.project = project
        configureDependencies()
        setup()
        project.afterEvaluate(configure)
    }

    void setup() {
        project.with {
            apply(plugin: 'war')

            extensions.create(WebAppPluginExtension.NAME, WebAppPluginExtension)
            webApp.extensions.create(ClosureCompilerExtension.NAME, ClosureCompilerExtension)
            webApp.extensions.create(YuiCompressorExtension.NAME, YuiCompressorExtension)
            webApp.extensions.create(JsLintExtension.NAME, JsLintExtension)
            webApp.extensions.create(JsHintExtension.NAME, JsHintExtension)
            webApp.extensions.create(AppcacheExtension.NAME, AppcacheExtension)

            task('explodeWar', type: Copy, description: "Creates a directory with the exploded war content") {}
            task('combineJs', type: CombineJsTask, group: 'Build', description: "Combine javascript files into single file") {}
            task('minifyJs', type: MinifyJsTask, group: 'Build', description: "Minify javascript using Closure Compiler") {}
            task('combineCss', type: CombineCssTask, group: 'Build', description: "Combine css files into single file") {}
            task('minifyCss', type: MinifyCssTask, group: 'Build', description: "Minify css using YUI Minifier") {}
            task('appcache', type: CreateAppcacheManifestTask, group: 'Build', description: "Creates html cache manifest", dependsOn: 'explodeWar') {}
            task('jslint', type: JsLintTask, group: 'Verification', description: "Analyze javascript sources with JsLint") {}
            task('jshint', type: JsHintTask, group: 'Verification', description: "Analyze javascript sources with JsHint") {}

            // include any jars the users drops in the lib directory
            repositories.flatDir(dirs: 'lib')
            dependencies.add('compile', fileTree(dir: 'src/main/webapp/WEB-INF/lib', include: '*.jar'))

            ext.spring = SPRING
        }
        pluginOutputDir = "${project.buildDir}/${project.webApp.webappPluginDir}"
    }

    void loadConfiguration() {
        def env = project.hasProperty('env') ? project.env : project.webApp.defaultEnv
        def configFile = project.file(project.webApp.configFileName)
        if(project.webApp.environmentalConfig)
            println "Environment is set to $env"

        project.ext.with {
            environment = env
            config = project.webApp.environmentalConfig ? new ConfigSlurper(env).parse(configFile.toURL()) : [:]
            config.inProd = env == project.webApp.productionEnv
            config.env = env
        }
    }

    void configureProd() {
        project.with {
            combineJs {
                dependsOn jshint
                source = fileTree(dir: "${webAppDirName}/js", include: "**/*.js")
                dest = file("${pluginOutputDir}/all.js")
            }

            minifyJs {
                source = combineJs
                dest = file("${pluginOutputDir}/${webApp.jsFileName}")
            }

            combineCss {
                source = fileTree(dir: "${webAppDirName}/css", include: "**/*.css")
                dest = file("${pluginOutputDir}/all.css")
            }

            minifyCss {
                source = combineCss
                dest = file("${pluginOutputDir}/${webApp.cssFileName}")
            }

            processResources {
                dependsOn minifyJs
                dependsOn minifyCss
            }

            war {
                dependsOn appcache
                exclude "js/*.js", "css/*.css"
                from("${pluginOutputDir}/${webApp.jsFileName}")  { into "js" }
                from("${pluginOutputDir}/${webApp.cssFileName}") { into "css" }
                from "${pluginOutputDir}/${webApp.appcache.destFile}"
            }
        }
    }

    def configure = {
        project.with {
            loadConfiguration()
            explodeWar {
                into "${buildDir}/${webApp.explodeDir}"
                with war
            }

            jshint {
                source = fileTree(dir: "${webAppDirName}/js", include: "**/*.js", exclude: webApp.jshint.excludes)
                dest = file("${pluginOutputDir}/${webApp.jshint.destFile}")
            }

            check.dependsOn jshint

            war {
                exclude "**/*.swp"      // vim creates swp files, but they're not needed in a build
                if(webApp.explode)
                    dependsOn explodeWar

                if(!webApp.expandFiles) {
                    println "Expanding all xml files"
                    filesMatching("**/*.xml") {
                        expand(config)
                    }
                }
                else {
                    println "Expanding each pattern"
                    webApp.expandFiles.each { String pattern ->
                        filesMatching(pattern) {
                            expand(config)
                        }
                    }
                }
            }

            test.testLogging.exceptionFormat "full"
            if(ext.environment == webApp.productionEnv)
                configureProd()
        }
    }

    def configureDependencies() {
        project.configurations {
            webApp
        }

        if(project.configurations.webApp.dependencies.empty) {
            project.dependencies.webApp(JsLintTask.ANT_JAR)
            project.dependencies.webApp(RhinoExec.RHINO_DEPENDENCY)
        }
    }
}
