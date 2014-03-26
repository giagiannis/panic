package gr.ntua.ece.cslab.panic.server;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import gr.ntua.ece.cslab.panic.server.shared.SystemLogger;
import java.io.InputStream;
import java.util.Properties;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

class RunServer {

	public static void main(String[] args) throws Exception{
            
            Properties prop = new Properties();
            InputStream resourceFile = RunServer.class.getClassLoader().getResourceAsStream("server.properties");
            if(resourceFile!=null) {
                prop.load(resourceFile);
            } else {
                System.err.println("File not exist...");
                System.exit(1);
            }
            
            ServletHolder holder = new ServletHolder(ServletContainer.class);
            holder.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
            holder.setInitParameter("com.sun.jersey.config.property.packages", "gr.ntua.ece.cslab.panic.server.rest");//Set the package where the services reside
            holder.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
            
            Server server = new Server(new Integer(prop.getProperty("server.port")));
            ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
            context.addServlet(holder, "/*");
            server.start();
            
            SystemLogger.configureLogger();
            
            SystemLogger.get().info("Server started");
            server.join();
            SystemLogger.get().info("Server joined");

            
        }
}
