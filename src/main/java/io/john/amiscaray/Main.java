package io.john.amiscaray;

import io.john.amiscaray.web.Application;
import org.apache.catalina.LifecycleException;

public class Main extends Application {
    private static Main instance = new Main();

    public static void main(String[] args) throws LifecycleException {
        instance.start();
    }

}