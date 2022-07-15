package org.grits.toolbox.tools.databasebot.wizards.createnew;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.core.utilShare.ErrorUtils;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.DatabaseUtils;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.GlycanStructureDatabase;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.GlycanStructureDatabaseIndex;
import org.grits.toolbox.tools.databasebot.om.DatabaseBotSettings;
import org.grits.toolbox.tools.databasebot.utils.DatabaseBotException;
import org.grits.toolbox.tools.databasebot.utils.DatabaseUtil;

public class DatabaseTypePage extends WizardPage
{
    private static final Logger logger = Logger.getLogger(DatabaseTypePage.class);

    private Button m_radioNewDatabase = null;
    private Button m_radioExtendDatabase = null;
    private Button m_radioFileDatabase = null;
    private Button m_buttonBrowseDatabaseFile = null;
    private Button m_buttonBrowseReportFile = null;
    private Button m_checkCreateReport = null;
    private Button m_checkCreateTopology = null;
    private Combo m_comboDatabases = null;
    private Combo m_comboScalingFactor = null;
    private Text m_textDatabaseFile = null;
    private Text m_textReportFile = null;
    private GlycanStructureDatabaseIndex m_databases = null;

    public DatabaseTypePage(ImageDescriptor a_titleImage)
    {
        super("Type of database", "Type of database", a_titleImage);
        this.setDescription("Create a new database or extend an existing database.");
    }

    @Override
    public void createControl(Composite a_parent)
    {
        // create a composite for the controls of this page
        Composite t_container = new Composite(a_parent, SWT.NONE);
        // set the GRID layout
        t_container.setLayout(new GridLayout(1, false));
        // place the Type of database group
        this.createControlSourceOfDatabase(t_container);
        // empty first cell
        new Label(t_container, SWT.NONE);
        // place the checkbox
        this.m_checkCreateTopology = new Button(t_container, SWT.CHECK);
        this.m_checkCreateTopology.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        this.m_checkCreateTopology.setText("Create topology database");
        // empty first cell
        new Label(t_container, SWT.NONE);
        // place the Report group
        this.createControlReport(t_container);
        // disable controls for the start and set radio buttons
        this.m_radioNewDatabase.setSelection(true);
        this.m_comboDatabases.setEnabled(false);
        this.m_textDatabaseFile.setEnabled(false);
        this.m_buttonBrowseDatabaseFile.setEnabled(false);
        this.m_textReportFile.setEnabled(false);
        this.m_buttonBrowseReportFile.setEnabled(false);
        this.m_comboScalingFactor.setEnabled(false);
        // page is finished by default
        this.setControl(t_container);
        this.setPageComplete(true);
    }

