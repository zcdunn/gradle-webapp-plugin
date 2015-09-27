package edu.clemson.dhhs.meds.webservice.webapp

import com.yahoo.platform.yui.compressor.CssCompressor

class CssMinifier {
    private static final String CHARSET = 'UTF-8'

    void minifyCssFile(final File inputFile, final File outputFile, final Integer lineBreakPos) {
        Reader reader
        CssCompressor compressor
        try {
            reader = new InputStreamReader(new FileInputStream(inputFile), CHARSET)
            compressor = new CssCompressor(reader)
        }
        finally {
            if(reader != null) {
                reader.close()
            }
        }

        Writer writer
        try {
            writer = new OutputStreamWriter(new FileOutputStream(outputFile), CHARSET)
            compressor.compress(writer, lineBreakPos)
        }
        finally {
            if(writer != null) {
                writer.close()
            }
        }
    }
    
}
