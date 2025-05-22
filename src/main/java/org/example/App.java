package org.example;

import com.google.cloud.language.v1.*;
import com.google.cloud.language.v1beta2.ModerateTextResponse;
import com.google.cloud.language.v1beta2.ClassificationCategory;

import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        Nouns nounList = new Nouns();
        Verbs verbList = new Verbs();
        Adjectives adjectiveList = new Adjectives();
        Pronouns pronounList = new Pronouns();
        Articles articleList = new Articles();
        Adverbs adverbList = new Adverbs();
        SentenceStructures sentenceStructures = new SentenceStructures();

        Scanner scanner = new Scanner(System.in);
        String text;
        AnalyzeSyntaxResponse response;// mi serve scritto così per fare il controllo dopo

        List<String> inputNouns;
        List<String> inputVerbs;
        List<String> inputAdjectives;
        List<String> inputAdverbs;
        List<String> inputArticles;
        List<String> inputPronouns;

        //continua a chiede se l'imput è non valido********************************************************
        while (true) {
            System.out.println("Please enter the sentence to be analyzed:");
            text = scanner.nextLine().trim();

            if (text.isEmpty() || !text.matches(".*[a-zA-Z]+.*")) {
                System.out.println("❌ Invalid input. Please enter a sentence containing actual words (not just numbers or symbols).");
                continue;
            }

            Document doc = Document.newBuilder()
                    .setContent(text)
                    .setType(Document.Type.PLAIN_TEXT)
                    .build();

            try (LanguageServiceClient language = LanguageServiceClient.create()) {
                AnalyzeSyntaxRequest request = AnalyzeSyntaxRequest.newBuilder()
                        .setDocument(doc)
                        .setEncodingType(EncodingType.UTF8)
                        .build();

                response = language.analyzeSyntax(request);
            }
            //*************************************************************************************************+

            inputNouns = new ArrayList<>();
            inputVerbs = new ArrayList<>();
            inputAdjectives = new ArrayList<>();
            inputAdverbs = new ArrayList<>();
            inputArticles = new ArrayList<>();
            inputPronouns = new ArrayList<>();

            for (Token token : response.getTokensList()) {//più compatto***************************************
                String word = token.getText().getContent();
                switch (token.getPartOfSpeech().getTag()) {
                    case NOUN -> inputNouns.add(word);
                    case VERB -> inputVerbs.add(word);
                    case ADJ -> inputAdjectives.add(word);
                    case ADV -> inputAdverbs.add(word);
                    case DET -> inputArticles.add(word);
                    case PRON -> inputPronouns.add(word);
                }
            }

            if (inputNouns.isEmpty() && inputVerbs.isEmpty() && inputAdjectives.isEmpty()
                    && inputAdverbs.isEmpty() && inputArticles.isEmpty() && inputPronouns.isEmpty()) {
                System.out.println("❌ No recognizable words found.");
                System.out.println("👉 Please try again with a real sentence.");
            } else {
                break;
            }
        }

        // Shuffle for variety
        Collections.shuffle(inputNouns);
        Collections.shuffle(inputVerbs);
        Collections.shuffle(inputAdjectives);
        Collections.shuffle(inputAdverbs);
        Collections.shuffle(inputArticles);
        Collections.shuffle(inputPronouns);

        //*********************************************************************************************************
        int nounIndex = 0, verbIndex = 0, adjIndex = 0, advIndex = 0, artIndex = 0, pronIndex = 0;
        List<String> finalSentences = new ArrayList<>();

        while (nounIndex < inputNouns.size() || verbIndex < inputVerbs.size()
                || adjIndex < inputAdjectives.size() || advIndex < inputAdverbs.size()
                || artIndex < inputArticles.size() || pronIndex < inputPronouns.size()) {

            SentenceStructureInfo bestTemplate = null;
            int maxWordsUsed = -1;

            for (SentenceStructureInfo template : sentenceStructures.getStructures()) {
                int used = Math.min(template.getCount("[noun]"), inputNouns.size() - nounIndex)
                        + Math.min(template.getCount("[verb]"), inputVerbs.size() - verbIndex)
                        + Math.min(template.getCount("[adjective]"), inputAdjectives.size() - adjIndex)
                        + Math.min(template.getCount("[adverb]"), inputAdverbs.size() - advIndex)
                        + Math.min(template.getCount("[article]"), inputArticles.size() - artIndex)
                        + Math.min(template.getCount("[pronoun]"), inputPronouns.size() - pronIndex);

                if (used > maxWordsUsed) {
                    bestTemplate = template;
                    maxWordsUsed = used;
                }
            }

            if (bestTemplate == null) break;
            //***********************************************************************************************************

            ArrayList<String> phrase = WordUtil.SentenceSplitter(bestTemplate.getTemplate());

            for (int i = 0; i < phrase.size(); i++) {
                String tokenType = WordUtil.TypeCheck(phrase.get(i));
                if (tokenType == null) continue;

                switch (tokenType) {// più compatto**********************************************************************
                    case "[noun]" -> phrase.set(i, WordUtil.TypeSubstitute(phrase.get(i), "[noun]",
                            nounIndex < inputNouns.size() ? inputNouns.get(nounIndex++) : nounList.Random()));
                    case "[verb]" -> phrase.set(i, WordUtil.TypeSubstitute(phrase.get(i), "[verb]",
                            verbIndex < inputVerbs.size() ? inputVerbs.get(verbIndex++) : verbList.Random()));
                    case "[adjective]" -> phrase.set(i, WordUtil.TypeSubstitute(phrase.get(i), "[adjective]",
                            adjIndex < inputAdjectives.size() ? inputAdjectives.get(adjIndex++) : adjectiveList.Random()));
                    case "[adverb]" -> phrase.set(i, WordUtil.TypeSubstitute(phrase.get(i), "[adverb]",
                            advIndex < inputAdverbs.size() ? inputAdverbs.get(advIndex++) : adverbList.Random()));
                    case "[article]" -> phrase.set(i, WordUtil.TypeSubstitute(phrase.get(i), "[article]",
                            artIndex < inputArticles.size() ? inputArticles.get(artIndex++) : articleList.Random()));
                    case "[pronoun]" -> phrase.set(i, WordUtil.TypeSubstitute(phrase.get(i), "[pronoun]",
                            pronIndex < inputPronouns.size() ? inputPronouns.get(pronIndex++) : pronounList.Random()));
                }
            }

            finalSentences.add(String.join(" ", phrase));
        }

        // Output section
        System.out.println("\nWords found:");
        System.out.println("Nouns: " + inputNouns);
        System.out.println("Verbs: " + inputVerbs);
        System.out.println("Adjectives: " + inputAdjectives);
        System.out.println("Adverbs: " + inputAdverbs);
        System.out.println("Articles: " + inputArticles);
        System.out.println("Pronouns: " + inputPronouns);

        // avvisa se genera pù frasi *****************************************************************************
        if (finalSentences.size() > 1) {
            System.out.println("\n⚠️ The input sentence contains many words.");
            System.out.println("👉 Multiple nonsense sentences have been generated to use them all.");
        }

        System.out.println("\n🌀 Generated nonsense sentence(s):");
        for (String s : finalSentences) {
            System.out.println("→ " + s);
        }

        // Controllo di tossicità / moderazione del testo
        try (com.google.cloud.language.v1beta2.LanguageServiceClient modClient = com.google.cloud.language.v1beta2.LanguageServiceClient.create()) {

            com.google.cloud.language.v1beta2.Document modDoc =
                    com.google.cloud.language.v1beta2.Document.newBuilder()
                            .setContent(text)
                            .setType(com.google.cloud.language.v1beta2.Document.Type.PLAIN_TEXT)
                            .build();

            ModerateTextResponse modResponse = modClient.moderateText(modDoc);
            List<ClassificationCategory> cats = modResponse.getModerationCategoriesList();

            // Filtro solo la categoria 'Toxic'
            //Need to set the confidence thresholds
            cats.stream()
                    .filter(cat -> "Toxic".equalsIgnoreCase(cat.getName()))
                    .findFirst()
                    .ifPresent(cat -> System.out.printf("Toxicity detected! Toxicity score: %.2f%n", cat.getConfidence()));
        }
    }
}