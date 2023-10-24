package org.example.resources;

import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public class RootResource extends org.restlet.resource.ServerResource {
    @Override
    protected void doInit() throws ResourceException {
        // Initialization code if needed
    }

    @Get
    public String represent() {
        return "welcome to the root resource";
    }
}
