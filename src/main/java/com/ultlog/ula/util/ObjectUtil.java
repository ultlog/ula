package com.ultlog.ula.util;

import org.springframework.util.StringUtils;

/**
 * @program: ula
 * @link: github.com/ultlog/ula
 * @author: will
 * @create: 2020-05-03
 **/
public class ObjectUtil {

    /**
     * all object null or empty
     *
     * @param objects object array
     * @return bool
     */
    public static boolean allObjectNullOrEmpty(Object... objects) {

        for (Object object : objects) {
            if (object != null && (!(object instanceof String) || !StringUtils.isEmpty(object))) {
                return false;
            }
        }
        return true;
    }

    /**
     * anyone object is not null and empty
     *
     * @param objects object array
     * @return bool
     */
    public static boolean anyObjectNullOrEmpty(Object... objects) {

        return !allObjectNullOrEmpty(objects);
    }
}
