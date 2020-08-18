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

import dev.nero.bettercolors.engine.io.RawFiler;
import dev.nero.bettercolors.engine.view.LogLevel;
import dev.nero.bettercolors.engine.view.Window;

import java.util.ArrayList;

public class Friends {

    private static ArrayList<String> friends = new ArrayList<>();

    /**
     * Adds a friend to the friend list. It updates the GUI accordingly
     * @param someone friend name
     */
    public static void addFriend(String someone) {
        if (!isFriend(someone)) {
            friends.add(someone);

            // Update the friend list only if the window has already been initialized
            if (Window.getInstance() != null) {
                Window.getInstance().updateFriends();

                // Update the file
                RawFiler friendsFile = new RawFiler("friends");
                friendsFile.write(someone, friends.size() > 1);

                Window.LOG(LogLevel.INFO, "[+] Added " + someone);
            }
        }
    }

    /**
     * Removes a friend from the friend list. It updates the GUI accordingly
     * @param friend friend's name
     */
    public static void removeFriend(String friend) {
        if (isFriend(friend)) {
            friends.remove(friend);

            // Update the friend list only if the window has already been initialized
            if (Window.getInstance() != null) {
                Window.getInstance().updateFriends();

                // Update the file
                RawFiler friendsFile = new RawFiler("friends");
                friendsFile.erase(friend);

                Window.LOG(LogLevel.INFO, "[+] Removed " + friend);
            }
        }
    }

    /**
     * Loads the friends from the list (bc_friends) and overrides the current friends.
     */
    public static void loadFriends() {
        RawFiler friendsFiler = new RawFiler("friends");
                
        ArrayList<String> readFriends = friendsFiler.readAll();
        if (readFriends == null) {
            friendsFiler.write("", false);
        } else {
            Friends.friends = readFriends;
        }

        if (Window.getInstance() != null) {
            Window.getInstance().updateFriends();
            Window.LOG(LogLevel.INFO, "[+] Loaded " + friends.size() + " friends");
        }
    }

    /**
     * @param someone their name
     * @return true if that name is in the friend list
     */
    public static boolean isFriend(String someone) {
        return friends.contains(someone);
    }

    public static ArrayList<String> getFriends() {
        return friends;
    }
}
