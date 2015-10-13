package no.stelar7.api.l4j8.basic.constants;

import java.util.stream.Stream;

public enum GameType
{
    /**
     * Custom games
     */
    CUSTOM_GAME,
    /**
     * Tutorial games
     */
    TUTORIAL_GAME,
    /**
     * All other games
     */
    MATCHED_GAME;

    /**
     * Returns a GameType from the provided code
     * 
     * @param code
     *            the lookup key
     * @return GameType
     */
    public static GameType getFromCode(final String gameType)
    {
        return Stream.of(GameType.values()).filter(t -> t.name().equals(gameType)).findFirst().get();
    }

    /**
     * The code used to map strings to objects
     * 
     * @return String
     */
    public String getCode()
    {
        return this.name();
    }
}