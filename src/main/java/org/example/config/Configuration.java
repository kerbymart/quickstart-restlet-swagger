package org.example.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Configuration {
    private final int rateLimit;

    // Constructor injection is used to ensure that the fields are always initialized.
    // The @Inject annotation indicates that this constructor should be used by the Guice dependency injection framework.
    // The @Named annotation specifies that the value of the parameters should be obtained from the Guice configuration using the keys.
    @Inject
    public Configuration(@Named("firewall.ratelimit") int rateLimit) {
        this.rateLimit = rateLimit;
    }

    public int getRateLimit() {
        return rateLimit;
    }
}
