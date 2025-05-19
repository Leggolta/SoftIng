package org.example;

import com.google.cloud.language.v1.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        org.example.Nouns NounList = new Nouns();
        Verbs VerbList = new Verbs();
        org.example.Adjectives AdjectiveList = new Adjectives();
        SentenceStructures SentenceList = new SentenceStructures();
        //*************************************************************************************
        Pronouns PronounList = new Pronouns();
        Adverbs AdverbList = new Adverbs();
        Articles ArticleList = new Articles();
        //***********************************************************************************
        Scanner scanner = new Scanner(System.in);
        System.out.println("please enter the sentence to be analyzed: ");
        String text = scanner.nextLine();
        scanner.close();

        Document doc = Document.newBuilder()
                .setContent(text)
                .setType(Document.Type.PLAIN_TEXT)
                .build();

        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            AnalyzeSyntaxRequest request = AnalyzeSyntaxRequest.newBuilder()
                    .setDocument(doc)
                    .setEncodingType(EncodingType.UTF8)
                    .build();

            AnalyzeSyntaxResponse response = language.analyzeSyntax(request);

            List<String> InputNouns = new ArrayList<>();
            List<String> InputVerbs = new ArrayList<>();
            List<String> InputArticles = new ArrayList<>();
            List<String> InputAdverbs = new ArrayList<>();
            List<String> InputAdjectives = new ArrayList<>();
            List<String> InputPronouns = new ArrayList<>();


            for (Token token : response.getTokensList()) {
                String word = token.getText().getContent();
                PartOfSpeech.Tag pos = token.getPartOfSpeech().getTag();

                switch (pos) {
                    case NOUN:
                        InputNouns.add(word);
                        break;
                    case VERB:
                        InputVerbs.add(word);
                        break;
                    case DET:
                        InputArticles.add(word);
                        break;
                    case ADV:
                        InputAdverbs.add(word);
                        break;
                    case ADJ:
                        InputAdjectives.add(word);
                        break;
                    case PRON:
                        InputPronouns.add(word);

                        break;
                    default:
                        break;
                }
            }
            ArrayList<String> Phrase = WordUtil.SentenceSplitter(SentenceList.Random());
            //Randomize Input lists
            Collections.shuffle(InputNouns);
            Collections.shuffle(InputVerbs);
            Collections.shuffle(InputArticles);
            Collections.shuffle(InputAdverbs);
            Collections.shuffle(InputAdjectives);
            Collections.shuffle(InputPronouns);
            //makes counters
            int NounCounter = 0;
            int VerbCounter = 0;
            int AdjectiveCounter = 0;

            //********************************************************************************
            int PronounCounter = 0;
            int AdverbCounter = 0;
            int ArticleCounter = 0;

            //********************************************************************************
            /*
            fills the phrase with input words as long as there are enough,
            then randomly selects words from the libraries
             */
            for (int i = 0; i < Phrase.size(); i++) {
                String tokenType = WordUtil.TypeCheck(Phrase.get(i));
                if (tokenType == null) continue; // Evita NullPointerException

                switch (tokenType) {
                    case "[noun]":
                        if (NounCounter < InputNouns.size()) {
                            Phrase.set(i, WordUtil.TypeSubstitute(Phrase.get(i), "[noun]", InputNouns.get(NounCounter)));
                            NounCounter++;
                        } else {
                            Phrase.set(i, WordUtil.TypeSubstitute(Phrase.get(i), "[noun]", NounList.Random()));
                        }
                        break;

                    case "[verb]":
                        if (VerbCounter < InputVerbs.size()) {
                            Phrase.set(i, WordUtil.TypeSubstitute(Phrase.get(i), "[verb]", InputVerbs.get(VerbCounter)));
                            VerbCounter++;
                        } else {
                            Phrase.set(i, WordUtil.TypeSubstitute(Phrase.get(i), "[verb]", VerbList.Random()));
                        }
                        break;

                    case "[adjective]":
                        if (AdjectiveCounter < InputAdjectives.size()) {
                            Phrase.set(i, WordUtil.TypeSubstitute(Phrase.get(i), "[adjective]", InputAdjectives.get(AdjectiveCounter)));
                            AdjectiveCounter++;
                        } else {
                            Phrase.set(i, WordUtil.TypeSubstitute(Phrase.get(i), "[adjective]", AdjectiveList.Random()));
                        }
                        break;

                    //**************************************************************************************************************
                    case "[pronouns]":
                        if (PronounCounter < InputPronouns.size()) {
                            Phrase.set(i, WordUtil.TypeSubstitute(Phrase.get(i), "[pronouns]", InputPronouns.get(PronounCounter)));
                            PronounCounter++;
                        } else {
                            Phrase.set(i, WordUtil.TypeSubstitute(Phrase.get(i), "[pronouns]", "they")); // oppure da libreria
                        }
                        break;

                    case "[adverb]":
                        if (AdverbCounter < InputAdverbs.size()) {
                            Phrase.set(i, WordUtil.TypeSubstitute(Phrase.get(i), "[adverb]", InputAdverbs.get(AdverbCounter)));
                            AdverbCounter++;
                        } else {
                            Phrase.set(i, WordUtil.TypeSubstitute(Phrase.get(i), "[adverb]", "quickly")); // o da file
                        }
                        break;

                    case "[article]":
                        if (ArticleCounter < InputArticles.size()) {
                            Phrase.set(i, WordUtil.TypeSubstitute(Phrase.get(i), "[article]", InputArticles.get(ArticleCounter)));
                            ArticleCounter++;
                        } else {
                            Phrase.set(i, WordUtil.TypeSubstitute(Phrase.get(i), "[article]", "the")); // fisso o da file
                        }
                        break;

                    //**************************************************************************************************************
                    default:
                        break;
                }
            }


            System.out.println("Nouns: " + String.join(", ", InputNouns));
            System.out.println("Verbs: " + String.join(", ", InputVerbs));
            System.out.println("Articles: " + String.join(", ", InputArticles));
            System.out.println("Adverbs: " + String.join(", ", InputAdverbs));
            System.out.println("Adjectives: " + String.join(", ", InputAdjectives));
            System.out.println("Pronouns: " + String.join(", ", InputPronouns));

            //*********************************************************************************************
            System.out.println("Frase generata:");
            System.out.println(String.join(" ", Phrase));
            //*********************************************************************************************

        }
    }
}