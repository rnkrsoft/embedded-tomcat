package com.rnkrsoft.embedded.boot;

import com.rnkrsoft.embedded.boot.annotation.EmbeddedBootApplication;
import com.rnkrsoft.embedded.boot.annotation.EmbeddedRemoteConfigure;
import com.rnkrsoft.framework.config.v1.RuntimeMode;
import org.junit.Test;

@EmbeddedBootApplication(
        remoteConfigure = @EmbeddedRemoteConfigure(
                host = "config.rnkrsoft.com",
                port = 8090,
                groupId = "com.rnkrsoft.configure",
                artifactId = "configure-web",
                version = "1.0.0",
                env = "DEV",
                runtimeMode = RuntimeMode.LOCAL
        ),
        runtimeDir = "./target/work",
        hostName = "localhost",
        port = 8090,
        connectionTimeoutSecond = 20*1000,
        maxThreads = 10,
        reloadConfigSecond = 60
)
public class EmbeddedBootRemoteTest {
    @Test
    public void test1() {
        EmbeddedApplicationLoader.runWith(EmbeddedBootRemoteTest.class, new String[]{"-p"});
    }
}
