package io.john.amiscaray;

import io.john.amiscaray.web.Application;
import org.apache.catalina.LifecycleException;

public class Main extends Application {

    public static void main(String[] args) throws LifecycleException {
        Application application = new Application();
        application.start();
    }

}