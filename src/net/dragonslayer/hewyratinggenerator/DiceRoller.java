package net.dragonslayer.hewyratinggenerator;

public class DiceRoller {
    public int rollDice (String dice) {
        return rollDice(dice, true);
    }

    public int rollDice (String dice, boolean explosions) {
        int result = 0;
        String rollPieces[] = dice.split("\\+");

        for (String rollPiece: rollPieces) {
            if (rollPiece.contains("d")) {
                String dicePieces[] = rollPiece.split("d");
                int numberOfRolls;
                if (dicePieces[0] == "")
                    numberOfRolls = 1;
                else
                    numberOfRolls = Integer.parseInt(dicePieces[0]);
                int valueOfDice = Integer.parseInt(dicePieces[1]);

                result = result + getDiceResult(numberOfRolls, valueOfDice, explosions);
            } else {
                result = result + Integer.parseInt(rollPiece);
            }
        }

        return result;
    }

    private int getDiceResult (int numberOfRolls, int valueOfDice, boolean explosions) {
        int result = 0;

        for (int i = 0; i < numberOfRolls; i++) {
            int diceResult = (int)(valueOfDice * Math.random()) + 1;
            if (diceResult == valueOfDice & explosions) {
                result = result + diceResult;
                int explosionResult;

                if (valueOfDice == 20)
                    explosionResult = getDiceResult(1, 6, true) -1;
                else if (valueOfDice == 100)
                    explosionResult = getDiceResult(1, 20,true) -1;
                else
                    explosionResult = getDiceResult(1, valueOfDice, true) -1;

                result = result + explosionResult;
            } else {
                result = result + diceResult;
            }
        }

        return result;
    }

}
