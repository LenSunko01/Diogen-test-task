package jub.diogen.utils;

import org.springframework.stereotype.Component;

@Component
public class EnvironmentUtil {
    public String getVariable(String name) {
        return System.getenv(name);
    }
}
