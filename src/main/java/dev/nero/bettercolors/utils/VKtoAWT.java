/*
 * Copyright 2018-2020 Bettercolors Contributors (https://github.com/N3ROO/Bettercolors)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.nero.bettercolors.utils;

import java.awt.event.KeyEvent;
import org.lwjgl.input.Keyboard;

public class VKtoAWT {

    /**
     * Needs improvements!
     *
     * ALT not working
     * No difference between left/right for CTRL and SHIFT. So right is returned by default.
     *
     * @param code java.awt.event.KeyEvent code
     * @return corrresponding org.lwjgl.input.Keyboard, or -1 if undefined, and -2 if not supported
     */
    public static int convertVKSwingtoAWT(int code) {
        switch (code) {
            case KeyEvent.CHAR_UNDEFINED:
                return -1;
            case KeyEvent.VK_SPACE:
                return Keyboard.KEY_SPACE;
            // no apostrophe
            case KeyEvent.VK_COMMA:
                return Keyboard.KEY_COMMA;
            case KeyEvent.VK_MINUS:
                return Keyboard.KEY_MINUS;
            case KeyEvent.VK_PERIOD:
                return Keyboard.KEY_PERIOD;
            case KeyEvent.VK_SLASH:
                return Keyboard.KEY_SLASH;
            case KeyEvent.VK_0:
                return Keyboard.KEY_0;
            case KeyEvent.VK_1:
                return Keyboard.KEY_1;
            case KeyEvent.VK_2:
                return Keyboard.KEY_2;
            case KeyEvent.VK_3:
                return Keyboard.KEY_3;
            case KeyEvent.VK_4:
                return Keyboard.KEY_4;
            case KeyEvent.VK_5:
                return Keyboard.KEY_5;
            case KeyEvent.VK_6:
                return Keyboard.KEY_6;
            case KeyEvent.VK_7:
                return Keyboard.KEY_7;
            case KeyEvent.VK_8:
                return Keyboard.KEY_8;
            case KeyEvent.VK_9:
                return Keyboard.KEY_9;
            case KeyEvent.VK_SEMICOLON:
                return Keyboard.KEY_SEMICOLON;
            case KeyEvent.VK_EQUALS:
                return Keyboard.KEY_EQUALS;
            case KeyEvent.VK_A:
                return Keyboard.KEY_A;
            case KeyEvent.VK_B:
                return Keyboard.KEY_B;
            case KeyEvent.VK_C:
                return Keyboard.KEY_C;
            case KeyEvent.VK_D:
                return Keyboard.KEY_D;
            case KeyEvent.VK_E:
                return Keyboard.KEY_E;
            case KeyEvent.VK_F:
                return Keyboard.KEY_F;
            case KeyEvent.VK_G:
                return Keyboard.KEY_G;
            case KeyEvent.VK_H:
                return Keyboard.KEY_H;
            case KeyEvent.VK_I:
                return Keyboard.KEY_I;
            case KeyEvent.VK_J:
                return Keyboard.KEY_J;
            case KeyEvent.VK_K:
                return Keyboard.KEY_K;
            case KeyEvent.VK_L:
                return Keyboard.KEY_L;
            case KeyEvent.VK_M:
                return Keyboard.KEY_M;
            case KeyEvent.VK_N:
                return Keyboard.KEY_N;
            case KeyEvent.VK_O:
                return Keyboard.KEY_O;
            case KeyEvent.VK_P:
                return Keyboard.KEY_P;
            case KeyEvent.VK_Q:
                return Keyboard.KEY_Q;
            case KeyEvent.VK_R:
                return Keyboard.KEY_R;
            case KeyEvent.VK_S:
                return Keyboard.KEY_S;
            case KeyEvent.VK_T:
                return Keyboard.KEY_T;
            case KeyEvent.VK_U:
                return Keyboard.KEY_U;
            case KeyEvent.VK_V:
                return Keyboard.KEY_V;
            case KeyEvent.VK_W:
                return Keyboard.KEY_W;
            case KeyEvent.VK_X:
                return Keyboard.KEY_X;
            case KeyEvent.VK_Y:
                return Keyboard.KEY_Y;
            case KeyEvent.VK_Z:
                return Keyboard.KEY_Z;
            case KeyEvent.VK_OPEN_BRACKET:
                return Keyboard.KEY_LBRACKET;
            case KeyEvent.VK_BACK_SLASH:
                return Keyboard.KEY_BACKSLASH;
            case KeyEvent.VK_CLOSE_BRACKET:
                return Keyboard.KEY_RBRACKET;
            // no grave accent with VK
            // world 1?
            // world 2?
            case KeyEvent.VK_ESCAPE:
                return Keyboard.KEY_ESCAPE;
            case KeyEvent.VK_ENTER:
                return Keyboard.KEY_RETURN;
            case KeyEvent.VK_TAB:
                return Keyboard.KEY_TAB;
            case KeyEvent.VK_BACK_SPACE:
                return Keyboard.KEY_BACK;
            case KeyEvent.VK_INSERT:
                return Keyboard.KEY_INSERT;
            case KeyEvent.VK_DELETE:
                return Keyboard.KEY_DELETE;
            case KeyEvent.VK_RIGHT:
                return Keyboard.KEY_RIGHT;
            case KeyEvent.VK_LEFT:
                return Keyboard.KEY_LEFT;
            case KeyEvent.VK_DOWN:
                return Keyboard.KEY_DOWN;
            case KeyEvent.VK_UP:
                return Keyboard.KEY_UP;
            case KeyEvent.VK_PAGE_UP:
                return -2;
            case KeyEvent.VK_PAGE_DOWN:
                return -2;
            case KeyEvent.VK_HOME:
                return Keyboard.KEY_HOME;
            case KeyEvent.VK_END:
                return Keyboard.KEY_END;
            case KeyEvent.VK_CAPS_LOCK:
                return Keyboard.KEY_CAPITAL;
            case KeyEvent.VK_SCROLL_LOCK:
                return Keyboard.KEY_SCROLL;
            case KeyEvent.VK_NUM_LOCK:
                return Keyboard.KEY_NUMLOCK;
            case KeyEvent.VK_PRINTSCREEN:
                return -2;
            case KeyEvent.VK_PAUSE:
                return Keyboard.KEY_PAUSE;
            case KeyEvent.VK_F1:
                return Keyboard.KEY_F1;
            case KeyEvent.VK_F2:
                return Keyboard.KEY_F2;
            case KeyEvent.VK_F3:
                return Keyboard.KEY_F3;
            case KeyEvent.VK_F4:
                return Keyboard.KEY_F4;
            case KeyEvent.VK_F5:
                return Keyboard.KEY_F5;
            case KeyEvent.VK_F6:
                return Keyboard.KEY_F6;
            case KeyEvent.VK_F7:
                return Keyboard.KEY_F7;
            case KeyEvent.VK_F8:
                return Keyboard.KEY_F8;
            case KeyEvent.VK_F9:
                return Keyboard.KEY_F9;
            case KeyEvent.VK_F10:
                return Keyboard.KEY_F10;
            case KeyEvent.VK_F11:
                return Keyboard.KEY_F11;
            case KeyEvent.VK_F12:
                return Keyboard.KEY_F12;
            case KeyEvent.VK_F13:
                return Keyboard.KEY_F13;
            case KeyEvent.VK_F14:
                return Keyboard.KEY_F14;
            case KeyEvent.VK_F15:
                return Keyboard.KEY_F15;
            case KeyEvent.VK_F16:
                return Keyboard.KEY_F16;
            case KeyEvent.VK_F17:
                return Keyboard.KEY_F17;
            case KeyEvent.VK_F18:
                return Keyboard.KEY_F18;
            case KeyEvent.VK_F19:
                return Keyboard.KEY_F19;
            case KeyEvent.VK_F20:
                return -2;
            case KeyEvent.VK_F21:
                return -2;
            case KeyEvent.VK_F22:
                return -2;
            case KeyEvent.VK_F23:
                return -2;
            case KeyEvent.VK_F24:
                return -2;
            // no F25
            case KeyEvent.VK_NUMPAD0:
                return Keyboard.KEY_NUMPAD0;
            case KeyEvent.VK_NUMPAD1:
                return Keyboard.KEY_NUMPAD1;
            case KeyEvent.VK_NUMPAD2:
                return Keyboard.KEY_NUMPAD2;
            case KeyEvent.VK_NUMPAD3:
                return Keyboard.KEY_NUMPAD3;
            case KeyEvent.VK_NUMPAD4:
                return Keyboard.KEY_NUMPAD4;
            case KeyEvent.VK_NUMPAD5:
                return Keyboard.KEY_NUMPAD5;
            case KeyEvent.VK_NUMPAD6:
                return Keyboard.KEY_NUMPAD6;
            case KeyEvent.VK_NUMPAD7:
                return Keyboard.KEY_NUMPAD7;
            case KeyEvent.VK_NUMPAD8:
                return Keyboard.KEY_NUMPAD8;
            case KeyEvent.VK_NUMPAD9:
                return Keyboard.KEY_NUMPAD9;
            case KeyEvent.VK_DECIMAL:
                return Keyboard.KEY_DECIMAL;
            case KeyEvent.VK_DIVIDE:
                return Keyboard.KEY_DIVIDE;
            case KeyEvent.VK_MULTIPLY:
                return Keyboard.KEY_MULTIPLY;
            case KeyEvent.VK_SUBTRACT:
                return Keyboard.KEY_SUBTRACT;
            case KeyEvent.VK_ADD:
                return Keyboard.KEY_ADD;
            // no kp enter
            // no kp equals
            case KeyEvent.VK_SHIFT:
                return Keyboard.KEY_RSHIFT;  // !! No difference between L and R
            case KeyEvent.VK_CONTROL:
                return Keyboard.KEY_RCONTROL;  // !! No difference between L and R
            case KeyEvent.VK_ALT:
                return -2;  // !! No difference between L and R
            // no super
            case KeyEvent.VK_CONTEXT_MENU:
                return -2;
            default:
                return -2;
        }
    }

}
