package org.example;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileReader;

public class WordUtil{

    public static ArrayList<String> importer (String LibName)
    {
        ArrayList<String> words = new ArrayList<String>();
        try{
            FileReader reader = new FileReader(LibName);
            Scanner in = new Scanner(reader);
            while(in.hasNextLine())
            {
                String nextWord = in.nextLine();
                if(!nextWord.isEmpty())
                {
                    words.add(nextWord);
                };
            }
            reader.close();
            in.close();
        }
        catch(IOException e)
        {
            System.out.println(e);
            System.exit(1);
        }
        return words;
    }

    public static int Randomizer(int max)
    {
        int num = (int)(Math.random()*(max + 1));
        return num;
    }
}