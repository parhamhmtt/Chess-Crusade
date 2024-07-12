package Blocks;

import java.io.Serializable;

public class blocks implements Serializable {
    public String tag;
    public int power;
    public int aroundAbility;
    String[] ways;
    public int MainPower;
    Boolean rushEnemy;
    public blocks(String tag, int power, int aroundAbility, String[] ways, Boolean rushEnemy,int mainPower) {
        this.tag = tag;
        this.power = power;
        this.aroundAbility = aroundAbility;
        this.ways = ways;
        this.rushEnemy = rushEnemy;
        this.MainPower=mainPower;
    }
}
