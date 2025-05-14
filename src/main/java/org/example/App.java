package org.example;

import com.google.cloud.language.v1.*;

import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Inserisci la frase da analizzare:");
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

            List<String> nouns = new ArrayList<>();
            List<String> verbs = new ArrayList<>();
            List<String> articles = new ArrayList<>();
            List<String> adverbs = new ArrayList<>();
            List<String> adjectives = new ArrayList<>();
            List<String> pronouns = new ArrayList<>();


            for (Token token : response.getTokensList()) {
                String word = token.getText().getContent();
                PartOfSpeech.Tag pos = token.getPartOfSpeech().getTag();

                switch (pos) {
                    case NOUN:
                        nouns.add(word);
                        break;
                    case VERB:
                        verbs.add(word);
                        break;
                    case DET:
                        articles.add(word);
                        break;
                    case ADV:
                        adverbs.add(word);
                        break;
                    case ADJ:
                        adjectives.add(word);
                        break;
                    case PRON:
                        pronouns.add(word);

                        break;
                    default:
                        break;
                }
            }

            System.out.println("Nomi: " + String.join(", ", nouns));
            System.out.println("Verbi: " + String.join(", ", verbs));
            System.out.println("Articoli: " + String.join(", ", articles));
            System.out.println("Avverbi: " + String.join(", ", adverbs));
            System.out.println("Aggettivi: " + String.join(", ", adjectives));
            System.out.println("Pronomi: " + String.join(", ", pronouns));
        }
    }
}
