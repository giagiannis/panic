package gr.ntua.ece.cslab.panic.server;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

class HelloWorld {

	public static void main(String[] args) throws Exception{
            ServletHolder holder = new ServletHolder(ServletContainer.class);
            holder.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
            holder.setInitParameter("com.sun.jersey.config.property.packages", "gr.ntua.ece.cslab.panic.server.rest");//Set the package where the services reside
            holder.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
            
            Server server = new Server(9999);
            ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
            context.addServlet(holder, "/*");
            server.start();
            server.join();
        }
}
