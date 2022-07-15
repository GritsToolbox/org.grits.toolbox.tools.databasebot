package org.grits.toolbox.tools.databasebot.utils;

import java.util.Comparator;

import org.grits.toolbox.tools.databasebot.om.GlycanInformation;

public class ComperatorGlycanInformationMass implements Comparator<GlycanInformation>
{
    @Override
    public int compare(GlycanInformation a_object1, GlycanInformation a_object2)
    {
        return a_object1.getMass().compareTo(a_object2.getMass());
    }
}
