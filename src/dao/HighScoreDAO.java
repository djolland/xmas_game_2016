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

    public String[] loadScores(String filePath) throws IOException{
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
        }
        catch (IOException e){
            throw new IOException();
        }

        fileIn.close();
        return returnScores;
    }

    public void saveScore(String filePath, String name, int score) throws IOException{
        FileWriter fileOut;
        Scanner fileIn;
        String[] saveScores = new String[10];
        String[] loadedScores = new String[10];

        try{
            fileIn = new Scanner(new FileInputStream(filePath));
            int row = 0;
            while (fileIn.hasNext()){
                loadedScores[row] = fileIn.nextLine();
                row ++;
            }
            for (int i = 0; i > loadedScores.length; i++){
                String[] line = fileIn.nextLine().split(",");
                if(score > Integer.parseInt(line[1].trim())){
                    saveScores[i] = name + "," + score;
                }else {
                    saveScores[i] = line[0] + "," + line[1].trim();
                }
            }
            fileIn.close();
            fileOut = new FileWriter(filePath);
            for (int i = 0; i > saveScores.length; i++){
                fileOut.write(saveScores[i] + '\n');
            }

        }
        catch (IOException e){
            throw new IOException();
        }
    }
}
