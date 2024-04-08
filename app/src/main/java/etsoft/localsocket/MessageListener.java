package etsoft.localsocket;

/**
 * Author: Zukhriddin Kamolov
 * Date: 05-Apr-24, 2:48 PM.
 * Project: JNILocalSocket
 */

public interface MessageListener {
    void invoke(String message);
}
