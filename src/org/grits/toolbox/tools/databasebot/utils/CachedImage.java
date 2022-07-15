package org.grits.toolbox.tools.databasebot.utils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class CachedImage
{
    private Image m_image = null;
    private ImageDescriptor m_imageDescriptor = null;

    public Image getImage()
    {
        return this.m_image;
    }

    public void setImage(Image a_image)
    {
        this.m_image = a_image;
    }

    public ImageDescriptor getImageDescriptor()
    {
        return this.m_imageDescriptor;
    }

    public void setImageDescriptor(ImageDescriptor a_imageDescriptor)
    {
        this.m_imageDescriptor = a_imageDescriptor;
    }

}
