package org.grits.toolbox.tools.databasebot.utils;

import org.eclipse.swt.widgets.Text;

public class TextUtil
{

    public static boolean isEmpty(Text a_text)
    {
        String t_textValue = a_text.getText();
        if (t_textValue == null)
        {
            return true;
        }
        if (t_textValue.trim().length() == 0)
        {
            return true;
        }
        return false;
    }

}
