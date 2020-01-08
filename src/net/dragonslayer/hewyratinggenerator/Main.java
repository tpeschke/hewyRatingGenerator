package net.dragonslayer.hewyratinggenerator;

public class Main {
    public static void main(String[] args) {
        int hr = 0;
        int timesToRunCombat = 10000;
        DiceRoller diceRollerHelper = new DiceRoller();

        double[] movement = {0, 5, 10, 15, 20};
        CombatSquare[] equipment = {new CombatSquare(12, 0, 0, 5,20,3,0,"3/d", "1d12+1d4+4", "0", 'm')};


        for (int i = 0; i < timesToRunCombat; i++) {
            Combat currentCombat = new Combat(new Fighter(diceRollerHelper.rollDice("1d8+30"), 15, equipment, movement ));
            int newHr = currentCombat.startCombat(diceRollerHelper);
            hr = hr + newHr;
        }

        hr = hr / timesToRunCombat;

        System.out.println("FINAL HR: " + hr);
    }
}
