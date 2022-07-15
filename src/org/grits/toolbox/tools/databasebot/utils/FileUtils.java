package org.grits.toolbox.tools.databasebot.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils
{
    public static String readFile(String a_path, Charset a_encoding) throws IOException
    {
        byte[] t_content = Files.readAllBytes(Paths.get(a_path));
        return new String(t_content, a_encoding);
    }

    public static String readFile(String a_path) throws IOException
    {
        return FileUtils.readFile(a_path, StandardCharsets.UTF_8);
    }
}
