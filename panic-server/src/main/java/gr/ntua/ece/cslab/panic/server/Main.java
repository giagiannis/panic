/*
 * Copyright 2014 Giannis Giannakopoulos.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.ntua.ece.cslab.panic.server;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import gr.ntua.ece.cslab.panic.server.shared.SystemLogger;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

class Main {

    private static String PROPERTIES_FILE="panic.properties";
    private static void loadProperties() throws IOException {
        InputStream stream = Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
        if (stream == null) {
            System.err.println("No celar-server.properties file was found! Exiting...");
            System.exit(1);
        }
        ServerStaticComponents.properties = new Properties();
        ServerStaticComponents.properties.load(stream);
    }

    private static void configureServer() throws Exception {
        ServerStaticComponents.server = new Server();
        int plainPort = -1, sslPort = -1;
        String keystorePath = null, keystorePassword = null;
        ServerConnector connector = null, sslConnector = null;

        if (ServerStaticComponents.properties.getProperty("server.plain.port") != null) {
            plainPort = new Integer(ServerStaticComponents.properties.getProperty("server.plain.port"));
        }
        if (ServerStaticComponents.properties.getProperty("server.ssl.port") != null) {
            sslPort = new Integer(ServerStaticComponents.properties.getProperty("server.ssl.port"));
            keystorePath = ServerStaticComponents.properties.getProperty("server.ssl.keystore.path");
            keystorePassword = ServerStaticComponents.properties.getProperty("server.ssl.keystore.password");
        }

        if (plainPort != -1) {
            connector = new ServerConnector(ServerStaticComponents.server);
            connector.setPort(plainPort);
        }

        if (sslPort != -1) {
            HttpConfiguration https = new HttpConfiguration();
            https.addCustomizer(new SecureRequestCustomizer());
            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStorePath(keystorePath);
            sslContextFactory.setKeyStorePassword(keystorePassword);
            sslContextFactory.setKeyManagerPassword(keystorePassword);

            sslConnector = new ServerConnector(
                    ServerStaticComponents.server,
                    new SslConnectionFactory(sslContextFactory, "http/1.1"),
                    new HttpConnectionFactory(https));
            sslConnector.setPort(sslPort);

        }
        if (sslConnector != null && connector != null) {
            ServerStaticComponents.server.setConnectors(new Connector[]{connector, sslConnector});
        } else if (connector != null) {
            ServerStaticComponents.server.setConnectors(new Connector[]{connector});
        } else if (sslConnector != null) {
            ServerStaticComponents.server.setConnectors(new Connector[]{sslConnector});
        } else {
            System.err.println("Please choose one of the plain and SSL connections!");
            System.exit(1);
        }

        ServletHolder holder = new ServletHolder(ServletContainer.class);
        holder.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
        holder.setInitParameter("com.sun.jersey.config.property.packages",
                "gr.ntua.ece.cslab.panic.server.rest;"
                + "org.codehaus.jackson.jaxrs");//Set the package where the services reside
        holder.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
        holder.setInitParameter("com.sun.jersey.config.feature.Formatted", "true");
//        
        holder.setInitOrder(1);
//
//        ServerStaticComponents.server = new Server();
        ServletContextHandler context = new ServletContextHandler(ServerStaticComponents.server, "/", ServletContextHandler.SESSIONS);
        context.addServlet(holder, "/*");
        Logger.getLogger(Main.class.getName()).info("Server configured");

    }

    private static void configureLogger() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        InputStream logPropertiesStream = Main.class.getClassLoader().getResourceAsStream("log4j.properties");
        PropertyConfigurator.configure(logPropertiesStream);
        Logger.getLogger(Main.class.getName()).info("Logger configured");
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Logger.getLogger(Main.class.getName()).info("Server is shutting down");
                    ServerStaticComponents.server.stop();
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.FATAL, null, ex);
                }
            }
        }));

    }

    public static void main(String[] args) throws Exception {
        configureLogger();
        loadProperties();
        addShutdownHook();
        configureServer();

        ServerStaticComponents.server.start();
        Logger.getLogger(Main.class.getName()).info("Server is started");
    }
}
