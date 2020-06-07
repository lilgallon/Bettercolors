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

package dev.nero.bettercolors.version;

import dev.nero.bettercolors.main.Reference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

public class Version {


    // Used to delimit the mod version from the MC version
    // A version is written this way: x.y.z-MCa.b.c (ex: 6.0.0-MC1.8.9)
    private final static String MC_PREFIX = "-MC";
    // This url will be used to get the latest version
    private final static String RELEASES_URL = "https://api.github.com/repos/n3roo/bettercolors/releases";

    // The minecraft version of the mod
    private String mcVersion;
    // Version notation major.minor.bug(-bbeta)
    private int majorRev;
    private int minorRev;
    private int bugRev;
    private int betaRev;

    // The changelog of that version
    private String changelog;

    /**
     * A version is written this way:
     * - major.minor.bug (ex: 6.1.0), or
     * - x.y.z-bw (ex: 6.0.0-b1)
     * @param mcVersion the Minecraft version ("1.15.2" for example)
     * @param majorRev major revision number
     * @param minorRev minor revision number
     * @param bugRev bug revision number
     * @param betaRev beta revision number
     * @param changelog changes of that version (supports emojis)
     */
    public Version(String mcVersion, int majorRev, int minorRev, int bugRev, int betaRev, String changelog) {
        this.mcVersion = mcVersion;
        this.majorRev = majorRev;
        this.minorRev = minorRev;
        this.bugRev = bugRev;
        this.betaRev = betaRev;
        this.changelog = changelog;
    }

    /**
     * A version is written this way:
     * - major.minor.bug (ex: 6.1.0), or
     * - x.y.z-bw (ex: 6.0.0-b1)
     * @param mcVersion the Minecraft version ("1.15.2" for example)
     * @param majorRev major revision number
     * @param minorRev minor revision number
     * @param bugRev bug revision number
     * @param changelog changes of that version (supports emojis)
     */
    public Version(String mcVersion, int majorRev, int minorRev, int bugRev, String changelog) {
        this.mcVersion = mcVersion;
        this.majorRev = majorRev;
        this.minorRev = minorRev;
        this.bugRev = bugRev;
        this.betaRev = 0;
        this.changelog = changelog;
    }

    /**
     * @param mcVersion the Minecraft version ("1.15.2" for example)
     * @return the latest version of the mod for the given Minecraft version
     */
    public static Version getLatestVersion(String mcVersion) throws VersionException {
        try {
            // We open the URL and read what's written
            URL url = new URL(RELEASES_URL);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String json = in.lines().collect(Collectors.joining());
            in.close();

            // Now that we have all the data in the json variable, we need to find the latest version of the mod for the
            // given minecraft version

            // Tags contains all the versions released for the mod, written this way: x.y.z(-bw)-MCa.b.c
            String[] tags = json.split("\"tag_name\"");
            String[] bodies = json.split("\"body\"");

            // It is sorted properly. It means that the first tags are the latest ones. So we need to find the first tag
            // that has the given minecraft version
            int i = 0;
            boolean found = false;
            String latestVersion = "";
            while (i < tags.length && !found){
                latestVersion = tags[i].split("\"")[1];
                if (latestVersion.endsWith(MC_PREFIX + mcVersion)) {
                    found = true;
                } else {
                    i ++;
                }
            }

            if(!found) {
                throw new VersionException("No version found");
            } else {
                // Remove the minecraft version from the mod version (6.0.0-MC1.15.2 -> 6.0.0)
                latestVersion = latestVersion.replace(MC_PREFIX + mcVersion, "");
                // Split the latest version this way: 6.0.0-b1 -> [6.0.0, 1] or 6.0.0 -> [6.0.0]
                String[] latestVersionSplit = latestVersion.split("-b");
                // Split the main part of the version this way: 6.1.0 -> [6, 1, 0]
                String[] majMinBugVersion = latestVersionSplit[0].split(".");

                if (latestVersionSplit.length == 2) {
                    // Means that it is a beta version
                    return new Version(
                            mcVersion,
                            Integer.parseInt(majMinBugVersion[0]),
                            Integer.parseInt(majMinBugVersion[1]),
                            Integer.parseInt(majMinBugVersion[2]),
                            Integer.parseInt(latestVersionSplit[1]),
                            bodies[i]
                    );
                } else {
                    // It is not a beta version
                    return new Version(
                            mcVersion,
                            Integer.parseInt(majMinBugVersion[0]),
                            Integer.parseInt(majMinBugVersion[1]),
                            Integer.parseInt(majMinBugVersion[2]),
                            bodies[i]
                    );
                }
            }
        } catch (MalformedURLException e){
            throw new VersionException("Url issue");
        } catch (IOException e){
            throw new VersionException("No internet connection");
        }
    }

    @Override
    public boolean equals(Object obj) {
        // TODO
        return super.equals(obj);
    }

    @Override
    public String toString() {
        // TODO
        return super.toString();
    }
}
