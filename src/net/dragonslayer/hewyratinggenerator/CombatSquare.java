package net.dragonslayer.hewyratinggenerator;

public class CombatSquare {
    private int speed, attack, init, defence, encumbrance, measure, parry, damageDiceCount;
    private String dr, damage, shieldDr;
    private char type;

    public int getAttack() { return attack; }
    public int getDefence() { return defence; }
    public int getEncumbrance() { return encumbrance; }
    public int getInit() { return init; }
    public int getMeasure() { return measure; }
    public int getParry() { return parry; }
    public int getSpeed() { return speed; }
    public char getType() { return type; }
    public String getDamage() { return damage; }
    public String getDr() { return dr; }
    public String getShieldDr() { return shieldDr; }
    public int getDamageDiceCount() { return damageDiceCount; }

    CombatSquare(int speed, int attack, int init, int defence, int encumbrance, int measure, int parry, String dr, String damage, String shieldDr, char type) {
        this.speed = speed;
        this.attack = attack;
        this.init = init;
        this.defence = defence;
        this.encumbrance = encumbrance;
        this.measure = measure;
        this.parry = parry;
        this.dr = dr;
        this.damage = damage;
        this.shieldDr = shieldDr;
        this.type = type;

        int diceCount = 0;
        String rollPieces[] = this.damage.split("\\+");
        for (String rollPiece: rollPieces) {
            if (rollPiece.contains("d"))
                diceCount = ++diceCount;
        }
        this.damageDiceCount = diceCount;
    }
}
