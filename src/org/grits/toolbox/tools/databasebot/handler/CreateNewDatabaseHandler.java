
package org.grits.toolbox.tools.databasebot.handler;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.tools.databasebot.dialog.process.ProgressDialog;
import org.grits.toolbox.tools.databasebot.process.DatabaseGenerator;
import org.grits.toolbox.tools.databasebot.wizards.CreateNewDatabaseWizard;

/**
 * Handler class for the creation of a new database. This class will open the
 * wizard to collect user settings and start the database creation process.
 *
 * @author rene
 */
public class CreateNewDatabaseHandler
{
    private static final Logger logger = Logger.getLogger(CreateNewDatabaseHandler.class);

    /**
     * Method to create the database wizard, start the download operation and
     * open the database editor
     *
     * @param a_shell
     *            active shell that can be used to create dialogs (injected)
     * @param a_partService
     *            Part service for the creation of the part used to display the
     *            database
     * @param a_modelService
     *            Model service for the retrieval of the editor part stack
     * @param a_application
     *            Application context to find the part descriptor for the
     */
    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell a_shell,
            EPartService a_partService, EModelService a_modelService, MApplication a_application)
    {
        logger.info("START COMMAND: Start database bot.");
        try
        {
            // create the wizard
            CreateNewDatabaseWizard t_wizard = new CreateNewDatabaseWizard();
            WizardDialog t_dialogWizard = new WizardDialog(PropertyHandler.getModalDialog(a_shell),
                    t_wizard);
            // set the size to make sure that the filter part does not blow up
            // the dialog
            t_dialogWizard.setPageSize(700, 450);
            // open the wizard
            if (t_dialogWizard.open() == Window.OK)
            {
                // OK was pressed now start processing the settings
                // creating a progress dialog and let the DB generator
                // (own thread) do the work
                ProgressDialog t_dialog = new ProgressDialog(a_shell);
                DatabaseGenerator t_process = new DatabaseGenerator();
                t_process.setSettings(t_wizard.getSettings());
                t_dialog.setWorker(t_process);
                t_dialog.open();
            }
        }
        catch (Exception e)
        {
            logger.fatal("Error starting databases bot: " + e.getMessage(), e);
            MessageDialog.openError(a_shell, "Error starting Database Bot",
                    "Unable run database bot due to an error:\n\n" + e.getMessage());
        }
        logger.info("END COMMAND:  Start database bot.");
    }

}