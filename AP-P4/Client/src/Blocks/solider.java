package Blocks;

import java.io.Serializable;

public class solider extends blocks implements Serializable {
    public solider(String tag, int power, int aroundAbility, String[] ways, Boolean rushEnemy, int mainPower) {
        super(tag, power, aroundAbility, ways, rushEnemy, mainPower);
    }
}
