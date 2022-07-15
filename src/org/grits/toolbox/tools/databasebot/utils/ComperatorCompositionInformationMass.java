package org.grits.toolbox.tools.databasebot.utils;

import java.util.Comparator;

import org.grits.toolbox.tools.databasebot.excel.om.CompositionInformation;

public class ComperatorCompositionInformationMass implements Comparator<CompositionInformation>
{
    @Override
    public int compare(CompositionInformation a_object1, CompositionInformation a_object2)
    {
        return a_object1.getMass().compareTo(a_object2.getMass());
    }
}
