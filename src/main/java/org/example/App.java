package org.example;
import org.example.words.*;
import com.google.cloud.language.v1.*;
import com.google.cloud.language.v1beta2.ModerateTextResponse;
import java.util.*;
import javafx.application.Application;

/**Core application logic for Nonsense generator.
 * Provides sentences generator method for GUI use and launches JavaFX UI.
 */
public class App {

    /**
     * Generates a list of "nonsense" sentences with toxicity scores, based on the input text.
     * If the input contains many words, prepends a notice before the generated sentences.
     * @param text the input sentence to analyze
     * @return list of formatted sentences with toxicity scores and notices
     * @throws Exception on API errors or invalid input
     */
    public List<String> generate(String text) throws Exception {
        // Validate input
        if (text == null || text.trim().isEmpty() || !text.matches(".*[a-zA-Z]+.*")) {
            throw new IllegalArgumentException("Invalid input: please enter a real sentence.");
        }

        // Initialize lists and structures
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
        AnalyzeSyntaxResponse syntaxResponse;
        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            AnalyzeSyntaxRequest req = AnalyzeSyntaxRequest.newBuilder()
                    .setDocument(doc)
                    .setEncodingType(EncodingType.UTF8)
                    .build();
            syntaxResponse = language.analyzeSyntax(req);
        }

        // Categorize tokens
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

        // Shuffle for variety
        Collections.shuffle(nouns);
        Collections.shuffle(verbs);
        Collections.shuffle(adjectives);
        Collections.shuffle(adverbs);
        Collections.shuffle(articles);
        Collections.shuffle(pronouns);

        // Generate nonsense sentences
        int nIdx=0, vIdx=0, adjIdx=0, advIdx=0, artIdx=0, prIdx=0;
        List<String> finalSentences = new ArrayList<>();

        while (nIdx < nouns.size() || vIdx < verbs.size() ||
                adjIdx < adjectives.size() || advIdx < adverbs.size() ||
                artIdx < articles.size()  || prIdx < pronouns.size()) {

            // Find best template
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

            // Split template and substitute
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
            finalSentences.add(String.join(" ", tokens));
        }

        // Moderate text toxicity
        List<String> withToxicity = new ArrayList<>();
        try (com.google.cloud.language.v1beta2.LanguageServiceClient modClient =
                     com.google.cloud.language.v1beta2.LanguageServiceClient.create()) {
            for (String s : finalSentences) {
                com.google.cloud.language.v1beta2.Document outDoc =
                        com.google.cloud.language.v1beta2.Document.newBuilder()
                                .setContent(s)
                                .setType(com.google.cloud.language.v1beta2.Document.Type.PLAIN_TEXT)
                                .build();
                ModerateTextResponse modResp = modClient.moderateText(outDoc);
                String tox = modResp.getModerationCategoriesList().stream()
                        .filter(c -> "Toxic".equalsIgnoreCase(c.getName()))
                        .findFirst()
                        .map(c -> String.format("%.2f%%", c.getConfidence() * 100))
                        .orElse("No toxicity detected");
                withToxicity.add("→ " + s + "  ---->  Toxicity score: " + tox);
            }
        }

        // If multiple sentences generated, prepend notice
        if (finalSentences.size() > 1) {
            List<String> notice = Arrays.asList(
                    "The input sentence contains many words.",
                    "Multiple nonsense sentences have been generated to use them all"
            );
            List<String> combined = new ArrayList<>();
            combined.addAll(notice);
            combined.addAll(withToxicity);
            return combined;
        }

        return withToxicity;
    }

    //Launch the JavaFX UI instead of console I/O

    public static void main(String[] args) {
        Application.launch(UI.class, args);
    }
}
