package Blocks;

import java.io.Serializable;

public class archer extends blocks implements Serializable {
    public archer(String tag, int power, int aroundAbility, String[] ways, Boolean rushEnemy, int mainPower) {
        super(tag, power, aroundAbility, ways, rushEnemy, mainPower);
    }

}
