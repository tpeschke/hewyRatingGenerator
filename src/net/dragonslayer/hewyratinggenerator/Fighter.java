package net.dragonslayer.hewyratinggenerator;

import java.awt.desktop.SystemEventListener;

public class Fighter {
    private int[] vitality = new int[5];
    private int currentVitality, currentVitalityCategory;
    private double stress;
    private int knockBack;
    private CombatSquare[] equipment;
    private CombatSquare equipped;
    private double[] movement;
    private int currentMovement = 0;
    private int nextAction;

    private String name;
    public String getName() {
        return name;
    }

    public int[] getVitality() { return vitality; }
    public int getCurrentVitality() {return currentVitality;}
    public void setCurrentVitality(int currentVitality) {this.currentVitality = currentVitality; }
    public double getStress() {return stress; }
    public void setStress(double stress) {this.stress = stress;}
    public int getCurrentVitalityCategory() { return currentVitalityCategory; }
    public void setCurrentVitalityCategory(int currentVitalityCategory) { this.currentVitalityCategory = currentVitalityCategory; }
    public int getNextAction() { return nextAction; }

    public Fighter(String name) {
        this.name = name;
        // the base Fighter is Hewy so you only have instantiate it to get him
        CombatSquare[] equipment = {new CombatSquare(7, 2, 1, 1,8, 6, 5, "1/D", "1d8+2d6+2", "2/d+3")};
        this.equipped = equipment[0];
        double[] movement = new double[5];
        movement[0] = 2.5;
        movement[1] = 5;
        movement[2] = 10;
        movement[3] = 15;
        movement[4] = 20;

        this.knockBack = 15;
        this.equipment = equipment;
        this.movement = movement;
        calculateVitalityCategories(7);
    }
    public Fighter (int knockBack, CombatSquare[] equipment, double[] movement) {
        this.knockBack = knockBack;
        this.equipment = equipment;
        this.movement = movement;
    }
    public Fighter(int vitality, int knockBack, CombatSquare[] equipment, double[] movement) {
        this(knockBack, equipment, movement);
        int categorySize = vitality / 4;
        calculateVitalityCategories(categorySize);
    }

    public Fighter(String vitality, int knockBack, CombatSquare[] equipment, double[] movement) {
        this(knockBack, equipment, movement);
        int categorySize = rollDice(vitality, false) / 4;
        calculateVitalityCategories(categorySize);
    }

    private void calculateVitalityCategories(double categorySize) {
        this.vitality[0] = 1;

        for (int i = 1; i < 5; i++) {
            if (i == 2 | i == 4)
                this.vitality[i] = (int) (this.vitality[i - 1] +  + Math.round(categorySize)) + 1;
            else
                this.vitality[i] = (int) (this.vitality[i - 1] +  + Math.round(categorySize));
        }
    }

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

    public void setEquipment (String enemyDR) {
        CombatSquare currentSelected = this.equipped;
        int currentDice = this.equipped.getDamageDiceCount();

        for (CombatSquare square: this.equipment) {
            int newDice = square.getDamageDiceCount();

            if (enemyDR.contains("/") & newDice < currentDice) {
                currentSelected = square;
                currentDice = newDice;
            } else if (newDice > currentDice) {
                currentSelected = square;
                currentDice = newDice;
            }
        }

        this.equipped = currentSelected;
    }

    public void inflictStress (double inflictedStress) { setStress(getStress() + inflictedStress); }

    public boolean takeDamage (int damage, int diceCount) {
        return takeDamage(damage, diceCount, false);
    }
    public boolean takeDamage (int damage, int diceCount, boolean parried) {
        if (this.getDr().contains("/"))
            damage = damage - calculateSlashDr(diceCount, this.equipped.getDr());
        else
            damage = damage - Integer.parseInt(this.equipped.getDr());

        if (parried)
            damage = damage - calculateSlashDr(diceCount, this.equipped.getShieldDr());

        if (damage < 0)
            damage = 0;

        setCurrentVitality(getCurrentVitality() + damage);

        // Need to add in knock backs as well

        int newCurrent = getCurrentVitality();
        int[] vitality = getVitality();

        for (int i = 0; i < 5; i++) {
            if (newCurrent >= vitality[i]) {
                setCurrentVitalityCategory(i);
            }
        }

        if (getCurrentVitalityCategory() >= 4)
            return true;

        return false;
    }

    private int calculateSlashDr (int diceCount, String dr) {
        String[] drPieces = dr.split("/");
        int base = Integer.parseInt(drPieces[0]);
        int bonus = 0;
        if (drPieces.length > 1 & drPieces[1].contains("\\+"))
            bonus = Integer.parseInt(drPieces[1].split("\\+")[1]);

        base = base * diceCount;

        return base + bonus;
    }

    public int dealDamage() { return rollDice(this.equipped.getDamage()); }
    public int rollAttack() { return rollDice("1d20+" + this.equipped.getAttack()); }
    public int rollDefense() { return rollDice("1d20+" + this.equipped.getDefence()); }
    public int getParry() { return this.equipped.getParry(); }
    public int getMeasure() { return this.equipped.getMeasure(); }
    public String getDr() {return this.equipped.getDr(); }
    public void setInitialAction() { nextAction = this.equipped.getInit(); }
    public void increaseAction() { nextAction = nextAction + this.equipped.getSpeed(); }
    public int getDiceCount() { return this.equipped.getDamageDiceCount(); }
}
