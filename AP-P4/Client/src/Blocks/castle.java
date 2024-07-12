package Blocks;

import java.io.Serializable;

public class castle extends blocks implements Serializable {
    public castle(String tag, int power, int aroundAbility, String[] ways, Boolean rushEnemy, int mainPower) {
        super(tag, power, aroundAbility, ways, rushEnemy, mainPower);
    }
}
