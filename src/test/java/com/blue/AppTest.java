package com.blue;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        String[] result = "153,123".split(",");
        System.out.println(result[0]);
    }

    @Test
    public void getUserName() {


        String username = null;
        try {
            username = Advapi32Util.getUserName();
        } catch (Win32Exception e) {
            username = "<unknown>";
        }
        System.out.println("Current logged in user: " + username);
        System.out.println("中国");
    }
}
