package org.modelio.module.scaladesigner.report;

import org.eclipse.swt.widgets.Display;
import org.modelio.api.modelio.Modelio;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.report.ReportModel.ElementMessage;

public class ReportManager {
    private static ReportDialog dialog;


    public static void showGenerationReport(ReportModel report) {
        if (report == null || report.isEmpty ()) {
            if (ReportManager.dialog != null &&
                    !ReportManager.dialog.isDisposed ()) {
                ReportManager.dialog.close ();
            }
        } else {
            if (ReportManager.dialog == null ||
                    ReportManager.dialog.isDisposed ()) {
                // Get the current display
                Display display = Display.getCurrent ();
                if (display == null) {
                    display = Display.getDefault ();
                }
        
                ReportManager.dialog = new ReportDialog (display.getActiveShell (), Modelio.getInstance().getNavigationService());
            }
        
            ReportManager.dialog.setModel (report);
            ReportManager.dialog.open ();
        }
    }

    public static ReportModel getNewReport() {
        return new ReportModel ();
    }

    public static void printGenerationReport(ReportModel report) {
        if (report != null && !report.isEmpty ()) {
            for (ElementMessage errorMsg : report.getErrors ()) {
                ScalaDesignerModule.logService.error(errorMsg.message);
            }
        
            for (ElementMessage warningMsg : report.getWarnings ()) {
                ScalaDesignerModule.logService.info (warningMsg.message);
            }
        
            for (ElementMessage infoMsg : report.getInfos ()) {
                ScalaDesignerModule.logService.info (infoMsg.message);
            }
        }
    }

}
