package com.rnkrsoft.embedded.boot;

import com.rnkrsoft.config.ConfigProvider;
import com.rnkrsoft.embedded.boot.annotation.EmbeddedBootApplication;
import com.rnkrsoft.embedded.boot.annotation.EmbeddedRemoteConfigure;
import com.rnkrsoft.embedded.tomcat.EmbeddedStartup;
import com.rnkrsoft.framework.config.v1.RuntimeMode;
import com.rnkrsoft.logtrace4j.ErrorContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class EmbeddedApplicationLoader {
    static final String MESSAGE;
    static ConfigProvider CONFIG = null;
    static {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Skeleton4j Boot Application").append("\n");
        buffer.append(" -generate(g): generate skeleton4j.properties file.").append("\n");
        buffer.append(" -verbose(v): y/n verbose mode.").append("\n");
        MESSAGE = buffer.toString();
    }

    public final static void runWith(Class bootLoaderClass, String... args) {
        boolean generateProperties = false;
        boolean verbose = false;
        for (int i = 0; i < args.length; i++) {
            String name = args[i];
            switch (name) {
                case "-generate":
                case "-g":
                    generateProperties = true;
                    break;
                case "-verbose":
                case "-v":
                    verbose = true;
                    if (verbose) {
                        log.info("begin verbose mode");
                    }
                    break;
                case "-help":
                case "-h":
                default:
                    log.info(MESSAGE);
                    System.exit(0);
                    return;
            }
        }

        EmbeddedBootApplication embeddedBootApplicationAnnotation = (EmbeddedBootApplication) bootLoaderClass.getAnnotation(EmbeddedBootApplication.class);
        if (embeddedBootApplicationAnnotation == null) {
            throw ErrorContextFactory.instance()
                    .message("Class {} unmarked @EmbeddedBootApplication annotation!", bootLoaderClass)
                    .solution("Class {} unmarked  @EmbeddedBootApplication annotation solute the problem！", bootLoaderClass)
                    .runtimeException();
        }
        Map<String, String> envs = System.getenv();
        List<String> keys = new ArrayList(envs.keySet());
        Collections.sort(keys);
        EmbeddedRemoteConfigure remoteConfigure = embeddedBootApplicationAnnotation.remoteConfigure();
        String configHost = remoteConfigure.host();
        Integer configPort = remoteConfigure.port();
        String configEnv= remoteConfigure.env();
        RuntimeMode runtimeMode = remoteConfigure.runtimeMode();
        String configWorkHome = System.getProperty("user.dir") + "/work";
        String configSecurityKey = remoteConfigure.securityKey();
        if (envs.containsKey("CONFIG_HOST")){
            configHost = envs.get("CONFIG_HOST");
        }
        if (envs.containsKey("CONFIG_PORT")){
            configPort = Integer.valueOf(envs.get("CONFIG_PORT"));
        }
        if (envs.containsKey("CONFIG_ENV")){
            configEnv = envs.get("CONFIG_ENV");
        }
        if (envs.containsKey("CONFIG_RUNTIME_MODE")){
            runtimeMode = RuntimeMode.valueOfCode(envs.get("CONFIG_RUNTIME_MODE"));
        }
        if (envs.containsKey("CONFIG_WORK_HOME")){
            configWorkHome = envs.get("CONFIG_WORK_HOME");
        }
        if (envs.containsKey("CONFIG_SECURITY_KEY")){
            configSecurityKey = envs.get("CONFIG_SECURITY_KEY");
        }
        if (runtimeMode == RuntimeMode.REMOTE || runtimeMode == RuntimeMode.AUTO){
            CONFIG = new EmbeddedRemoteConfigProvider(configHost, configPort, remoteConfigure.groupId(), remoteConfigure.artifactId(), remoteConfigure.version(), configEnv, configSecurityKey, runtimeMode, verbose);
            try{
                CONFIG.init(configWorkHome, embeddedBootApplicationAnnotation.reloadConfigSecond());
            }catch (Exception e){
                if (runtimeMode == RuntimeMode.AUTO){
                    CONFIG = new EmbeddedAnnotationConfigProvider(embeddedBootApplicationAnnotation, generateProperties);
                    CONFIG.init(configWorkHome, embeddedBootApplicationAnnotation.reloadConfigSecond());
                    log.error("fetch remote config happens error,fallback:LOCAL" , e);
                }else {
                    log.error("fetch remote config happens error" , e);
                    throw e;
                }
            }
        }else{
            CONFIG = new EmbeddedAnnotationConfigProvider(embeddedBootApplicationAnnotation, generateProperties);
            CONFIG.init(configWorkHome, embeddedBootApplicationAnnotation.reloadConfigSecond());
        }
        System.setProperty("file.encoding", CONFIG.getString("fileEncoding" , "UTF-8"));
        /**
         * 生成properties文件
         */
        if (generateProperties) {
            if (verbose) {
                log.info("generate tomcat.properties file--------begin --------");
            }
            CONFIG.save();
            if (verbose) {
                log.info("generate tomcat.properties file--------end --------");
            }
            System.exit(0);
            return;
        }
        try {
            EmbeddedStartup.main(CONFIG);
        } catch (Exception e) {
            log.error("Embedded Tomcat startup happens error!", e);
        }
    }

    public static ConfigProvider getConfigProvider() {
        if (CONFIG == null) {
            throw ErrorContextFactory.instance()
                    .message("Config Provider is not already!")
                    .solution("please use Skeleton4jApplicationLoader.runWith(bootLoaderClass, args);")
                    .runtimeException();
        } else {
            return CONFIG;
        }
    }
}
