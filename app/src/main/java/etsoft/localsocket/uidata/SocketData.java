package etsoft.localsocket.uidata;

import androidx.annotation.NonNull;

/**
 * Author: Zukhriddin Kamolov
 * Date: 05-Apr-24, 12:37 PM.
 * Project: JNILocalSocket
 */

/**
 * LocalSocket message data class
 * */
public class SocketData {

    final public int id;
    final public String from;
    final public String to;
    final public String message;

    public SocketData(int id, String from, String to, String message) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
    }

    @NonNull
    @Override
    public String toString() {
        return "\n[" + "id: " + id +
                ", from: " + from +
                ", to: " + to +
                ", message: " + message + "]";
    }
}
