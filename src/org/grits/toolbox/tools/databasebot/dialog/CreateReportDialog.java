package org.grits.toolbox.tools.databasebot.dialog;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.core.utilShare.ErrorUtils;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.DatabaseUtils;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.GlycanStructureDatabase;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.GlycanStructureDatabaseIndex;
import org.grits.toolbox.tools.databasebot.utils.DatabaseUtil;

public class CreateReportDialog extends TitleAreaDialog
{
    private static final Logger logger = Logger.getLogger(CreateReportDialog.class);
    private Button m_radioGelatoDatabase = null;
    private Button m_radioFileDatabase = null;
    private Button m_buttonBrowseDatabaseFile = null;
    private Button m_buttonBrowseReportFile = null;
    private Combo m_comboDatabases = null;
    private Combo m_comboScalingFactor = null;
    private Text m_textDatabaseFile = null;
    private Text m_textReportFile = null;
    private GlycanStructureDatabaseIndex m_databases = null;

    public CreateReportDialog(Shell a_parentShell)
    {
        super(a_parentShell);
    }

    @Override
    protected Control createDialogArea(Composite a_parent)
    {
        logger.debug("START : Creating Tasklist UI");
        Composite t_container = (Composite) super.createDialogArea(a_parent);
        // set the GRID layout
        t_container.setLayout(new GridLayout(1, false));
        // place the Type of database group
        this.createControlSourceOfDatabase(t_container);
        // empty first cell
        new Label(t_container, SWT.NONE);
        // place the Report group
        this.createControlReport(t_container);
        // disable controls for the start and set radio buttons
        this.m_radioGelatoDatabase.setSelection(true);
        this.m_comboDatabases.setEnabled(true);
        this.m_textDatabaseFile.setEnabled(false);
        this.m_buttonBrowseDatabaseFile.setEnabled(false);
        this.m_textReportFile.setEnabled(false);
        this.m_buttonBrowseReportFile.setEnabled(false);
        this.m_comboScalingFactor.setEnabled(false);
        return a_parent;
    }

    private void createControlSourceOfDatabase(Composite a_container)
    {
        // create a new group for database type and fill horizontal layout
        Group t_groupSourceOfDatabase = new Group(a_container, SWT.NONE);
        GridData t_gridTypeOfDatabase = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        t_groupSourceOfDatabase.setLayoutData(t_gridTypeOfDatabase);
        t_groupSourceOfDatabase.setText("Select database");
        // configure the group with 3 columns
        t_groupSourceOfDatabase.setLayout(new GridLayout(3, false));
        // radio button for extend existing database, span 3 columns
        this.m_radioGelatoDatabase = new Button(t_groupSourceOfDatabase, SWT.RADIO);
        this.m_radioGelatoDatabase.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        this.m_radioGelatoDatabase.setText("GELATO database");
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
        this.m_radioFileDatabase.setText("Database file");
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
        this.m_radioGelatoDatabase.addSelectionListener(new SelectionAdapter()
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

    private void createControlReport(Composite a_container)
    {
        // create a new group for report and fill horizontal layout
        Group t_groupReport = new Group(a_container, SWT.NONE);
        GridData t_gridReport = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        t_groupReport.setLayoutData(t_gridReport);
        t_groupReport.setText("Excel report");
        // 3 column grid
        t_groupReport.setLayout(new GridLayout(3, false));
        // *** First Row : File field and browse button
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
        // *** Second Row : Scale control
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
        // click Save as button
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

    private void validate()
    {
        if (this.isValidInput())
        {
            this.setErrorMessage(null);
        }
    }

    private boolean isValidInput()
    {
        // if extending is selected look if a db is selected in the combo
        if (this.m_radioGelatoDatabase.getSelection())
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
        return true;
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
