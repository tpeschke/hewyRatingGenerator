package net.dragonslayer.hewyratinggenerator;

public class Combat {
    private int count;
    private int hr;
    private boolean hewyDead = false;
    private boolean monsterDead = false;
    private Fighter hewy;
    private Fighter monster;
    private int distance;
    private char[] engagementZones = {'n', 'e', 'i'};

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        count = count++;
    }

    public int getHr() {
        return hr;
    }

    public Combat(Fighter monster) {
        this.hewy = new Fighter("Hewy");
        this.monster = monster;

        hewy.setEquipment(monster.getDr());
        monster.setEquipment(hewy.getDr());
    }

    public int startCombat() {
        do {
            runCombat();
        } while (!this.monsterDead && !this.hewyDead);

        return this.hr;
    }

    private void runCombat() {
        this.count = ++this.count;
        if (hewy.getNextAction() <= this.count) {
            int monsterDefense = this.monster.rollDefense();
            int hewyAttack = this.hewy.rollAttack();

            if (hewyAttack > monsterDefense & hewyAttack > monsterDefense + monster.getParry()) {
                int damage = hewy.dealDamage();
                this.monsterDead = monster.takeDamage(damage, hewy.getDiceCount());
            } else if (hewyAttack > monsterDefense & hewyAttack <= monsterDefense + monster.getParry())
                System.out.print("");

            hewy.increaseAction();
        }

        if (monster.getNextAction() <= this.count) {
            int hewyDefense = this.hewy.rollDefense();
            int monsterAttack = this.monster.rollAttack();

            if (monsterAttack > hewyDefense & monsterAttack > hewyDefense + hewy.getParry()) {
                int damage = monster.dealDamage();
                this.hewyDead = hewy.takeDamage(damage, monster.getDiceCount());
            } else if (monsterAttack > hewyDefense & monsterAttack <= hewyDefense + hewy.getParry())
                System.out.print("");

            monster.increaseAction();
        }

        if (this.hewyDead && !this.monsterDead) {
            this.hr = ++this.hr;
            runCombat();
        } else if (this.monsterDead && !this.hewyDead) {
            this.hr = --this.hr;
            runCombat();
        }
    }
}
