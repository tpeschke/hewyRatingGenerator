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

    public int[] getVitality() { return vitality; }
    public int getCurrentVitality() {return currentVitality;}
    public void setCurrentVitality(int currentVitality) {this.currentVitality = currentVitality; }
    public double getStress() {return stress; }
    public void setStress(double stress) {this.stress = stress;}
    public void setCurrentVitalityCategory(int currentVitalityCategory) { this.currentVitalityCategory = currentVitalityCategory; }
    public int getNextAction() { return nextAction; }
    public int getKnockBack() { return knockBack;}

    public Fighter() {
        // the base Fighter is Hewy so you only have instantiate it to get him
        CombatSquare[] equipment = {new CombatSquare(8, 2, 1, 0,8, 0, 0, "4", "2d8", "0", 'r'),
                                    new CombatSquare(7, 2, 1, 1,8, 6, 0, "4", "1d8+2d6+2", "0", 'm'),
                                    new CombatSquare(8, 2, 1, 1,8, 2, 0, "4", "1d12+4", "0", 'm'),
                                    new CombatSquare(10, 2, 1, 0,14, 14, 9, "4", "1d8+1d6+1d4+2", "2/d+4", 'm')};
        this.equipped = equipment[0];
        double[] movement = new double[5];
        movement[0] = 0;
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
        this.equipped = equipment[0];
    }
    public Fighter(int vitality, int knockBack, CombatSquare[] equipment, double[] movement) {
        this(knockBack, equipment, movement);
        int categorySize = vitality / 4;
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

    public void grabWeapon (String enemyDR, char type) {
        CombatSquare currentSelected = this.equipped;
        int currentDice = this.equipped.getDamageDiceCount();

        for (CombatSquare square: this.equipment) {
            int newDice = square.getDamageDiceCount();

            if (enemyDR.contains("/") & newDice < currentDice & square.getType() == type) {
                currentSelected = square;
                currentDice = newDice;
            } else if (newDice > currentDice & square.getType() == type) {
                currentSelected = square;
                currentDice = newDice;
            }
        }

        this.equipped = currentSelected;
    }

    public void inflictStress (double inflictedStress) { setStress(getStress() + inflictedStress); }

    public void takeDamage (int damage, int diceCount) {
        takeDamage(damage, diceCount, false);
    }
    public void takeDamage (int damage, int diceCount, boolean parried) {
        if (this.getDr().contains("/"))
            damage = damage - calculateSlashDr(diceCount, this.equipped.getDr());
        else
            damage = damage - Integer.parseInt(this.equipped.getDr());

        if (parried)
            damage = damage - calculateSlashDr(diceCount, this.equipped.getShieldDr());

        if (damage < 0)
            damage = 0;

        setCurrentVitality(getCurrentVitality() + damage);

        int newCurrent = getCurrentVitality();
        int[] vitality = getVitality();

        for (int i = 0; i < 5; i++) {
            if (newCurrent >= vitality[i]) {
                setCurrentVitalityCategory(i);
            }
        }
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

    public int decideAction(Fighter enemy, int distance, DiceRoller diceRollerHelper, int count) {
        // resolve movement
        double movementSpeedMargin = currentMovement - 2 >= 0 ? movement[currentMovement] + movement[currentMovement - 2] : movement[currentMovement] ;
        if (distance > movementSpeedMargin && currentMovement < 4)
            currentMovement += 1;
        else if (distance == movementSpeedMargin) { }
        else
            currentMovement = currentMovement - 2 >= 0 ? currentMovement - 2 : currentMovement - 1 >= 0 ? currentMovement - 1 : currentMovement;

        distance -= movement[currentMovement];

        // resolve attack
        if (distance <= this.equipped.getMeasure() & this.nextAction == count) {
            this.grabWeapon(enemy.getDr(), 'm');
            int enemyDefense = diceRollerHelper.rollDice(enemy.getDefenseDice());
            int actorAttack = diceRollerHelper.rollDice(this.getAttackDice());

            if (actorAttack > enemyDefense & actorAttack > enemyDefense + enemy.getParry()) {
                int damage = diceRollerHelper.rollDice(this.getWeaponDamage());
                // resolve knock back
                if (damage > enemy.getKnockBack()) {
                    distance = distance + (int)(damage / enemy.getKnockBack());
                }
                enemy.takeDamage(damage, this.getDiceCount());
            } else if (actorAttack > enemyDefense & actorAttack <= enemyDefense + enemy.getParry())
                System.out.print("");
        } else if (this.nextAction == count && this.equipped.getType() == 'r') {
            int enemyDefense = diceRollerHelper.rollDice("1d8");
            int actorAttack = diceRollerHelper.rollDice(this.getAttackDice());

            if (actorAttack > enemyDefense & actorAttack > enemyDefense) {
                int damage = diceRollerHelper.rollDice(this.getWeaponDamage());
                enemy.takeDamage(damage, this.getDiceCount());
            }
        }

      nextAction = nextAction + this.equipped.getSpeed();
      return distance;
    }

    public boolean checkIfDead () {
        if ((vitality[4] - currentVitality) >= 0)
            return false;
        return true;
    }

    private void specialAbility(String resolution) { }

    public String getWeaponDamage() { return this.equipped.getDamage(); }
    public String getAttackDice() { return "1d20+" + this.equipped.getAttack(); }
    public String getDefenseDice() { return "1d20+" + this.equipped.getDefence(); }
    public int getParry() { return this.equipped.getParry(); }
    public String getDr() {return this.equipped.getDr(); }
    public int getDiceCount() { return this.equipped.getDamageDiceCount(); }
}
