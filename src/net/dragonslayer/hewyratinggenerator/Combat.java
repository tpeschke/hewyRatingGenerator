package net.dragonslayer.hewyratinggenerator;

public class Combat {
    private int count;
    private int hr;
    private Fighter hewy;
    private Fighter monster;
    private int distance;

    public Combat(Fighter monster) {
        this.hewy = new Fighter();
        this.monster = monster;

        hewy.grabWeapon(monster.getDr(), 'r');
        monster.grabWeapon(hewy.getDr(), 'r');
    }

    public int startCombat(DiceRoller diceRollerHelper) {
        this.distance = 50;
        do {
            runCombat(diceRollerHelper);
        } while (!monster.checkIfDead() && !hewy.checkIfDead());

        return this.hr;
    }

    private void runCombat(DiceRoller diceRollerHelper) {
        this.count = ++this.count;
        if (hewy.getNextAction() <= this.count) {
            distance = hewy.decideAction(this.monster, this.distance, diceRollerHelper, this.count);
        }

        if (monster.getNextAction() <= this.count) {
            distance = monster.decideAction(this.hewy, this.distance, diceRollerHelper, this.count);
        }

        if (hewy.checkIfDead() && !monster.checkIfDead()) {
            this.hr = ++this.hr;
            runCombat(diceRollerHelper);
        } else if (monster.checkIfDead() && !hewy.checkIfDead()) {
            this.hr = --this.hr;
            runCombat(diceRollerHelper);
        }
    }
}
