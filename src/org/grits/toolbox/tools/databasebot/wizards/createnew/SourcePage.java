package org.grits.toolbox.tools.databasebot.wizards.createnew;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.grits.toolbox.core.utilShare.ErrorUtils;
import org.grits.toolbox.tools.databasebot.om.DatabaseBotSettings;
import org.grits.toolbox.tools.databasebot.utils.FileUtils;

/**
 * <code>WizardPage</code> with the settings for adding new structures to an
 * existing database or new database
 *
 * @author rene
 *
 */
public class SourcePage extends WizardPage
{
    private static final Logger logger = Logger.getLogger(SourcePage.class);
    /**
     * Radio button group: (1) no additional structures, (2) download from
     * Qrator, and (3) add GWS files.
     */
    private Button m_radioNoNewStructures = null;
    private Button m_radioFiles = null;
    /** Add button to add more GWS files to the List field */
    private Button m_buttonAdd = null;
    /** Remove button to delete GWS files from the list field */
    private Button m_buttonRemove = null;
    /** List of GWS files */
    private List m_listFiles = null;

    /**
     * Default constructor of the page. Sets title, description and banner icon.
     *
     * @param a_titleImage
     *            Banner icon for this page
     */
    public SourcePage(ImageDescriptor a_titleImage)
    {
        super("Structure source", "Source for new structures", a_titleImage);
        this.setDescription("Select the source for the glycan structures that are added to the database.");
    }