    private void createControlReport(Composite a_container)
    {
        // create a new group for report and fill horizontal layout
        Group t_groupReport = new Group(a_container, SWT.NONE);
        GridData t_gridReport = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        t_groupReport.setLayoutData(t_gridReport);
        t_groupReport.setText("Excel report");
        // 3 column grid
        t_groupReport.setLayout(new GridLayout(4, false));
        // *** First Row : Checkbox
        // place the checkbox
        this.m_checkCreateReport = new Button(t_groupReport, SWT.CHECK);
        this.m_checkCreateReport.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1));
        this.m_checkCreateReport.setText("Create report");
        // *** Second Row : File field and browse button
        // empty first cell
        Label t_placeholder = new Label(t_groupReport, SWT.NONE);
        GridData t_gridDataLabel = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        t_gridDataLabel.widthHint = 15;
        t_placeholder.setLayoutData(t_gridDataLabel);
        // text field for report file
        this.m_textReportFile = new Text(t_groupReport, SWT.BORDER);
        this.m_textReportFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        this.m_textReportFile.setEditable(false);
        // browse button
        this.m_buttonBrowseReportFile = new Button(t_groupReport, SWT.NONE);
        GridData t_gridDataButton = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        t_gridDataButton.widthHint = 100;
        this.m_buttonBrowseReportFile.setLayoutData(t_gridDataButton);
        this.m_buttonBrowseReportFile.setText("Save As");
        // *** Third Row : Scale control
        // empty first cell
        t_placeholder = new Label(t_groupReport, SWT.NONE);
        t_gridDataLabel = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        t_gridDataLabel.widthHint = 15;
        t_placeholder.setLayoutData(t_gridDataLabel);
        // Label
        Label t_dropdownText = new Label(t_groupReport, SWT.NONE);
        GridData t_gridDataDropLabel = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        t_dropdownText.setLayoutData(t_gridDataDropLabel);
        t_dropdownText.setText("Image Scaling Factor");
        // dropdown
        this.m_comboScalingFactor = new Combo(t_groupReport, SWT.DROP_DOWN | SWT.READ_ONLY);
        this.m_comboScalingFactor.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        this.m_comboScalingFactor.add("50%");
        this.m_comboScalingFactor.add("75%");
        this.m_comboScalingFactor.add("100%");
        this.m_comboScalingFactor.select(0);
        // check box action
        this.m_checkCreateReport.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent a_event)
            {
                Button t_button = (Button) a_event.widget;
                if (t_button.getSelection())
                {
                    m_textReportFile.setEnabled(true);
                    m_buttonBrowseReportFile.setEnabled(true);
                    m_comboScalingFactor.setEnabled(true);
                }
                else
                {
                    m_textReportFile.setEnabled(false);
                    m_buttonBrowseReportFile.setEnabled(false);
                    m_comboScalingFactor.setEnabled(false);
                }
                validate();
            }
        });
        this.m_buttonBrowseReportFile.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent a_event)
            {
                try
                {
                    FileDialog t_dialogSave = new FileDialog(a_container.getShell(), SWT.SAVE);
                    t_dialogSave.setFilterNames(new String[] { "Excel Report (*.xls)" });
                    t_dialogSave.setFilterExtensions(new String[] { "*.xls" });
                    t_dialogSave.setFileName("DatabaseReport.xls");
                    String t_file = t_dialogSave.open();
                    if (t_file != null)
                    {
                        File t_fileHandle = new File(t_file);
                        if (t_fileHandle.exists())
                        {
                            int t_response = ErrorUtils.createSingleConfirmationMessageBoxReturn(a_container.getShell(),
                                    "Report exists",
                                    "The selected report file already exists. Do you want to overwrite it?");
                            if (t_response == SWT.YES)
                            {
                                m_textReportFile.setText(t_file);
                            }
                        }
                        else
                        {
                            m_textReportFile.setText(t_file);
                        }
                    }
                    validate();
                }
                catch (Exception e)
                {
                    logger.fatal("Unable to select an excel file for a exclude report.", e);
                }
            }
        });
    }

    private void createControlSourceOfDatabase(Composite a_container)
    {
        // create a new group for database type and fill horizontal layout
        Group t_groupSourceOfDatabase = new Group(a_container, SWT.NONE);
        GridData t_gridTypeOfDatabase = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        t_groupSourceOfDatabase.setLayoutData(t_gridTypeOfDatabase);
        t_groupSourceOfDatabase.setText("Type of database");
        // configure the group with 3 columns
        t_groupSourceOfDatabase.setLayout(new GridLayout(3, false));
        // radio button for new database, span 3 columns
        this.m_radioNewDatabase = new Button(t_groupSourceOfDatabase, SWT.RADIO);
        this.m_radioNewDatabase.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        this.m_radioNewDatabase.setText("Create new database");
        // radio button for extend existing database, span 3 columns
        this.m_radioExtendDatabase = new Button(t_groupSourceOfDatabase, SWT.RADIO);
        this.m_radioExtendDatabase.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        this.m_radioExtendDatabase.setText("Extend GELATO database");
        // place a label to fill the first cell in the next row
        Label t_placeholder = new Label(t_groupSourceOfDatabase, SWT.NONE);
        GridData t_gridDataLabel = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        t_gridDataLabel.widthHint = 15;
        t_placeholder.setLayoutData(t_gridDataLabel);
        // now place the combo box for the existing databases, span two columns
        // and use max horizontal space
        this.m_comboDatabases = new Combo(t_groupSourceOfDatabase, SWT.DROP_DOWN | SWT.READ_ONLY);
        this.m_comboDatabases.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        // fill the combo field
        try
        {
            this.m_databases = DatabaseUtils.getGelatoDatabases();
            if (this.m_databases.getDatabase().size() > 0)
            {
                for (GlycanStructureDatabase t_database : this.m_databases.getDatabase())
                {
                    this.m_comboDatabases.add(t_database.getName());
                }
                this.m_comboDatabases.select(0);
            }
        }
        catch (Exception e)
        {
            logger.error("Unable to load GELATO databases", e);
        }
        // radio button for extend file database, span 3 columns
        this.m_radioFileDatabase = new Button(t_groupSourceOfDatabase, SWT.RADIO);
        this.m_radioFileDatabase.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        this.m_radioFileDatabase.setText("Extend file database");
        // place a label to fill the first cell in the next row
        new Label(t_groupSourceOfDatabase, SWT.NONE);
        // add the text field for the database file
        this.m_textDatabaseFile = new Text(t_groupSourceOfDatabase, SWT.BORDER);
        this.m_textDatabaseFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        this.m_textDatabaseFile.setEditable(false);
        // add the browse button
        this.m_buttonBrowseDatabaseFile = new Button(t_groupSourceOfDatabase, SWT.NONE);
        GridData t_gridDataButton = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        t_gridDataButton.widthHint = 100;
        this.m_buttonBrowseDatabaseFile.setLayoutData(t_gridDataButton);
        this.m_buttonBrowseDatabaseFile.setText("Browse");
        // actions for the radio buttons
        this.m_radioNewDatabase.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                m_comboDatabases.setEnabled(false);
                m_textDatabaseFile.setEnabled(false);
                m_buttonBrowseDatabaseFile.setEnabled(false);
                validate();
            }
        });
        this.m_radioExtendDatabase.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                m_comboDatabases.setEnabled(true);
                m_textDatabaseFile.setEnabled(false);
                m_buttonBrowseDatabaseFile.setEnabled(false);
                validate();
            }
        });
        this.m_radioFileDatabase.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                m_comboDatabases.setEnabled(false);
                m_textDatabaseFile.setEnabled(true);
                m_buttonBrowseDatabaseFile.setEnabled(true);
                validate();
            }
        });
        // click action for the browse button
        this.m_buttonBrowseDatabaseFile.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent a_event)
            {
                try
                {
                    FileDialog t_dialogSave = new FileDialog(a_container.getShell());
                    t_dialogSave.setFilterNames(new String[] { "Glycan structure database (.xml)", "All files" });
                    t_dialogSave.setFilterExtensions(new String[] { "*.xml", "*.*" });
                    String t_file = t_dialogSave.open();
                    if (t_file != null)
                    {
                        if (DatabaseUtil.isValidDatabaseFile(t_file))
                        {
                            m_textDatabaseFile.setText(t_file);
                        }
                        else
                        {
                            ErrorUtils.createErrorMessageBox(a_container.getShell(), "Error in the database file",
                                    "The selected databases file is not in a valid format.\nPlease select a valid file.");
                        }
                    }
                    validate();
                }
                catch (Exception e)
                {
                    logger.fatal("Unable to select a database file.", e);
                }
            }
        });
    }

    private void validate()
    {
        if (this.isValidInput())
        {
            this.setErrorMessage(null);
            this.setPageComplete(true);
        }
        else
        {
            this.setPageComplete(false);
        }
    }

    private boolean isValidInput()
    {
        // if extending is selected look if a db is selected in the combo
        if (this.m_radioExtendDatabase.getSelection())
        {
            String t_databaseName = this.m_comboDatabases.getText();
            GlycanStructureDatabase t_db = this.findDatabaseForName(t_databaseName);
            if (t_db == null)
            {
                this.setErrorMessage("Please select a valid GELATO database.");
                return false;
            }
        }
        // if file database is selected check the file
        if (this.m_radioFileDatabase.getSelection())
        {
            String t_fileName = this.m_textDatabaseFile.getText();
            if (t_fileName != null)
            {
                File t_file = new File(t_fileName);
                if (!t_file.exists() || !t_file.isFile())
                {
                    this.setErrorMessage("Please select a valid database file.");
                    return false;
                }
            }
            else
            {
                this.setErrorMessage("Please select a database file.");
                return false;
            }
        }
        // see if a report file is selected
        if (this.m_checkCreateReport.getSelection())
        {
            String t_fileName = this.m_textReportFile.getText();
            if (t_fileName != null)
            {
                if (t_fileName.length() == 0)
                {
                    this.setErrorMessage("Please select a report file.");
                    return false;
                }
            }
            else
            {
                this.setErrorMessage("Please select a report file.");
                return false;
            }
        }
        return true;
    }

    public void fillCreateDatabaseSettings(DatabaseBotSettings a_settings) throws DatabaseBotException
    {
        // if we extend a GELATO database
        if (this.m_radioExtendDatabase.getSelection())
        {
            // check if its a valid entry in the GELATO list, otherwise throw
            // exception
            String t_databaseName = this.m_comboDatabases.getText();
            GlycanStructureDatabase t_db = this.findDatabaseForName(t_databaseName);
            String t_databaseFile;
            try
            {
                if (t_db == null)
                {
                    logger.error("Could not find database (" + t_databaseName + ") building settings object");
                    throw new DatabaseBotException("Unable to load database that should be extended.");
                }
                t_databaseFile = DatabaseUtils.getDatabasePath() + File.separator + t_db.getFileName();
            }
            catch (Exception e)
            {
                logger.error("Problems building the path for the database to be extended", e);
                throw new DatabaseBotException("Unable to load database that should be extended.");
            }
            a_settings.setExtendDatabaseFile(t_databaseFile);
        }
        // if we extend a database file, store the path
        if (this.m_radioFileDatabase.getSelection())
        {
            a_settings.setExtendDatabaseFile(this.m_textDatabaseFile.getText());
        }
        // if a exclude report is to be created select the file
        if (this.m_checkCreateReport.getSelection())
        {
            a_settings.setReportFile(this.m_textReportFile.getText());
            a_settings.setImageScalingFactor(this.getImageScalingFactor());
        }
        // topology database
        if (this.m_checkCreateTopology.getSelection())
        {
            a_settings.setTopologyDatabase(true);
        }
        else
        {
            a_settings.setTopologyDatabase(false);
        }
    }

    /**
     * Retieve the text from the dropdown for image scaling factor and convert
     * into a 1 based (1=100%) number.
     *
     * @return Scaling factor where 1 = 100%
     */
    private Double getImageScalingFactor()
    {
        String t_selection = this.m_comboScalingFactor.getText();
        if (t_selection.equals("100%"))
        {
            return 1D;
        }
        else if (t_selection.equals("75%"))
        {
            return 0.75D;
        }
        else
        {
            return 0.5D;
        }
    }

    private GlycanStructureDatabase findDatabaseForName(String a_name)
    {
        // if database have not been loaded return null
        if (this.m_databases == null)
        {
            return null;
        }
        // search in the list for the right one by name
        for (GlycanStructureDatabase t_database : this.m_databases.getDatabase())
        {
            if (t_database.getName().equals(a_name))
            {
                return t_database;
            }
        }
        logger.error("Unable to find GELATO database: " + a_name);
        return null;
    }

}
