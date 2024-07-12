package Game;

import Blocks.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.io.*;

public  interface Board  {
     static blocks map[][] = new blocks[8][8];
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    static String[] soldierWay = {"q", "w", "e"};
    static String[] nullWay = {};
    static String[] castleWay = {"q", "w", "e", "d", "c", "x", "z", "a"};
    static String[] knightWay = {"qq", "ww", "ee", "dd", "cc", "xx", "zz", "aa"};
    static String[] archerWay = {"q", "w", "e", "d", "c", "x", "z", "a"};
    static String[] hashashinWay = {"q", "w", "e", "d", "c", "x", "z", "a"};

    static int aroundPowerSoldier = 1;
    static int aroundPowerKnight = 0;
    static int aroundPowerCastle = 1;
    static int aroundPowerArcher = 0;
    static int aroundPowerHashashin = -2;
    static int mainPowerSolider = 1;
    static int mainPowerKnight = 1;
    static int mainPowerCastle = 1;
    static int mainPowerArcher = 2;
    static int mainPowerHashashin = 0;


    public static void firstMapFixer() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                map[i][j] = new emp("e", 0, 0, nullWay, false,0);
            }

        }

        map[0][0] = new archer("c", 0, aroundPowerArcher, archerWay, true,mainPowerArcher);
        map[0][1] = new archer("c", 0, aroundPowerArcher, archerWay, true,mainPowerArcher);
        map[0][6] = new archer("c", 0, aroundPowerArcher, archerWay, true,mainPowerArcher);
        map[0][7] = new archer("c", 0, aroundPowerArcher, archerWay, true,mainPowerArcher);
        for (int i = 0; i < 8; i++) {
            map[1][i] = new solider("c", 0, aroundPowerSoldier, soldierWay, true,mainPowerSolider);
        }
        map[0][2] =new knight("c", 0, aroundPowerKnight, knightWay, true,mainPowerKnight);
        map[0][3] = new castle("c", 0, aroundPowerCastle, castleWay, false,mainPowerCastle);
        map[0][4] = new hashashin("c", 0, aroundPowerHashashin, hashashinWay, true,mainPowerHashashin);
        map[0][5] = new knight("c", 0, aroundPowerKnight, knightWay, true,mainPowerKnight);


        map[7][0] = new archer("m", 0, aroundPowerArcher, archerWay, true,mainPowerArcher);
        map[7][1] = new archer("m", 0, aroundPowerArcher, archerWay, true,mainPowerArcher);
        map[7][6] = new archer("m", 0, aroundPowerArcher, archerWay, true,mainPowerArcher);
        map[7][7] = new archer("m", 0, aroundPowerArcher, archerWay, true,mainPowerArcher);

        for (int i = 0; i < 8; i++) {
            map[6][i] = new solider("m", 0, aroundPowerSoldier, soldierWay, true,mainPowerSolider);
        }
        map[7][2] = new knight("m", 0, aroundPowerKnight, knightWay, true,mainPowerKnight);
        map[7][4] = new castle("m", 0, aroundPowerCastle, castleWay, false,mainPowerCastle);
        map[7][3] = new hashashin("m", 0, aroundPowerHashashin, hashashinWay, true,mainPowerHashashin);
        map[7][5] = new knight("m", 0, aroundPowerKnight, knightWay, true,mainPowerKnight);
    }

    public static void arrangePower() {
        for (int i = 0; i <8; i++) {
            for (int j = 0; j < 8; j++) {
                map[i][j].power=0;
            }
        }

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (map[i][j] instanceof emp)
                        continue;
                    for (int a = i - 1; a <= i + 1; a++) {
                        for (int b = j - 1; b <= j + 1; b++) {
                            if (a == i && b == j)
                                continue;
                            if (a < 0 || a >7 || b < 0 || b >7)
                                continue;
                            if (map[a][b] instanceof emp)
                                continue;
                            if (map[a][b] instanceof solider && map[i][j].tag.equals(map[a][b].tag)) {
                                if (map[i][j] instanceof solider) {
                                    map[i][j].power=(map[i][j].power + map[a][b].aroundAbility);
                                }
                            } else if (map[a][b] instanceof hashashin) {
                                if (!map[i][j].tag.equals(map[a][b].tag)) {
                                    map[i][j].power=(map[i][j].power - 2);
                                }
                            } else if (map[i][j].tag.equals(map[a][b].tag))
                                map[i][j].power=(map[i][j].power + map[a][b].aroundAbility);
                        }
                    }
                    if (map[i][j].MainPower + map[i][j].power <= 0)
                        map[i][j].power=(0);
                    else
                        map[i][j].power=(map[i][j].MainPower + map[i][j].power);

                }
            }



    }

    static Boolean[][] availableStep(int i, int j) {
        int[] turnXK = {-2, -2, -2, 0, 0, 2, 2, 2,-1, -1, -1, 0, 0, 1, 1, 1};
        int[] turnYK = {-2, 0, 2, -2, 2, -2, 0, 2,-1, 0, 1, -1, 1, -1, 0, 1};


        int[] turnX = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] turnY = {-1, 0, 1, -1, 1, -1, 0, 1};
        Boolean[][] circlePut=new Boolean[8][8];
        for (int k = 0; k < 8; k++) {
            for (int l = 0; l < 8; l++) {
                circlePut[k][l]=false;
            }
        }
            switch (map[i][j].tag) {
                case "c":
                    if(map[i][j] instanceof hashashin ||  map[i][j] instanceof castle || map[i][j] instanceof archer ){
                        for (int f = 0; f <8; f++) {
                            if(turnX[f]+i>=0 && turnX[f]+i<8 &&  turnY[f]+j<8 && turnY[f]+j<8 && !map[turnX[f]+i][turnY[f]+j].tag.equals("c"))
                                circlePut[turnX[f]+i][turnY[f]+j]=true;
                        }
                    }
                    else if(map[i][j] instanceof solider){
                        for (int f =5; f <8; f++) {
                            if(turnX[f]+i>=0 && turnX[f]+i<8 &&  turnY[f]+j<8 && turnY[f]+j>=0 && !map[turnX[f]+i][turnY[f]+j].tag.equals("c")){
                            circlePut[turnX[f] + i][turnY[f] + j] = true;
                          }
                        }
                    }
                    else if(map[i][j] instanceof knight){
                        for (int f = 0; f <16; f++) {
                            if(turnXK[f]+i>=0 && turnXK[f]+i<8 &&  turnYK[f]+j<8 && turnYK[f]+j<8 && !map[turnXK[f]+i][turnYK[f]+j].tag.equals("c"))
                                circlePut[turnXK[f]+i][turnYK[f]+j]=true;
                        }
                    }
                    break;
                case "m":
                    if(map[i][j] instanceof hashashin ||  map[i][j] instanceof castle || map[i][j] instanceof archer ){
                        for (int f = 0; f <8; f++) {
                            if(turnX[f]+i>=0 && turnX[f]+i<8 &&  turnY[f]+j<8 && turnY[f]+j<8 && !map[turnX[f]+i][turnY[f]+j].tag.equals("m"))
                                circlePut[turnX[f]+i][turnY[f]+j]=true;
                        }
                    }
                    else if(map[i][j] instanceof solider){
                        for (int f = 0; f <3; f++) {
                            if(turnX[f]+i>=0 && turnX[f]+i<8 &&  turnY[f]+j<8 && turnY[f]+j<8 && !map[turnX[f]+i][turnY[f]+j].tag.equals("m"))
                                circlePut[turnX[f]+i][turnY[f]+j]=true;
                        }
                    }
                    else if(map[i][j] instanceof knight){
                        for (int f = 0; f <16; f++) {
                            if(turnXK[f]+i>=0 && turnXK[f]+i<8 &&  turnYK[f]+j<8 && turnYK[f]+j<8 && !map[turnXK[f]+i][turnYK[f]+j].tag.equals("m"))
                                circlePut[turnXK[f]+i][turnYK[f]+j]=true;
                        }
                    }
                    break;
             }

        return circlePut;
    }

    public static void move(int i, int j, int newI, int newJ) {
        map[newI][newJ] = map[i][j];
        map[i][j] = new emp("e", 0, 0, null, false,0);
    }

    public static void saveBoard() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("board.dat"))) {
            oos.writeObject(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void save(String log,String chat,int turn) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("log.dat"))) {
            oos.writeUTF(log);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("chat.dat"))) {
            oos.writeUTF(chat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("turn.dat"))) {
            oos.writeUTF(String.valueOf(turn));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String load(int index) {
        String path="";
        if (index==0)
            path="log.dat";
        else if(index==1)
            path="chat.dat";
        else if(index==2)
            path="turn.dat";

            String str = "";
        if (index!=2){
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
                str = ois.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
                str =ois.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public static void saveName(String text,int index){
        String path="";
        if (index==0)
            path="nameS.dat";
        else
            path="nameC.dat";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeUTF(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String loadName(int index) {
        String path="";
        if (index==0)
            path="nameS.dat";
        else
            path="nameC.dat";
        String str="";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
             str=ois.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
    public static void loadBoard() {
        blocks[][] Board=new blocks[8][8];
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("board.dat"))) {
            Board = (blocks[][]) ois.readObject();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    map[i][j]=Board[i][j];
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    static void ClearFiles() {
        try {
            File myFile = new File("board.dat");
            if (myFile.exists()) {
                myFile.delete();
                myFile.createNewFile();
            }
                myFile = new File("nameS.dat");
                if (myFile.exists()) {
                    myFile.delete();
                    myFile.createNewFile();
                }

                    myFile = new File("nameC.dat");
                    if (myFile.exists()) {
                        myFile.delete();
                        myFile.createNewFile();

                    }
                    myFile = new File("turn.dat");
                     if (myFile.exists()) {
                       myFile.delete();
                       myFile.createNewFile();

                    }
                       myFile = new File("log.dat");
                       if (myFile.exists()) {
                        myFile.delete();
                       myFile.createNewFile();

                        }
                         myFile = new File("chat.dat");
                         if (myFile.exists()) {
                            myFile.delete();
                            myFile.createNewFile();

                        }
                }catch(IOException e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,"IOException, " + e.getMessage());
                }
            }

    static boolean ifEmpty() {
        File myFile = new File("board.dat");
        return (myFile.length()==0);
    }
}


