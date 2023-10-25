package org.example.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.example.config.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigModule extends AbstractModule {
    private static final Logger LOGGER = Logger.getLogger(ConfigModule.class.getName());

    @Override
    protected void configure() {
        Names.bindProperties(binder(), readProperties());
        bind(Configuration.class);
    }

    protected Properties readProperties(){
        InputStream is = this.getClass().getResourceAsStream("/app.properties");
        Properties props = new Properties();
        try {
            if (is != null) {
                props.load(is);
            } else {
                LOGGER.log(Level.SEVERE, "Could not find app.properties file");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while reading app.properties file", e);
        }
        return props;
    }
}
