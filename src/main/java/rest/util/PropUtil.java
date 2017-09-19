package rest.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Kevingyc on 9/19/2017.
 */
@Component
public class PropUtil {

    private static String firebaseUrl;
    private static String defaultPassword;

    public static String getFirebaseUrl() {
        return firebaseUrl;
    }

    public static String getDefaultPassword() {
        return defaultPassword;
    }

    @Value("${default.password}")
    public void setDefaultPassword(String defaultPassword) {
        PropUtil.defaultPassword = defaultPassword;
    }

    @Value("${firebase.url}")
    public void setFirebaseUrl(String firebaseUrl) {
        PropUtil.firebaseUrl = firebaseUrl;
    }

}
