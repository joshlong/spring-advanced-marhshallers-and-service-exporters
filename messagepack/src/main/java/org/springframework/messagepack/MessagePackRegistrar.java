package org.springframework.messagepack;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Josh Long
 */
public class MessagePackRegistrar {

    private boolean remapResults, exportServiceParameters,  serializeJavaBeanProperties;

    private Set<Class> classes = new HashSet<Class>();

    /**
     * Should the results be re-built based on heuristics designed to capture the intent of the code
     *
     * @param remapResults
     */
    public void setRemapResults(boolean remapResults) {
        this.remapResults = remapResults;
    }

    public void setExportServiceParameters(boolean exportServiceParameters) {
        this.exportServiceParameters = exportServiceParameters;
    }

    public void setSerializeJavaBeanProperties(boolean serializeJavaBeanProperties) {
        this.serializeJavaBeanProperties = serializeJavaBeanProperties;
    }

    public void registerClass(Class ... clazz ){
        for(Class c : clazz)
            classes.add(c) ;

    }


}
