package org.modelio.module.scaladesigner.impl;

import java.util.Map;

import org.modelio.api.log.ILogService;
import org.modelio.api.module.DefaultModuleSession;
import org.modelio.api.module.ModuleException;
import org.modelio.api.modelio.Modelio;
import org.modelio.vbasic.version.Version;

/**
 * Implementation of the IModuleSession interface.
 * <br>This default implementation may be inherited by the module developers in order to simplify the code writing of the module session.
 */
public class ScalaDesignerSession extends DefaultModuleSession {

	/**
	 * Constructor.
	 * @param module the Module this session is instanciated for.
	 */
	public ScalaDesignerSession(ScalaDesignerModule module) {
		super(module);
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleSession#start()
	 */
	@Override
	public boolean start() throws ModuleException {
		// get the version of the module
		Version moduleVersion = this.module.getVersion();

		// get the Modelio log service
		ILogService logService = Modelio.getInstance().getLogService();

		String message = "Start of " + this.module.getName() + " " + moduleVersion;
		logService.info(this.module, message);
		return super.start();
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleSession#stop()
	 */
	@Override
	public void stop() throws ModuleException {
		super.stop();
	}

	public static boolean install(String modelioPath, String mdaPath) throws ModuleException {
		return DefaultModuleSession.install(modelioPath, mdaPath);
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleSession#select()
	 */
	@Override
	public boolean select() throws ModuleException {
		return super.select();
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleSession#unselect()
	 */
	@Override
	public void unselect() throws ModuleException {
		super.unselect();
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleSession#upgrade(org.modelio.api.modelio.Version, java.util.Map)
	 */
	@Override
	public void upgrade(Version oldVersion, Map<String, String> oldParameters) throws ModuleException {
		super.upgrade(oldVersion, oldParameters);
	}
}
