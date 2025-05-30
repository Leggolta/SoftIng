Group Quadri (Garzotto, Pozza, Silvello, Gavrilovska)
# Nonsense Generator
- [Domain model](#domain-model)

- [System sequence diagram](#system-sequence-diagram)

- [Sequence Diagram](#sequence-diagram)

- [Internal sequence diagrams](#internal-sequence-diagrams)

- [Design class model](#design-class-model)

---
## Domain model
![Domain model](img/DomainModel.png)

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

## System sequence diagram
![System sequence diagram](img/SystemSequenceDiagram.png)

```plantuml
@startuml
actor User
boundary "Nonsense Generator" as System

User -> System: Enters sentence to analyze
activate System

System -> System: Validate input (no: punctuation, blanks, numbers)
alt invalid input
    System --> User: Error message: invalid input
    deactivate System
    return
end

System -> "Google Cloud API\n(AnalyzeSyntax)": Request syntactic analysis
activate "Google Cloud API\n(AnalyzeSyntax)"
"Google Cloud API\n(AnalyzeSyntax)" --> System: Response with tokens and parts of speech
deactivate "Google Cloud API\n(AnalyzeSyntax)"

System -> System: Extract and classify words with internal lists (noun, verb, etc.)
alt no recognized words
    System --> User: Error message: no valid words found
    deactivate System
    return
end

System -> System: Select random sentence structure(s) from SentenceStructure.txt
System -> System: Select random nouns,verbs,.. from file.txt
System -> System: Generate nonsense sentences using extracted words and internal lists
System -> "Google Cloud API\n(ModerateText)": Evaluate toxicity of generated sentences
activate "Google Cloud API\n(ModerateText)"
"Google Cloud API\n(ModerateText)" --> System: Response with toxicity scores
deactivate "Google Cloud API\n(ModerateText)"

System --> User: Output: Sentence structures, (Syntactic Tree), nonsense sentence, toxicity
deactivate System
@enduml
```

## Sequence Diagram

![Sequence diagram](img/SequenceDiagram.png)

```plantuml
@startuml
actor User
participant "Graphic Interface\n(UI)" as UI
participant App
participant Scanner
participant "Google Cloud API\n(AnalyzeSyntax)" as AnalyzeSyntax
participant "Google Cloud API\n(ModerateText)" as ModerateText
participant SentenceStructures
participant SentenceStructureInfo
participant WordUtil
participant WordList
database "resources/*.txt" as Resources

' ------------------ Start and Input ------------------
User -> App: start()
activate App

App -> UI: open start screen
activate UI


loop enter valid input
    UI -> UI: display text field
    UI -> UI: wait for input
    UI -> App: submit(text)
end
    App -> Scanner: nextLine(text)
    Scanner --> App: text
    
    alt invalid text
        App -> UI: display error ("Invalid input")
    else valid text
        App -> AnalyzeSyntax: analyzeSyntax(text)
        activate AnalyzeSyntax
        AnalyzeSyntax --> App: AnalyzeSyntaxResponse
        deactivate AnalyzeSyntax

        App -> App: extract and classify words 
end

' ------------------ Template Selection------------------
App -> SentenceStructures: getStructures()
SentenceStructures --> App: template lists

loop for each template
    App -> SentenceStructureInfo: getCount([type])
    SentenceStructureInfo --> App: number
end

App -> App: select best template
App -> WordUtil: SentenceSplitter(template)
WordUtil --> App: tokenTemplate[]

loop for each tokenTemplate
    App -> WordUtil: TypeCheck(token)
    WordUtil --> App: type (e.g. [noun])
    
    alt is placeholder
        App -> WordList: get word from input or Random()
        WordList --> App: word
        App -> WordUtil: TypeSubstitute(token, type, word)
        WordUtil --> App: replaced token
    end
end

App -> App: generate sentence with replaced words
App -> App: repeat if any words remain
App -> App: save generated sentences

' ------------------ Output and GUI ------------------
App -> UI: shows found words (nouns, verbs, ecc.)
UI -> User: render the lists of words

== Toxicity analysis ==
loop for each sentence
    App -> ModerateText: moderateText(sentence)
    activate ModerateText
    ModerateText --> App: toxicity score
    deactivate ModerateText
    
    App -> UI: update sentence lists with score
    UI -> User: render sentence + toxicity level bar
end

deactivate App
deactivate UI
@enduml
```

## Internal sequence diagrams

### generate()

![generate diagram](img/generate.png)
```plantuml
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

### importer(String)

![importer diagram](img/importer.png)
```plantuml
@startuml
participant WordList
participant WordUtil
participant FileReader
participant Scanner

WordList -> WordUtil: importer(file_path)
activate WordUtil 
WordUtil -> FileReader: create a reader with the file path
activate FileReader
FileReader --> WordUtil: return reader
deactivate FileReader
WordUtil -> Scanner: creater a scanner with reader
activate Scanner
Scanner --> WordUtil: return scanner
deactivate Scanner
loop for each line of scanner
    alt end of file
        WordUtil -> Scanner: extracts the word
        activate Scanner 
        Scanner -> WordUtil: returns word
        deactivate Scanner
    end
end
WordUtil --> WordList: return list of words
deactivate WordUtil
@enduml
```

## Design class model
![Design class model](img/DesignClassModel.png)

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
