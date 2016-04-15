package com.diseasemeter.data_colector.common;

import java.util.regex.Pattern;

/**
 * Created by Light on 02/01/16.
 */
public class UtilsSpark {

    private static final String MASTER_LOCAL_REGEX = "local\\[\\d\\]";
    private static final Pattern pattern = Pattern.compile(MASTER_LOCAL_REGEX);

    public static boolean checkSparkMaster(String sMaster) {
        return (sMaster.equalsIgnoreCase("yarn-client") || sMaster.equalsIgnoreCase("yarn-cluster") ||
                pattern.matcher(sMaster).find());
    }
}
