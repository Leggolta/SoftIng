import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileReader;

public class wordLoader{
    public ArrayList<String> importer (String LibName)
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
}