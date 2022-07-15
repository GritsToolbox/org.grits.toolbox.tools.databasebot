package org.grits.toolbox.tools.databasebot.om;

import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.util.structure.glycan.filter.om.FilterSetting;

/**
 * Object for storing the settings of the database creation: (1) should an
 * existing database be extended or a new one be created, (2) should a report
 * file with the excluded structures be created, (3) list of GWB files that
 * should be added, and (5) database metadata and filename
 *
 * @author rene
 *
 */
public class DatabaseBotSettings
{

    /** String with the database file to be extended, otherwise null. */
    private String m_extendDatabaseFile = null;
    /**
     * String with the file location for the report file, null if no report file
     * is created
     */
    private String m_reportFile = null;
    /**
     * Scaling factor for the image in the report file. The number is 1 based
     * which mean 1 is 100% size.
     */
    private Double m_imageScalingFactor = 0.5D;
    /** list of GWS file locations that should be added to the database */
    private List<String> m_gwsFiles = new ArrayList<>();
    /** name of the database */
    private String m_name = null;
    /** description of the database */
    private String m_description = null;
    /** version number of the database */
    private String m_version = null;
    /** filename for the new database file */
    private String m_filename = null;
    /** prefix for the GlycanIDs */
    private String m_idPrefix = "";
    /** postfix for the GlycanIDs */
    private String m_idPostfix = "";
    /** overwrite existing IDs with new IDs */
    private Boolean m_overwriteIds = Boolean.FALSE;
    /** filters that have to be applied */
    private FilterSetting m_filterSetting = null;
    /** should a topology database be created */
    private Boolean m_topologyDatabase = Boolean.FALSE;

    public String getExtendDatabaseFile()
    {
        return this.m_extendDatabaseFile;
    }

    public void setExtendDatabaseFile(String a_extendDatabaseFile)
    {
        this.m_extendDatabaseFile = a_extendDatabaseFile;
    }

    public String getReportFile()
    {
        return this.m_reportFile;
    }

    public void setReportFile(String a_reportFile)
    {
        this.m_reportFile = a_reportFile;
    }

    public List<String> getGwsFiles()
    {
        return this.m_gwsFiles;
    }

    public void setGwsFiles(List<String> a_gwsFiles)
    {
        this.m_gwsFiles = a_gwsFiles;
    }

    public String getName()
    {
        return this.m_name;
    }

    public void setName(String a_name)
    {
        this.m_name = a_name;
    }

    public String getDescription()
    {
        return this.m_description;
    }

    public void setDescription(String a_description)
    {
        this.m_description = a_description;
    }

    public String getVersion()
    {
        return this.m_version;
    }

    public void setVersion(String a_version)
    {
        this.m_version = a_version;
    }

    public String getFilename()
    {
        return this.m_filename;
    }

    public void setFilename(String a_filename)
    {
        this.m_filename = a_filename;
    }

    public String getIdPrefix()
    {
        return m_idPrefix;
    }

    public void setIdPrefix(String a_idPrefix)
    {
        this.m_idPrefix = a_idPrefix;
    }

    public FilterSetting getFilterSetting()
    {
        return m_filterSetting;
    }

    public void setFilterSetting(FilterSetting a_filterSetting)
    {
        this.m_filterSetting = a_filterSetting;
    }

    public String getIdPostfix()
    {
        return m_idPostfix;
    }

    public void setIdPostfix(String a_idPostfix)
    {
        this.m_idPostfix = a_idPostfix;
    }

    public Boolean getTopologyDatabase()
    {
        return m_topologyDatabase;
    }

    public void setTopologyDatabase(Boolean a_topologyDatabase)
    {
        this.m_topologyDatabase = a_topologyDatabase;
    }

    public Boolean getOverwriteIds()
    {
        return m_overwriteIds;
    }

    public void setOverwriteIds(Boolean m_overwriteIds)
    {
        this.m_overwriteIds = m_overwriteIds;
    }

    public Double getImageScalingFactor()
    {
        return m_imageScalingFactor;
    }

    public void setImageScalingFactor(Double a_imageScalingFactor)
    {
        this.m_imageScalingFactor = a_imageScalingFactor;
    }

}
