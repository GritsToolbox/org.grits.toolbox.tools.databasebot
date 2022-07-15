package org.grits.toolbox.tools.databasebot.utils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ImageCache
{
    private static HashMap<URL, CachedImage> m_cache = new HashMap<>();

    private static CachedImage findImage(Class<?> a_clazz, String a_path) throws IOException
    {
        Bundle t_bundle = FrameworkUtil.getBundle(a_clazz);
        if (t_bundle == null)
        {
            throw new IOException("Unable to find bundle for class: " + a_clazz.getName());
        }
        URL t_url = FileLocator.find(t_bundle, new Path(a_path), null);
        if (t_url == null)
        {
            throw new IOException(
                    "Unable to find file in bundle (" + t_bundle.toString() + "): " + a_path);
        }
        return ImageCache.findImage(t_url);
    }

    private static CachedImage findImage(URL a_url) throws IOException
    {
        CachedImage t_cachedImage = ImageCache.m_cache.get(a_url);
        if (t_cachedImage == null)
        {
            t_cachedImage = new CachedImage();
            ImageDescriptor t_imageDescriptor = ImageDescriptor.createFromURL(a_url);
            t_cachedImage.setImageDescriptor(t_imageDescriptor);
            Image t_image = t_imageDescriptor.createImage();
            if (t_image == null)
            {
                throw new IOException("Unable to create image from image descriptor: "
                        + t_imageDescriptor.toString());
            }
            t_cachedImage.setImage(t_image);
            ImageCache.m_cache.put(a_url, t_cachedImage);
        }
        return t_cachedImage;
    }

    /**
     * Get the image based on a class within a bundle and a path. Class is used
     * to find the bundle and path to find the image.
     *
     * @param a_clazz
     *            Class in the bundle with the image
     * @param a_path
     *            Path and image file name
     * @return Image
     * @throws IOException
     *             if its not possible to find and create the image
     */
    public static Image getImage(Class<?> a_clazz, String a_path) throws IOException
    {
        CachedImage t_image = ImageCache.findImage(a_clazz, a_path);
        if (t_image.getImage().isDisposed())
        {
            t_image.setImage(t_image.getImageDescriptor().createImage());
        }
        return t_image.getImage();
    }

    /**
     * Get the image based on a class within a bundle and a path. Class is used
     * to find the bundle and path to find the image.
     *
     * @param a_clazz
     *            Class in the bundle with the image
     * @param a_path
     *            Path and image file name
     * @return Image descriptor
     * @throws IOException
     *             if its not possible to find and create the image
     */
    public static ImageDescriptor getImageDescriptor(Class<?> a_clazz, String a_path)
            throws IOException
    {
        CachedImage t_image = ImageCache.findImage(a_clazz, a_path);
        return t_image.getImageDescriptor();
    }

    /**
     * Get the image based on a URL
     *
     * @param a_url
     *            URL with the image location
     * @return Image
     * @throws IOException
     *             if its not possible to find and create the image
     */
    public static Image getImage(URL a_url) throws IOException
    {
        CachedImage t_image = ImageCache.findImage(a_url);
        if (t_image.getImage().isDisposed())
        {
            t_image.setImage(t_image.getImageDescriptor().createImage());
        }
        return t_image.getImage();
    }

    /**
     * Get the image based on a URL
     *
     * @param a_url
     *            URL with the image location
     * @return Image descriptor
     * @throws IOException
     *             if its not possible to find and create the image
     */
    public static ImageDescriptor getImageDescriptor(URL a_url) throws IOException
    {
        CachedImage t_image = ImageCache.findImage(a_url);
        return t_image.getImageDescriptor();
    }

    /**
     * Get the image based on a string with a bundleURL (e.g.
     * platform:/plugin/com.github.reneranzinger.fstools.core/icons/category24.
     * png)
     *
     * @param a_bundleUrl
     *            Bundle URL
     * @return Image
     * @throws IOException
     *             if its not possible to find and create the image
     */
    public static Image getImage(String a_bundleUrl) throws IOException
    {
        URL t_url = FileLocator.find(new URL(a_bundleUrl));
        if (t_url == null)
        {
            throw new IOException("Image could not be found: " + a_bundleUrl);
        }
        CachedImage t_image = ImageCache.findImage(t_url);
        if (t_image.getImage().isDisposed())
        {
            t_image.setImage(t_image.getImageDescriptor().createImage());
        }
        return t_image.getImage();
    }

    /**
     * Get the image based on a string with a bundleURL (e.g.
     * platform:/plugin/com.github.reneranzinger.fstools.core/icons/category24.
     * png)
     *
     * @param a_bundleUrl
     *            Bundle URL
     * @return Image descriptor
     * @throws IOException
     *             if its not possible to find and create the image
     */
    public static ImageDescriptor getImageDescriptor(String a_bundleUrl) throws IOException
    {
        URL t_url = FileLocator.find(new URL(a_bundleUrl));
        if (t_url == null)
        {
            throw new IOException("Image could not be found: " + a_bundleUrl);
        }
        CachedImage t_image = ImageCache.findImage(t_url);
        return t_image.getImageDescriptor();
    }

    /**
     * Dispose of all the images in the cache to release system resources.
     */
    public static void disposeAll()
    {
        for (CachedImage t_cachedImage : ImageCache.m_cache.values())
        {
            Image t_image = t_cachedImage.getImage();
            if (!t_image.isDisposed())
            {
                t_image.dispose();
            }
        }
    }

}
