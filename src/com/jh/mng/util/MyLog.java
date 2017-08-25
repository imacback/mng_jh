package com.jh.mng.util;

import org.apache.log4j.Logger;

public class MyLog {

	private static Logger logger = null;
	
	public static void InfoLog(String msg) {
        if ( logger == null )
        {
            logger = Logger.getLogger(MyLog.class);
        }

        logger.info(msg);
    }
}
