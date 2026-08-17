package org.allsparks.echo.render;

public final class RendererException extends Exception {
    public RendererException(String message) {
        super(message);
    }

    public RendererException(String message, Throwable cause) {
        super(message, cause);
    }
}
