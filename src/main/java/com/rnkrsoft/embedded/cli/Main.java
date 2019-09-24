package com.rnkrsoft.embedded.cli;

import com.rnkrsoft.embedded.boot.EmbeddedApplicationLoader;
import com.rnkrsoft.embedded.boot.annotation.EmbeddedBootApplication;

/**
 * Created by rnkrsoft.com on 2019/9/23.
 */
@EmbeddedBootApplication
public class Main {
    public static void main(String[] args) {
        EmbeddedApplicationLoader.runWith(Main.class, args);
    }
}
