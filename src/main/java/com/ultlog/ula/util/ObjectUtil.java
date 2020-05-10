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
     * all object null
     *
     * @param objects object array
     * @return bool
     */
    public static boolean AllObjectNull(Object... objects) {

        for (Object object : objects) {
            // todo change #isEmpty to @isBotBlank
            if (object != null && (!(object instanceof String) || !StringUtils.isEmpty(object))) {
                return false;
            }
        }
        return true;
    }
}
