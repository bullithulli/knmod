# Getting Started

### Prerequisites to build on your own:

Linux(or Windows), Java21-jdk, Java21-jre, Maven, dos2unix

Download the project and build the project

```diff
git clone https://github.com/bullithulli/knmod
cd knmod
mvn clean install
```

### Basic Usage/Example

Either build on your own or download from the release Page

```
# for knmod
cd target/
java -Xss1g -Xmx4g -jar modder-2.jar --feature=KNMOD --file=/some/path/to/your/game/script.rpy --startModFromSymbol="label start"


# for labelReplace
java -jar modder-2.jar  --feature=LABEL_REPLACE --file=/tmp/script.rpy --indentType=TAB --indentSize=1  --patchFrom=/tmp/iscript.rpy --replaceBy="label1->label1patch,label2->label2Patch"


```

Advanced features:

```
No command-line arguments provided.
Usage: java -jar modder-2.jar [OPTIONS]
Options:
  -h, --help                Display help information
  -v, --version             Display version information
  --file=FILENAME           Specify a file
  --outfile=FILENAME        Destination output. Defaults to /tmp/out
  --feature=FEATURE_NAME    The Feature you want to use. Available, KNMOD,LABEL_LOOKUP, LABEL_REPLACE
                            KNMOD:          mandatory fields: --file; Optional fields: --outfile --forceDontKNModForStartsWith --forceKNModForStartsWith
                                            --startModFromSymbol=String         Don't process the lines till it starts with this symbol. Defaults to null
                                            --forceDontKNModForStartsWith=String               Don't KNMOD the symbols. Defaults to null
                                            --forceKNModForStartsWith=String                   did you find new symbols that need to be KNMODed. Add them here
                            LABEL_LOOKUP:   mandatory fields: --file --key; Optional fields: --removefromsource --stopOnNewlabel --stopOnNextLabelJump --followInnerJumps --followInnerCalls --ignoreLabels --followScreenCalls
                                            --key=STRING                        Label to Lookup
                                            --removefromsource=BOOLEAN          Erase label definition in source file if matches. Defaults to false
                                            --stopOnNewlabel=BOOLEAN            Stop lookup label once new label is found. Defaults to true
                                            --stopOnNextLabelJump=BOOLEAN       Stop lookup label once new jump is found within definition. Defaults to false
                                            --followInnerJumps=BOOLEAN          Continue lookups if innerJumps are found, and proceed same with innerJumps. Defaults to false
                                            --followInnerCalls=BOOLEAN          Continue lookups if innerCalls are found, and proceed same with innerCalls labels. Defaults to false
                                            --followScreenCalls=BOOLEAN         Continue lookups if innerCalls for screen are found, and proceed same with innerCalls labels. Defaults to false
                                            --ignoreLabels=label1,label2        List of Labels to ignore while processing. Defaults to []
                            LABEL_REPLACE:   mandatory fields: --file --patchFrom --replaceBy; Optional fields: --outfile --indentType --indentSize
                                             --patchFrom=/path/toFile           A patch rpy file where you want to patch the source file
                                             --replaceBy=LIST[STR->STR]         A list of labels you want to patch, eg. --replaceBy=labelA->labelPatchA,labelB->labelPatchB. Defaults to []
                                             --indentType=SPACE|TAB             Can be either Space or Tab. It informs the parser how the code is structured. Defaults to Space
                                             --indentSize=INT                   It says, how much spaces are there for single indent, supply this if you are passing --indentTyp=SPACE. Defaults to 4
                            TRANSLATE_RPY:   mandatory fields: --file --tlFile; Optional fields: --outfile --indentType --indentSize
                                             --tlFile=/path/toFile              A translation rpy file where it contains translations
                                             --indentType=SPACE|TAB             Can be either Space or Tab. It informs the parser how the code is structured. Defaults to Space
                                             --indentSize=INT                   It says, how much spaces are there for single indent, supply this if you are passing --indentTyp=SPACE. Defaults to 4
```

Alternatively, you can watch
these [videos](https://drive.google.com/drive/folders/1Hv6X9Uq4Av-iW701AJxpQCF5JPllnUPi?usp=sharing) to get started
