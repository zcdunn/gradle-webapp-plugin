package edu.clemson.dhhs.meds.webservice.webapp

import edu.clemson.dhhs.meds.webservice.webapp.tasks.*
import org.gradle.api.*
import org.gradle.api.plugins.*
import org.gradle.api.artifacts.*
import org.gradle.api.tasks.*
import org.gradle.api.file.*
import org.gradle.api.invocation.Gradle
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import org.junit.Before
import static org.junit.Assert.*

class WebAppPluginTest {
    final def DEFAULT_ENV = "dev"
    final def PRODUCTION_ENV = "prod"
    final def CONFIG_FILE_NAME = "envConfig.groovy"
    final def EXPLODE_DIR = "explodedWar"
    final def WEBAPP_PLUGIN_DIR = "webappTemp"
    final def JS_FILENAME = "all-min.js"
    final def CSS_FILENAME = "all-min.css"
    final def ENVIRONMENTAL_CONFIG = true
    final def EXPLODE = true

    Project project
    WebAppPlugin plugin

    @Before
    void setup() {
        project = ProjectBuilder.builder().build()
        plugin = new WebAppPlugin()

        plugin.apply(project)
    }

    @Test
    void testTasksAreAdded() {
        assertTrue(project.tasks.explodeWar instanceof Copy)
        assertTrue(project.tasks.combineJs instanceof CombineJsTask)
        assertTrue(project.tasks.minifyJs instanceof MinifyJsTask)
        assertTrue(project.tasks.combineCss instanceof CombineCssTask)
        assertTrue(project.tasks.minifyCss instanceof MinifyCssTask)
        assertTrue(project.tasks.jslint instanceof JsLintTask)
        assertTrue(project.tasks.jshint instanceof JsHintTask)
        assertTrue(project.tasks.appcache instanceof CreateAppcacheManifestTask)
    }

    @Test
    void testExtensionsAreAdded() {
        assertTrue(project.webApp instanceof WebAppPluginExtension)
        assertTrue(project.webApp.closure instanceof ClosureCompilerExtension)
        assertTrue(project.webApp.yuicompressor instanceof YuiCompressorExtension)
        assertTrue(project.webApp.jslint instanceof JsLintExtension)
        assertTrue(project.webApp.jshint instanceof JsHintExtension)
        assertTrue(project.webApp.appcache instanceof AppcacheExtension)
    }

    @Test
    void testExtenstionsDefaults() {
        assertTrue(project.webApp.defaultEnv == DEFAULT_ENV)
        assertTrue(project.webApp.productionEnv == PRODUCTION_ENV)
        assertTrue(project.webApp.configFileName == CONFIG_FILE_NAME)
        assertTrue(project.webApp.explodeDir == EXPLODE_DIR)
        assertTrue(project.webApp.webappPluginDir == WEBAPP_PLUGIN_DIR)
        assertTrue(project.webApp.jsFileName == JS_FILENAME)
        assertTrue(project.webApp.cssFileName == CSS_FILENAME)
        assertTrue(project.webApp.environmentalConfig == ENVIRONMENTAL_CONFIG)
        assertTrue(project.webApp.explode == EXPLODE)
    }

    @Test
    void testConfigurations() {
        assertFalse(project.configurations.webApp.dependencies.empty)
        assertFalse(project.tasks.war.dependsOn.isEmpty())
    }
}
