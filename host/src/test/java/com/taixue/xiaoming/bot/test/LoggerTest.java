package com.taixue.xiaoming.bot.test;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerTest {
    static Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    public static void main(String[] args) {
        System.setProperty("log4j.appender.R.File", "logs/lastest.log");
        logger.info("Qwqwq!");
    }
}
