package edu.clemson.dhhs.meds.webservice.webapp

class WebAppPluginConvention {
    private def compilationGroups = [
        spring: [
            groupID: "org.springframework",
            version: "4.1.6.RELEASE",
            prefix: "spring-",
            modules: [ ]
        ],
        springSecurity: [
            groupID: "org.springframework.security",
            version: "4.0.1.RELEASE",
            prefix: "spring-security-",
            modules: [ ]
        ],
        jackson: [
            groupID: "com.fasterxml.jackson.core",
            version: "2.5.1",
            prefix: "jackson-",
            modules: [ ]
        ],
        microsoftAPI: [
            groupID: "org.apache.poi",
            version: "3.13",
            modules: [ ]
        ]
    ]

    def getSpecificGroups(String... groups) {
        compilationGroups.findAll { key, value ->
            key in groups
        }
    }

    def collectCompilationGroups() {
        compilationGroups
            .collect { key, group ->
                group.modules.collect { "$group.groupID:${group.prefix ?: ''}$it:$group.version" }
            }
            .flatten()
    }
}