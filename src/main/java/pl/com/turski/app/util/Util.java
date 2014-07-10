package pl.com.turski.app.util;

import android.widget.EditText;

/**
 * User: Adam
 */
public class Util {

    public static int validateEmptyEditText(EditText editText) {
        boolean empty = isEmpty(editText);
        if (empty) {
            editText.setError("Pole nie może być puste");
        } else {
            editText.setError(null);
        }
        return !empty ? 0 : 1;
    }

    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
