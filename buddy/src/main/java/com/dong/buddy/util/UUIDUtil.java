package com.dong.buddy.util;

import java.util.UUID;

public class UUIDUtil
{

    public static String getId()
    {
        return UUID.randomUUID().toString();
    }

    public synchronized static String getTimeId()
    {
        return String.valueOf(System.currentTimeMillis());
    }

}
