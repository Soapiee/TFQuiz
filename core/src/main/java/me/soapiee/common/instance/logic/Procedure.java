package me.soapiee.common.instance.logic;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.Reward;
import me.soapiee.common.manager.MessageManager;
import me.soapiee.common.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Procedure implements Listener {

    private final Game game;
    private final TFQuiz main;
    private final MessageManager messageManager;
    private boolean commandEnd;
    private boolean canAnswer;

    private final ArrayList<Question> trueQuestions;
    private final ArrayList<Question> falseQuestions;
    private String correctionMessage;
    private final int maxRounds;

    private final ArrayList<Player> toEliminate;
    private boolean correctAnswer;
    private final ArrayList<Player> answeredCorrectly;
    private RoundTimer timer;
    private int roundCount;

    public Procedure(TFQuiz main, Game game) {
        this.main = main;
        this.game = game;
        this.messageManager = main.getMessageManager();
        this.commandEnd = false;
        this.canAnswer = false;

        Bukkit.getPluginManager().registerEvents(this, main);

        this.trueQuestions = new ArrayList<>();
        this.falseQuestions = new ArrayList<>();
        this.maxRounds = game.getMaxRounds();

        this.toEliminate = new ArrayList<>();
        this.answeredCorrectly = new ArrayList<>();
        this.roundCount = 0;
    }

    public void start() {
        this.game.setState(GameState.LIVE);
        this.game.sendMessage(this.messageManager.get(Message.GAMESTARTED));
        this.trueQuestions.addAll(main.getGameManager().getTrueQuestions());
        this.falseQuestions.addAll(main.getGameManager().getFalseQuestions());
        this.stageOne();
    }

    public void stageOne() {
        //Stage 1: Asks question
        this.roundCount++;
        this.toEliminate.clear();
        this.answeredCorrectly.clear();
        this.toEliminate.addAll(game.getPlayingPlayers());
        this.canAnswer = true;

        int randomNumber = new Random().nextInt(2); //random number between 0 and 1

        Question question;
        if (randomNumber == 0) {
            //Ask a true question
            question = this.trueQuestions.get(new Random().nextInt(this.trueQuestions.size()));
            this.trueQuestions.remove(question);
            this.correctAnswer = true;
        } else {
            //Ask a false question
            question = this.falseQuestions.get(new Random().nextInt(this.falseQuestions.size()));
            this.falseQuestions.remove(question);
            this.correctAnswer = false;
        }
        this.correctionMessage = question.getCorrectionMessage();

        game.sendMessage(messageManager.getWithPlaceholder(Message.GAMEPROMPT, question.getQuestion()));
        this.stageTwo();
    }

    public void stageTwo() {
        //Stage 2: count down
        this.timer = new RoundTimer(main, game, this, 10);
        this.timer.start();
    }

    public void revealOutcomeStage() {
        this.canAnswer = false;
        if (this.correctAnswer)
            this.game.sendMessage(messageManager.getWithPlaceholder(Message.GAMETRUEOUTCOME, this.correctionMessage));
        else
            this.game.sendMessage(messageManager.getWithPlaceholder(Message.GAMEFALSEOUTCOME, this.correctionMessage));
    }

    public void eliminateStage() {
        if (!this.toEliminate.isEmpty()) {
            for (Player player : this.toEliminate) {
                if (this.main.getGameManager().getGame(player) == this.game) {
                    player.sendMessage(Utils.addColour(messageManager.get(Message.GAMEELIMMESSAGE)));
                    if (this.answeredCorrectly.isEmpty()) {
                        // If all players have/are to be eliminated,
                        // theres no point running NMS. However this code needs to run in order to run a winners message to the last people in the game
                        this.game.removePlayingPlayer(player);
                        continue;
                    }
                    if (this.game.allowsSpectators()) {
                        this.game.addSpectator(player);
                        continue;
                    }
                    this.game.removePlayer(player);
                }
            }
        }
        if (!this.answeredCorrectly.isEmpty()) {
            for (Player player : this.answeredCorrectly) {
                if (this.main.getGameManager().getGame(player) == this.game) {
                    player.sendMessage(Utils.addColour(messageManager.get(Message.GAMECONTINUEDMESSAGE)));
                }
            }
        }

        //Start stage one again
        if (!this.hasEnded()) {
            this.stageOne();
        }
    }

    public boolean hasEnded() {
        //Stage 4: end runnables and do checks
        if (this.timer != null && (!this.timer.isCancelled())) {
            this.timer.cancel();
        }

        //if the game was force ended via admin command
        if (this.commandEnd) {
            this.forceEnd();
            return true;
        }

        //if the game has no players (including spectators)
        if (game.getAllPlayers().isEmpty()) {
            game.announceWinners();
            game.reset(false, false);
            return true;
        }

        //if the game has no playing players (but has spectators)
        if (game.getPlayingPlayers().isEmpty()) {
            game.announceWinners();
            game.reset(true, false);
            return true;
        }

        //if there is a single playing player remaining
        if (game.getPlayingPlayers().size() == 1) {
            if (!main.debugMode()) {
                game.announceWinners();
                Player player = game.getPlayingPlayers().iterator().next();
                game.reset(true, false);
                game.getReward().give(player);
                return true;
            }
        }

        //if there are no unique questions left to ask, or the maximum amount of rounds was reached
        if (this.falseQuestions.isEmpty() || this.trueQuestions.isEmpty() || this.roundCount >= this.maxRounds) {
            this.forceEnd();
            return true;
        }

        //Start stage one again
        return false;
    }

    public void forceEnd() {
        //The game must be ended, regardless of how many players are left
        game.announceWinners();

        int size = game.getPlayingPlayers().size();
        Reward reward = game.getReward();
        HashSet<Player> players = new HashSet<>();

        if (size >= 1) players.addAll(game.getPlayingPlayers());

        game.reset(true, false);

        for (Player player : players) {
            reward.give(player);
        }
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public void onReset() {
        if (this.timer != null) {
            try {
                this.timer.cancel();
            } catch (IllegalStateException ignored) {
            }
        }
    }

    public void setCommandEnd() {
        this.commandEnd = true;
    }

    // !*!*!*!*!*!*!*!*!*!*!                            EVENTS                            !*!*!*!*!*!*!*!*!*!*!
    //                     ----------------------------------------------------------------
    @EventHandler
    public void onPlayerAnswer(AsyncPlayerChatEvent event) {
        if (!this.canAnswer) return;
        Player player = event.getPlayer();
        if (!game.getPlayingPlayers().contains(player) && game.getState() == GameState.LIVE) return;

        String answer = event.getMessage();

        if (answer.equalsIgnoreCase("true") || answer.equalsIgnoreCase("false")) {
            if (answer.equalsIgnoreCase(String.valueOf(this.correctAnswer))) {
                if (!this.answeredCorrectly.contains(player)) {
                    this.answeredCorrectly.add(player);
                    this.toEliminate.remove(player);
                }
            } else {
                if (!this.toEliminate.contains(player)) {
                    this.toEliminate.add(player);
                    this.answeredCorrectly.remove(player);
                }
            }
        }
    }
}