    /**
     * Call back method to place the controls in the page
     *
     * @param a_parent
     *            Parent composite of the page
     */
    @Override
    public void createControl(Composite a_parent)
    {
        // create a composite for the controls of this page
        Composite t_container = new Composite(a_parent, SWT.NONE);
        // set the GRID layout
        t_container.setLayout(new GridLayout(3, false));
        // radio button for no new structures
        this.m_radioNoNewStructures = new Button(t_container, SWT.RADIO);
        this.m_radioNoNewStructures.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        this.m_radioNoNewStructures.setText("No new structures");
        // TODO radio button for Qrator
        // this.m_radioQrator = new Button(t_container, SWT.RADIO);
        // this.m_radioQrator.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
        // false, false, 3, 1));
        // this.m_radioQrator.setText("Structures from Qrator");
        // radio button for loading from Files
        this.m_radioFiles = new Button(t_container, SWT.RADIO);
        this.m_radioFiles.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        this.m_radioFiles.setText("Structures from GWS files");
        // add a empty cell before the list
        Label t_placeholder = new Label(t_container, SWT.NONE);
        GridData t_gridDataLabel = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        t_gridDataLabel.widthHint = 15;
        t_placeholder.setLayoutData(t_gridDataLabel);
        // list of files
        this.m_listFiles = new List(t_container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
        this.m_listFiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
        // Add button
        this.m_buttonAdd = new Button(t_container, SWT.NONE);
        this.m_buttonAdd.setText("Browse");
        GridData t_gridButton = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
        t_gridButton.widthHint = 100;
        this.m_buttonAdd.setLayoutData(t_gridButton);
        // placeholder to fill the first cell
        new Label(t_container, SWT.NONE);
        // Delete button
        this.m_buttonRemove = new Button(t_container, SWT.NONE);
        this.m_buttonRemove.setText("Remove");
        t_gridButton = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
        t_gridButton.widthHint = 100;
        this.m_buttonRemove.setLayoutData(t_gridButton);
        // if the no new structure radio button is selected the GWS controls
        // must
        // be disabled
        this.m_radioNoNewStructures.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent a_event)
            {
                Button t_button = (Button) a_event.widget;
                if (t_button.getSelection())
                {
                    m_listFiles.setEnabled(false);
                    m_buttonAdd.setEnabled(false);
                    m_buttonRemove.setEnabled(false);
                }
            }
        });
        // TODO if the qrator radio button is selected the GWS controls must be
        // disabled
        // this.m_radioQrator.addSelectionListener(new SelectionAdapter()
        // {
        // @Override
        // public void widgetSelected(SelectionEvent a_event)
        // {
        // Button t_button = (Button) a_event.widget;
        // if (t_button.getSelection())
        // {
        // m_listFiles.setEnabled(false);
        // m_buttonAdd.setEnabled(false);
        // m_buttonRemove.setEnabled(false);
        // }
        // }
        // });
        // if the GWS radio button is selected the GWS controls must be enabled
        this.m_radioFiles.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent a_event)
            {
                Button t_button = (Button) a_event.widget;
                if (t_button.getSelection())
                {
                    m_listFiles.setEnabled(true);
                    m_buttonAdd.setEnabled(true);
                    m_buttonRemove.setEnabled(true);
                }
            }
        });
        // if remove button is pressed the selected elements will be removed
        // from the GWS list
        this.m_buttonRemove.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent a_event)
            {
                removeFromList();
            }
        });
        // if the add button is pressed a file dialog is opened and the selected
        // file(s) are added to the GWS list
        this.m_buttonAdd.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent a_event)
            {
                try
                {
                    // Open file dialog that allows multi selection
                    FileDialog t_dialogOpen = new FileDialog(a_parent.getShell(), SWT.OPEN | SWT.MULTI);
                    // create file extension filters
                    t_dialogOpen.setFilterNames(new String[] { "GlycoWorkbench structure (.gws)", "All files" });
                    t_dialogOpen.setFilterExtensions(new String[] { "*.gws", "*.*" });
                    t_dialogOpen.open();
                    // get the selected path the selected files from the dialog
                    // if the cancel button is pressed the path will be "" or
                    // null and the list of files will be empty
                    String t_path = t_dialogOpen.getFilterPath();
                    String[] t_files = t_dialogOpen.getFileNames();
                    ArrayList<String> t_invalidFiles = new ArrayList<String>();
                    // if the open button was pressed copy the files in the
                    // control list but check if they are valid
                    if (t_files.length > 0 && t_path != null)
                    {
                        for (String t_fileName : t_files)
                        {
                            String t_fullFileName = t_path + File.separator + t_fileName;
                            if (isValidFileGWS(t_fullFileName))
                            {
                                m_listFiles.add(t_fullFileName);
                            }
                            else
                            {
                                t_invalidFiles.add(t_fileName);
                            }
                        }
                        if (t_invalidFiles.size() > 0)
                        {
                            StringBuffer t_message = new StringBuffer(
                                    "The following files are not valid GWS files or contain glycans that can not be loaded:\n");
                            for (String t_string : t_invalidFiles)
                            {
                                t_message.append("\t" + t_string + "\n");
                            }
                            ErrorUtils.createErrorMessageBox(a_parent.getShell(), "Error loading GWS files",
                                    t_message.toString());
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.fatal("Unable to add files to the list of GWS files.", e);
                }
            }
        });
        // initialization
        this.m_radioNoNewStructures.setSelection(true);
        this.m_listFiles.setEnabled(false);
        this.m_buttonAdd.setEnabled(false);
        this.m_buttonRemove.setEnabled(false);
        // page is finished by default
        this.setPageComplete(true);
        this.setControl(t_container);
    }

    private void removeFromList()
    {
        int[] t_indices = this.m_listFiles.getSelectionIndices();
        if (t_indices.length > 0)
        {
            try
            {
                this.m_listFiles.remove(t_indices);
            }
            catch (Exception e)
            {
                logger.error("Trying to remove from list of files", e);
            }
        }
    }

    private boolean isValidFileGWS(String a_fileName)
    {
        try
        {
            String t_gwsMulti = FileUtils.readFile(a_fileName);
            String[] t_gwsList = t_gwsMulti.split(";");
            for (String t_gws : t_gwsList)
            {
                // create GWB glycan object and translate to GlycoCT
                Glycan t_glycan = Glycan.fromString(t_gws);
                if (t_glycan == null)
                {
                    logger.info("Invalid GWS sequence in file (" + a_fileName + "): " + t_gws);
                    return false;
                }
                t_glycan.computeMass();
            }
            return true;
        }
        catch (Exception e)
        {
            logger.debug("Problem loading GWS file", e);
            return false;
        }
    }

    public void fillCreateDatabaseSettings(DatabaseBotSettings a_settings)
    {
        // check if Qrator is to be used
        // if (this.m_radioQrator.getSelection())
        // {
        // a_settings.setUseQrator(true);
        // }
        // check if GWS files are to be used and add them to the settings
        if (this.m_radioFiles.getSelection())
        {
            a_settings.setGwsFiles(Arrays.asList(this.m_listFiles.getItems()));
        }
    }

}
