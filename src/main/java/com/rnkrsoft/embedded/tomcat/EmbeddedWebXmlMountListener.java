package com.rnkrsoft.embedded.tomcat;

import org.apache.catalina.*;
import org.apache.catalina.WebResourceRoot.ResourceSetType;
import org.apache.catalina.webresources.StandardRoot;

import java.net.MalformedURLException;
import java.net.URL;
import static com.rnkrsoft.embedded.tomcat.EmbeddedConstant.*;

/**
 * find "webapp.WEB-INF/web.xml" from app classpath, and mount into WebResourceRoot.
 */
class EmbeddedWebXmlMountListener implements LifecycleListener {

	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		if (event.getType().equals(Lifecycle.BEFORE_START_EVENT)) {
			Context context = (Context) event.getLifecycle();
			WebResourceRoot resources = context.getResources();
			if (resources == null) {
				resources = new StandardRoot(context);
				context.setResources(resources);
			}

			/**
			 * <pre>
			 * when run as embeded tomcat, context.getParentClassLoader() is AppClassLoader,
			 * so it can load "WEB-INF/web.xml" from app classpath.
			 * </pre>
			 */
			URL resource = context.getParentClassLoader().getResource(WEB_INF_WEB_XML);
			if (resource != null) {
				String webXmlUrlString = resource.toString();
				URL root;
				try {
					root = new URL(webXmlUrlString.substring(0, webXmlUrlString.length() - WEB_INF_WEB_XML.length()));
					resources.createWebResourceSet(ResourceSetType.RESOURCE_JAR, "/" + WEB_INF, root, "/" + WEB_INF);
				} catch (MalformedURLException e) {
					// ignore
				}
			}
		}

	}

}
