package org.grits.toolbox.tools.databasebot.utils;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.grits.toolbox.util.structure.glycan.database.GlycanDatabase;

public class DatabaseUtil
{
    @SuppressWarnings("unused")
    public static boolean isValidDatabaseFile(String a_file)
    {
        try
        {
            // see if we can use the database file using the JAXB annotations
            JAXBContext jaxbContext = JAXBContext.newInstance(GlycanDatabase.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            GlycanDatabase t_db = (GlycanDatabase) jaxbUnmarshaller.unmarshal(new File(a_file));
            return true;
        }
        catch (JAXBException e)
        {
            return false;
        }
    }
}
