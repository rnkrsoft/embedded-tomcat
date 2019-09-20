package com.rnkrsoft.embedded.tomcat;

/**
 * Created by rnkrsoft.com on 2019/9/20.
 */
public interface EmbeddedConstant {
    String WEB_INF = "WEB-INF";
    String WEB_INF_WEB_XML = WEB_INF + "/web.xml";
    String WEB_INF_CLASSES = "/" + WEB_INF + "/classes";
    String META_INF = "META-INF";
    String META_INF_RESOURCES = META_INF + "/resources";
    String WEB_INF_CLASSES_META_INF = WEB_INF_CLASSES + "/" + META_INF;
    String FILE = "file:";
    String JAR = "jar:";
    String HTTP = "http:";
    String HTTPS = "https:";
    String FILE_ENCODING = "file.encoding";
}
