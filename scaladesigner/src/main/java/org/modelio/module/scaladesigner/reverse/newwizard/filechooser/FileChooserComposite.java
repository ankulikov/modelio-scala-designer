package org.modelio.module.scaladesigner.reverse.newwizard.filechooser;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.modelio.api.ui.UIColor;
import org.modelio.module.scaladesigner.i18n.Messages;
import org.modelio.module.scaladesigner.reverse.newwizard.ImageManager;
import org.modelio.module.scaladesigner.reverse.newwizard.api.IFileChooserModel;

import java.io.File;

public class FileChooserComposite extends Composite implements Listener {
    public IFileChooserModel model;

    public CheckboxTreeViewer treeViewer;

    public Text addressText;

    public Button fileChooserButton;

    public Text previewText;

    public ComboViewer granularityCombo;


    public FileChooserComposite(Composite parent, IFileChooserModel model) {
        super(parent, SWT.NONE);
        
        this.model = model;
        this.createContent();
    }

    public void createContent() {
        this.setLayout(new GridLayout(2, false));

        boolean isDirectory = model.getInitialDirectory().isDirectory();
        String absolutePath = isDirectory ? model.getInitialDirectory().getAbsolutePath(): model.getInitialDirectory().getParentFile().getAbsolutePath();

        this.addressText = new Text(this, SWT.BORDER);
        this.addressText.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
                true, false));
        this.addressText.setText(absolutePath);
        this.addressText.addListener(SWT.Modify, this);
        
        this.fileChooserButton = new Button(this, SWT.NONE);
        this.fileChooserButton.setImage(ImageManager.getInstance().getIcon("directory"));
        this.fileChooserButton.addListener(SWT.Selection, this);
        this.fileChooserButton.setLayoutData(new GridData(SWT.FILL,
                SWT.FILL, false, false));
        
        this.previewText = new Text(this, SWT.READ_ONLY);
        this.previewText.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
                true, false, 2, 1));
        this.previewText.setText(Messages.getString("Gui.ScalaReverseWizardView.FileChooserTab.Preview",  absolutePath));
        this.previewText.setForeground(UIColor.LABEL_TIP_FG);

        
        this.treeViewer = new CheckboxTreeViewer(this, SWT.SINGLE
                | SWT.BORDER | SWT.CHECK);
        this.treeViewer.setContentProvider(new FileContentProvider());
        this.treeViewer.setLabelProvider(new FileLabelProvider());
        this.treeViewer.setInput(isDirectory? this.model.getInitialDirectory():model.getInitialDirectory().getParentFile());

        
        FileFilter[] filters = { new FileFilter(this.model.getValidExtensions()) };
        this.treeViewer.setFilters(filters);
        
        this.treeViewer.setSorter(new FileViewerSorter());
        
        TreeViewerListener hook = new TreeViewerListener(this.treeViewer, this.model.getFilesToImport());
        this.treeViewer.addTreeListener(hook);
        this.treeViewer.addCheckStateListener(hook);
        
        this.treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        if (!isDirectory) {
            treeViewer.setChecked(model.getInitialDirectory(), true);
            //small hack to apply 'on check' logic
            hook.checkStateChanged(new CheckStateChangedEvent(treeViewer,model.getInitialDirectory(),true));
        }
    }


    @Override
    public void handleEvent(Event event) {
        if (event.widget.equals(this.fileChooserButton)) {
            DirectoryDialog dialog = new DirectoryDialog(this.getShell());
            dialog.setFilterPath(this.model.getInitialDirectory().getAbsolutePath());
            String file = dialog.open();
            if (file != null) {
                this.addressText.setText(file);
            }
        } else if (event.widget.equals(this.addressText)) {
            Text textField = (Text) event.widget;
            String fileName = textField.getText();
            if (!fileName.endsWith("/")) {
                fileName += "/";
            }
            File newFile = new File(fileName);
            if (newFile.exists()) {
                this.model.setInitialDirectory(newFile);
                this.previewText.setText(Messages.getString("Gui.ScalaReverseWizardView.FileChooserTab.Preview", fileName));
                this.previewText.redraw();
                this.treeViewer.setInput(newFile);
            }
        }
    }

}
