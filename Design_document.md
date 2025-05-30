Group Quadri (Garzotto, Pozza, Silvello, Gavrilovska)
# Nonsense Generator
[Domain model](#domain-model)

[System sequence diagrams](#system-sequence-diagrams)

[Design class model](#design-class-model)

[Internal sequence diagrams](#internal-sequence-diagrams)

---
## Domain model
![Domain model]()

```plantuml
@startuml
left to right direction
Object User

Object App
Object AnalyzeSyntax
Object Google_API
Object Toxicity
object SentenceStructure
Object UI{
input sentence
output sentence
Syntactic tree
Results.txt
toxicity
}
Object WordList{
Nouns
Verbs
Adjectives
Articles
Pronoun
Adverb
}
Object Box{
SyntacticTree []
}
Object resources {
Nouns.txt
Verbs.txt
Adverb.txt
Article.txt
Pronoun.txt
Articles.txt
Adjectives.txt
SentenceStructure.txt
}

Box "1" <|-- "1" User : "tick"
Box"1" *-- "1" UI : "contains"
UI "1" <|-- "1" User : "writes input"
User"1" <|--"1" UI : output
UI "0" --|> "1" App : "submit"
UI "1" <|-- "0" App : "output"

App "1" --|> "*" SentenceStructure :"extract"
SentenceStructure "*" --|> "1"resources :"Randomly select"
App "1" *-- "2" Google_API : "requests"
App "1" --|> "*" WordList : "extract"
resources "1" <|-- "0" WordList : "Randomly select"
Google_API "1" *-- "1" AnalyzeSyntax : "Returns word type"
Google_API "1" *-- "1" Toxicity: "calculate"
@enduml
```

## System sequence diagrams
![System sequence diagrams]()

```plantuml
@startuml
actor User
participant "App" as App
participant "Scanner" as Scanner

participant "GoogleCloudAPI(AnalyzeSyntax)" as AnalyzeSyntax
participant "GoogleCloudAPI(ModerateText)" as ModerateText

participant "SentenceStructures" as Structures
participant "SentenceStructureInfo" as StructureInfo

participant "WordUtil" as WordUtil
participant "WordList (Nouns, Verbs, ...)" as WordList
participant "resources/*.txt" as Resources

User -> App: start main()
App -> Scanner: readLine()
Scanner --> App: input sentence

App -> AnalyzeSyntax: analyzeSyntax(text)
AnalyzeSyntax --> App: AnalyzeSyntaxResponse (tokens)

App -> Structures: getStructures()
Structures --> App: List<SentenceStructureInfo>

loop per ogni template
    App -> StructureInfo: getCount("[noun]") / altri placeholder
    StructureInfo --> App: count placeholder
end

App -> WordUtil: SentenceSplitter(template)
WordUtil --> App: tokens

loop per ogni token
    App -> WordUtil: TypeCheck(token)
    WordUtil --> App: tipo token
    App -> WordList: Random()  // Ottiene parola random da lista specifica
    WordList --> App: parola sostitutiva
    App -> WordUtil: TypeSubstitute(token, tipo, parola)
    WordUtil --> App: token sostituito
end

note right of WordList
  WordList carica parole da
  resources/*.txt tramite
  WordUtil.importer()
end note

App -> ModerateText: moderateText(frase generata)
ModerateText --> App: ModerateTextResponse (toxicity score)

App -> App: Stampa parole riconosciute per tipo
App -> App: Stampa frasi generate

loop per ogni frase generata
    App -> ModerateText: moderateText(frase)
    ModerateText --> App: ModerateTextResponse (toxicity score)
    App -> App: Stampa frase con punteggio tossicità
end

App -> User: output finale (frasi + tossicità)
@enduml
```

## Design class model
![Design class model]()

```plantuml
@startuml
allowmixing
left to right direction
skinparam classAttributeIconSize 0
package java <<folder>>{
package org.exemple{
package css{
artifact style.css
}
class App{
+ List<String>generate(String)
+ Void main(string[])
}
class UI{
+ void start(Stage)
}
package controller{
class MainController{
-TextField inputField
-TextArea outputArea
-App processor
+void OnGenerateClicked()
}
}
package SentenceStructures{
class SentenceStructureInfo{
-String Template
-Map<String, Integer> placeholderCount
+SentenceStructureInfo(String)
+Void CountPlaceHolders()
+String getTemplate()
+int getCount()
+Map<String, Integer> getAllCounts()
}
class SentenceStructure{
-List<SentenceStructureInfo> getStructures()
+SentenceStructure()
+List<SentenceStructureInfo> getStructures()
}
}

Package JavaFX{
Class Application{
+void launch()
}
}
package GoogleAPI
{
class Document{}
class Token {}
class EncodingType{}
class LanguageServiceClient{}
class AnalizeSyntaxRequest{}
class AnalyzeSyntaxResponse{}
class ModerateTextResponse{}
}
package Words{
class Adjectives{
+Adjectives()
}
class Adverbs{
+Adverbs()
}
class Articles{
+Articles()
}
class Nouns{
+Nouns()
}
class Pronouns{
+Pronouns()
}
class Verbs{
+Verbs()
}
abstract class WordList{
-ArrayList<String> words
+WordList(String)
+String Random()
}
class WordUtil{
+ArrayList<String> importer(String)
+int Randomizer (int)
+ArrayList SentenceSplitter(String)
+String TypeCheck(String)
+String TypeSubstitute (String, String, String)
}
}
UI <|-- JavaFX
JavaFX <-- App : Uses
App"0" *-- "1" Words : Contains & instances
App"0" *-- "0"SentenceStructures : Contains & instances
App --> GoogleAPI : Uses
controller"0" *-- "1"App : Contains & instances
UI"1" <-- "0"App : Uses
WordList --> WordUtil : Uses
WordUtil"0" <-- "0"App : Uses
WordUtil"0" <-- "0"SentenceStructure : Uses
SentenceStructureInfo"0" <-- "0"SentenceStructure : Uses
WordList <|-- Nouns
WordList <|-- Verbs
WordList <|-- Articles
WordList <|-- Adjectives
WordList <|-- Pronouns
WordList <|-- Adverbs
}
}
package resources <<folder>> {
package Interface {
file "main.fxml"
}
artifact "Nouns.txt"
artifact "Verbs.txt"
artifact "Adverbs.txt"
artifact "Pronouns.txt"
artifact "Articles.txt"
artifact "Adjectives.txt"
artifact "SentenceStructure.txt"
}
package Results {
artifact “Log.txt” as log
}
UI --> Interface
resources "*" <.. Words :reads
resources “*” <.. “1” SentenceStructures :reads
log <-- "1" WordUtil :writes
@enduml
```

## Internal sequence diagrams
![Internal sequence diagrams]()

```plantuml
generate()
@startuml
participant MainController
participant App
participant WordList
collections "Google Cloud API" as Google
collections SentenceStructures
participant WordUtil
participant WordList

MainController -> App: generate(inputText)
activate App

App -> WordList: initialize lists (Nouns, Adjectives, Verbs, ...) and structures
activate WordList
WordList -> WordUtil: importer(filePath)
WordUtil --> WordList: lists
WordList --> App: lists
deactivate WordList

loop for each Token
    App -> Google: API analyze syntax
    Google--> App: word type
end

App -> App: shuffle lists (Nouns, Verbs, Adjective, ...) 

loop input words remaining
    App -> SentenceStructures: select best template for these words
    activate SentenceStructures
    SentenceStructures --> App: return template
    deactivate SentenceStructures
    App -> WordUtil: split sentence into a list of tokens 
    activate WordUtil
    WordUtil --> App: list of tokens
    deactivate WordUtil
    loop for each Token
        App -> WordUtil: check type of token 
        activate WordUtil
        WordUtil --> App: token type
        alt correct type words remaining
            App -> WordUtil: replacing placeholder with an input word
        else
            WordUtil -> WordList: get a random word of the correct type from the libraries
            WordList --> WordUtil: random word 
        end
        WordUtil --> App: token changed with a word
        deactivate WordUtil
    end
    App -> App: join tokens into the sentence
end

loop for each word of the sentence
    App -> Google: ModerateText analyze
    activate Google
    Google --> App: toxicity level
    deactivate Google
end
App --> MainController: list of sentences
@enduml
```
