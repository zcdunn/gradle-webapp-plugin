package edu.clemson.dhhs.meds.webservice.webapp

import org.gradle.api.tasks.*

class YuiCompressorExtension {
    public static final NAME = 'yuicompressor'
    @Input Integer lineBreakPos = -1
}
