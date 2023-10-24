package org.example.resources;

import com.wordnik.swagger.annotations.*;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

@Api(value = "/hello")
public class HelloWorldResource extends org.restlet.resource.ServerResource {
    private String name;

    @Override
    protected void doInit() throws ResourceException {
        // Extract the "name" query parameter from the request
        this.name = getQueryValue("name");
        if (this.name == null) {
            this.name = "world";  // default to "world" if no name is provided
        }
    }

    @Get
    @ApiOperation(value = "Get a hello message",
            notes = "Returns a hello message. You can specify a name as a query parameter.",
            response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "Name to be included in the hello message", required = false, dataType = "string", paramType = "query")
    })
    public String represent() {
        return "hello, " + this.name;
    }
}
