package org.example.words;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.FileReader;
import java.io.FileWriter;


public class WordUtil{

    /**
     * Loads all non-empty lines from a text file into a list of strings.
     *
     * @param LibName the path to the text file to import
     * @return an ArrayList of all non-empty lines read from the file
     * @throws RuntimeException if an I/O error occurs during file reading
     */
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

    /**
     * Generates a random integer between 0 (inclusive) and max (exclusive).
     *
     * @param max the upper bound (exclusive) for the random number
     * @return a random index in the range [0, max)
     */
    public static int Randomizer(int max) {
        int num = (int)(Math.random() * max);   // Multiply a random double [0.0, 1.0) by max and cast to int
        return num;
    }

    /**
     * Splits an input string on spaces into a list of tokens.
     *
     * @param Input the sentence or phrase to split
     * @return an ArrayList where each element is one word/token from the input
     */
    public static ArrayList<String> SentenceSplitter(String Input) //divides a string in arraylist for easier elaboration
    {
        String[] WordArray = Input.split(" ");
        ArrayList<String> Splitter = new ArrayList<String>();
        Collections.addAll(Splitter, WordArray);
        return Splitter;
    }

    /**
     * Checks if a token contains a placeholder type in square brackets,
     * and returns the bracketed type if present.
     *
     * @param Name the string to examine for a [TYPE] pattern
     * @return the placeholder (e.g., "[NOUN]") if found, or null otherwise
     */
    public static String TypeCheck(String Name)
    {
        if (Name.contains("[") && Name.contains("]"))
        {
            String TypeName = Name.substring(Name.indexOf("[") + 1, Name.indexOf("]"));
            return "[" + TypeName + "]";
        }
        else return null;
    }

    /**
     * Replaces a placeholder in the original string with a concrete word.
     *
     * @param Original the string containing the placeholder
     * @param Type the placeholder text to replace (e.g., "[VERB]")
     * @param NewWord the word to substitute in place of the placeholder
     * @return a new string with the placeholder replaced by newWord
     */
    public static String TypeSubstitute(String Original, String Type, String NewWord)
    {
        String Changed = Original.replace(Type, NewWord);
        return Changed;
    }

    public static void Log(String Path, String Phrase)
    {
        try (FileWriter fw = new FileWriter(Path, true)) {
            fw.write(Phrase+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
