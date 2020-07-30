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

import java.util.HashMap;

public class KeysManager {

    public enum KeyState { JUST_PRESSED, BEING_PRESSED, JUST_RELEASED, RELEASED}

    private static HashMap<Integer, KeyState> KEYS = new HashMap<>();

    /**
     * !!! IT ONLY WORKS IF BettercolorsEngine#keyEvent is used !!!
     *
     * @param code key code
     * @return the state of the given key
     */
    public static KeyState getKeyState (int code) {
        return KEYS.getOrDefault(code, KeyState.RELEASED);
    }

    /**
     * Used by the engine to manage the keys.
     *
     * @param code key code
     * @param state state of the given key
     */
    public static void setKeyState (int code, KeyState state) {
        KEYS.put(code, state);
    }
}
