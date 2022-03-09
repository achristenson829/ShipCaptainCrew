package edu.wctc;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.IntStream.*;

public class DiceGame {
    private final List<Player> players = new ArrayList<>();
    private final List<Die> dice = new ArrayList<>();
    private final int maxRolls;
    private Player currentPlayer;

    public DiceGame(int countPlayers, int countDice, int maxRolls) throws Exception {

        Exception IllegalArgumentException = null;
        if (countPlayers < 2) {
            assert false;
            throw IllegalArgumentException;
        }
        else {
            range(0, countPlayers).mapToObj(i -> new Player(2)).forEach(players::add);
            range(0, countDice).mapToObj(sides -> new Die(6)).forEach(dice::add);
            this.maxRolls = maxRolls;
        }
    }
    private boolean allDiceHeld(){

        return dice.stream().allMatch(Die::isBeingHeld);

    }
    public boolean autoHold(int faceValue){
        boolean hold = false;

        var result = dice.stream()
                .filter(die -> die.getFaceValue() == faceValue).findFirst();
        if (result.isPresent()) {
            var result2 = result.stream().filter(Die::isBeingHeld).findFirst();
                    if (result2.isPresent()) hold = true;
                    else {var result3 = result.stream().filter(die -> !die.isBeingHeld()).findFirst();
                        if (result3.isPresent()) hold = true;
                        result3.ifPresent(Die::holdDie);}
        }

        return hold;
    }
    public boolean currentPlayerCanRoll(){

        if (maxRolls == currentPlayer.getRollsUsed() || allDiceHeld()) {
            return false;
        } else {
            return true;
        }
    }

    public int getCurrentPlayerNumber(){

        return currentPlayer.getPlayerNumber();

    }
    public int getCurrentPlayerScore (){

       return currentPlayer.getScore();
    }
    public String getDiceResults(){

        return dice.stream()
                .map(Die::toString)
                .collect(Collectors.joining());

    }
    public String getFinalWinner(){

         var winner = players.stream()
                .max(Comparator.comparingInt(Player::getWins)).orElse(null);
        assert winner != null;
        return winner.toString();

    }
    public String getGameResults(){
       Stream<Player> sortedScore = players.stream()
               .sorted(Comparator.comparingInt(Player::getScore)
                       .reversed());
        Optional<Player> first = sortedScore.findFirst();
        first.ifPresent(Player::addWin);
        Stream<Player> sortedScore2 = players.stream()
                .sorted(Comparator.comparingInt(Player::getScore)
                        .reversed());
        sortedScore2.skip(1).forEach(Player::addLoss);

        return players.stream()
                .map(Player::toString)
                .collect(Collectors.joining());

    }
    private boolean isHoldingDie(int faceValue){
        boolean isHolding = false;
        var heldDie = dice.stream()
                .filter(Die::isBeingHeld).filter(die -> die.getFaceValue() == faceValue).findFirst();
        if (heldDie.isPresent()){
            isHolding = true;
        }
        return isHolding;
    }
    public boolean nextPlayer(){
        boolean isNextPlayer = false;
        for (int i = 1; i < players.size(); i++){
            if (currentPlayer.getPlayerNumber()+1 <= players.size()) {
                currentPlayer=players.get(i);
                isNextPlayer = true;
            }
        }
        return isNextPlayer;
    }
    public void playerHold(char dieNum){

        var answer = dice.stream().filter(die -> die.getDieNum()==dieNum).findFirst();
            answer.ifPresent(Die::holdDie);
    }

    public void resetDice(){

        dice.forEach(Die::resetDie);

    }
    public void resetPlayers(){

        players.forEach(Player::resetPlayer);

    }
    public void rollDice(){

        currentPlayer.roll();
        dice.forEach(Die::rollDie);

    }
    public void scoreCurrentPlayer(){
        int score = 0;
        if (isHoldingDie(6) && isHoldingDie(5) && isHoldingDie(4)) {
            List<Die> toSort = new ArrayList<>(dice);
            toSort.sort(Comparator.comparingInt(Die::getFaceValue).reversed());

            long toSkip = 3;
            for (Die die : toSort) {
                int faceValue = die.getFaceValue();
                if (toSkip > 0) {
                    toSkip--;
                    continue;
                }
                score += faceValue;
            }
        }
        currentPlayer.setScore(score);

    }
    public void startNewGame(){
        currentPlayer = players.get(0);
        List<Player> toSort = new ArrayList<>(players);
        toSort.sort(Comparator.comparingInt(Player::getScore).reversed());
        resetPlayers();
    }

}
