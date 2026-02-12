package me.soapiee.common.handlers;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.EndGameResult;
import me.soapiee.common.enums.GameState;
import me.soapiee.common.enums.Message;
import me.soapiee.common.instance.Game;
import me.soapiee.common.instance.Question;
import me.soapiee.common.managers.GameManager;
import me.soapiee.common.managers.GamePlayerManager;
import me.soapiee.common.managers.MessageManager;
import me.soapiee.common.managers.QuestionManager;
import me.soapiee.common.tasks.RoundTimer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

public class LiveGameHandler implements Listener {

    private final Game game;
    private final int gameID;
    private final TFQuiz main;
    private final MessageManager messageManager;
    private final GameManager gameManager;
    private final GamePlayerManager playerManager;
    private final GamePlayerHandler playerHandler;
    private final GameMessageHandler messageHandler;
    private final QuestionManager questionManager;
    private boolean commandEnd;
    private boolean canAnswer;

    private final List<Question> trueQuestions = new ArrayList<>();
    private final List<Question> falseQuestions = new ArrayList<>();
    private String correctionMessage;
    private final int maxRounds;

    private final List<UUID> toEliminate = new ArrayList<>();
    private boolean correctAnswer;
    private final List<UUID> answeredCorrectly = new ArrayList<>();
    private RoundTimer timer;
    private int roundCount;

    public LiveGameHandler(TFQuiz main, Game game) {
        this.main = main;
        this.game = game;
        gameID = game.getIdentifier();
        messageManager = main.getMessageManager();
        gameManager = main.getGameManager();
        playerManager = main.getGamePlayerManager();
        playerHandler = game.getPlayerHandler();
        messageHandler = game.getMessageHandler();
        questionManager = main.getQuestionManager();
        commandEnd = false;
        canAnswer = false;

        Bukkit.getPluginManager().registerEvents(this, main);

        maxRounds = game.getMaxRounds();
        roundCount = 0;
    }

    public void generateQuestions() {
        trueQuestions.addAll(questionManager.getTrueQuestions());
        falseQuestions.addAll(questionManager.getFalseQuestions());
    }

    public void startNewRound() {
        roundCount++;
        toEliminate.clear();
        answeredCorrectly.clear();
        toEliminate.addAll(playerManager.getPlayingPlayers(gameID));
        canAnswer = true;

        askQuestion();
        startRoundTimer();
    }

    private Question getQuestion() {
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

        return question;
    }

    private void askQuestion() {
        messageHandler.sendMessageToAll(messageManager.getWithPlaceholder(Message.GAMEPROMPT, getQuestion().toString()));
    }

    private void startRoundTimer() {
        timer = new RoundTimer(main, game, this, 10);
        timer.start();
    }

    public void revealOutcome() {
        canAnswer = false;

        if (correctAnswer)
            messageHandler.sendMessageToAll(messageManager.getWithPlaceholder(Message.GAMETRUEOUTCOME, correctionMessage));
        else
            messageHandler.sendMessageToAll(messageManager.getWithPlaceholder(Message.GAMEFALSEOUTCOME, correctionMessage));
    }

    public void eliminatePlayers() {
        if (!toEliminate.isEmpty()) {
            for (UUID uuid : toEliminate) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;

                if (gameManager.getGame(uuid) == game) {
                    messageHandler.eliminated(player);

                    if (answeredCorrectly.isEmpty()) {
                        // If all players have/are to be eliminated,
                        // theres no point running NMS. However this code needs to run in order to run a winners message to the last people in the game
                        playerHandler.eliminate(uuid);
                        continue;
                    }

                    if (game.getArenaHandler().isAllowSpectators())
                        playerHandler.setSpectator(player);
                    else
                        playerHandler.removePlayer(uuid);
                }
            }
        }

        if (!answeredCorrectly.isEmpty()) {
            for (UUID uuid : answeredCorrectly) {
                Player player = Bukkit.getPlayer(uuid);
                if (gameManager.getGame(uuid) == game) {
                    messageHandler.survived(player);
                }
            }
        }
    }

    public EndGameResult shouldEnd() {
        Set<UUID> playingPlayer = playerManager.getPlayingPlayers(gameID);
        if (playingPlayer.isEmpty()) return EndGameResult.NO_WINNERS_END;
        if (falseQuestions.isEmpty() || trueQuestions.isEmpty() || roundCount >= maxRounds)
            return EndGameResult.WINNERS_END;
        if (playingPlayer.size() == 1 && (!main.getSettingsManager().isDebugMode())) return EndGameResult.WINNERS_END;
        if (commandEnd) return EndGameResult.WINNERS_END;

        return EndGameResult.NEW_ROUND;
    }

    public void cancelTimer() {
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

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    // !*!*!*!*!*!*!*!*!*!*!                            EVENTS                            !*!*!*!*!*!*!*!*!*!*!
    //                     ----------------------------------------------------------------

    @EventHandler
    public void onPlayerAnswer(AsyncPlayerChatEvent event) {
        if (!canAnswer) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!playerManager.getPlayingPlayers(gameID).contains(uuid) && game.getState() == GameState.LIVE) return;

        String answer = event.getMessage();

        if (answer.equalsIgnoreCase("true") || answer.equalsIgnoreCase("false")) {
            if (answer.equalsIgnoreCase(String.valueOf(correctAnswer))) {
                if (!answeredCorrectly.contains(uuid)) {
                    answeredCorrectly.add(uuid);
                    toEliminate.remove(uuid);
                }
            } else {
                if (!toEliminate.contains(uuid)) {
                    toEliminate.add(uuid);
                    answeredCorrectly.remove(uuid);
                }
            }
        }
    }
}
