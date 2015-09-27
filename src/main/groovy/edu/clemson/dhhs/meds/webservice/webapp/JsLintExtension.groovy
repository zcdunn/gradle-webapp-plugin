package edu.clemson.dhhs.meds.webservice.webapp

import org.gradle.api.tasks.*

class JsLintExtension {
    public static final String NAME = 'jslint'
    public static final String TXT = 'txt'
    public static final String XML = 'xml'
    public static final String PLAIN = 'plain'
    public static final String HTML = 'html'

    @Input List<String> inputDirs = [ 'src/main/webapp/js'] 
    @Input String formatterType = PLAIN
    @Input String destFile = NAME
    @Input String excludes = ''
    @Input Map optionsMap = [
        haltOnFailure: false
    ]
    @Input boolean displayErrors = false

    def inputDirs(def dirs) { inputDirs << dirs }
    def haltOnFailure(boolean haltOnFailure) { optionsMap.haltOnFailure = haltOnFailure }
    def options(String options) { optionsMap.options = options }
    def formatterType(String formatterType) { this.formatterType = formatterType }
    def destFile(String destFile) { this.destFile = destFile }
    def excludes(String excludes) { this.excludes = excludes }
    def displayErrors(boolean displayErrors) { this.displayErrors = displayErrors }

    String getDestFilename() {
        switch(formatterType) {
            case PLAIN:
                return "${destFile}.${TXT}"
            case ([XML, HTML]):
                return "${destFile}.${XML}"
        }
    }

    String getFormatterType() {
        switch(formatterType) {
            case PLAIN:
                return PLAIN
            case ([XML, HTML]):
                return XML
        }
    }
}
