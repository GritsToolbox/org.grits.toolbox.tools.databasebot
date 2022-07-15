package org.grits.toolbox.tools.databasebot.wizards.createnew;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.entry.ms.annotation.glycan.filter.MSGlycanAnnotationFilterSetup;
import org.grits.toolbox.entry.ms.annotation.glycan.filter.MSGlycanCustomFilterSelection;
import org.grits.toolbox.entry.ms.annotation.glycan.util.FileUtils;
import org.grits.toolbox.tools.databasebot.om.DatabaseBotSettings;
import org.grits.toolbox.util.structure.glycan.filter.om.FiltersLibrary;
import org.grits.toolbox.util.structure.glycan.util.FilterUtils;

public class FilterPage extends WizardPage
{
    private static final Logger logger = Logger.getLogger(FilterPage.class);

    private FiltersLibrary m_filterLibrary = new FiltersLibrary();
    private MSGlycanAnnotationFilterSetup m_filterSetup;

    public FilterPage(ImageDescriptor a_titleImage)
    {
        super("Filter", "Filter settings", a_titleImage);
        this.setDescription("Configure filters to exclude structures.");
        String t_filterFile = null;
        try
        {
            // t_filterFile = ResourceLocatorUtils.getLegalPathOfResource(this,
            // "/filter/filters_db.xml");
            this.m_filterLibrary = FilterUtils.readFilters(FileUtils.getFilterPath());
        }
        catch (Exception e)
        {
            logger.error("Unable to load filter library:" + t_filterFile, e);
        }
    }

    @Override
    public void createControl(Composite a_parent)
    {
        // create a scrollable composite
        ScrolledComposite t_compositeScrollable = new ScrolledComposite(a_parent,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        t_compositeScrollable.setExpandVertical(true);
        t_compositeScrollable.setExpandHorizontal(true);
        GridData t_gridDataScrollable = new GridData(SWT.FILL, SWT.FILL, true, true);
        t_compositeScrollable.setLayoutData(t_gridDataScrollable);
        // create a composite inside the scrollable composite
        Composite t_container = new Composite(t_compositeScrollable, SWT.NONE);
        // create the named filter option above the filter settings
        MSGlycanCustomFilterSelection t_customFilterSelection = new MSGlycanCustomFilterSelection();
        t_customFilterSelection.createFilterSelectionArea(t_container);
        // create the filter control
        this.m_filterSetup = new MSGlycanAnnotationFilterSetup(this.m_filterLibrary.getCategories());
        this.m_filterSetup.setFilterList(this.m_filterLibrary.getFilters());
        try
        {
            this.m_filterSetup.createFilterTableSection(t_container);
            // filterTableSetup.addFilterChangedListener(this);
        }
        catch (Exception e)
        {
            logger.error("Error creating the filter table", e);
        }
        // allow the custom filters to change the filter settings
        t_customFilterSelection.setFilterTableSetup(this.m_filterSetup);
        // set the content of the scrollable composite and initialize size
        // computing
        t_compositeScrollable.setContent(t_container);
        t_compositeScrollable.setMinSize(t_container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        this.setControl(t_compositeScrollable);
        this.setPageComplete(true);
    }

    public void fillCreateDatabaseSettings(DatabaseBotSettings a_settings)
    {
        a_settings.setFilterSetting(this.m_filterSetup.getFilterSetting());
    }

}