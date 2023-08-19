package com.thehuginn.resolution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResolutionContext {

    private final String locale;
    private final String gameId;
    private final String player;
    private final List<String> players;
    private final List<String> shuffledPlayers;

    private ResolutionContext(String locale) {
        this.locale = locale;
        gameId = null;
        player = null;
        players = null;
        shuffledPlayers = null;
    }

    public ResolutionContext() {
        shuffledPlayers = null;
        players = null;
        player = null;
        gameId = null;
        locale = null;
    }

    private ResolutionContext(
            String locale,
            String gameId,
            String player,
            List<String> players
    ) {
        if (!players.contains(player)) {
            throw new IllegalArgumentException("Player can not be found between all players.");
        }
        this.locale = locale;
        this.gameId = gameId;
        this.player = player;
        this.players = Collections.unmodifiableList(players);
        this.shuffledPlayers = players.stream()
                .filter(player1 -> !player1.equals(player))
                .collect(Collectors.toList());
        Collections.shuffle(this.shuffledPlayers);
    }

    public static Builder builder(String gameId) {
        return new Builder(gameId);
    }

    public static ResolutionContext locale(String locale) {
        return new ResolutionContext(locale);
    }

    public static class Builder {
        private String locale = "en";
        private final String gameId;
        private String player = null;
        private List<String> players = null;

        private Builder(@Nonnull String gameId) {
            this.gameId = gameId;
        }

        public Builder locale(@Nonnull String locale) {
            this.locale = locale;
            return this;
        }

        public Builder player(@Nonnull String player) {
            this.player = player;
            return this;
        }

        public Builder players(@Nonnull List<String> players) {
            this.players = players;
            return this;
        }

        public ResolutionContext build() {
            return new ResolutionContext(locale, gameId, player, players);
        }
    }

    public String getLocale() {
        return locale;
    }

    public String getGameId() {
        return gameId;
    }

    public String getPlayer() {
        return player;
    }

    public List<String> getPlayers() {
        return players;
    }

    public String getRandomPlayer(Integer index) {
        if (index.compareTo(0) == 0) {
            return getPlayer();
        }
        if (index < 1 || index > shuffledPlayers.size()) {
            throw new ArrayIndexOutOfBoundsException("Not enough players to resolve this task");
        }
        return shuffledPlayers.get(index - 1);
    }
}
