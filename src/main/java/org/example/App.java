package org.example;
import org.example.words.*;
import org.example.SentenceStructures.SentenceStructures;
import org.example.SentenceStructures.SentenceStructureInfo;
import com.google.cloud.language.v1.*;
import com.google.cloud.language.v1beta2.ClassificationCategory;
import com.google.cloud.language.v1beta2.ModerateTextResponse;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.language.v1.LanguageServiceSettings;
import java.util.*;
import javafx.application.Application;

public class App {

    /**
     * Holds a generated sentence along with its toxicity score.
     */
    public static class SentenceResult {
        private final String text;
        private final double toxicity;

        /**
         * @param text      the generated sentence
         * @param toxicity  toxicity confidence score from Google API
         */
        public SentenceResult(String text, double toxicity) {
            this.text = text;
            this.toxicity = toxicity;
        }

        /** @return the sentence text */
        public String getText() { return text; }

        /** @return the toxicity score */
        public double getToxicity() { return toxicity; }
    }

    /**
     * Generates a list of nonsense sentences from the input text by analyzing
     * its syntax, substituting words into templates, and then scoring toxicity.
     *
     * @param text  the source text to analyze and remix
     * @return      list of SentenceResult with generated sentences and scores
     * @throws Exception if input is invalid or API calls fail
     */
    public List<SentenceResult> generate(String text) throws Exception {
        // Validate input
        if (text == null || text.trim().isEmpty() || !text.matches(".*[a-zA-Z]+.*")) {
            throw new IllegalArgumentException("Invalid input: please enter a real sentence.");
        }
        // Load credentials provider singleton
        FixedCredentialsProvider credsProvider = org.example.GoogleCredentialsProvider.getProvider();

        // Initialize word lists and structures
        Nouns nounList = new Nouns();
        Verbs verbList = new Verbs();
        Adjectives adjectiveList = new Adjectives();
        Pronouns pronounList = new Pronouns();
        Articles articleList = new Articles();
        Adverbs adverbList = new Adverbs();
        SentenceStructures sentenceStructures = new SentenceStructures();

        // Syntax analysis via Google Cloud Language API
        Document doc = Document.newBuilder()
                .setContent(text)
                .setType(Document.Type.PLAIN_TEXT)
                .build();

        LanguageServiceSettings v1settings = LanguageServiceSettings.newBuilder()
                .setCredentialsProvider(credsProvider)
                .build();
        AnalyzeSyntaxResponse syntaxResponse;
        try (LanguageServiceClient language = LanguageServiceClient.create(v1settings)) {
            syntaxResponse = language.analyzeSyntax(
                    AnalyzeSyntaxRequest.newBuilder()
                            .setDocument(Document.newBuilder()
                                    .setContent(text)
                                    .setType(Document.Type.PLAIN_TEXT)
                                    .build())
                            .setEncodingType(EncodingType.UTF8)
                            .build()
            );
        }

        // Collect tokens by part of speech
        List<String> nouns      = new ArrayList<>();
        List<String> verbs      = new ArrayList<>();
        List<String> adjectives = new ArrayList<>();
        List<String> adverbs    = new ArrayList<>();
        List<String> articles   = new ArrayList<>();
        List<String> pronouns   = new ArrayList<>();

        for (Token token : syntaxResponse.getTokensList()) {
            String w = token.getText().getContent();
            switch (token.getPartOfSpeech().getTag()) {
                case NOUN -> nouns.add(w);
                case VERB -> verbs.add(w);
                case ADJ  -> adjectives.add(w);
                case ADV  -> adverbs.add(w);
                case DET  -> articles.add(w);
                case PRON -> pronouns.add(w);
                default   -> {}
            }
        }

        // Shuffle lists to introduce variety
        Collections.shuffle(nouns);
        Collections.shuffle(verbs);
        Collections.shuffle(adjectives);
        Collections.shuffle(adverbs);
        Collections.shuffle(articles);
        Collections.shuffle(pronouns);

        // Build nonsense sentences using templates
        List<String> finalSentences = new ArrayList<>();
        int nIdx=0, vIdx=0, adjIdx=0, advIdx=0, artIdx=0, prIdx=0;

        while (nIdx < nouns.size() || vIdx < verbs.size() ||
                adjIdx < adjectives.size() || advIdx < adverbs.size() ||
                artIdx < articles.size()  || prIdx < pronouns.size()) {

            // Select template that uses the most available words
            SentenceStructureInfo bestTpl = null;
            int maxUsed = -1;
            for (SentenceStructureInfo tpl : sentenceStructures.getStructures()) {
                int used = Math.min(tpl.getCount("[noun]"), nouns.size()-nIdx)
                        + Math.min(tpl.getCount("[verb]"), verbs.size()-vIdx)
                        + Math.min(tpl.getCount("[adjective]"), adjectives.size()-adjIdx)
                        + Math.min(tpl.getCount("[adverb]"), adverbs.size()-advIdx)
                        + Math.min(tpl.getCount("[article]"), articles.size()-artIdx)
                        + Math.min(tpl.getCount("[pronoun]"), pronouns.size()-prIdx);
                if (used > maxUsed) {
                    maxUsed = used;
                    bestTpl = tpl;
                }
            }
            if (bestTpl == null) break;

            // Split the chosen template into tokens, then replace placeholders with actual words or random fallbacks
            List<String> tokens = WordUtil.SentenceSplitter(bestTpl.getTemplate());
            for (int i = 0; i < tokens.size(); i++) {
                String typeTag = WordUtil.TypeCheck(tokens.get(i));
                if (typeTag == null) continue;
                switch (typeTag) {
                    case "[noun]" -> tokens.set(i, WordUtil.TypeSubstitute(tokens.get(i), "[noun]",
                            nIdx < nouns.size() ? nouns.get(nIdx++) : nounList.Random()));
                    case "[verb]" -> tokens.set(i, WordUtil.TypeSubstitute(tokens.get(i), "[verb]",
                            vIdx < verbs.size() ? verbs.get(vIdx++) : verbList.Random()));
                    case "[adjective]" -> tokens.set(i, WordUtil.TypeSubstitute(tokens.get(i), "[adjective]",
                            adjIdx < adjectives.size() ? adjectives.get(adjIdx++) : adjectiveList.Random()));
                    case "[adverb]" -> tokens.set(i, WordUtil.TypeSubstitute(tokens.get(i), "[adverb]",
                            advIdx < adverbs.size() ? adverbs.get(advIdx++) : adverbList.Random()));
                    case "[article]" -> tokens.set(i, WordUtil.TypeSubstitute(tokens.get(i), "[article]",
                            artIdx < articles.size() ? articles.get(artIdx++) : articleList.Random()));
                    case "[pronoun]" -> tokens.set(i, WordUtil.TypeSubstitute(tokens.get(i), "[pronoun]",
                            prIdx < pronouns.size() ? pronouns.get(prIdx++) : pronounList.Random()));
                }
            }
            // capitalize first letter of generated sentence
            String raw = String.join(" ", tokens);
            String capitalized = raw.isEmpty()
                    ? raw
                    : Character.toUpperCase(raw.charAt(0)) + raw.substring(1);
            finalSentences.add(capitalized);
        }

        // Moderate each sentence and record toxicity
        var betaSettings = com.google.cloud.language.v1beta2.LanguageServiceSettings.newBuilder()
                .setCredentialsProvider(credsProvider)
                .build();
        List<SentenceResult> results = new ArrayList<>();
        try (var modClient =
                     com.google.cloud.language.v1beta2.LanguageServiceClient.create(betaSettings)) {
            for (String s : finalSentences) {
                var outDoc = com.google.cloud.language.v1beta2.Document.newBuilder()
                        .setContent(s)
                        .setType(com.google.cloud.language.v1beta2.Document.Type.PLAIN_TEXT)
                        .build();
                ModerateTextResponse modResp = modClient.moderateText(outDoc);
                double tox = modResp.getModerationCategoriesList().stream()
                        .filter(c -> "Toxic".equalsIgnoreCase(c.getName()))
                        .mapToDouble(ClassificationCategory::getConfidence)
                        .findFirst().orElse(0.0);
                results.add(new SentenceResult(s, tox));
            }
        }
        return results;
    }

    /**
     * Launches the JavaFX UI.
     */
    public static void main(String[] args) {
        Application.launch(UI.class, args);
    }
}
