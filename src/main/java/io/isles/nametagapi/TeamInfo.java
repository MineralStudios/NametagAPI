package io.isles.nametagapi;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Represents a scoreboard team, used in the NametagManager
 * object.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Data
class TeamInfo {
    private final String name;
    private String prefix, suffix;
}