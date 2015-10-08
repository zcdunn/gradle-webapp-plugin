package edu.clemson.dhhs.meds.webservice.webapp

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.api.file.*

class WebappInit extends DefaultTask {
    @OutputFile def srcDir = project.file("src/")
    @OutputFile def envConfigFile = project.file("envConfig.groovy")

    @TaskAction
    def run() {
        envConfigFile.text = """environments {
    dev {
    }

    prod {
    }
}
"""
    }
}
