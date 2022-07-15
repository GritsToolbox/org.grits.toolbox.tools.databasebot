package org.grits.toolbox.tools.databasebot.utils;

public class DatabaseBotException extends Exception
{
    private static final long serialVersionUID = 1L;

    public DatabaseBotException()
    {
        super();
    }

    public DatabaseBotException(String a_message)
    {
        super(a_message);
    }

    public DatabaseBotException(String a_message, Throwable a_exception)
    {
        super(a_message, a_exception);
    }
}
