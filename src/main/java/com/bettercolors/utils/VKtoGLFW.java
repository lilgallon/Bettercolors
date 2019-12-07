package com.bettercolors.utils;

import org.lwjgl.glfw.GLFW;

import java.awt.event.KeyEvent;

public class VKtoGLFW {

    /**
     * All the keys listed in java.awt.event are listed here. If there
     * is no GLFW equivalent the function returns -2. Otherwise it returns
     * the GLFW code. -1 is reserved for the GLFW's unknown code.
     *
     * /!\ 1. java.awt.event.KeyEvent does not make any difference between left and right keys for:
     * - shift,
     * - control,
     * - alt.
     * So we had to decide, and this function return the code of the right one.
     *
     * /!\ 2. java.awt.event.KeyEvent takes into account your keyboard layout.
     * So if your keyboard is french, and you type "A", GLFW will say that it's Q,
     * whereas KeyEvent's VKs will say that it's "A". You have to keep that in mind
     * when you convert a KeyEvent's VK to a GLFW key.
     * This function will return GLFW_KEY_A with the KeyEvent's key is VK_A.
     *
     * Version 1.0
     * Future updates here: https://gist.github.com/N3ROO/8f3bc1574f9fee318b3b7a879d1b26b4
     *
     * @param code java.awt.event.KeyEvent VK code
     * @return -2 not supported, otherwise it returns the GLFW code.
     */
    public static int convertVKSwingtoGLFW(int code) {
        switch (code) {
            case KeyEvent.CHAR_UNDEFINED:   return GLFW.GLFW_KEY_UNKNOWN;
            case KeyEvent.VK_SPACE:         return GLFW.GLFW_KEY_SPACE;
            // no apostrophe
            case KeyEvent.VK_COMMA:         return GLFW.GLFW_KEY_COMMA;
            case KeyEvent.VK_MINUS:         return GLFW.GLFW_KEY_MINUS;
            case KeyEvent.VK_PERIOD:        return GLFW.GLFW_KEY_PERIOD;
            case KeyEvent.VK_SLASH:         return GLFW.GLFW_KEY_SLASH;
            case KeyEvent.VK_0:             return GLFW.GLFW_KEY_0;
            case KeyEvent.VK_1:             return GLFW.GLFW_KEY_1;
            case KeyEvent.VK_2:             return GLFW.GLFW_KEY_2;
            case KeyEvent.VK_3:             return GLFW.GLFW_KEY_3;
            case KeyEvent.VK_4:             return GLFW.GLFW_KEY_4;
            case KeyEvent.VK_5:             return GLFW.GLFW_KEY_5;
            case KeyEvent.VK_6:             return GLFW.GLFW_KEY_6;
            case KeyEvent.VK_7:             return GLFW.GLFW_KEY_7;
            case KeyEvent.VK_8:             return GLFW.GLFW_KEY_8;
            case KeyEvent.VK_9:             return GLFW.GLFW_KEY_9;
            case KeyEvent.VK_SEMICOLON:     return GLFW.GLFW_KEY_SEMICOLON;
            case KeyEvent.VK_EQUALS:        return GLFW.GLFW_KEY_EQUAL;
            case KeyEvent.VK_A:             return GLFW.GLFW_KEY_A;
            case KeyEvent.VK_B:             return GLFW.GLFW_KEY_B;
            case KeyEvent.VK_C:             return GLFW.GLFW_KEY_C;
            case KeyEvent.VK_D:             return GLFW.GLFW_KEY_D;
            case KeyEvent.VK_E:             return GLFW.GLFW_KEY_E;
            case KeyEvent.VK_F:             return GLFW.GLFW_KEY_F;
            case KeyEvent.VK_G:             return GLFW.GLFW_KEY_G;
            case KeyEvent.VK_H:             return GLFW.GLFW_KEY_H;
            case KeyEvent.VK_I:             return GLFW.GLFW_KEY_I;
            case KeyEvent.VK_J:             return GLFW.GLFW_KEY_J;
            case KeyEvent.VK_K:             return GLFW.GLFW_KEY_K;
            case KeyEvent.VK_L:             return GLFW.GLFW_KEY_L;
            case KeyEvent.VK_M:             return GLFW.GLFW_KEY_M;
            case KeyEvent.VK_N:             return GLFW.GLFW_KEY_N;
            case KeyEvent.VK_O:             return GLFW.GLFW_KEY_O;
            case KeyEvent.VK_P:             return GLFW.GLFW_KEY_P;
            case KeyEvent.VK_Q:             return GLFW.GLFW_KEY_Q;
            case KeyEvent.VK_R:             return GLFW.GLFW_KEY_R;
            case KeyEvent.VK_S:             return GLFW.GLFW_KEY_S;
            case KeyEvent.VK_T:             return GLFW.GLFW_KEY_T;
            case KeyEvent.VK_U:             return GLFW.GLFW_KEY_U;
            case KeyEvent.VK_V:             return GLFW.GLFW_KEY_V;
            case KeyEvent.VK_W:             return GLFW.GLFW_KEY_W;
            case KeyEvent.VK_X:             return GLFW.GLFW_KEY_X;
            case KeyEvent.VK_Y:             return GLFW.GLFW_KEY_Y;
            case KeyEvent.VK_Z:             return GLFW.GLFW_KEY_Z;
            case KeyEvent.VK_OPEN_BRACKET:  return GLFW.GLFW_KEY_LEFT_BRACKET;
            case KeyEvent.VK_BACK_SLASH:    return GLFW.GLFW_KEY_BACKSLASH;
            case KeyEvent.VK_CLOSE_BRACKET: return GLFW.GLFW_KEY_RIGHT_BRACKET;
            // no grave accent with VK
            // world 1?
            // world 2?
            case KeyEvent.VK_ESCAPE:        return GLFW.GLFW_KEY_ESCAPE;
            case KeyEvent.VK_ENTER:         return GLFW.GLFW_KEY_ENTER;
            case KeyEvent.VK_TAB:           return GLFW.GLFW_KEY_TAB;
            case KeyEvent.VK_BACK_SPACE:    return GLFW.GLFW_KEY_BACKSPACE;
            case KeyEvent.VK_INSERT:        return GLFW.GLFW_KEY_INSERT;
            case KeyEvent.VK_DELETE:        return GLFW.GLFW_KEY_DELETE;
            case KeyEvent.VK_RIGHT:         return GLFW.GLFW_KEY_RIGHT;
            case KeyEvent.VK_LEFT:          return GLFW.GLFW_KEY_LEFT;
            case KeyEvent.VK_DOWN:          return GLFW.GLFW_KEY_DOWN;
            case KeyEvent.VK_UP:            return GLFW.GLFW_KEY_UP;
            case KeyEvent.VK_PAGE_UP:       return GLFW.GLFW_KEY_PAGE_UP;
            case KeyEvent.VK_PAGE_DOWN:     return GLFW.GLFW_KEY_PAGE_DOWN;
            case KeyEvent.VK_HOME:          return GLFW.GLFW_KEY_HOME;
            case KeyEvent.VK_END:           return GLFW.GLFW_KEY_END;
            case KeyEvent.VK_CAPS_LOCK:     return GLFW.GLFW_KEY_CAPS_LOCK;
            case KeyEvent.VK_SCROLL_LOCK:   return GLFW.GLFW_KEY_SCROLL_LOCK;
            case KeyEvent.VK_NUM_LOCK:      return GLFW.GLFW_KEY_NUM_LOCK;
            case KeyEvent.VK_PRINTSCREEN:   return GLFW.GLFW_KEY_PRINT_SCREEN;
            case KeyEvent.VK_PAUSE:         return GLFW.GLFW_KEY_PAUSE;
            case KeyEvent.VK_F1:            return GLFW.GLFW_KEY_F1;
            case KeyEvent.VK_F2:            return GLFW.GLFW_KEY_F2;
            case KeyEvent.VK_F3:            return GLFW.GLFW_KEY_F3;
            case KeyEvent.VK_F4:            return GLFW.GLFW_KEY_F4;
            case KeyEvent.VK_F5:            return GLFW.GLFW_KEY_F5;
            case KeyEvent.VK_F6:            return GLFW.GLFW_KEY_F6;
            case KeyEvent.VK_F7:            return GLFW.GLFW_KEY_F7;
            case KeyEvent.VK_F8:            return GLFW.GLFW_KEY_F8;
            case KeyEvent.VK_F9:            return GLFW.GLFW_KEY_F9;
            case KeyEvent.VK_F10:           return GLFW.GLFW_KEY_F10;
            case KeyEvent.VK_F11:           return GLFW.GLFW_KEY_F11;
            case KeyEvent.VK_F12:           return GLFW.GLFW_KEY_F12;
            case KeyEvent.VK_F13:           return GLFW.GLFW_KEY_F13;
            case KeyEvent.VK_F14:           return GLFW.GLFW_KEY_F14;
            case KeyEvent.VK_F15:           return GLFW.GLFW_KEY_F15;
            case KeyEvent.VK_F16:           return GLFW.GLFW_KEY_F16;
            case KeyEvent.VK_F17:           return GLFW.GLFW_KEY_F17;
            case KeyEvent.VK_F18:           return GLFW.GLFW_KEY_F18;
            case KeyEvent.VK_F19:           return GLFW.GLFW_KEY_F19;
            case KeyEvent.VK_F20:           return GLFW.GLFW_KEY_F20;
            case KeyEvent.VK_F21:           return GLFW.GLFW_KEY_F21;
            case KeyEvent.VK_F22:           return GLFW.GLFW_KEY_F22;
            case KeyEvent.VK_F23:           return GLFW.GLFW_KEY_F23;
            case KeyEvent.VK_F24:           return GLFW.GLFW_KEY_F24;
            // no F25
            case KeyEvent.VK_NUMPAD0:       return GLFW.GLFW_KEY_KP_0;
            case KeyEvent.VK_NUMPAD1:       return GLFW.GLFW_KEY_KP_1;
            case KeyEvent.VK_NUMPAD2:       return GLFW.GLFW_KEY_KP_2;
            case KeyEvent.VK_NUMPAD3:       return GLFW.GLFW_KEY_KP_3;
            case KeyEvent.VK_NUMPAD4:       return GLFW.GLFW_KEY_KP_4;
            case KeyEvent.VK_NUMPAD5:       return GLFW.GLFW_KEY_KP_5;
            case KeyEvent.VK_NUMPAD6:       return GLFW.GLFW_KEY_KP_6;
            case KeyEvent.VK_NUMPAD7:       return GLFW.GLFW_KEY_KP_7;
            case KeyEvent.VK_NUMPAD8:       return GLFW.GLFW_KEY_KP_8;
            case KeyEvent.VK_NUMPAD9:       return GLFW.GLFW_KEY_KP_9;
            case KeyEvent.VK_DECIMAL:       return GLFW.GLFW_KEY_KP_DECIMAL;
            case KeyEvent.VK_DIVIDE:        return GLFW.GLFW_KEY_KP_DIVIDE;
            case KeyEvent.VK_MULTIPLY:      return GLFW.GLFW_KEY_KP_MULTIPLY;
            case KeyEvent.VK_SUBTRACT:      return GLFW.GLFW_KEY_KP_SUBTRACT;
            case KeyEvent.VK_ADD:           return GLFW.GLFW_KEY_KP_ADD;
            // no kp enter
            // no kp equals
            case KeyEvent.VK_SHIFT:         return GLFW.GLFW_KEY_RIGHT_SHIFT;  // !! No difference between L and R
            case KeyEvent.VK_CONTROL:       return GLFW.GLFW_KEY_RIGHT_CONTROL;  // !! No difference between L and R
            case KeyEvent.VK_ALT:           return GLFW.GLFW_KEY_RIGHT_ALT;  // !! No difference between L and R
            // no super
            case KeyEvent.VK_CONTEXT_MENU:  return GLFW.GLFW_KEY_MENU;
            default: return -2;
        }
    }

