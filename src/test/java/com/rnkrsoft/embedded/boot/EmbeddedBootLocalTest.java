package com.rnkrsoft.embedded.boot;

import com.rnkrsoft.embedded.boot.annotation.EmbeddedBootApplication;
import com.rnkrsoft.embedded.boot.annotation.EmbeddedRemoteConfigure;
import com.rnkrsoft.framework.config.v1.RuntimeMode;
import org.junit.Test;
@EmbeddedBootApplication(
        remoteConfigure = @EmbeddedRemoteConfigure(
            runtimeMode = RuntimeMode.LOCAL
        )
)
public class EmbeddedBootLocalTest {
    @Test
    public void test1(){
        EmbeddedApplicationLoader.runWith(EmbeddedBootLocalTest.class, new String[0]);
    }
}
