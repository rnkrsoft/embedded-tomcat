package com.rnkrsoft.embedded.tomcat;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import javax.servlet.http.HttpServlet;
import java.util.*;

@HandlesTypes({HttpServlet.class})
@Slf4j
public class LoadedServletContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext ctx) throws ServletException {
        if (set != null && log.isDebugEnabled()) {
            for (Class c : set) {
                log.info(c.getName());
            }
        }
    }

}