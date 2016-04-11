package org.modelio.module.scaladesigner.reverse.newwizard;

import org.eclipse.swt.graphics.Image;
import org.modelio.api.modelio.Modelio;
import org.modelio.vcore.smkernel.mapi.MObject;

import java.util.HashMap;

/**
 * Singleton class used to load images. Images are loaded from the Modelio path,
 * and stored in a map to avoid multiple loadings.
 */
public class ImageManager {
    private static ImageManager INSTANCE;

    private static String modulePath = null;

    private HashMap<String, Image> map;


    public static void setModulePath(String cxxPath) {
        ImageManager.modulePath = cxxPath;
    }

    private ImageManager() {
        this.map = new HashMap<>();
        
        // Here, you can define images for buttons...
        this.map.put("up", new Image(null, modulePath + "/res/png/up.png"));
        this.map.put("down", new Image(null, modulePath + "/res/png/down.png"));
        this.map.put("delete", new Image(null, modulePath + "/res/png/delete.png"));
        this.map.put("java", new Image(null, modulePath + "/res/png/javaFile.png"));
        this.map.put("class", new Image(null, modulePath + "/res/png/classFile.png"));
        this.map.put("jarfile", new Image(null, modulePath + "/res/png/jarfile.png"));
        this.map.put("jar", new Image(null, modulePath + "/res/png/jar.png"));
        this.map.put("directory", new Image(null, modulePath + "/res/png/directory.png"));
        this.map.put("missing", new Image(null, modulePath + "/res/png/missing.png"));

        this.map.put("scala", new Image(null, modulePath + "/res/png/ScalaClass.png"));
        
        this.map.put("javaclass", new Image(null, modulePath + "/res/png/class.png"));
        this.map.put("javadatatype", new Image(null, modulePath + "/res/png/datatype.png"));
        this.map.put("javaenumeration", new Image(null, modulePath + "/res/png/enumeration.png"));
        this.map.put("javainterface", new Image(null, modulePath + "/res/png/interface.png"));
        this.map.put("javapackage", new Image(null, modulePath + "/res/png/package.png"));
    }

    public static ImageManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImageManager();
        }
        return INSTANCE;
    }

    /**
     * Get the Image corresponding to the String.
     * @param element
     * The element to use for choosing the image.
     * @return An Image.
     */
    public Image getIcon(String element) {
        Image ret;
        
        ret = this.map.get(element);
        return ret;
    }

    /**
     * Get the Image corresponding to the object.
     * @param element
     * The element to use for choosing the image.
     * @return An Image.
     */
    public Image getIcon(MObject element) {
        Image ret = Modelio.getInstance().getImageService().getStereotypedImage(element, null, false);
        return ret;
    }

}