    /**
     * The most used keys are mapped here. If not, the function
     * will return -2. -1 is reserved for "GLFW_KEY_UNKNOWN".
     *
     * Version 1.0
     * Future updates here: https://gist.github.com/N3ROO/eb0cc2fc38920fb5081aba2c542eda55
     *
     * @param vk_code Virtual key code
     * @return the GLFW key code corresponding (-2 if not supported)
     */
    public static int convertVKtoGLFW(int vk_code) {

        // 1 -> L mouse
        // 2 -> R mouse
        // 3 -> control break processing
        // 4 -> middle mouse button
        // 5 -> windows 2000 X1
        // 6 -> windows 2000 X2
        // 7 -> undefined
        if (vk_code == 8) {
            // 8 -> back space
            return 259;
        }
        if (vk_code == 9) {
            // 9 -> tab key
            return  258;
        }
        // 10, 11 -> reserved
        // 12 -> clear key
        if (vk_code == 13) {
            // 13 -> enter key
            return 257;
        }
        // 14, 15 -> undefined
        // 16 -> shift - there is no equivalent in GLFW
        // 17 -> ctrl - there is no equivalent in GLFW
        // 18 -> alt - there is no equivalent in GLFW
        if (vk_code == 19) {
            // 19 -> pause
            return 284;
        }
        if (vk_code == 20) {
            // 20 -> caps lock
            return 280;
        }
        // 21, ..., 26 -> IME stuff
        if (vk_code == 27) {
            // 27 -> esc
            return 256;
        }
        // 28, ..., 31 -> IME stuff
        if (vk_code == 32) {
            // space
            return 32;
        }
        if (vk_code == 33) {
            // 33 -> page up
            return 266;
        }
        if (vk_code == 34) {
            // 34 -> page down
            return 267;
        }
        if (vk_code == 35) {
            // 35 -> end
            return 269;
        }
        if (vk_code == 36) {
            // 36 -> home
            return 268;
        }
        if (vk_code == 37) {
            // 37 -> left arrow
            return 263;
        }
        if (vk_code == 38) {
            // 38 -> up arrow
            return 265;
        }
        if (vk_code == 39) {
            // 39 -> right arrow
            return 262;
        }
        if (vk_code == 40) {
            // 40 -> down arrow
            return 264;
        }
        // 41 -> select
        // 42 -> print
        // 43 -> execute
        // 44 -> print screen
        if (vk_code == 45) {
            // 45 -> insert
            return 260;
        }
        if (vk_code == 46) {
            // 46 -> del
            return 261;
        }
        // 47 -> help
        if (vk_code >= 48 && vk_code <= 57) {
            // 48-57 -> 0-9
            return vk_code;
        }
        // 58-64 -> undefined
        if (vk_code >= 65 && vk_code <= 90) {
            // 65-90 -> a-z
            return vk_code;
        }
        // 91-93 -> win keys
        // 94 -> reserved
        // 95 -> sleep
        if (vk_code >= 96 && vk_code <= 105) {
            // 96-105 -> 0-9 numpad
            return vk_code - 224;
        }
        if (vk_code == 106) {
            // 106 -> multiply
            return 332;
        }
        if (vk_code == 107) {
            // 107 -> add
            return 334;
        }
        // 108 -> separator
        if (vk_code == 109) {
            // 109 -> subtract
            return 333;
        }
        if (vk_code == 110) {
            // 110 -> decimal
            return 330;
        }
        if (vk_code == 111) {
            // 111 -> divide
            return 331;
        }
        if (vk_code >= 112 && vk_code <= 135) {
            // 112-135 -> F1-F25
            return vk_code + 178;
        }
        // 136-143 -> unassigned
        if (vk_code == 144) {
            // 144 -> num lock
            return 282;
        }
        if (vk_code == 145) {
            return 281;
        }
        // 146-150 -> OEM specific
        // 151-159 -> unassigned
        if (vk_code == 160) {
            // 160 -> left shift
            return 340;
        }
        if (vk_code == 161) {
            // 161 -> right shift
            return 344;
        }
        if (vk_code == 162) {
            // 162 -> left control
            return 341;
        }
        if (vk_code == 163) {
            // 163 -> right control
            return 345;
        }
        // 164 -> left menu
        // 165 -> right menu
        // 166-183 -> windows media keys
        // 184-185 -> reserved
        // 186-192 -> windows 2000 keys
        // 193-215 -> reserved
        // 216-218 -> unassigned
        // 219-222 -> windows 2000 keys
        // 223 -> ?
        // 224 -> reserved
        // 225 -> OEM specific
        // 226 -> windows 2000 key
        // 227-228 -> OEM specific
        // 229 -> IME process key
        // 230 -> OEM specific
        // 231 -> used to pass unicode chars
        // 232 -> unassigned
        // 233-245 -> OEM specific
        // 246 -> attn
        // 247 -> crsel
        // 248 -> exsel
        // 249 -> erase EOF
        // 250 -> play
        // 251 -> zoom
        // 252 -> reserved
        // 253 -> pa1
        // 254 -> clear (oem)

        // Not supported yet
        return -2;
    }
}
