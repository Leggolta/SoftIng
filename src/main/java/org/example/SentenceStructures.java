package org.example;

import java.util.ArrayList;
public class SentenceStructures
{
    private ArrayList<String> SentenceStructureList;
    public SentenceStructures()
    {
        SentenceStructureList = org.example.WordUtil.importer("src/main/resources/SentenceStructure.txt");

        //********************************+**************************************************
        // Stampa di debug per verificare che il file sia stato caricato correttamente
        System.out.println("Loaded SentenceStructureList: " + SentenceStructureList); //da cancellare solo per verifica
        //**********************************************************************************

    }
    public String Random()
    {
        //**************************************************************************
        // Verifica la dimensione della lista
        System.out.println("SentenceStructureList size: " + SentenceStructureList.size()); //da cancellare solo per verifica

        // Se la lista è vuota, ritorna una struttura predefinita!!!!!!!!!!!!!!!!! ???????? importante
        if (SentenceStructureList.isEmpty()) {
            return "[article] [noun] [verb] [article] [noun]";
        }
        //*********************************************************************

        String RandElem = SentenceStructureList.get(WordUtil.Randomizer(SentenceStructureList.size()));
        return RandElem;
    }
}