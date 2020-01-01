package net.dragonslayer.hewyratinggenerator;

public class Main {
    public static void main(String[] args) {
        int hr = 0;
        for (int i = 0; i < 10000; i++) {
            Combat currentCombat = new Combat(new Fighter("The Monster"));
            int newHr = currentCombat.startCombat();
            hr = hr + newHr;
        }

        hr = hr / 10000;

        System.out.println("FINAL HR: " + hr);
    }
}
