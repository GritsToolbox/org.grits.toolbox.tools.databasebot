package org.grits.toolbox.tools.databasebot.wizards;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eurocarbdb.application.glycanbuilder.GlycanRendererAWT;
import org.eurocarbdb.application.glycoworkbench.GlycanWorkspace;
import org.grits.toolbox.core.utilShare.ErrorUtils;
import org.grits.toolbox.tools.databasebot.om.DatabaseBotSettings;
import org.grits.toolbox.tools.databasebot.utils.DatabaseBotException;
import org.grits.toolbox.tools.databasebot.utils.ImageCache;
import org.grits.toolbox.tools.databasebot.wizards.createnew.DatabaseInformationPage;
import org.grits.toolbox.tools.databasebot.wizards.createnew.DatabaseTypePage;
import org.grits.toolbox.tools.databasebot.wizards.createnew.FilterPage;
import org.grits.toolbox.tools.databasebot.wizards.createnew.SourcePage;

/**
 * Wizard for collecting the settings for the database creation process.
 *
 * @author rene
 *
 */
public class CreateNewDatabaseWizard extends Wizard
{
    /**
     * ImageDescriptor with the banner icon, will be loaded in the contructor.
     * Null if not be able to load it
     */
    public static ImageDescriptor DIALOG_IMAGE = null;
    /** logging object */
    private static final Logger logger = Logger.getLogger(CreateNewDatabaseWizard.class);
    /** First page: Source information */
    private SourcePage m_pageSource = null;
    /** Second page: Database information */
    private DatabaseInformationPage m_pageInformation = null;
    /** Third page: Database type */
    private DatabaseTypePage m_pageDatabaseType = null;
    /** Forth page: General filter settings */
    private FilterPage m_pageFilter = null;
    /** GWB object, has to be initialized to be able to load sequences */
    protected GlycanWorkspace m_gwb = null;
    /**
     * Collection of settings from the pages, is fill once the Finish button is
     * clicked
     */
    private DatabaseBotSettings m_settings = new DatabaseBotSettings();

    /**
     * Default constructor will load the banner icon if possible, create the
     * pages and initialize the GWB object.
     */
    public CreateNewDatabaseWizard()
    {
        super();
        // set the title and load the banner icon
        this.setWindowTitle("Start Database Bot");
        try
        {
            CreateNewDatabaseWizard.DIALOG_IMAGE = ImageCache.getImageDescriptor(CreateNewDatabaseWizard.class,
                    "icons/wizard-logo.png");
        }
        catch (IOException e)
        {
            // did not work, so icon will be null => nothing shown
            logger.error("Unable to load dialog image", e);
        }
        // create the pages
        this.m_pageSource = new SourcePage(CreateNewDatabaseWizard.DIALOG_IMAGE);
        this.m_pageInformation = new DatabaseInformationPage(CreateNewDatabaseWizard.DIALOG_IMAGE);
        this.m_pageDatabaseType = new DatabaseTypePage(CreateNewDatabaseWizard.DIALOG_IMAGE);
        this.m_pageFilter = new FilterPage(CreateNewDatabaseWizard.DIALOG_IMAGE);
        // create GWB object to initialize static fields needed to load
        // sequences
        try
        {
            this.m_gwb = new GlycanWorkspace(new GlycanRendererAWT());
        }
        catch (Exception e)
        {
            logger.error("Unable to initialitze GWB", e);
        }
    }

    /**
     * Returns the wizard page that would to be shown if the user was to press
     * the Next button.
     *
     * @return the next wizard page, or <code>null</code> if none
     */
    @Override
    public IWizardPage getNextPage(IWizardPage a_page)
    {
        return super.getNextPage(a_page);
    }

    /**
     * Adds all pages to the wizard. Is called by the wizard framework.
     */
    @Override
    public void addPages()
    {
        this.addPage(this.m_pageDatabaseType);
        this.addPage(this.m_pageInformation);
        this.addPage(this.m_pageSource);
        this.addPage(this.m_pageFilter);
    }

    /**
     * Performs any actions appropriate in response to the user having pressed
     * the Finish button, or refuse if finishing now is not permitted. It will
     * try to fill the settings object from the pages and show error messages (+
     * return false) if that is not possible
     *
     * @return <code>true</code> to indicate the finish request was accepted,
     *         and <code>false</code> to indicate that the finish request was
     *         refused because the settings object could not be created
     */
    @Override
    public boolean performFinish()
    {
        // copy the database settings
        try
        {
            this.m_pageDatabaseType.fillCreateDatabaseSettings(this.m_settings);
        }
        catch (DatabaseBotException e)
        {
            // happens if the database selected for extension can not be found
            ErrorUtils.createErrorMessageBox(this.getShell(), "Error creating settings for DatabaseBot",
                    e.getMessage());
            logger.error("Error creating database settings settings", e);
            return false;
        }
        // copy the information, can not fail
        this.m_pageInformation.fillCreateDatabaseSettings(this.m_settings);
        // copy the source settings, can not fail
        this.m_pageSource.fillCreateDatabaseSettings(this.m_settings);
        // copy the filter settings, can not fail
        this.m_pageFilter.fillCreateDatabaseSettings(this.m_settings);
        return true;
    }

    /**
     * Used to retrieve the settings object after pressing the finish button. If
     * the cancel button was pressed the object is initialized with default
     * values
     *
     * @return <code>DatabaseBotSettings</code> object with the user settings if
     *         finished button was pressed in the wizard
     */
    public DatabaseBotSettings getSettings()
    {
        return this.m_settings;
    }

}
