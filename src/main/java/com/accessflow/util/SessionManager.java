package com.accessflow.util;

import com.accessflow.model.Admin;

public class SessionManager {

    private static Admin currentAdmin;

    public static void setAdmin(Admin admin) { currentAdmin = admin; }
    public static Admin getAdmin() { return currentAdmin; }
    public static boolean isLoggedIn() { return currentAdmin != null; }
    public static void logout() { currentAdmin = null; }
}
