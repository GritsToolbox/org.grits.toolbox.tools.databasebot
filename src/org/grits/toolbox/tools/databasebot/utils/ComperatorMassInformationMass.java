package org.grits.toolbox.tools.databasebot.utils;

import java.util.Comparator;

import org.grits.toolbox.tools.databasebot.excel.om.MassInformation;

public class ComperatorMassInformationMass implements Comparator<MassInformation>
{
    @Override
    public int compare(MassInformation a_object1, MassInformation a_object2)
    {
        return a_object1.getMass().compareTo(a_object2.getMass());
    }
}
