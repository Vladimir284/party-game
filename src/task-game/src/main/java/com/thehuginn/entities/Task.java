package com.thehuginn.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Entity
public class Task extends PanacheEntity {

    public enum Type {SINGLE, DUO, ALL}

    public enum Repeat {ALWAYS, PER_PLAYER, NEVER}

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "task_sequence", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "task", nullable = false)
    @NotEmpty(message = "task sequence can't be empty")
    @JsonProperty
    public List<String> task;

    @JsonProperty
    public Type type = Type.SINGLE;

    @JsonProperty
    public Repeat repeat = Repeat.NEVER;

    @JsonProperty
    public short frequency = 1;

    @JsonProperty
    public Price price = new Price();

    @JsonProperty
    public Timer timer = new Timer();

    @Embeddable
    public static class Price {
        @Column(name = "price_enabled")
        public boolean enabled = true;
        public int price = 1;

        public Price() {}

        public Price(boolean enabled, int price) {
            this.enabled = enabled;
            this.price = price;
        }
    }

    @Embeddable
    public static class Timer {
        @Column(name = "timer_enabled")
        public boolean enabled = false;
        public int duration = 60;

        public Timer() {}

        public Timer(boolean enabled, int duration) {
            this.enabled = enabled;
            this.duration = duration;
        }
    }

    public Task() {}

    public static class Builder {

        private long id;

        private List<String> task;

        private Type type = Type.SINGLE;

        private Repeat repeat = Repeat.NEVER;

        private short frequency = 1;

        private Price price = new Price();

        private Timer timer = new Timer();

        public Builder(List<String> task) {
            this.task = task;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder repeat(Repeat repeat) {
            this.repeat = repeat;
            return this;
        }

        public Builder frequency(short frequency) {
            this.frequency = frequency;
            return this;
        }

        public  Builder price(Price price) {
            this.price = price;
            return this;
        }

        public Builder timer(Timer timer) {
            this.timer = timer;
            return this;
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Task build() {
            Task builtTask = new Task();
            builtTask.id = id;
            builtTask.task = task;
            builtTask.type = type;
            builtTask.repeat = repeat;
            builtTask.frequency = frequency;
            builtTask.price = price;
            builtTask.timer = timer;
            return builtTask;
        }
    }

    public static Uni<List<Task>> findByIds(List<Task> tasks) {
        List<Long> ids = tasks.stream()
                .map(task -> task.id)
                .toList();
        return Task.<Task>find("From Task t where t.id IN :ids", Parameters.with("ids", ids)).list();
    }
}
