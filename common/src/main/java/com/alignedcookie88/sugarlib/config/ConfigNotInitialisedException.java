package com.alignedcookie88.sugarlib.config;

public class ConfigNotInitialisedException extends IllegalStateException {

    public ConfigNotInitialisedException() {
        super("This config has not been initialised yet. Client and common configs are initialised as soon as they are registered (usually at game start), however server configs are initialised at world load.");
    }

}
