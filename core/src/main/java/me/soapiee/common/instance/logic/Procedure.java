package me.soapiee.common.instance.logic;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.rewards.Reward;
import me.soapiee.common.manager.GameManager;
import me.soapiee.common.manager.MessageManager;
import me.soapiee.common.manager.QuestionManager;
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
    private final GameManager gameManager;
    private final QuestionManager questionManager;
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
        messageManager = main.getMessageManager();
        gameManager = main.getGameManager();
        questionManager = main.getQuestionManager();
        commandEnd = false;
        canAnswer = false;

        Bukkit.getPluginManager().registerEvents(this, main);

        trueQuestions = new ArrayList<>();
        falseQuestions = new ArrayList<>();
        maxRounds = game.getMaxRounds();

        toEliminate = new ArrayList<>();
        answeredCorrectly = new ArrayList<>();
        roundCount = 0;
    }

    public void start() {
        game.setState(GameState.LIVE);
        game.sendMessage(messageManager.get(Message.GAMESTARTED));
        trueQuestions.addAll(questionManager.getTrueQuestions());
        falseQuestions.addAll(questionManager.getFalseQuestions());
        stageOne();
    }

    public void stageOne() {
        //Stage 1: Asks question
        roundCount++;
        toEliminate.clear();
        answeredCorrectly.clear();
        toEliminate.addAll(game.getPlayingPlayers());
        canAnswer = true;

        int randomNumber = new Random().nextInt(2); //random number between 0 and 1

        Question question;
        if (randomNumber == 0) {
            //Ask a true question
            question = trueQuestions.get(new Random().nextInt(trueQuestions.size()));
            trueQuestions.remove(question);
            correctAnswer = true;
        } else {
            //Ask a false question
            question = falseQuestions.get(new Random().nextInt(falseQuestions.size()));
            falseQuestions.remove(question);
            correctAnswer = false;
        }
        correctionMessage = question.getCorrectionMessage();

        game.sendMessage(messageManager.getWithPlaceholder(Message.GAMEPROMPT, question.getQuestion()));
        stageTwo();
    }

    public void stageTwo() {
        //Stage 2: count down
        timer = new RoundTimer(main, game, this, 10);
        timer.start();
    }

    public void revealOutcomeStage() {
        canAnswer = false;
        if (correctAnswer)
            game.sendMessage(messageManager.getWithPlaceholder(Message.GAMETRUEOUTCOME, correctionMessage));
        else
            game.sendMessage(messageManager.getWithPlaceholder(Message.GAMEFALSEOUTCOME, correctionMessage));
    }

    public void eliminateStage() {
        if (!toEliminate.isEmpty()) {
            for (Player player : toEliminate) {
                if (gameManager.getGame(player) == game) {
                    player.sendMessage(Utils.addColour(messageManager.get(Message.GAMEELIMMESSAGE)));
                    if (answeredCorrectly.isEmpty()) {
                        // If all players have/are to be eliminated,
                        // theres no point running NMS. However this code needs to run in order to run a winners message to the last people in the game
                        game.removePlayingPlayer(player);
                        continue;
                    }
                    if (game.isAllowSpectators()) {
                        game.addSpectator(player);
                        continue;
                    }
                    game.removePlayer(player);
                }
            }
        }
        if (!answeredCorrectly.isEmpty()) {
            for (Player player : answeredCorrectly) {
                if (gameManager.getGame(player) == game) {
                    player.sendMessage(Utils.addColour(messageManager.get(Message.GAMECONTINUEDMESSAGE)));
                }
            }
        }

        //Start stage one again
        if (!hasEnded()) {
            stageOne();
        }
    }

    public boolean hasEnded() {
        //Stage 4: end runnables and do checks
        if (timer != null && (!timer.isCancelled())) {
            timer.cancel();
        }

        //if the game was force ended via admin command
        if (commandEnd) {
            forceEnd();
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
            if (!main.getSettingsManager().isDebugMode()) {
                game.announceWinners();
                Player player = game.getPlayingPlayers().iterator().next();
                game.reset(true, false);
                game.getReward().give(player);
                return true;
            }
        }

        //if there are no unique questions left to ask, or the maximum amount of rounds was reached
        if (falseQuestions.isEmpty() || trueQuestions.isEmpty() || roundCount >= maxRounds) {
            forceEnd();
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
        if (timer != null) {
            try {
                timer.cancel();
            } catch (IllegalStateException ignored) {
            }
        }
    }

    public void setCommandEnd() {
        commandEnd = true;
    }

    // !*!*!*!*!*!*!*!*!*!*!                            EVENTS                            !*!*!*!*!*!*!*!*!*!*!
    //                     ----------------------------------------------------------------
    @EventHandler
    public void onPlayerAnswer(AsyncPlayerChatEvent event) {
        if (!canAnswer) return;
        Player player = event.getPlayer();
        if (!game.getPlayingPlayers().contains(player) && game.getState() == GameState.LIVE) return;

        String answer = event.getMessage();

        if (answer.equalsIgnoreCase("true") || answer.equalsIgnoreCase("false")) {
            if (answer.equalsIgnoreCase(String.valueOf(correctAnswer))) {
                if (!answeredCorrectly.contains(player)) {
                    answeredCorrectly.add(player);
                    toEliminate.remove(player);
                }
            } else {
                if (!toEliminate.contains(player)) {
                    toEliminate.add(player);
                    answeredCorrectly.remove(player);
                }
            }
        }
    }
}
