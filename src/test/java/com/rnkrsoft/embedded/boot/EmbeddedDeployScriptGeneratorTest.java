package com.rnkrsoft.embedded.boot;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by woate on 2019/9/21.
 */
public class EmbeddedDeployScriptGeneratorTest {

    @Test
    public void testGenerateDeployScript() throws Exception {
        EmbeddedDeployScriptGenerator.generateDeployScript("./target", "log4j2.xml");
        EmbeddedDeployScriptGenerator.generateDeployScript("./target", "README.md");
        EmbeddedDeployScriptGenerator.generateDeployScript("./target", "startup.bat");
        EmbeddedDeployScriptGenerator.generateDeployScript("./target", "startup.sh");
    }
}