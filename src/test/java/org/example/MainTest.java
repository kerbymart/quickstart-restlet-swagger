package org.example;

import org.example.resources.HelloWorldResource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.*;
import org.junit.runner.RunWith;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class MainTest {

    private Client client;

    @ArquillianResource
    URL deploymentUrl;

    @Deployment
    public static WebArchive createDeployment() {
        return createMavenDeployment();
    }

    @Before
    public void setUp() {
        client = new Client(Protocol.HTTP);
    }

    @After
    public void tearDown() throws Exception {
        if (client != null) {
            client.stop();
        }
    }

    @Test
    public void testRootResource() {
        String targetUrl = deploymentUrl.toString();

        Request request = new Request(Method.GET, targetUrl);
        Response response = client.handle(request);

        assertNotNull(response);
        assertEquals(Status.SUCCESS_OK, response.getStatus());
        assertEquals("welcome to the root resource", response.getEntityAsText());
    }

    @Test
    public void testHelloWorldResource() {
        String targetUrl = deploymentUrl.toString() + "hello";

        Request request = new Request(Method.GET, targetUrl);
        Response response = client.handle(request);

        assertNotNull(response);
        assertEquals(Status.SUCCESS_OK, response.getStatus());
        assertEquals("hello, world", response.getEntityAsText());
    }

    public static WebArchive createSimpleDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClass(Main.class)
                .addClass(HelloWorldResource.class);
    }

    public static WebArchive createMavenDeployment() {
        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "org.example")
                .addAsResource(new File("src/main/resources/"), "")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"), "web.xml")
                .addAsLibraries(files);
    }
}
