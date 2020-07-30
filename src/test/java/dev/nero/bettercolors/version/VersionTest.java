package dev.nero.bettercolors.version;

import dev.nero.bettercolors.engine.version.Version;
import dev.nero.bettercolors.engine.version.VersionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class VersionTest {

    @Test
    void toStringTest() {
        Assertions.assertNotEquals(
                new Version("1.15.2", 6, 1, 0, 0, "").toString(),
                new Version("1.15.2", 6, 0, 0, 0, "").toString()
        );

        Assertions.assertNotEquals(
                new Version("1.15.2", 6, 0, 1, 0, "").toString(),
                new Version("1.15.2", 6, 0, 0, 0, "").toString()
        );

        Assertions.assertNotEquals(
                new Version("1.15.2", 6, 0, 0, 1, "").toString(),
                new Version("1.15.2", 6, 0, 0, 0, "").toString()
        );

        Assertions.assertNotEquals(
                new Version("1.15.2", 0, 0, 0, 0, "").toString(),
                new Version("1.15.2", 6, 0, 0, 0, "").toString()
        );

        Assertions.assertEquals(
                new Version("1.15.2", 6, 3, 2, 1, "").toString(),
                new Version("1.15.2", 6, 3, 2, 1, "").toString()
        );

        Assertions.assertEquals(
                new Version("1.8.2", 6, 3, 2, 1, "").toString(),
                new Version("1.15.2", 6, 3, 2, 1, "").toString()
        );
    }

    @Test
    void equalsTest() {
        Assertions.assertNotEquals(
                new Version("1.15.2", 6, 1, 0, 0, ""),
                new Version("1.15.2", 6, 0, 0, 0, "")
        );

        Assertions.assertNotEquals(
                new Version("1.15.2", 6, 0, 1, 0, ""),
                new Version("1.15.2", 6, 0, 0, 0, "")
        );

        Assertions.assertNotEquals(
                new Version("1.15.2", 6, 0, 0, 1, ""),
                new Version("1.15.2", 6, 0, 0, 0, "")
        );

        Assertions.assertNotEquals(
                new Version("1.15.2", 0, 0, 0, 0, ""),
                new Version("1.15.2", 6, 0, 0, 0, "")
        );

        Assertions.assertNotEquals(
                new Version("1.8.2", 6, 3, 2, 1, ""),
                new Version("1.15.2", 6, 3, 2, 1, "")
        );

        Assertions.assertEquals(
                new Version("1.15.2", 6, 3, 2, 1, ""),
                new Version("1.15.2", 6, 3, 2, 1, "")
        );
    }

    @Test
    void getLatestVersion() {
        /* This test should not be uncommented in production, bc it can fail if a new version is released
        try {
            Version latest = Version.getLatestVersion("1.15.2");
            Assertions.assertEquals(
                    latest,
                    new Version("1.15.2", 6, 1, 2, 0, "ignored")
            );
        } catch (VersionException e) {
            e.printStackTrace();
        }
        */

        Assertions.assertThrows(VersionException.class, () -> Version.getLatestVersion("1.15"));

        try {
            Version.getLatestVersion("1.15");
        } catch (VersionException e) {
            // Can't find a mod version for the given minecraft version
            Assertions.assertEquals(e.getCode(), VersionException.Error.NO_VERSION);
        }
    }

    @Test
    void compareWith() {
        Version current = new Version("1.15.2", 6, 3, 2, 1, "ignored");

        Assertions.assertEquals(
                Version.VersionDiff.UPDATED,
                current.compareWith(new Version("1.15.2", 6, 3, 2, 1, "ignored"))
        );

        Assertions.assertEquals(
                Version.VersionDiff.DEVELOPMENT,
                current.compareWith(new Version("1.15.2", 6, 0, 0, 0, "ignored"))
        );

        Assertions.assertEquals(
                Version.VersionDiff.DEVELOPMENT,
                current.compareWith(new Version("1.15.2", 5, 800, 900, 100, "ignored"))
        );

        Assertions.assertEquals(
                Version.VersionDiff.DEVELOPMENT,
                current.compareWith(new Version("1.15.2", 6, 2, 900, 800, "ignored"))
        );

        Assertions.assertEquals(
                Version.VersionDiff.DEVELOPMENT,
                current.compareWith(new Version("1.15.2", 6, 3, 1, 800, "ignored"))
        );

        Assertions.assertEquals(
                // The current is a beta it means that the current is outdated
                Version.VersionDiff.OUTDATED,
                current.compareWith(new Version("1.15.2", 6, 3, 2, 0, "ignored"))
        );

        Assertions.assertEquals(
                Version.VersionDiff.OUTDATED,
                current.compareWith(new Version("1.15.2", 7, 3, 2, 1, "ignored"))
        );

        Assertions.assertEquals(
                Version.VersionDiff.OUTDATED,
                current.compareWith(new Version("1.15.2", 6, 4, 2, 1, "ignored"))
        );

        Assertions.assertEquals(
                Version.VersionDiff.OUTDATED,
                current.compareWith(new Version("1.15.2", 6, 3, 3, 1, "ignored"))
        );

        Assertions.assertEquals(
                Version.VersionDiff.OUTDATED,
                current.compareWith(new Version("1.15.2", 6, 3, 2, 2, "ignored"))
        );
    }
}
