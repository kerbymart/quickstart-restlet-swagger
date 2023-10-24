package org.example;

import org.example.resources.HelloWorldResource;
import org.example.resources.RootResource;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.engine.application.CorsFilter;
import org.restlet.ext.swagger.Swagger2SpecificationRestlet;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;

import java.util.Collections;
import java.util.HashSet;

public class Main extends Application {

    public static void main(String[] args) throws Exception {
        // Create a new Component.
        Component component = new Component();

        // Add a new HTTP server listening on port 8080.
        component.getServers().add(Protocol.HTTP, 8080);

        // Attach the sample application.
        component.getDefaultHost().attach("", new Main());

        // Start the component.
        component.start();
    }

    @Override
    public Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of HelloWorldResource.
        Router router = new Router(getContext());

        // Defines only one route
        router.attach("/hello", HelloWorldResource.class);
        router.attachDefault(RootResource.class);

        // Serve the swagger-ui static resources
        Directory swaggerUiDirectory = new Directory(getContext(), "war:///swagger-ui/");
        router.attach("/ui/", swaggerUiDirectory);

        attachSwaggerSpecification2(router);
        applyCORSFilter(router);

        return router;
    }

    private void applyCORSFilter(Router router) {
        // Apply CORS filter
        CorsFilter corsFilter = new CorsFilter(getContext());
        corsFilter.setAllowedOrigins(new HashSet(Collections.singletonList("*")));
        corsFilter.setAllowedCredentials(true);

        corsFilter.setNext(router);
    }

    private void attachSwaggerSpecification2(Router router) {
        Swagger2SpecificationRestlet swagger2SpecificationRestlet
                = new Swagger2SpecificationRestlet(this);
        swagger2SpecificationRestlet.setBasePath("http://localhost:8080/");
        swagger2SpecificationRestlet.attach(router, "/api-docs");
    }

}