package org.example;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.example.config.Configuration;
import org.example.modules.ConfigModule;
import org.example.docs.EnhancedSwagger2SpecificationRestlet;
import org.example.resources.HelloWorldResource;
import org.example.resources.RootResource;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.engine.application.CorsFilter;
import org.restlet.ext.apispark.internal.firewall.FirewallFilter;
import org.restlet.ext.apispark.internal.firewall.handler.RateLimitationHandler;
import org.restlet.ext.apispark.internal.firewall.handler.policy.UniqueLimitPolicy;
import org.restlet.ext.apispark.internal.firewall.rule.FirewallRule;
import org.restlet.ext.apispark.internal.firewall.rule.PeriodicFirewallCounterRule;
import org.restlet.ext.apispark.internal.firewall.rule.policy.IpAddressCountingPolicy;
import org.restlet.ext.guice.FinderFactory;
import org.restlet.ext.guice.RestletGuice;
import org.restlet.ext.swagger.Swagger2SpecificationRestlet;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;

import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    public static void main(String[] args) throws Exception {
        // Set up and start the main application component, binding it to an HTTP server on port 8080.
        Component component = new Component();
        component.getServers().add(Protocol.HTTP, 8080);
        component.getDefaultHost().attach("", new Main());
        component.start();
    }

    @Override
    public Restlet createInboundRoot() {
        // Using Guice to manage dependencies modularly for easier testing and maintenance.
        ConfigModule configModule = new ConfigModule();
        FinderFactory factory = new RestletGuice.Module(configModule);
        Injector injector = Guice.createInjector(configModule);

        // Set up the router to map incoming requests to their respective resources.
        Router router = new Router(getContext());
        router.attachDefault(factory.finder(RootResource.class));
        router.attach("/hello", factory.finder(HelloWorldResource.class));

        // Provide UI for API visualization and testing.
        attachSwaggerUI(router);
        attachSwaggerSpecification2(router);

        // Enable CORS to allow secure cross-origin requests.
        CorsFilter corsFilter = createCORSFilter(router);

        // Introduce rate limiting to protect the API from excessive requests.
        Configuration config = injector.getInstance(Configuration.class);
        int rateLimit = config.getRateLimit();
        FirewallFilter firewallFilter = createFirewallFilter(corsFilter, rateLimit);

        return firewallFilter;
    }

    private FirewallFilter createFirewallFilter(Restlet next, int rateLimit) {
        // Implement a firewall rule for IP-based rate limiting. This helps prevent single-source request flooding.
        FirewallRule rule = new PeriodicFirewallCounterRule(60, TimeUnit.SECONDS, new IpAddressCountingPolicy());
        ((PeriodicFirewallCounterRule)rule).addHandler(new RateLimitationHandler(new UniqueLimitPolicy(rateLimit)));
        FirewallFilter firewallFiler = new FirewallFilter(getContext(), Collections.singletonList(rule));
        firewallFiler.setNext(next);

        return firewallFiler;
    }

    private CorsFilter createCORSFilter(Router router) {
        // Set up CORS support to enable secure cross-origin requests, crucial for modern web apps.
        CorsFilter corsFilter = new CorsFilter(getContext());
        corsFilter.setAllowedOrigins(new HashSet(Collections.singletonList("*")));
        corsFilter.setAllowedCredentials(true);

        corsFilter.setNext(router);
        return corsFilter;
    }

    private void attachSwaggerUI(Router router) {
        // Integrate SwaggerUI to offer an interactive API documentation interface.
        Directory swaggerUiDirectory = new Directory(getContext(), "war:///swagger-ui/");
        router.attach("/ui/", swaggerUiDirectory);
    }

    private void attachSwaggerSpecification2(Router router) {
        // Attach the Swagger V2 specification, giving a standardized view of our API's capabilities.
        Swagger2SpecificationRestlet swagger2SpecificationRestlet
                = new EnhancedSwagger2SpecificationRestlet(this);
        swagger2SpecificationRestlet.setBasePath("http://localhost:8080/");
        swagger2SpecificationRestlet.attach(router, "/api-docs");
    }

}