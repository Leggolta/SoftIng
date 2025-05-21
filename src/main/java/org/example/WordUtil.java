package org.example;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

    public static int Randomizer(int max) {
        int num = (int)(Math.random() * max);   // Genera da 0 a max - 1 (xchè se no dava index out of boundaries)**********
        return num;
    }


    public static ArrayList<String> SentenceSplitter(String Input) //divides a string in arraylist for easier elaboration
    {
        ArrayList<String> Splitter = new ArrayList<String>();
        String[] WordArray = Input.split(" ");

        Collections.addAll(Splitter, WordArray);//***********************************************

        return Splitter;
    }

    public static String TypeCheck(String Name)
    {
        if (Name.contains("[") && Name.contains("]"))
        {
            String TypeName = Name.substring(Name.indexOf("[") + 1, Name.indexOf("]"));
            return "[" + TypeName + "]";
        }
        else return null;
    }

    public static String TypeSubstitute(String Original, String Type, String NewWord)
    {
        String Changed = Original.replace(Type, NewWord);
        return Changed;
    }
}
