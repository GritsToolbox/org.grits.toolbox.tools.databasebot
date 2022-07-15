package org.grits.toolbox.tools.databasebot.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Section;

public class GuiUtils
{
    public static Color PART_BACKGROUND_COLOR = new Color(Display.getCurrent(), 255, 255, 255);
    public static Color SECTION_TEXT_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
    public static Color SECTION_TITLE_BAR_COLOR = new Color(Display.getCurrent(), 224, 232, 241);

    public static Section createSection(Composite a_parent, String a_label)
    {
        Section t_section = new Section(a_parent, Section.EXPANDED | Section.TITLE_BAR);
        t_section.setText(a_label);
        t_section.setTitleBarBackground(GuiUtils.SECTION_TITLE_BAR_COLOR);
        t_section.setTitleBarForeground(GuiUtils.SECTION_TEXT_COLOR);
        t_section.setBackground(GuiUtils.PART_BACKGROUND_COLOR);
        return t_section;
    }
}
