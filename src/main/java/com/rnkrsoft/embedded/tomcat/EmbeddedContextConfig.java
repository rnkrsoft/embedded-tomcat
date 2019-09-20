package com.rnkrsoft.embedded.tomcat;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.ContextConfig;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.scan.Jar;
import org.apache.tomcat.util.scan.JarFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import static com.rnkrsoft.embedded.tomcat.EmbeddedConstant.*;

@Slf4j
class EmbeddedContextConfig extends ContextConfig {

	/**
	 * Scan JARs that contain web-fragment.xml files that will be used to
	 * configure this application to see if they also contain static resources.
	 * If static resources are found, add them to the context. Resources are
	 * added in web-fragment.xml priority order.
	 */
	@Override
	protected void processResourceJARs(Set<WebXml> fragments) {
		for (WebXml fragment : fragments) {
			URL url = fragment.getURL();

			try {
				String urlString = url.toString();
				if (isInsideNestedJar(urlString)) {
					// It's a nested jar but we now don't want the suffix
					// because
					// Tomcat
					// is going to try and locate it as a root URL (not the
					// resource
					// inside it)
					urlString = urlString.substring(0, urlString.length() - 2);
				}
				url = new URL(urlString);

				if ("jar".equals(url.getProtocol())) {
					try (Jar jar = JarFactory.newInstance(url)) {
						jar.nextEntry();
						String entryName = jar.getEntryName();
						while (entryName != null) {
							if (entryName.startsWith(META_INF_RESOURCES + "/")) {
								context.getResources().createWebResourceSet(WebResourceRoot.ResourceSetType.RESOURCE_JAR, "/", url, "/" + META_INF_RESOURCES);
								break;
							}
							jar.nextEntry();
							entryName = jar.getEntryName();
						}
					}
				} else if ("file".equals(url.getProtocol())) {
					File file = new File(url.toURI());
					File resources = new File(file, META_INF_RESOURCES);
					if (resources.isDirectory()) {
						context.getResources().createWebResourceSet(WebResourceRoot.ResourceSetType.RESOURCE_JAR, "/", resources.getAbsolutePath(), null, "/");
					}
				}
			} catch (IOException ioe) {
				log.error(sm.getString("contextConfig.resourceJarFail", url, context.getName()));
			} catch (URISyntaxException e) {
				log.error(sm.getString("contextConfig.resourceJarFail", url, context.getName()));
			}
		}
	}

	private static boolean isInsideNestedJar(String dir) {
		return dir.indexOf("!/") < dir.lastIndexOf("!/");
	}
}
