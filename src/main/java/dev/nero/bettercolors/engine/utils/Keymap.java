/*
 * Copyright 2018-2020
 * - Bettercolors Contributors (https://github.com/N3ROO/Bettercolors) and
 * - Bettercolors Engine Contributors (https://github.com/N3ROO/BettercolorsEngine)
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
package dev.nero.bettercolors.engine.utils;

import java.awt.event.KeyEvent;

public class Keymap {

    /**
     * Converts AWT key code to:
     * - GLFW (if glfw is true)
     * - VK (if glfw is false)
     *
     * @param code java.awt.event.KeyEvent code
     * @return corresponding org.lwjgl.input.Keyboard, or -1 if undefined, and -2 if not supported
     */
    public static int map(int code, boolean glfw) {
        if (glfw) {
            switch (code) {
                case KeyEvent.CHAR_UNDEFINED:
                    return -1;
                case KeyEvent.VK_SPACE:
                    return 32;
                // no apostrophe
                case KeyEvent.VK_COMMA:
                    return 44;
                case KeyEvent.VK_MINUS:
                    return 45;
                case KeyEvent.VK_PERIOD:
                    return 46;
                case KeyEvent.VK_SLASH:
                    return 47;
                case KeyEvent.VK_0:
                    return 48;
                case KeyEvent.VK_1:
                    return 49;
                case KeyEvent.VK_2:
                    return 50;
                case KeyEvent.VK_3:
                    return 51;
                case KeyEvent.VK_4:
                    return 52;
                case KeyEvent.VK_5:
                    return 53;
                case KeyEvent.VK_6:
                    return 54;
                case KeyEvent.VK_7:
                    return 55;
                case KeyEvent.VK_8:
                    return 56;
                case KeyEvent.VK_9:
                    return 57;
                case KeyEvent.VK_SEMICOLON:
                    return 59;
                case KeyEvent.VK_EQUALS:
                    return 61;
                case KeyEvent.VK_A:
                    return 65;
                case KeyEvent.VK_B:
                    return 66;
                case KeyEvent.VK_C:
                    return 67;
                case KeyEvent.VK_D:
                    return 68;
                case KeyEvent.VK_E:
                    return 69;
                case KeyEvent.VK_F:
                    return 70;
                case KeyEvent.VK_G:
                    return 71;
                case KeyEvent.VK_H:
                    return 72;
                case KeyEvent.VK_I:
                    return 73;
                case KeyEvent.VK_J:
                    return 74;
                case KeyEvent.VK_K:
                    return 75;
                case KeyEvent.VK_L:
                    return 76;
                case KeyEvent.VK_M:
                    return 77;
                case KeyEvent.VK_N:
                    return 78;
                case KeyEvent.VK_O:
                    return 79;
                case KeyEvent.VK_P:
                    return 80;
                case KeyEvent.VK_Q:
                    return 81;
                case KeyEvent.VK_R:
                    return 82;
                case KeyEvent.VK_S:
                    return 83;
                case KeyEvent.VK_T:
                    return 84;
                case KeyEvent.VK_U:
                    return 85;
                case KeyEvent.VK_V:
                    return 86;
                case KeyEvent.VK_W:
                    return 87;
                case KeyEvent.VK_X:
                    return 88;
                case KeyEvent.VK_Y:
                    return 89;
                case KeyEvent.VK_Z:
                    return 90;
                case KeyEvent.VK_OPEN_BRACKET:
                    return 91;
                case KeyEvent.VK_BACK_SLASH:
                    return 92;
                case KeyEvent.VK_CLOSE_BRACKET:
                    return 93;
                // no grave accent with VK
                // world 1?
                // world 2?
                case KeyEvent.VK_ESCAPE:
                    return 256;
                case KeyEvent.VK_ENTER:
                    return 257;
                case KeyEvent.VK_TAB:
                    return 258;
                case KeyEvent.VK_BACK_SPACE:
                    return 259;
                case KeyEvent.VK_INSERT:
                    return 260;
                case KeyEvent.VK_DELETE:
                    return 261;
                case KeyEvent.VK_RIGHT:
                    return 262;
                case KeyEvent.VK_LEFT:
                    return 263;
                case KeyEvent.VK_DOWN:
                    return 264;
                case KeyEvent.VK_UP:
                    return 265;
                case KeyEvent.VK_PAGE_UP:
                    return 266;
                case KeyEvent.VK_PAGE_DOWN:
                    return 267;
                case KeyEvent.VK_HOME:
                    return 268;
                case KeyEvent.VK_END:
                    return 269;
                case KeyEvent.VK_CAPS_LOCK:
                    return 280;
                case KeyEvent.VK_SCROLL_LOCK:
                    return 281;
                case KeyEvent.VK_NUM_LOCK:
                    return 282;
                case KeyEvent.VK_PRINTSCREEN:
                    return 283;
                case KeyEvent.VK_PAUSE:
                    return 284;
                case KeyEvent.VK_F1:
                    return 290;
                case KeyEvent.VK_F2:
                    return 291;
                case KeyEvent.VK_F3:
                    return 292;
                case KeyEvent.VK_F4:
                    return 293;
                case KeyEvent.VK_F5:
                    return 294;
                case KeyEvent.VK_F6:
                    return 295;
                case KeyEvent.VK_F7:
                    return 296;
                case KeyEvent.VK_F8:
                    return 297;
                case KeyEvent.VK_F9:
                    return 298;
                case KeyEvent.VK_F10:
                    return 299;
                case KeyEvent.VK_F11:
                    return 300;
                case KeyEvent.VK_F12:
                    return 301;
                case KeyEvent.VK_F13:
                    return 302;
                case KeyEvent.VK_F14:
                    return 303;
                case KeyEvent.VK_F15:
                    return 304;
                case KeyEvent.VK_F16:
                    return 305;
                case KeyEvent.VK_F17:
                    return 306;
                case KeyEvent.VK_F18:
                    return 307;
                case KeyEvent.VK_F19:
                    return 308;
                case KeyEvent.VK_F20:
                    return 309;
                case KeyEvent.VK_F21:
                    return 310;
                case KeyEvent.VK_F22:
                    return 311;
                case KeyEvent.VK_F23:
                    return 312;
                case KeyEvent.VK_F24:
                    return 313;
                // no F25
                case KeyEvent.VK_NUMPAD0:
                    return 320;
                case KeyEvent.VK_NUMPAD1:
                    return 321;
                case KeyEvent.VK_NUMPAD2:
                    return 322;
                case KeyEvent.VK_NUMPAD3:
                    return 323;
                case KeyEvent.VK_NUMPAD4:
                    return 324;
                case KeyEvent.VK_NUMPAD5:
                    return 325;
                case KeyEvent.VK_NUMPAD6:
                    return 326;
                case KeyEvent.VK_NUMPAD7:
                    return 327;
                case KeyEvent.VK_NUMPAD8:
                    return 328;
                case KeyEvent.VK_NUMPAD9:
                    return 329;
                case KeyEvent.VK_DECIMAL:
                    return 330;
                case KeyEvent.VK_DIVIDE:
                    return 331;
                case KeyEvent.VK_MULTIPLY:
                    return 332;
                case KeyEvent.VK_SUBTRACT:
                    return 333;
                case KeyEvent.VK_ADD:
                    return 334;
                // no kp enter
                // no kp equals
                case KeyEvent.VK_SHIFT:
                    return 344;  // !! No difference between L and R
                case KeyEvent.VK_CONTROL:
                    return 345;  // !! No difference between L and R
                case KeyEvent.VK_ALT:
                    return 346;  // !! No difference between L and R
                // no super
                case KeyEvent.VK_CONTEXT_MENU:
                    return 348;
                default:
                    return -2;
            }
        } else {
            switch (code) {
                case KeyEvent.CHAR_UNDEFINED:
                    return -1;
                case KeyEvent.VK_SPACE:
                    return 57;
                // no apostrophe
                case KeyEvent.VK_COMMA:
                    return 51;
                case KeyEvent.VK_MINUS:
                    return 12;
                case KeyEvent.VK_PERIOD:
                    return 52;
                case KeyEvent.VK_SLASH:
                    return 53;
                case KeyEvent.VK_0:
                    return 11;
                case KeyEvent.VK_1:
                    return 2;
                case KeyEvent.VK_2:
                    return 3;
                case KeyEvent.VK_3:
                    return 4;
                case KeyEvent.VK_4:
                    return 5;
                case KeyEvent.VK_5:
                    return 6;
                case KeyEvent.VK_6:
                    return 7;
                case KeyEvent.VK_7:
                    return 8;
                case KeyEvent.VK_8:
                    return 9;
                case KeyEvent.VK_9:
                    return 10;
                case KeyEvent.VK_SEMICOLON:
                    return 39;
                case KeyEvent.VK_EQUALS:
                    return 13;
                case KeyEvent.VK_A:
                    return 30;
                case KeyEvent.VK_B:
                    return 48;
                case KeyEvent.VK_C:
                    return 46;
                case KeyEvent.VK_D:
                    return 32;
                case KeyEvent.VK_E:
                    return 18;
                case KeyEvent.VK_F:
                    return 33;
                case KeyEvent.VK_G:
                    return 34;
                case KeyEvent.VK_H:
                    return 35;
                case KeyEvent.VK_I:
                    return 23;
                case KeyEvent.VK_J:
                    return 36;
                case KeyEvent.VK_K:
                    return 37;
                case KeyEvent.VK_L:
                    return 38;
                case KeyEvent.VK_M:
                    return 50;
                case KeyEvent.VK_N:
                    return 49;
                case KeyEvent.VK_O:
                    return 24;
                case KeyEvent.VK_P:
                    return 25;
                case KeyEvent.VK_Q:
                    return 16;
                case KeyEvent.VK_R:
                    return 19;
                case KeyEvent.VK_S:
                    return 31;
                case KeyEvent.VK_T:
                    return 20;
                case KeyEvent.VK_U:
                    return 22;
                case KeyEvent.VK_V:
                    return 47;
                case KeyEvent.VK_W:
                    return 17;
                case KeyEvent.VK_X:
                    return 45;
                case KeyEvent.VK_Y:
                    return 21;
                case KeyEvent.VK_Z:
                    return 44;
                case KeyEvent.VK_OPEN_BRACKET:
                    return 26;
                case KeyEvent.VK_BACK_SLASH:
                    return 43;
                case KeyEvent.VK_CLOSE_BRACKET:
                    return 27;
                // no grave accent with VK
                // world 1?
                // world 2?
                case KeyEvent.VK_ESCAPE:
                    return 1;
                case KeyEvent.VK_ENTER:
                    return 28;
                case KeyEvent.VK_TAB:
                    return 15;
                case KeyEvent.VK_BACK_SPACE:
                    return 14;
                case KeyEvent.VK_INSERT:
                    return 210;
                case KeyEvent.VK_DELETE:
                    return 211;
                case KeyEvent.VK_RIGHT:
                    return 205;
                case KeyEvent.VK_LEFT:
                    return 203;
                case KeyEvent.VK_DOWN:
                    return 208;
                case KeyEvent.VK_UP:
                    return 200;
                case KeyEvent.VK_PAGE_UP:
                    return -2;
                case KeyEvent.VK_PAGE_DOWN:
                    return -2;
                case KeyEvent.VK_HOME:
                    return 199;
                case KeyEvent.VK_END:
                    return 207;
                case KeyEvent.VK_CAPS_LOCK:
                    return 58;
                case KeyEvent.VK_SCROLL_LOCK:
                    return 70;
                case KeyEvent.VK_NUM_LOCK:
                    return 69;
                case KeyEvent.VK_PRINTSCREEN:
                    return -2;
                case KeyEvent.VK_PAUSE:
                    return 197;
                case KeyEvent.VK_F1:
                    return 59;
                case KeyEvent.VK_F2:
                    return 60;
                case KeyEvent.VK_F3:
                    return 61;
                case KeyEvent.VK_F4:
                    return 62;
                case KeyEvent.VK_F5:
                    return 63;
                case KeyEvent.VK_F6:
                    return 64;
                case KeyEvent.VK_F7:
                    return 65;
                case KeyEvent.VK_F8:
                    return 66;
                case KeyEvent.VK_F9:
                    return 67;
                case KeyEvent.VK_F10:
                    return 68;
                case KeyEvent.VK_F11:
                    return 87;
                case KeyEvent.VK_F12:
                    return 88;
                case KeyEvent.VK_F13:
                    return 100;
                case KeyEvent.VK_F14:
                    return 101;
                case KeyEvent.VK_F15:
                    return 102;
                case KeyEvent.VK_F16:
                    return 103;
                case KeyEvent.VK_F17:
                    return 104;
                case KeyEvent.VK_F18:
                    return 105;
                case KeyEvent.VK_F19:
                    return 113;
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
                    return 82;
                case KeyEvent.VK_NUMPAD1:
                    return 79;
                case KeyEvent.VK_NUMPAD2:
                    return 80;
                case KeyEvent.VK_NUMPAD3:
                    return 81;
                case KeyEvent.VK_NUMPAD4:
                    return 75;
                case KeyEvent.VK_NUMPAD5:
                    return 76;
                case KeyEvent.VK_NUMPAD6:
                    return 77;
                case KeyEvent.VK_NUMPAD7:
                    return 71;
                case KeyEvent.VK_NUMPAD8:
                    return 72;
                case KeyEvent.VK_NUMPAD9:
                    return 73;
                case KeyEvent.VK_DECIMAL:
                    return 83;
                case KeyEvent.VK_DIVIDE:
                    return 181;
                case KeyEvent.VK_MULTIPLY:
                    return 55;
                case KeyEvent.VK_SUBTRACT:
                    return 74;
                case KeyEvent.VK_ADD:
                    return 78;
                // no kp enter
                // no kp equals
                case KeyEvent.VK_SHIFT:
                    return 54;  // !! No difference between L and R
                case KeyEvent.VK_CONTROL:
                    return 157;  // !! No difference between L and R
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
}
