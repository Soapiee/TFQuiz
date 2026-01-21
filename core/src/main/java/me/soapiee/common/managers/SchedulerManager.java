package me.soapiee.common.managers;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.instance.Game;
import me.soapiee.common.tasks.Scheduler;

import java.util.HashMap;

public class SchedulerManager {

    private final TFQuiz main;
    private final GameManager gameManager;

    private final HashMap<Integer, Scheduler> schedulers = new HashMap<>();

    public SchedulerManager(TFQuiz main) {
        this.main = main;
        gameManager = main.getGameManager();
    }

    public void reload() {
        startSchedulers();
    }

    public void startSchedulers() {
        for (Game game : gameManager.getGames()) {
            int id = game.getIdentifier();
            if (game.isPhysicalArena()) continue;

            int delay = game.getSchedulerDelay();
            int resetDelay = game.getSchedulerResetterDelay();
            if (delay == -1 || resetDelay == -1) continue;

            schedulers.put(id, new Scheduler(main, game));
        }
    }

    public void cancelSchedulers() {
        for (Scheduler scheduler : schedulers.values()) {
            try {
                scheduler.setPlayed();
                scheduler.cancel();
            } catch (IllegalStateException ignored) {
            }
        }

        schedulers.clear();
    }

    public boolean hasScheduler(Game game) {
        return schedulers.containsKey(game.getIdentifier());
    }

    public Scheduler getScheduler(int gameID) {
        return schedulers.get(gameID);
    }

    public void newScheduler(Game game) {
        int gameID = game.getIdentifier();
        Scheduler scheduler = schedulers.get(gameID);

        try {
            scheduler.setPlayed();
            scheduler.cancel();
        } catch (IllegalStateException ignored) {
        }

        schedulers.put(gameID, new Scheduler(main, game));
    }
}
