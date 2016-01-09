package org.modelio.module.scaladesigner.impl;

import org.modelio.api.log.ILogService;
import org.modelio.api.module.IModule;

/**
 * Proxy for the Modelio {@link ILogService}, configuring the JavaDesignerModule.
 */
public class ScalaDesignerLogService {
    private ILogService logService;
    private IModule module;

    /**
     * Default constructor.
     * @param logService the Modelio log service.
     * @param module the current instance of {@link IModule}.
     */
    public ScalaDesignerLogService(ILogService logService, IModule module) {
        this.logService = logService;
        this.module = module;
    }


    /**
     * Output an information message in the Modelio console.
     * <p>
     * This method send logs on Modelio console only if the logs have been activated. The file and line of the log is
     * displayed in the Modelio console before the message.
     * @param msg a message to be displayed as a log.
     * */
    public void info(final String msg) {
        this.logService.info(this.module, msg);
    }

    /**
     * Output a warning message in the Modelio console.
     * <p>
     * This method send logs on Modelio console only if the logs have been activated. The file and line of the log is
     * displayed in the Modelio console before the message.
     * @param msg a message to be displayed as a log.
     * */
    public void warning(final String msg) {
        this.logService.warning(this.module, msg);
    }

    /**
     * Output an error message in the Modelio console.
     * <p>
     * This method send logs on Modelio console only if the logs have been activated. The file and line of the log is
     * displayed in the Modelio console before the message.
     * @param msg a message to be displayed as a log.
     */
    public void error(final String msg) {
        this.logService.error(this.module, msg);
    }

    /**
     * Log the given exception with its stack trace as an information.
     * <p>
     * This method send logs on Modelio console only if the logs have been activated. The file and line of the log is
     * displayed in the Modelio console before the message.
     * @param t an exception to be displayed as a log.
     * */
    public void info(final Throwable t) {
        this.logService.info(this.module, t);
    }

    /**
     * Log the given exception with its stack trace as a warning.
     * <p>
     * This method send logs on Modelio console only if the logs have been activated. The file and line of the log is
     * displayed in the Modelio console before the message.
     * @param t an exception to be displayed as a log.
     */
    public void warning(final Throwable t) {
        this.logService.warning(this.module, t);
    }

    /**
     * Log the given exception with its stack trace as an error.
     * <p>
     * This method send logs on Modelio console only if the logs have been activated. The file and line of the log is
     * displayed in the Modelio console before the message.
     * @param t an exception to be displayed as a log.
     */
    public void error(final Throwable t) {
        this.logService.error(this.module, t);
    }
}
