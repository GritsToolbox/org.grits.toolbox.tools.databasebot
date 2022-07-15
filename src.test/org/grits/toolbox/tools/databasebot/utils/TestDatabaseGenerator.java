package org.grits.toolbox.tools.databasebot.utils;

import java.util.ArrayList;

import org.grits.toolbox.tools.databasebot.om.DatabaseBotSettings;
import org.grits.toolbox.tools.databasebot.process.DatabaseGenerator;

public class TestDatabaseGenerator
{
    public static void main(String[] a_args)
    {
        DatabaseBotSettings t_settings = new DatabaseBotSettings();
        // D:/GRITS
        // Toolbox/Deployment/GRITS-Toolbox-DB/win32.win32.x86_64/GRITS/configuration/org.eclipse.osgi/148/0/.cp/databases/\All-Glycan.xml
        // D:\Dropbox\renes folder\GRITS\Elisabet Gas
        // Pascual\Full-EGP-topology.xml
        t_settings.setName("Name");
        t_settings.setVersion("1");
        t_settings.setDescription("Description");
        t_settings.setExtendDatabaseFile(
                "D:\\workspace\\GRITS.2016.06.17\\org.grits.toolbox.importer.ms.annotation.glycan.simiansearch\\databases\\N-Glycan-topology.xml");
        t_settings.setGwsFiles(new ArrayList<String>());
        t_settings.setReportFile(null);
        // create generator and let it run
        DatabaseGenerator t_generator = new DatabaseGenerator();
        t_generator.setSettings(t_settings);
        // t_generator.generateDatabase();

        System.out.println("Finished");
    }
}
