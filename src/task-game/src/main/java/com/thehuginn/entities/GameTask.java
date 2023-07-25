package com.thehuginn.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.util.ArrayList;
import java.util.List;

@Entity
public class GameTask extends PanacheEntityBase {

    public static class GameTaskPK {
        public String game;

        public long id;

        public GameTaskPK() {}

        @Override
        public boolean equals(Object obj) {
            if (! (obj instanceof GameTaskPK)) {
                return false;
            } else {
                GameTaskPK taskPK = (GameTaskPK) obj;
                return game.equals(taskPK.game) && id == taskPK.id;
            }
        }

        @Override
        public int hashCode() {
            return game.hashCode() + Long.hashCode(id);
        }
    }

    @Id
    public String game;

    @Id
    @GeneratedValue
    public long id;

    @ManyToOne(fetch = FetchType.EAGER)
    public Task unresolvedTask;

    // List of players, who already answered this question in case of type PER_PLAYER
    public GameTask() {}

    public GameTask(Game game, Task task) {
        this.game = game.gameId;
        this.unresolvedTask = task;
    }

    public ResolvedTask resolve() {
        return new ResolvedTask();
    }

    public static List<GameTask> generateGameTasks(Game game,  List<Task> tasks) {
        GameTask.delete("game", game.gameId);

        List<GameTask> createdTasks = new ArrayList<>();
        for (var task : tasks) {
            for (short amount = 0; amount < task.frequency; amount++) {
                createdTasks.add(new GameTask(game, task));
            }
        }

        return createdTasks;
    }
}
