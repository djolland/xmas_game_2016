package dao;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 1/4/2017.
 */
public class HighScoreDAO {

    final static int MAXENTRIES = 10;

    public String[] loadScores(String filePath) {
        Scanner fileIn;
        String[] returnScores = new String[10];

        try {
            fileIn = new Scanner(new FileInputStream(filePath));

            int row = 0;
            while (fileIn.hasNext()){
                String[] line = fileIn.nextLine().split(",");
                String returnString = (line[0].trim() + " - " + line[1].trim());
                returnScores[row] = returnString;
                row ++;
            }
            fileIn.close();
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("High Score File Not Found!");
        }

        return returnScores;
    }

    public void saveScore(String filePath, String name, int score) {
        FileWriter fileOut;
        Scanner fileIn;
        String[] saveScores = new String[MAXENTRIES];
        String[] loadedScores = new String[MAXENTRIES];

        try{
            fileIn = new Scanner(new FileInputStream(filePath));
            int row = 0;
            while (fileIn.hasNext()){
                loadedScores[row] = fileIn.nextLine();
                row ++;
            }
            fileIn.close();
            saveScores[0] = name + "," + score;
            String nextEntry = saveScores[0];
            for (int i = 0; i < loadedScores.length; i++){
                if (loadedScores[i] == null){
                    saveScores[i] = nextEntry;
                    break;
                }
                else {
                    if (Integer.parseInt(nextEntry.split(",")[1].trim()) >
                            Integer.parseInt(loadedScores[i].split(",")[1].trim())) {
                        saveScores[i] = nextEntry;
                        nextEntry = loadedScores[i];
                    } else {
                        saveScores[i] = loadedScores[i];
                    }
                }
            }
            fileOut = new FileWriter(filePath);
            for (int i = 0; i < saveScores.length; i++){
                if (saveScores[i] != null){
                    fileOut.write(saveScores[i] + '\n');
                }
            }
            fileOut.close();
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("High Score File Not Found!");
        }
    }
}
