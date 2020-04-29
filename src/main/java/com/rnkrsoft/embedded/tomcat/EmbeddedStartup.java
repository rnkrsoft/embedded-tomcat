package com.rnkrsoft.embedded.tomcat;

import com.rnkrsoft.config.ConfigProvider;
import com.rnkrsoft.config.ConfigProviderFactory;
import com.rnkrsoft.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11NioProtocol;

import java.io.File;
import java.io.IOException;

import static com.rnkrsoft.embedded.tomcat.EmbeddedConstant.*;
/**
 * Created by rnkrsoft.com on 2018/11/7.
 * 嵌入容器启动类
 */
@Slf4j
public class EmbeddedStartup {

    static File createTempDir(String runtimeDir,  int port) throws IOException {
        File runtimeDirFile = new File(runtimeDir);
        if (!runtimeDirFile.exists()) {
            runtimeDirFile.mkdirs();
        }
        File tempDir = File.createTempFile(StringUtils.fill(Integer.toString(port), false, '-', 5), Integer.toString(port), runtimeDirFile);
        tempDir.delete();
        tempDir.mkdirs();
        tempDir.deleteOnExit();
        return tempDir;
    }

    public static void main(String[] args) throws Exception {
        main();
    }
    public static void main() throws Exception {
        ConfigProvider config = ConfigProviderFactory.getPropertiesInstance("tomcat");
        config.param("server.http.hostName", "localhost");
        config.param("server.http.port", "8080");
        config.param("server.http.protocol", "HTTP/1.1");
        config.param("server.http.contextPath", "");
        config.param("server.http.runtimeDir", "./work");
        config.param("server.http.useBodyEncodingForURI", "true");
        config.param("server.http.uriEncoding", "UTF-8");
        config.param("server.http.asyncTimeout", "30000");
        config.param("server.http.connectionTimeout", "30000");
        config.param("server.http.maxConnections", "30000");
        config.param("server.http.maxThreads", "100");
        config.param("file.encoding", "UTF-8");
        config.init("./work", 60);
        main(config);
    }
    public static void main(ConfigProvider config) throws Exception {
        String hostName = config.getString("server.http.hostName", "localhost");
        int port = config.getInteger("server.http.port",8080);
        String protocol = config.getString("server.http.protocol", "HTTP/1.1");
        String contextPath = config.getString("server.http.contextPath", "");
        String runtimeDir = config.getString("server.http.runtimeDir", "./work");
        String temp = runtimeDir + "/temp";
        String contextDocBase = runtimeDir + "/tomcat-docBase";
        boolean useBodyEncodingForURI = config.getBoolean("server.http.useBodyEncodingForURI", true);
        String uriEncoding = config.getString("server.http.uriEncoding", "UTF-8");
        int asyncTimeout = config.getInteger("server.http.asyncTimeout", 30000);
        int connectionTimeout = config.getInteger("server.http.connectionTimeout", 30000);
        int maxConnections = config.getInteger("server.http.maxConnections", 30000);
        int maxThreads = config.getInteger("server.http.maxThreads", 100);
        String file_encoding = config.getString("file.encoding", "UTF-8");
        final File baseDirPath = createTempDir(temp, port);
        final File contextDocBasePath = createTempDir(contextDocBase, port);
        Tomcat tomcat = new Tomcat();
        //设置连接器监听的端口(0-65535)。如果设置成0，将随机生成(通常只用于嵌入式和测试应用程序)。
        tomcat.setPort(port);
        tomcat.setHostname(hostName);
        tomcat.setBaseDir(baseDirPath.getAbsolutePath());
        Connector connector = new Connector(protocol);
        tomcat.setConnector(connector);
        //设置JVM字符集
        connector.setPort(port);

        connector.setAsyncTimeout(asyncTimeout);
        //设置系统文件编码
        connector.setProperty(FILE_ENCODING, file_encoding);
        //设置连接器字符集
        //配置URI使用的字符编码，来解码?之前的字符串。 一般情况下默认使用utf-8，在org.apache.catalina.STRICT_SERVLET_COMPLIANCE(系统属性)为true的情况下使用 ISO-8859-1。
        connector.setURIEncoding(uriEncoding);
        //设置启用UseBodyEncodingForURI
        connector.setUseBodyEncodingForURI(useBodyEncodingForURI);
        connector.setProperty("org.apache.catalina.STRICT_SERVLET_COMPLIANCE", "false");
        ProtocolHandler protocolHandler = connector.getProtocolHandler();
        if (protocolHandler instanceof Http11NioProtocol) {
            Http11NioProtocol http11NioProtocol = (Http11NioProtocol) protocolHandler;
            //设置连接超时毫秒数
            http11NioProtocol.setConnectionTimeout(connectionTimeout);
            //设置最大线程数
            http11NioProtocol.setMaxThreads(maxThreads);
            //设置最大连接数
            http11NioProtocol.setMaxConnections(maxConnections);
            log.info("connectionTimeout use '{}'ms", connectionTimeout);
            log.info("maxThreads use '{}'", maxThreads);
            log.info("maxConnections use '{}'", maxConnections);
        }

        Host host = tomcat.getHost();
        host.setAutoDeploy(false);

        Context context = tomcat.addWebapp(host, contextPath, contextDocBasePath.getAbsolutePath(), new EmbeddedContextConfig());


        context.setJarScanner(new EmbeddedStandardJarScanner());

        ClassLoader classLoader = EmbeddedStartup.class.getClassLoader();

        context.setParentClassLoader(classLoader);

        // context load webapp.WEB-INF/web.xml from classpath
        context.addLifecycleListener(new EmbeddedWebXmlMountListener());
        tomcat.start();

        tomcat.getServer().await();
    }
}
