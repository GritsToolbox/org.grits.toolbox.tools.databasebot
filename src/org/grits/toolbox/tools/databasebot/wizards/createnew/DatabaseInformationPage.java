package org.grits.toolbox.tools.databasebot.wizards.createnew;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.core.utilShare.ErrorUtils;
import org.grits.toolbox.tools.databasebot.om.DatabaseBotSettings;
import org.grits.toolbox.tools.databasebot.utils.TextUtil;

public class DatabaseInformationPage extends WizardPage
{
    private static Integer TEXT_LENGTH_NAME_VERSION = 64;
    private static Integer TEXT_LENGTH_DESCRITION = 1024;
    private static final Logger logger = Logger.getLogger(DatabaseInformationPage.class);

    private Text m_textDatabaseFile = null;
    private Text m_textDatabaseName = null;
    private Text m_textDatabaseVersion = null;
    private Text m_textDescription = null;
    private Text m_textIdPrefix = null;
    private Text m_textIdPostfix = null;
    private Button m_checkOverwriteIds = null;

    public DatabaseInformationPage(ImageDescriptor a_titleImage)
    {
        super("Database information", "Database information", a_titleImage);
        this.setDescription("Choose file location and database information.");
    }

    @Override
    public void createControl(Composite a_parent)
    {
        // create a composite for the controls of this page
        Composite t_container = new Composite(a_parent, SWT.NONE);
        // set the GRID layout
        t_container.setLayout(new GridLayout(3, false));
        // create controls for database file
        this.createFilesControl(t_container);
        // database information section
        this.createDatabaseInformationSection(t_container);
        // section for database IDs
        this.createIDsection(t_container);
        // add change listener to the text fields
        this.m_textDatabaseName.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                validate();
            }
        });
        this.m_textDatabaseVersion.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                validate();
            }
        });
        this.m_textDescription.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                validate();
            }
        });
        // page is finished by default
        this.setControl(t_container);
        this.setPageComplete(false);
    }

    private void createIDsection(Composite a_container)
    {
        // create a new group for the information and fill horizontal layout
        Group t_groupID = new Group(a_container, SWT.NONE);
        GridData t_gridID = new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1);
        t_groupID.setLayoutData(t_gridID);
        t_groupID.setText("Database IDs");
        // 2 column grid
        t_groupID.setLayout(new GridLayout(2, false));
        // controls for Prefix (label + text)
        Label t_labelPrefix = new Label(t_groupID, NONE);
        t_labelPrefix.setText("Prefix");
        t_labelPrefix.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        this.m_textIdPrefix = new Text(t_groupID, SWT.BORDER);
        this.m_textIdPrefix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        // controls for Prefix (label + text)
        Label t_labelPostfix = new Label(t_groupID, NONE);
        t_labelPostfix.setText("Postfix");
        t_labelPostfix.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        this.m_textIdPostfix = new Text(t_groupID, SWT.BORDER);
        this.m_textIdPostfix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        // help text
        Label t_labelPlaceholder = new Label(t_groupID, NONE);
        t_labelPlaceholder.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        Label t_labelHelp = new Label(t_groupID, NONE);
        t_labelHelp.setText(
                "Database IDs will be generated following the format <prefix>Number<postfix>.\nIf prefix and postfix are empty the ID will consist of a number alone.");
        t_labelHelp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        // place the checkbox for overwriting
        this.m_checkOverwriteIds = new Button(t_groupID, SWT.CHECK);
        this.m_checkOverwriteIds.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        this.m_checkOverwriteIds.setText("Overwrite: For glycans with existing IDs generate new IDs as well.");
    }

    private void createDatabaseInformationSection(Composite a_container)
    {
        // create a new group for the information and fill horizontal layout
        Group t_groupInformation = new Group(a_container, SWT.NONE);
        GridData t_gridInformation = new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1);
        t_groupInformation.setLayoutData(t_gridInformation);
        t_groupInformation.setText("Database Information");
        // 2 column grid
        t_groupInformation.setLayout(new GridLayout(2, false));
        // controls for name (label + text)
        Label t_labelDatabaseName = new Label(t_groupInformation, NONE);
        t_labelDatabaseName.setText("Name");
        t_labelDatabaseName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        this.m_textDatabaseName = new Text(t_groupInformation, SWT.BORDER);
        this.m_textDatabaseName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        // controls for version number
        Label t_labelDatabaseVersion = new Label(t_groupInformation, NONE);
        t_labelDatabaseVersion.setText("Version");
        t_labelDatabaseVersion.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        this.m_textDatabaseVersion = new Text(t_groupInformation, SWT.BORDER);
        this.m_textDatabaseVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        // controls for description
        Label t_labelDescription = new Label(t_groupInformation, NONE);
        t_labelDescription.setText("Description");
        t_labelDescription.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        this.m_textDescription = new Text(t_groupInformation, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        GridData t_gridDataDescrition = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        t_gridDataDescrition.heightHint = 100;
        this.m_textDescription.setLayoutData(t_gridDataDescrition);
    }

    private void createFilesControl(Composite a_container)
    {
        // create the label in first column
        Label t_labelFilename = new Label(a_container, NONE);
        t_labelFilename.setText("Database file");
        t_labelFilename.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        // text field in the middle column
        this.m_textDatabaseFile = new Text(a_container, SWT.BORDER);
        this.m_textDatabaseFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        this.m_textDatabaseFile.setEditable(false);
        // add the button to the last column
        Button t_buttonBrowseDatabaseFile = new Button(a_container, SWT.NONE);
        GridData t_gridDataButton = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        t_gridDataButton.widthHint = 100;
        t_buttonBrowseDatabaseFile.setLayoutData(t_gridDataButton);
        t_buttonBrowseDatabaseFile.setText("Save As");
        // click action for the browse button
        t_buttonBrowseDatabaseFile.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent a_event)
            {
                try
                {
                    // create a file save dialog for xml files
                    FileDialog t_dialogSave = new FileDialog(a_container.getShell(), SWT.SAVE);
                    t_dialogSave.setFilterNames(new String[] { "Glycan structure database (.xml)", "All files" });
                    t_dialogSave.setFilterExtensions(new String[] { "*.xml", "*.*" });
                    String t_file = t_dialogSave.open();
                    // was something selected?
                    if (t_file != null)
                    {
                        // does the file exist already?
                        File t_fileHandle = new File(t_file);
                        if (t_fileHandle.exists())
                        {
                            // yes, ask to overwrite it
                            int t_response = ErrorUtils.createSingleConfirmationMessageBoxReturn(a_container.getShell(),
                                    "Database file exists",
                                    "The selected database file already exists. Do you want to overwrite it?");
                            if (t_response == SWT.YES)
                            {
                                // yes was selected for overwrite
                                m_textDatabaseFile.setText(t_file);
                            }
                        }
                        else
                        {
                            // no, set the file name
                            m_textDatabaseFile.setText(t_file);
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

    public void fillCreateDatabaseSettings(DatabaseBotSettings a_settings)
    {
        // copy the text field values in the settings object
        a_settings.setFilename(this.m_textDatabaseFile.getText());
        a_settings.setName(this.m_textDatabaseName.getText());
        a_settings.setDescription(this.m_textDescription.getText());
        a_settings.setVersion(this.m_textDatabaseVersion.getText());
        a_settings.setIdPrefix(this.m_textIdPrefix.getText());
        a_settings.setIdPostfix(this.m_textIdPostfix.getText());
        if (this.m_checkOverwriteIds.getSelection())
        {
        	a_settings.setOverwriteIds(true);
        }
        else
        {
        	a_settings.setOverwriteIds(false);
        }
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
        // check if the file is there
        if (TextUtil.isEmpty(this.m_textDatabaseFile))
        {
            this.setErrorMessage("Please select a file to save the database.");
            return false;
        }
        // check if there is a name
        if (TextUtil.isEmpty(this.m_textDatabaseName))
        {
            this.setErrorMessage("Please provide a name for the database.");
            return false;
        }
        else
        {
            // check length
            if (this.m_textDatabaseName.getText().length() > DatabaseInformationPage.TEXT_LENGTH_NAME_VERSION)
            {
                this.setErrorMessage("The database name can not be longer than "
                        + DatabaseInformationPage.TEXT_LENGTH_NAME_VERSION.toString() + " characters.");
                return false;
            }
        }
        // check version
        if (TextUtil.isEmpty(this.m_textDatabaseVersion))
        {
            this.setErrorMessage("Please provide a version for the database.");
            return false;
        }
        else
        {
            // check length
            if (this.m_textDatabaseVersion.getText().length() > DatabaseInformationPage.TEXT_LENGTH_NAME_VERSION)
            {
                this.setErrorMessage("The database version can not be longer than "
                        + DatabaseInformationPage.TEXT_LENGTH_NAME_VERSION.toString() + " characters.");
                return false;
            }
        }
        // description
        if (!TextUtil.isEmpty(this.m_textDescription))
        {
            // check length
            if (this.m_textDescription.getText().length() > DatabaseInformationPage.TEXT_LENGTH_DESCRITION)
            {
                this.setErrorMessage("The description can not be longer than "
                        + DatabaseInformationPage.TEXT_LENGTH_DESCRITION.toString() + " characters.");
                return false;
            }
        }
        return true;
    }
}
