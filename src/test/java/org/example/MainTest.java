package org.example;

import org.example.resources.HelloWorldResource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.*;
import org.junit.runner.RunWith;
import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class MainTest {

    private static Component component;
    private Client client;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClass(Main.class)
                .addClass(HelloWorldResource.class);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        // Start the embedded Restlet server
        component = new Component();
        component.getServers().add(Protocol.HTTP, 8080);
        component.getDefaultHost().attach("/", new Main());
        component.start();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (component != null) {
            component.stop();
        }
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
    public void testHelloWorldResource() throws IOException {
        Request request = new Request(Method.GET, "http://localhost:8080/hello");
        Response response = client.handle(request);

        assertNotNull(response);
        assertEquals(Status.SUCCESS_OK, response.getStatus());
        assertEquals("hello, world", response.getEntity().getText());
    }
}


