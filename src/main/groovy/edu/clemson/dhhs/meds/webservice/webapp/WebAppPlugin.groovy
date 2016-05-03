package edu.clemson.dhhs.meds.webservice.webapp

import edu.clemson.dhhs.meds.webservice.webapp.tasks.*
import org.gradle.api.*
import org.gradle.api.plugins.*
import org.gradle.api.artifacts.*
import org.gradle.api.tasks.*
import org.gradle.api.file.*

class WebAppPlugin implements Plugin<Project> {
    private Project project
    private String pluginOutputDir

    void apply(Project project) {
        this.project = project
        configureDependencies()
        createExtensionsAndTasks()
        project.afterEvaluate configure
        setupCompilationGroups()
    }

    void createExtensionsAndTasks() {
        project.with {
            apply plugin: 'war'

            extensions.create WebAppPluginExtension.NAME, WebAppPluginExtension
            webApp.extensions.create ClosureCompilerExtension.NAME, ClosureCompilerExtension
            webApp.extensions.create YuiCompressorExtension.NAME, YuiCompressorExtension
            webApp.extensions.create JsLintExtension.NAME, JsLintExtension
            webApp.extensions.create JsHintExtension.NAME, JsHintExtension
            webApp.extensions.create AppcacheExtension.NAME, AppcacheExtension

            task 'explodeWar', type: Copy, description: "Creates a directory with the exploded war content", {}
            task 'combineJs', type: CombineJsTask, group: 'Build', description: "Combine javascript files into single file", {}
            task 'minifyJs', type: MinifyJsTask, group: 'Build', description: "Minify javascript using Closure Compiler", {}
            task 'combineCss', type: CombineCssTask, group: 'Build', description: "Combine css files into single file", {}
            task 'minifyCss', type: MinifyCssTask, group: 'Build', description: "Minify css using YUI Minifier", {}
            task 'appcache', type: CreateAppcacheManifestTask, group: 'Build', description: "Creates html cache manifest", dependsOn: 'explodeWar', {}
            task 'jslint', type: JsLintTask, group: 'Verification', description: "Analyze javascript sources with JsLint", {}
            task 'jshint', type: JsHintTask, group: 'Verification', description: "Analyze javascript sources with JsHint", {}

            // include any jars the users drops in the lib directory
            repositories.flatDir dirs: 'lib'
            dependencies.add 'compile', fileTree(dir: 'src/main/webapp/WEB-INF/lib', include: '*.jar')
        }
    }

    void configureProd() {
        project.with {
            combineJs {
                dependsOn jshint
                source = fileTree(dir: "${webAppDirName}/js", include: "**/*.js", excludes: webApp.closure.excludes)
                dest = file("${pluginOutputDir}/all.js")
            }

            minifyJs {
                source = combineJs
                dest = file("${pluginOutputDir}/${webApp.jsFileName}")
                sourceMap = file("${pluginOutputDir}/${webApp.jsSourceMap}")
                sourceMappingUrlPrefix = "${webApp.jsSourceMappingUrlPrefix}"
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
                from("${pluginOutputDir}/${webApp.jsSourceMap}") { into "js" }
                from("${pluginOutputDir}/${webApp.cssFileName}") { into "css" }
                from "${pluginOutputDir}/${webApp.appcache.destFile}"
            }
        }
    }

    def configure = { proj ->
        pluginOutputDir = "${proj.buildDir}/${proj.webApp.webappPluginDir}"
        def currentEnv = proj.hasProperty('env') ? proj.env : proj.webApp.defaultEnv;
        proj.ext.config = [ env: currentEnv ]
        println "Environment is set to $currentEnv"

        proj.with {
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
                if(webApp.explodeDir)
                    dependsOn explodeWar

                webApp.expandFiles.each { String pattern ->
                    filesMatching(pattern) { expand config }
                }
            }

            test.testLogging.exceptionFormat "full"
            if(currentEnv == webApp.productionEnv)
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

    def setupCompilationGroups() {
        project.convention.plugins.webApp = new WebAppPluginConvention()
    }
}