package org.modelio.module.scaladesigner.impl;

import org.modelio.api.model.IModelingSession;
import org.modelio.api.modelio.Modelio;
import org.modelio.api.module.AbstractJavaModule;
import org.modelio.api.module.IParameterEditionModel;
import org.modelio.api.module.IModuleAPIConfiguration;
import org.modelio.api.module.IModuleSession;
import org.modelio.api.module.IModuleUserConfiguration;
import org.modelio.api.module.paramEdition.*;
import org.modelio.metamodel.mda.ModuleComponent;
import org.modelio.module.scaladesigner.api.ScalaDesignerParameters;
import org.modelio.module.scaladesigner.i18n.Messages;

/**
 * Implementation of the IModule interface.
 * <br>All Modelio java modules should inherit from this class.
 * 
 */
public class ScalaDesignerModule extends AbstractJavaModule {

	private ScalaDesignerPeerModule peerModule = null;

	private ScalaDesignerSession session = null;

	public static ScalaDesignerLogService logService;

	@Override
	public ScalaDesignerPeerModule getPeerModule() {
		return this.peerModule;
	}

	/**
	 * Return the session attached to the current module.
	 * <p>
	 * <p>
	 * This session is used to manage the module lifecycle by declaring the
	 * desired implementation on start, select... methods.
	 */
	@Override
	public IModuleSession getSession() {
		return this.session;
	}

	/**
	 * Method automatically called just after the creation of the module.
	 * <p>
	 * <p>
	 * The module is automatically instanciated at the beginning of the MDA
	 * lifecycle and constructor implementation is not accessible to the module
	 * developer.
	 * <p>
	 * <p>
	 * The <code>init</code> method allows the developer to execute the desired initialization code at this step. For
     * example, this is the perfect place to register any IViewpoint this module provides.
	 *
	 *
	 * @see org.modelio.api.module.AbstractJavaModule#init()
	 */
	@Override
	public void init() {
		// Add the module initialization code
	    super.init();
	}

    /**
     * Method automatically called just before the disposal of the module.
     * <p>
     * <p>
     * 
     * 
     * The <code>uninit</code> method allows the developer to execute the desired un-initialization code at this step.
     * For example, if IViewpoints have been registered in the {@link #init()} method, this method is the perfect place
     * to remove them.
     * <p>
     * <p>
     * 
     * This method should never be called by the developer because it is already invoked by the tool.
     * 
     * @see org.modelio.api.module.AbstractJavaModule#uninit()
     */
    @Override
    public void uninit() {
        // Add the module un-initialization code
        super.uninit();
    }
    
	/**
	 * Builds a new module.
	 * <p>
	 * <p>
	 * This constructor must not be called by the user. It is automatically
	 * invoked by Modelio when the module is installed, selected or started.
     * @param modelingSession the modeling session this module is deployed into.
     * @param moduleComponent the model part of this module.
     * @param moduleConfiguration the module configuration, to get and set parameter values from the module itself.
     * @param peerConfiguration the peer module configuration, to get and set parameter values from another module. 
	 */
	public ScalaDesignerModule(IModelingSession modelingSession, ModuleComponent moduleComponent, IModuleUserConfiguration moduleConfiguration, IModuleAPIConfiguration peerConfiguration) {
	    super(modelingSession, moduleComponent, moduleConfiguration);
		this.session = new ScalaDesignerSession(this);
		this.peerModule = new ScalaDesignerPeerModule(this, peerConfiguration);
		logService = new ScalaDesignerLogService(Modelio.getInstance().getLogService(), this);
		this.peerModule.init(); //TODO: in java there is no this line
	}

	/**
	 * @see org.modelio.api.module.AbstractJavaModule#getParametersEditionModel()
	 */
	@Override
	public IParameterEditionModel getParametersEditionModel() {
	    if (this.parameterEditionModel == null) {
			IModuleUserConfiguration configuration = this.getConfiguration();
			ParametersEditionModel parameters = new ParametersEditionModel(this);
			EnumParameterModel enumParameter;
			this.parameterEditionModel = parameters;

			ParameterGroupModel locations = new ParameterGroupModel("LocationsGM",
					Messages.getString("Ui.Parameter.GroupModel.Locations"));
			parameters.addGroup(locations);
			DirectoryParameterModel scalaSourcesParameter = new DirectoryParameterModel(configuration, ScalaDesignerParameters.SCALA_SOURCES,
					Messages.getString("Ui.Parameter.ScalaSources.Label"), Messages.getString("Ui.Parameter.ScalaSources.Description"), "");
			locations.addParameter(scalaSourcesParameter);

			FileParameterModel scalaCompilerParameter = new FileParameterModel(configuration, ScalaDesignerParameters.SCALA_COMPILER,
					Messages.getString("Ui.Parameter.ScalaCompiler.Label"), Messages.getString("Ui.Parameter.ScalaCompiler.Description"), "");
			//TODO: add .sh for UNIX systems
			scalaCompilerParameter.addAllowedExtension("*.exe","Executable files (*.exe)");
			locations.addParameter(scalaCompilerParameter);
	    }
		return this.parameterEditionModel;
	}
	
	@Override
    public String getModuleImagePath() {
        return "/res/icons/module_16.png";
    }

}
