package org.example.docs;

import com.wordnik.swagger.models.Contact;
import com.wordnik.swagger.models.Info;
import com.wordnik.swagger.models.License;
import com.wordnik.swagger.models.Swagger;
import com.wordnik.swagger.models.auth.ApiKeyAuthDefinition;
import com.wordnik.swagger.models.auth.In;
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition;
import org.restlet.Application;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.swagger.Swagger2SpecificationRestlet;
import org.restlet.representation.Representation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnhancedSwagger2SpecificationRestlet extends Swagger2SpecificationRestlet {

    public EnhancedSwagger2SpecificationRestlet(Application application) {
        super(application);
    }

    @Override
    public Representation getSwagger() {
        Representation representation = super.getSwagger();
        JacksonRepresentation<Swagger> jacksonRepresentation = (JacksonRepresentation<Swagger>) representation;
        Swagger swagger = null;
        try {
            swagger = jacksonRepresentation.getObject();
            // Set the info with contact and terms of service
            Info info = swagger.getInfo();
            if (info == null) {
                info = new Info();
            }
            info.setContact(new Contact()
                    .name("API Support")
                    .url("http://support-url.com")
                    .email("support@example.com"));
            info.setTermsOfService("http://terms-of-service-url.com");
            info.setLicense(new License()
                    .name("Apache 2.0")
                    .url("http://www.apache.org/licenses/LICENSE-2.0.html"));
            swagger.setInfo(info);

            // Add security definitions
            Map<String, SecuritySchemeDefinition> securityDefinitions = new HashMap<>();
            securityDefinitions.put("api_key", new ApiKeyAuthDefinition("api_key", In.HEADER));
            swagger.setSecurityDefinitions(securityDefinitions);

            // Update the Jackson representation with the modified Swagger object
            jacksonRepresentation.setObject(swagger);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jacksonRepresentation;
    }
}

