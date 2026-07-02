package com.example.data

data class AmharicSentence(
    val english: String,
    val correctWords: List<String>,
    val scrambledWords: List<String>,
    val translit: String,
    val finalSentence: String,
    val explanation: String
)

object SentenceData {
    val sentences = listOf(
        AmharicSentence(
            english = "I love cows. 🐄",
            correctWords = listOf("እኔ", "ላም", "እወዳለሁ።"),
            scrambledWords = listOf("እኔ", "ላም", "እወዳለሁ።", "ሆድ", "ውሻ"),
            translit = "Ine lam iwedalehu.",
            finalSentence = "እኔ ላም እወዳለሁ ።",
            explanation = "In Amharic, 'እኔ' is I, 'ላም' is cow, and 'እወዳለሁ' (love) goes at the end!"
        ),
        AmharicSentence(
            english = "The car is red. 🚗",
            correctWords = listOf("መኪናዋ", "ቀይ", "ናት።"),
            scrambledWords = listOf("መኪናዋ", "ቀይ", "ናት።", "ድመት", "እህት"),
            translit = "Mekinawa qey nat.",
            finalSentence = "መኪናዋ ቀይ ናት።",
            explanation = "Since car ('መኪናዋ') is feminine, we use 'ናት' (she is) instead of 'ነው'!"
        ),
        AmharicSentence(
            english = "The lion is big. 🦁",
            correctWords = listOf("አንበሳው", "ትልቅ", "ነው።"),
            scrambledWords = listOf("አንበሳው", "ትልቅ", "ነው።", "ወፍ", "ትንሽ"),
            translit = "Anbesaw tiliq new.",
            finalSentence = "አንበሳው ትልቅ ነው።",
            explanation = "Subject-Object-Verb order is fundamental: 'አንበሳው' (The lion) 'ትልቅ' (big) 'ነው' (is)!"
        ),
        AmharicSentence(
            english = "I like reading books. 📚",
            correctWords = listOf("መጽሐፍ", "ማንበብ", "እወዳለሁ።"),
            scrambledWords = listOf("መጽሐፍ", "ማንበብ", "እወዳለሁ።", "ውሃ", "መብላት"),
            translit = "Metsihaf manbeb iwedalehu.",
            finalSentence = "መጽሐፍ ማንበብ እወዳለሁ።",
            explanation = "Metsihaf (book) + manbeb (to read) + iwedalehu (I like) make up this fun sentence!"
        ),
        AmharicSentence(
            english = "I love mummy and daddy. 👨‍👩‍👧‍👦",
            correctWords = listOf("እማማንና", "አባባን", "እወዳለሁ።"),
            scrambledWords = listOf("እማማንና", "አባባን", "እወዳለሁ።", "ወንድም", "አሻንጉሊት"),
            translit = "Imamanina ababan iwedalehu.",
            finalSentence = "እማማንና አባባን እወዳለሁ።",
            explanation = "'እማማንና' (mummy and) + 'አባባን' (daddy) followed by 'እወዳለሁ' (I love)!"
        ),
        AmharicSentence(
            english = "The sun is hot. ☀️",
            correctWords = listOf("ፀሐይዋ", "ትኩስ", "ናት።"),
            scrambledWords = listOf("ፀሐይዋ", "ትኩስ", "ናት።", "ቀዝቃዛ", "ጨረቃ"),
            translit = "Tsehaywa tikus nat.",
            finalSentence = "ፀሐይዋ ትኩስ ናት።",
            explanation = "Sun ('ፀሐይዋ') is feminine in this context, so we pair it with 'ናት' (she is) and 'ትኩስ' (hot)!"
        ),
        AmharicSentence(
            english = "The bird is flying. 🐦",
            correctWords = listOf("ወፏ", "እየበረረች", "ናት።"),
            scrambledWords = listOf("ወፏ", "እየበረረች", "ናት።", "እየዋኘች", "ዓሣ"),
            translit = "Wofwa eyebererech nat.",
            finalSentence = "ወፏ እየበረረች ናት።",
            explanation = "Bird ('ወፏ') is flying ('እየበረረች') is feminine present continuous!"
        ),
        AmharicSentence(
            english = "I eat bananas. 🍌",
            correctWords = listOf("እኔ", "ሙዝ", "እበላለሁ።"),
            scrambledWords = listOf("እኔ", "ሙዝ", "እበላለሁ።", "እጠጣለሁ", "ወተት"),
            translit = "Ine muz ibelahelehu.",
            finalSentence = "እኔ ሙዝ እበላለሁ።",
            explanation = "Subject 'እኔ' (I), Object 'ሙዝ' (banana), Verb 'እበላለሁ' (eat) follows the SOV rule!"
        ),
        AmharicSentence(
            english = "The water is cold. 💧",
            correctWords = listOf("ውኃው", "ቀዝቃዛ", "ነው።"),
            scrambledWords = listOf("ውኃው", "ቀዝቃዛ", "ነው።", "ትኩስ", "ሻይ"),
            translit = "Wuhaw qezqaza new.",
            finalSentence = "ውኃው ቀዝቃዛ ነው።",
            explanation = "'ውኃው' (The water) is masculine, so it goes with 'ቀዝቃዛ' (cold) and 'ነው' (is)!"
        ),
        AmharicSentence(
            english = "My house is big. 🏠",
            correctWords = listOf("ቤቴ", "ትልቅ", "ነው።"),
            scrambledWords = listOf("ቤቴ", "ትልቅ", "ነው።", "ትንሽ", "መኪና"),
            translit = "Bete tiliq new.",
            finalSentence = "ቤቴ ትልቅ ነው።",
            explanation = "'ቤቴ' (My house) + 'ትልቅ' (big) + 'ነው' (is) makes a complete statement!"
        ),
        AmharicSentence(
            english = "He goes to school. 🏫",
            correctWords = listOf("እሱ", "ትምህርት", "ቤት", "ይሄዳል።"),
            scrambledWords = listOf("እሱ", "ትምህርት", "ቤት", "ይሄዳል።", "ይጫወታል", "ሱቅ"),
            translit = "Isu timihirt bet yihedal.",
            finalSentence = "እሱ ትምህርት ቤት ይሄዳል።",
            explanation = "'እሱ' is He, 'ትምህርት ቤት' is school, and 'ይሄዳለ' is goes. The verb is at the end!"
        ),
        AmharicSentence(
            english = "She has a cat. 🐱",
            correctWords = listOf("እሷ", "ድመት", "አላት።"),
            scrambledWords = listOf("እሷ", "ድመት", "አላት።", "አለው", "ውሻ"),
            translit = "Iswa dimet alat.",
            finalSentence = "እሷ ድመት አላት።",
            explanation = "'እሷ' (She) has 'ድመት' (cat) 'አላት' (she has)!"
        ),
        AmharicSentence(
            english = "The flower is beautiful. 🌸",
            correctWords = listOf("አበባው", "ቆንጆ", "ነው።"),
            scrambledWords = listOf("አበባው", "ቆንጆ", "ነው።", "መጥፎ", "ድንጋይ"),
            translit = "Abebaw qonjo new.",
            finalSentence = "አበባው ቆንጆ ነው።",
            explanation = "Flower ('አበባው') is paired with 'ቆንጆ' (beautiful) and 'ነው' (is)!"
        ),
        AmharicSentence(
            english = "We drink milk. 🥛",
            correctWords = listOf("እኛ", "ወተት", "እንጠጣለን።"),
            scrambledWords = listOf("እኛ", "ወተት", "እንጠጣለን።", "እንበላለን", "ዳቦ"),
            translit = "Inya wetet unintetalen.",
            finalSentence = "እኛ ወተት እንጠጣለን።",
            explanation = "We ('እኛ') drink ('እንጠጣለን') is first-person plural!"
        ),
        AmharicSentence(
            english = "I have a ball. ⚽",
            correctWords = listOf("እኔ", "ኳስ", "አለኝ።"),
            scrambledWords = listOf("እኔ", "ኳስ", "አለኝ።", "አላት", "ብዕር"),
            translit = "Ine qwas aleygn.",
            finalSentence = "እኔ ኳስ አለኝ።",
            explanation = "'እኔ' (I) + 'ኳስ' (ball) + 'አለኝ' (I have)!"
        ),
        AmharicSentence(
            english = "My brother is tall. 👦",
            correctWords = listOf("ወንድሜ", "ረጅም", "ነው።"),
            scrambledWords = listOf("ወንድሜ", "ረጅም", "ነው።", "አጭር", "እህት"),
            translit = "Wendime rejim new.",
            finalSentence = "ወንድሜ ረጅም ነው።",
            explanation = "My brother ('ወንድሜ') is masculine, so we use 'ነው' (he is) with 'ረጅም' (tall)!"
        ),
        AmharicSentence(
            english = "The sky is blue. ☁️",
            correctWords = listOf("ሰማዩ", "ሰማያዊ", "ነው።"),
            scrambledWords = listOf("ሰማዩ", "ሰማያዊ", "ነው።", "ቀይ", "ምድር"),
            translit = "Semayu semayawi new.",
            finalSentence = "ሰማዩ ሰማያዊ ነው።",
            explanation = "Sky ('ሰማዩ') + 'ሰማያዊ' (blue) + 'ነው' (is)!"
        ),
        AmharicSentence(
            english = "Today is warm. 🌡️",
            correctWords = listOf("ዛሬ", "ሞቃት", "ነው።"),
            scrambledWords = listOf("ዛሬ", "ሞቃት", "ነው።", "ቀዝቃዛ", "ትናንት"),
            translit = "Zare moqat new.",
            finalSentence = "ዛሬ ሞቃት ነው።",
            explanation = "'ዛሬ' means Today, 'ሞቃት' means warm, and 'ነው' means is!"
        ),
        AmharicSentence(
            english = "The tree is green. 🌳",
            correctWords = listOf("ዛፉ", "አረንጓዴ", "ነው።"),
            scrambledWords = listOf("ዛፉ", "አረንጓዴ", "ነው።", "ቢጫ", "ቅጠል"),
            translit = "Zafu arengwade new.",
            finalSentence = "ዛፉ አረንጓዴ ነው።",
            explanation = "'ዛፉ' (The tree) + 'አረንጓዴ' (green) + 'ነው' (is)!"
        ),
        AmharicSentence(
            english = "My sister is happy. 👧",
            correctWords = listOf("እህቴ", "ደስተኛ", "ናት።"),
            scrambledWords = listOf("እህቴ", "ደስተኛ", "ናት።", "ያዘነች", "ወንድም"),
            translit = "Ihite destenya nat.",
            finalSentence = "እህቴ ደስተኛ ናት።",
            explanation = "My sister ('እህቴ') is feminine, so she is ('ናት') happy ('ደስተኛ')!"
        ),
        AmharicSentence(
            english = "The coffee is tasty. ☕",
            correctWords = listOf("ቡናው", "ጣፋጭ", "ነው።"),
            scrambledWords = listOf("ቡናው", "ጣፋጭ", "ነው።", "መር", "ጨው"),
            translit = "Bunaw tafach new.",
            finalSentence = "ቡናው ጣፋጭ ነው።",
            explanation = "'ቡናው' (The coffee) is masculine, so it is ('ነው') tasty/sweet ('ጣፋጭ')!"
        ),
        AmharicSentence(
            english = "They are playing. ⚽",
            correctWords = listOf("እነሱ", "እየተጫወቱ", "ናቸው።"),
            scrambledWords = listOf("እነሱ", "እየተጫወቱ", "ናቸው።", "እየተኙ", "ትምህርት"),
            translit = "Inesu eyetechawetu nachew.",
            finalSentence = "እነሱ እየተጫወቱ ናቸው።",
            explanation = "They ('እነሱ') are playing ('እየተጫወቱ') are ('ናቸው')!"
        ),
        AmharicSentence(
            english = "I am a child. 👶",
            correctWords = listOf("እኔ", "ልጅ", "ነኝ።"),
            scrambledWords = listOf("እኔ", "ልጅ", "ነኝ።", "ነው", "አዋቂ"),
            translit = "Ine lij negn.",
            finalSentence = "እኔ ልጅ ነኝ።",
            explanation = "'እኔ' (I) + 'ልጅ' (child) + 'ነኝ' (I am)!"
        ),
        AmharicSentence(
            english = "Dogs are loyal. 🐶",
            correctWords = listOf("ውሾች", "ታማኝ", "ናቸው።"),
            scrambledWords = listOf("ውሾች", "ታማኝ", "ናቸው።", "ክፉዎች", "ድመቶች"),
            translit = "Wushoch tamagn nachew.",
            finalSentence = "ውሾች ታማኝ ናቸው።",
            explanation = "Dogs ('ውሾች') are loyal ('ታማኝ') are ('ናቸው')!"
        ),
        AmharicSentence(
            english = "Fish live in water. 🐟",
            correctWords = listOf("ዓሣ", "ውኃ", "ውስጥ", "ይኖራል።"),
            scrambledWords = listOf("ዓሣ", "ውኃ", "ውስጥ", "ይኖራል።", "የመሬት", "ይበርራል"),
            translit = "Asa wuha wist yinoral.",
            finalSentence = "ዓሣ ውኃ ውስጥ ይኖራል።",
            explanation = "Fish ('ዓሣ') lives ('ይኖራል') inside ('ውስጥ') water ('ውኃ')!"
        ),
        AmharicSentence(
            english = "The elephant is heavy. 🐘",
            correctWords = listOf("ዝሆኑ", "ከባድ", "ነው።"),
            scrambledWords = listOf("ዝሆኑ", "ከባድ", "ነው።", "ቀላል", "ወፍ"),
            translit = "Zihonu kebad new.",
            finalSentence = "ዝሆኑ ከባድ ነው።",
            explanation = "The elephant ('ዝሆኑ') is ('ነው') heavy ('ከባድ')!"
        ),
        AmharicSentence(
            english = "I want bread. 🍞",
            correctWords = listOf("ዳቦ", "እፈልጋለሁ።"),
            scrambledWords = listOf("ዳቦ", "እፈልጋለሁ።", "እጠላለሁ", "ውሃ"),
            translit = "Dabo ifeligalehu.",
            finalSentence = "ዳቦ እፈልጋለሁ።",
            explanation = "I want ('እፈልጋለሁ') bread ('ዳቦ')!"
        ),
        AmharicSentence(
            english = "The moon is round. 🌕",
            correctWords = listOf("ጨረቃዋ", "ክብ", "ናት።"),
            scrambledWords = listOf("ጨረቃዋ", "ክብ", "ናት።", "ረጅም", "ፀሐይ"),
            translit = "Chereqawa kib nat.",
            finalSentence = "ጨረቃዋ ክብ ናት።",
            explanation = "The moon ('ጨረቃዋ') is feminine, so she is ('ናት') round ('ክብ')!"
        ),
        AmharicSentence(
            english = "I write with a pencil. ✏️",
            correctWords = listOf("በእርሳስ", "እጽፋለሁ።"),
            scrambledWords = listOf("በእርሳስ", "እጽፋለሁ።", "በደብተር", "አነባለሁ"),
            translit = "Be'ersas itsifalehu.",
            finalSentence = "በእርሳስ እጽፋለሁ።",
            explanation = "I write ('እጽፋለሁ') using ('በ') pencil ('እርሳስ')!"
        ),
        AmharicSentence(
            english = "He reads a book. 📖",
            correctWords = listOf("እሱ", "መጽሐፍ", "ያነባል።"),
            scrambledWords = listOf("እሱ", "መጽሐፍ", "ያነባል።", "ይጽፋል", "ሰሌዳ"),
            translit = "Isu metsihaf yanebal.",
            finalSentence = "እሱ መጽሐፍ ያነባል።",
            explanation = "He ('እሱ') reads ('ያነባለ') book ('መጽሐፍ')!"
        ),
        AmharicSentence(
            english = "She sings beautifully. 🎤",
            correctWords = listOf("እሷ", "በጥሩ ሁኔታ", "ትዘፍናለች።"),
            scrambledWords = listOf("እሷ", "በጥሩ ሁኔታ", "ትዘፍናለች።", "ይጫወታል", "ቅስ"),
            translit = "Iswa betiru huneta tizefinaleche.",
            finalSentence = "እሷ በጥሩ ሁኔታ ትዘፍናለች።",
            explanation = "She ('እሷ') sings ('ትዘፍናለች') beautifully ('በጥሩ ሁኔታ')!"
        ),
        AmharicSentence(
            english = "The baby is sleeping. 👶",
            correctWords = listOf("ህፃኑ", "እየተኛ", "ነው።"),
            scrambledWords = listOf("ህፃኑ", "እየተኛ", "ነው።", "እየሮጠ", "መጫወቻ"),
            translit = "Hitsanu eyetenya new.",
            finalSentence = "ህፃኑ እየተኛ ነው።",
            explanation = "The baby ('ህፃኑ') is sleeping ('እየተኛ ነው')!"
        ),
        AmharicSentence(
            english = "Rain is falling. 🌧️",
            correctWords = listOf("ዝናብ", "እየዘነበ", "ነው።"),
            scrambledWords = listOf("ዝናብ", "እየዘነበ", "ነው።", "እየነፈሰ", "ፀሐይ"),
            translit = "Zinab eyezenebe new.",
            finalSentence = "ዝናብ እየዘነበ ነው።",
            explanation = "Rain ('ዝናብ') is falling/raining ('እየዘነበ ነው')!"
        ),
        AmharicSentence(
            english = "My dad is strong. 💪",
            correctWords = listOf("አባቴ", "ጠንካራ", "ነው።"),
            scrambledWords = listOf("አባቴ", "ጠንካራ", "ነው።", "ደካማ", "እናቴ"),
            translit = "Abate tenkara new.",
            finalSentence = "አባቴ ጠንካራ ነው።",
            explanation = "My dad ('አባቴ') is ('ነው') strong ('ጠንካራ')!"
        ),
        AmharicSentence(
            english = "My mom is kind. ❤️",
            correctWords = listOf("እናቴ", "ደግ", "ናት።"),
            scrambledWords = listOf("እናቴ", "ደግ", "ናት።", "ክፉ", "አባቴ"),
            translit = "Inate deg nat.",
            finalSentence = "እናቴ ደግ ናት።",
            explanation = "My mom ('እናቴ') is feminine, so she is ('ናት') kind ('ደግ')!"
        ),
        AmharicSentence(
            english = "I like sweet honey. 🍯",
            correctWords = listOf("ጣፋጭ", "ማር", "እወዳለሁ።"),
            scrambledWords = listOf("ጣፋጭ", "ማር", "እወዳለሁ።", "መራራ", "ሎሚ"),
            translit = "Tafach mar iwedalehu.",
            finalSentence = "ጣፋጭ ማር እወዳለሁ።",
            explanation = "I like ('እወዳለሁ') sweet ('ጣፋጭ') honey ('ማር')!"
        ),
        AmharicSentence(
            english = "The orange is sweet. 🍊",
            correctWords = listOf("ብርቱካኑ", "ጣፋጭ", "ነው።"),
            scrambledWords = listOf("ብርቱካኑ", "ጣፋጭ", "ነው።", "ኮምጣጣ", "ሙዝ"),
            translit = "Birtukanu tafach new.",
            finalSentence = "ብርቱካኑ ጣፋጭ ነው።",
            explanation = "The orange ('ብርቱካኑ') is ('ነው') sweet ('ጣፋጭ')!"
        ),
        AmharicSentence(
            english = "Apples are red. 🍎",
            correctWords = listOf("ፖም", "ቀይ", "ነው።"),
            scrambledWords = listOf("ፖም", "ቀይ", "ነው።", "አረንጓዴ", "ወይን"),
            translit = "Pom qey new.",
            finalSentence = "ፖም ቀይ ነው።",
            explanation = "Apple ('ፖም') is red ('ቀይ')!"
        ),
        AmharicSentence(
            english = "Open the door. 🚪",
            correctWords = listOf("በሩን", "ክፈተው።"),
            scrambledWords = listOf("በሩን", "ክፈተው።", "ዝጋው", "መስኮቱን"),
            translit = "Berun kifetew.",
            finalSentence = "በሩን ክፈተው።",
            explanation = "Open ('ክፈተው') the door ('በሩን')!"
        ),
        AmharicSentence(
            english = "Close the window. 🪟",
            correctWords = listOf("መስኮቱን", "ዝጋው።"),
            scrambledWords = listOf("መስኮቱን", "ዝጋው።", "ክፈተው", "በሩን"),
            translit = "Meskotun zigaw.",
            finalSentence = "መስኮቱን ዝጋው።",
            explanation = "Close ('ዝጋው') the window ('መስኮቱን')!"
        ),
        AmharicSentence(
            english = "Wash your hands. 🧼",
            correctWords = listOf("እጅህን", "ታጠብ።"),
            scrambledWords = listOf("እጅህን", "ታጠብ።", "እግርህን", "ብላ"),
            translit = "Ejihin tateb.",
            finalSentence = "እጅህን ታጠብ።",
            explanation = "Wash ('ታጠብ') your hand ('እጅህን')!"
        ),
        AmharicSentence(
            english = "I brush my teeth. 🪥",
            correctWords = listOf("ጥርሴን", "እቦርሻለሁ።"),
            scrambledWords = listOf("ጥርሴን", "እቦርሻለሁ።", "እጠበዋለሁ", "ፊት"),
            translit = "Tirsien iborshalehu.",
            finalSentence = "ጥርሴን እቦርሻለሁ።",
            explanation = "I brush ('እቦርሻለሁ') my teeth ('ጥርሴን')!"
        ),
        AmharicSentence(
            english = "Sit on the chair. 🪑",
            correctWords = listOf("ወንበር", "ላይ", "ተቀመጥ።"),
            scrambledWords = listOf("ወንበር", "ላይ", "ተቀመጥ።", "ጠረጴዛ", "ተኛ"),
            translit = "Wenber lay teqemet.",
            finalSentence = "ወንበር ላይ ተቀመጥ።",
            explanation = "Sit ('ተቀመጥ') on ('ላይ') the chair ('ወንበር')!"
        ),
        AmharicSentence(
            english = "Clean the table. 🧹",
            correctWords = listOf("ጠረጴዛውን", "አጽዳው።"),
            scrambledWords = listOf("ጠረጴዛውን", "አጽዳው።", "ጣለው", "መሬት"),
            translit = "Terepezawun atsidaw.",
            finalSentence = "ጠረጴዛውን አጽዳው።",
            explanation = "Clean ('አጽዳው') the table ('ጠረጴዛውን')!"
        ),
        AmharicSentence(
            english = "The tea is hot. 🍵",
            correctWords = listOf("ሻዩ", "ትኩስ", "ነው።"),
            scrambledWords = listOf("ሻዩ", "ትኩስ", "ነው።", "ቀዝቃዛ", "ቡና"),
            translit = "Shayu tikus new.",
            finalSentence = "ሻዩ ትኩስ ነው።",
            explanation = "The tea ('ሻዩ') is ('ነው') hot ('ትኩስ')!"
        ),
        AmharicSentence(
            english = "I hear music. 🎶",
            correctWords = listOf("ሙዚቃ", "እሰማለሁ።"),
            scrambledWords = listOf("ሙዚቃ", "እሰማለሁ።", "አያለሁ", "ፊልም"),
            translit = "Muziqa isemalehu.",
            finalSentence = "ሙዚቃ እሰማለሁ።",
            explanation = "I hear ('እሰማለሁ') music ('ሙዚቃ')!"
        ),
        AmharicSentence(
            english = "The pencil is sharp. ✏️",
            correctWords = listOf("እርሳሱ", "ስል", "ነው።"),
            scrambledWords = listOf("እርሳሱ", "ስል", "ነው።", "ደደብ", "ደብተር"),
            translit = "Irsasu sil new.",
            finalSentence = "እርሳሱ ስል ነው።",
            explanation = "The pencil ('እርሳሱ') is ('ነው') sharp ('ስል')!"
        ),
        AmharicSentence(
            english = "This is my room. 🛏️",
            correctWords = listOf("ይህ", "ክፍሌ", "ነው።"),
            scrambledWords = listOf("ይህ", "ክፍሌ", "ነው።", "ናት", "ቤት"),
            translit = "Yih kiflie new.",
            finalSentence = "ይህ ክፍሌ ነው።",
            explanation = "This ('ይህ') is ('ነው') my room ('ክፍሌ')!"
        ),
        AmharicSentence(
            english = "We love Ethiopia. 🇪🇹",
            correctWords = listOf("እኛ", "ኢትዮጵያን", "እንወዳለን።"),
            scrambledWords = listOf("እኛ", "ኢትዮጵያን", "እንወዳለን።", "ይወዳሉ", "ሀገር"),
            translit = "Inya Ityopyan uninwedalen.",
            finalSentence = "እኛ ኢትዮጵያን እንወዳለን።",
            explanation = "We ('እኛ') love ('እንወዳለን') Ethiopia ('ኢትዮጵያን')!"
        ),
        AmharicSentence(
            english = "The bag is light. 🎒",
            correctWords = listOf("ቦርሳው", "ቀላል", "ነው።"),
            scrambledWords = listOf("ቦርሳው", "ቀላል", "ነው።", "ከባድ", "ድንጋይ"),
            translit = "Borsaw qelal new.",
            finalSentence = "ቦርሳው ቀላል ነው።",
            explanation = "The bag ('ቦርሳው') is ('ነው') light ('ቀላል')!"
        ),
        AmharicSentence(
            english = "Where is the cat? 🐱",
            correctWords = listOf("ድመቷ", "የታለች?"),
            scrambledWords = listOf("ድመቷ", "የታለች?", "የት ነው", "ውሻው"),
            translit = "Dimetwa yetalech?",
            finalSentence = "ድመቷ የታለች?",
            explanation = "Where is ('የታለች') the cat ('ድመቷ')? She is feminine!"
        ),
        AmharicSentence(
            english = "The baby is laughing. 😄",
            correctWords = listOf("ህፃኑ", "እየሳቀ", "ነው።"),
            scrambledWords = listOf("ህፃኑ", "እየሳቀ", "ነው።", "እያለቀሰ", "ወተት"),
            translit = "Hitsanu eyesaqe new.",
            finalSentence = "ህፃኑ እየሳቀ ነው።",
            explanation = "The baby ('ህፃኑ') is ('ነው') laughing ('እየሳቀ')!"
        ),
        AmharicSentence(
            english = "My shoes are new. 👟",
            correctWords = listOf("ጫማዬ", "አዲስ", "ነው።"),
            scrambledWords = listOf("ጫማዬ", "አዲስ", "ነው።", "አሮጌ", "ልብስ"),
            translit = "Chamaye adis new.",
            finalSentence = "ጫማዬ አዲስ ነው።",
            explanation = "My shoes ('ጫማዬ') is/are ('ነው') new ('አዲስ')!"
        ),
        AmharicSentence(
            english = "I wear a hat. 🎩",
            correctWords = listOf("ቆብ", "እለብሳለሁ።"),
            scrambledWords = listOf("ቆብ", "እለብሳለሁ።", "እጫወታለሁ", "ኳስ"),
            translit = "Qob ilebisalehu.",
            finalSentence = "ቆብ እለብሳለሁ።",
            explanation = "I wear ('እለብሳለሁ') a hat ('ቆብ')!"
        ),
        AmharicSentence(
            english = "The soup is warm. 🍲",
            correctWords = listOf("ሾርባው", "ሞቃት", "ነው።"),
            scrambledWords = listOf("ሾርባው", "ሞቃት", "ነው።", "ቀዝቃዛ", "ውሃ"),
            translit = "Shorbaw moqat new.",
            finalSentence = "ሾርባው ሞቃት ነው።",
            explanation = "The soup ('ሾርባው') is ('ነው') warm/hot ('ሞቃት')!"
        ),
        AmharicSentence(
            english = "I draw a circle. ⭕",
            correctWords = listOf("ክብ", "እስላለሁ።"),
            scrambledWords = listOf("ክብ", "እስላለሁ።", "እጽፋለሁ", "ቃል"),
            translit = "Kib isilalehu.",
            finalSentence = "ክብ እስላለሁ።",
            explanation = "I draw ('እስላለሁ') a circle ('ክብ')!"
        ),
        AmharicSentence(
            english = "He kicks the ball. ⚽",
            correctWords = listOf("እሱ", "ኳሱን", "ይመታል።"),
            scrambledWords = listOf("እሱ", "ኳሱን", "ይመታል።", "ይበላል", "ሜዳ"),
            translit = "Isu qwasun yimetal.",
            finalSentence = "እሱ ኳሱን ይመታል።",
            explanation = "He ('እሱ') kicks ('ይመታለ') the ball ('ኳሱን')!"
        ),
        AmharicSentence(
            english = "The butterfly is beautiful. 🦋",
            correctWords = listOf("ቢራቢሮዋ", "ቆንጆ", "ናት።"),
            scrambledWords = listOf("ቢራቢሮዋ", "ቆንጆ", "ናት።", "ትልቅ", "ንብ"),
            translit = "Birabirowa qonjo nat.",
            finalSentence = "ቢራቢሮዋ ቆንጆ ናት።",
            explanation = "The butterfly ('ቢራቢሮዋ') is feminine, so she is ('ናት') beautiful ('ቆንጆ')!"
        ),
        AmharicSentence(
            english = "We eat dinner. 🍽️",
            correctWords = listOf("እኛ", "እራት", "እንበላለን።"),
            scrambledWords = listOf("እኛ", "እራት", "እንበላለን።", "ምሳ", "እንጠጣለን"),
            translit = "Inya irat uninbelalen.",
            finalSentence = "እኛ እራት እንበላለን።",
            explanation = "We ('እኛ') eat ('እንበላለን') dinner ('እራት')!"
        ),
        AmharicSentence(
            english = "I drink orange juice. 🥤",
            correctWords = listOf("የብርቱካን", "ጁስ", "እጠጣለሁ።"),
            scrambledWords = listOf("የብርቱካን", "ጁስ", "እጠጣለሁ።", "ውሃ", "እበላለሁ"),
            translit = "Yebirtukan jus itetalehu.",
            finalSentence = "የብርቱካን ጁስ እጠጣለሁ።",
            explanation = "I drink ('እጠጣለሁ') orange ('የብርቱካን') juice ('ጁስ')!"
        ),
        AmharicSentence(
            english = "The grass is green. 🌱",
            correctWords = listOf("ሳሩ", "አረንጓዴ", "ነው።"),
            scrambledWords = listOf("ሳሩ", "አረንጓዴ", "ነው።", "ቀይ", "አበባ"),
            translit = "Saru arengwade new.",
            finalSentence = "ሳሩ አረንጓዴ ነው።",
            explanation = "The grass ('ሳሩ') is ('ነው') green ('አረንጓዴ')!"
        ),
        AmharicSentence(
            english = "I see a horse. 🐎",
            correctWords = listOf("ፈረስ", "አያለሁ።"),
            scrambledWords = listOf("ፈረስ", "አያለሁ።", "እሰማለሁ", "በግ"),
            translit = "Feres ayalehu.",
            finalSentence = "ፈረስ አያለሁ።",
            explanation = "I see ('አያለሁ') a horse ('ፈረስ')!"
        ),
        AmharicSentence(
            english = "The stars are shining. ⭐",
            correctWords = listOf("ከዋክብቱ", "እያበሩ", "ናቸው።"),
            scrambledWords = listOf("ከዋክብቱ", "እያበሩ", "ናቸው።", "ደመና", "እየዘነቡ"),
            translit = "Kewakibtu eyaberu nachew.",
            finalSentence = "ከዋክብቱ እያበሩ ናቸው።",
            explanation = "The stars ('ከዋክብቱ') are ('ናቸው') shining ('እያበሩ')!"
        ),
        AmharicSentence(
            english = "It is a sunny day. ☀️",
            correctWords = listOf("ፀሐያማ", "ቀን", "ነው።"),
            scrambledWords = listOf("ፀሐያማ", "ቀን", "ነው።", "ዝናባማ", "ሌሊት"),
            translit = "Tsehayama qen new.",
            finalSentence = "ፀሐያማ ቀን ነው።",
            explanation = "It is a sunny ('ፀሐያማ') day ('ቀን')!"
        ),
        AmharicSentence(
            english = "I have five fingers. 🖐️",
            correctWords = listOf("አምስት", "ጣቶች", "አሉኝ።"),
            scrambledWords = listOf("አምስት", "ጣቶች", "አሉኝ።", "አስር", "እግሮች"),
            translit = "Amist tatoch alugn.",
            finalSentence = "አምስት ጣቶች አሉኝ።",
            explanation = "I have ('አሉኝ') five ('አምስት') fingers ('ጣቶች')!"
        ),
        AmharicSentence(
            english = "The clock is ticking. ⏰",
            correctWords = listOf("ሰዓቱ", "እየሰራ", "ነው።"),
            scrambledWords = listOf("ሰዓቱ", "እየሰራ", "ነው።", "ቆሟል", "መብራት"),
            translit = "Se'atu eyesera new.",
            finalSentence = "ሰዓቱ እየሰራ ነው።",
            explanation = "The clock ('ሰዓቱ') is working/ticking ('እየሰራ ነው')!"
        ),
        AmharicSentence(
            english = "She is reading stories. 📖",
            correctWords = listOf("እሷ", "ታሪክ", "እያነበበች", "ናት።"),
            scrambledWords = listOf("እሷ", "ታሪክ", "እያነበበች", "ናት።", "እየጻፈች", "ደብተር"),
            translit = "Iswa tarik eyanebebech nat.",
            finalSentence = "እሷ ታሪክ እያነበበች ናት።",
            explanation = "She ('እሷ') is reading ('እያነበበች ናት') a story ('ታሪክ')!"
        ),
        AmharicSentence(
            english = "We are happy children. 👧",
            correctWords = listOf("እኛ", "ደስተኛ", "ልጆች", "ነን።"),
            scrambledWords = listOf("እኛ", "ደስተኛ", "ልጆች", "ነን።", "አዋቂዎች", "ናቸው"),
            translit = "Inya destenya lijoch nen.",
            finalSentence = "እኛ ደስተኛ ልጆች ነን።",
            explanation = "We ('እኛ') are ('ነን') happy ('ደስተኛ') children ('ልጆች')!"
        ),
        AmharicSentence(
            english = "I like to jump. 🦘",
            correctWords = listOf("መዝለል", "እወዳለሁ።"),
            scrambledWords = listOf("መዝለል", "እወዳለሁ።", "መሮጥ", "መተኛት"),
            translit = "Mezlel iwedalehu.",
            finalSentence = "መዝለል እወዳለሁ።",
            explanation = "I like ('እወዳለሁ') to jump ('መዝለል')!"
        ),
        AmharicSentence(
            english = "The boat is on water. ⛵",
            correctWords = listOf("ጀልባዋ", "ውኃ", "ላይ", "ናት።"),
            scrambledWords = listOf("ጀልባዋ", "ውኃ", "ላይ", "ናት።", "መሬት", "አየር"),
            translit = "Jelbawa wuha lay nat.",
            finalSentence = "ጀልባዋ ውኃ ላይ ናት።",
            explanation = "The boat ('ጀልባዋ') is feminine, so she is ('ናት') on ('ላይ') water ('ውኃ')!"
        ),
        AmharicSentence(
            english = "I hear the bird sing. 🐦",
            correctWords = listOf("ወፏ", "ስትዘምር", "እሰማለሁ።"),
            scrambledWords = listOf("ወፏ", "ስትዘምር", "እሰማለሁ።", "ሲጮህ", "ውሻ"),
            translit = "Wofwa sitzemir isemalehu.",
            finalSentence = "ወፏ ስትዘምር እሰማለሁ።",
            explanation = "I hear ('እሰማለሁ') the bird ('ወፏ') singing ('ስትዘምር')!"
        ),
        AmharicSentence(
            english = "The book has pictures. 🖼️",
            correctWords = listOf("መጽሐፉ", "ስዕሎች", "አሉት።"),
            scrambledWords = listOf("መጽሐፉ", "ስዕሎች", "አሉት።", "ቃላት", "የለውም"),
            translit = "Metsihafu siloch alut.",
            finalSentence = "መጽሐፉ ስዕሎች አሉት።",
            explanation = "The book ('መጽሐፉ') has ('አሉት') pictures ('ስዕሎች')!"
        ),
        AmharicSentence(
            english = "My grandmother is sweet. 👵",
            correctWords = listOf("አያቴ", "ደግ", "ናት።"),
            scrambledWords = listOf("አያቴ", "ደግ", "ናት።", "ክፉ", "እናት"),
            translit = "Ayate deg nat.",
            finalSentence = "አያቴ ደግ ናት።",
            explanation = "My grandmother ('አያቴ') is feminine, so she is ('ናት') kind ('ደግ')!"
        ),
        AmharicSentence(
            english = "I brush my hair. 🪮",
            correctWords = listOf("ፀጉሬን", "እበጥራለሁ።"),
            scrambledWords = listOf("ፀጉሬን", "እበጥራለሁ።", "እታጠባለሁ", "እጅ"),
            translit = "Tseguren ibetiralehu.",
            finalSentence = "ፀጉሬን እበጥራለሁ።",
            explanation = "I brush ('እበጥራለሁ') my hair ('ፀጉሬን')!"
        ),
        AmharicSentence(
            english = "The elephant is grey. 🐘",
            correctWords = listOf("ዝሆኑ", "ግራጫ", "ነው።"),
            scrambledWords = listOf("ዝሆኑ", "ግራጫ", "ነው።", "ነጭ", "ቀይ"),
            translit = "Zihonu giracha new.",
            finalSentence = "ዝሆኑ ግራጫ ነው።",
            explanation = "The elephant ('ዝሆኑ') is ('ነው') grey ('ግራጫ')!"
        ),
        AmharicSentence(
            english = "I want to sleep. 🛌",
            correctWords = listOf("መተኛት", "እፈልጋለሁ።"),
            scrambledWords = listOf("መተኛት", "እፈልጋለሁ።", "መጫወት", "መብላት"),
            translit = "Metenyat ifeligalehu.",
            finalSentence = "መተኛት እፈልጋለሁ።",
            explanation = "I want ('እፈልጋለሁ') to sleep ('መተኛት')!"
        ),
        AmharicSentence(
            english = "The rabbit is fast. 🐇",
            correctWords = listOf("ጥንቸሏ", "ፈጣን", "ናት።"),
            scrambledWords = listOf("ጥንቸሏ", "ፈጣን", "ናት።", "ቀስተኛ", "ኤሊ"),
            translit = "Tincheluwa fetan nat.",
            finalSentence = "ጥንቸሏ ፈጣን ናት።",
            explanation = "The rabbit ('ጥንቸሏ') is feminine, so she is ('ናት') fast ('ፈጣን')!"
        ),
        AmharicSentence(
            english = "I eat sweet apples. 🍎",
            correctWords = listOf("ጣፋጭ", "ፖም", "እበላለሁ።"),
            scrambledWords = listOf("ጣፋጭ", "ፖም", "እበላለሁ።", "ኮምጣጣ", "ሽንኩርት"),
            translit = "Tafach pom ibelalehu.",
            finalSentence = "ጣፋጭ ፖም እበላለሁ።",
            explanation = "I eat ('እበላለሁ') sweet ('ጣፋጭ') apple ('ፖም')!"
        ),
        AmharicSentence(
            english = "We love our teacher. 👩‍🏫",
            correctWords = listOf("እኛ", "አስተማሪያችንን", "እንወዳለን።"),
            scrambledWords = listOf("እኛ", "አስተማሪያችንን", "እንወዳለን።", "ክፍላችንን", "ይጠላሉ"),
            translit = "Inya astemariyachinin uninwedalen.",
            finalSentence = "እኛ አስተማሪያችንን እንወዳለን።",
            explanation = "We ('እኛ') love ('እንወዳለን') our teacher ('አስተማሪያችንን')!"
        ),
        AmharicSentence(
            english = "He rides a bicycle. 🚲",
            correctWords = listOf("እሱ", "ብስክሌት", "ይነዳል።"),
            scrambledWords = listOf("እሱ", "ብስክሌት", "ይነዳል።", "መኪና", "ይበላል"),
            translit = "Isu biskilet yinedal.",
            finalSentence = "እሱ ብስክሌት ይነዳል።",
            explanation = "He ('እሱ') rides ('ይነዳለ') a bicycle ('ብስክሌት')!"
        ),
        AmharicSentence(
            english = "She flies a kite. 🪁",
            correctWords = listOf("እሷ", "ካይት", "ታበራለች።"),
            scrambledWords = listOf("እሷ", "ካይት", "ታበራለች።", "ኳስ", "ትጫወታለች"),
            translit = "Iswa kayit taberalech.",
            finalSentence = "እሷ ካይት ታበራለች።",
            explanation = "She ('እሷ') flies ('ታበራለች') a kite ('ካይት')!"
        ),
        AmharicSentence(
            english = "My dog is white. 🐕",
            correctWords = listOf("ውሻዬ", "ነጭ", "ነው።"),
            scrambledWords = listOf("ውሻዬ", "ነጭ", "ነው።", "ጥቁር", "ቀይ"),
            translit = "Wushaye nech new.",
            finalSentence = "ውሻዬ ነጭ ነው።",
            explanation = "My dog ('ውሻዬ') is ('ነው') white ('ነጭ')!"
        ),
        AmharicSentence(
            english = "The box is empty. 📦",
            correctWords = listOf("ሣጥኑ", "ባዶ", "ነው።"),
            scrambledWords = listOf("ሣጥኑ", "ባዶ", "ነው።", "ሙሉ", "ከባድ"),
            translit = "Satinu bado new.",
            finalSentence = "ሣጥኑ ባዶ ነው።",
            explanation = "The box ('ሣጥኑ') is ('ነው') empty ('ባዶ')!"
        ),
        AmharicSentence(
            english = "I like warm tea. 🍵",
            correctWords = listOf("ሙቅ", "ሻይ", "እወዳለሁ።"),
            scrambledWords = listOf("ሙቅ", "ሻይ", "እወዳለሁ።", "ቀዝቃዛ", "ቡና"),
            translit = "Muq shayi iwedalehu.",
            finalSentence = "ሙቅ ሻይ እወዳለሁ።",
            explanation = "I like ('እወዳለሁ') hot/warm ('ሙቅ') tea ('ሻይ')!"
        ),
        AmharicSentence(
            english = "The frog hops. 🐸",
            correctWords = listOf("እንቁራሪቱ", "ይዘልላል።"),
            scrambledWords = listOf("እንቁራሪቱ", "ይዘልላል።", "ይዋኛል", "ይበርራል"),
            translit = "Inquraritu yizelilal.",
            finalSentence = "እንቁራሪቱ ይዘልላል።",
            explanation = "The frog ('እንቁራሪቱ') hops/jumps ('ይዘልላል')!"
        ),
        AmharicSentence(
            english = "My school is close. 🏫",
            correctWords = listOf("ትምህርት", "ቤቴ", "ቅርብ", "ነው።"),
            scrambledWords = listOf("ትምህርት", "ቤቴ", "ቅርብ", "ነው።", "ሩቅ", "ቤት"),
            translit = "Timihirt bete qirb new.",
            finalSentence = "ትምህርት ቤቴ ቅርብ ነው።",
            explanation = "My school ('ትምህርት ቤቴ') is ('ነው') close/near ('ቅርብ')!"
        ),
        AmharicSentence(
            english = "I have a blue pen. 🖊️",
            correctWords = listOf("ሰማያዊ", "ብዕር", "አለኝ።"),
            scrambledWords = listOf("ሰማያዊ", "ብዕር", "አለኝ።", "ቀይ", "እርሳስ"),
            translit = "Semayawi bir alergn.",
            finalSentence = "ሰማያዊ ብዕር አለኝ።",
            explanation = "I have ('አለኝ') a blue ('ሰማያዊ') pen ('ብዕር')!"
        ),
        AmharicSentence(
            english = "The window is open. 🪟",
            correctWords = listOf("መስኮቱ", "ክፍት", "ነው።"),
            scrambledWords = listOf("መስኮቱ", "ክፍት", "ነው።", "ዝግ", "በሩ"),
            translit = "Meskotu kift new.",
            finalSentence = "መስኮቱ ክፍት ነው።",
            explanation = "The window ('መስኮቱ') is ('ነው') open ('ክፍት')!"
        ),
        AmharicSentence(
            english = "He cooks tasty food. 🍳",
            correctWords = listOf("እሱ", "ጣፋጭ", "ምግብ", "ይሰራል።"),
            scrambledWords = listOf("እሱ", "ጣፋጭ", "ምግብ", "ይሰራል።", "መጥፎ", "ውሃ"),
            translit = "Isu tafach migib yiseral.",
            finalSentence = "እሱ ጣፋጭ ምግብ ይሰራል።",
            explanation = "He ('እሱ') cooks/makes ('ይሰራል') delicious ('ጣፋጭ') food ('ምግብ')!"
        ),
        AmharicSentence(
            english = "The duck is swimming. 🦆",
            correctWords = listOf("ዳክዬዋ", "እየዋኘች", "ናት።"),
            scrambledWords = listOf("ዳክዬዋ", "እየዋኘች", "ናት።", "እየበረረች", "ወፍ"),
            translit = "Dakyewa eyewanyech nat.",
            finalSentence = "ዳክዬዋ እየዋኘች ናት።",
            explanation = "The duck ('ዳክዬዋ') is feminine, so she is ('ናት') swimming ('እየዋኘች')!"
        ),
        AmharicSentence(
            english = "My mom has a ring. 💍",
            correctWords = listOf("እናቴ", "ቀለበት", "አላት።"),
            scrambledWords = listOf("እናቴ", "ቀለበት", "አላት።", "አምባር", "አባቴ"),
            translit = "Inate qelebet alat.",
            finalSentence = "እናቴ ቀለበት አላት።",
            explanation = "My mom ('እናቴ') has ('አላት') a ring ('ቀለበት')!"
        ),
        AmharicSentence(
            english = "I see a green leaf. 🍃",
            correctWords = listOf("አረንጓዴ", "ቅጠል", "አያለሁ።"),
            scrambledWords = listOf("አረንጓዴ", "ቅጠል", "አያለሁ።", "ቀይ", "አበባ"),
            translit = "Arengwade qitel ayalehu.",
            finalSentence = "አረንጓዴ ቅጠል አያለሁ።",
            explanation = "I see ('አያለሁ') a green ('አረንጓዴ') leaf ('ቅጠል')!"
        ),
        AmharicSentence(
            english = "The baby is drinking milk. 🍼",
            correctWords = listOf("ህፃኑ", "ወተት", "እየጠጣ", "ነው።"),
            scrambledWords = listOf("ህፃኑ", "ወተት", "እየጠጣ", "ነው።", "እየበላ", "ውሃ"),
            translit = "Hitsanu wetet eyeteta new.",
            finalSentence = "ህፃኑ ወተት እየጠጣ ነው።",
            explanation = "The baby ('ህፃኑ') is ('ነው') drinking ('እየጠጣ') milk ('ወተት')!"
        ),
        AmharicSentence(
            english = "They walk to school. 🚶‍♂️",
            correctWords = listOf("እነሱ", "ወደ", "ትምህርት", "ቤት", "ይሄዳሉ።"),
            scrambledWords = listOf("እነሱ", "ወደ", "ትምህርት", "ቤት", "ይሄዳሉ።", "ይሮጣሉ", "መጫወቻ"),
            translit = "Inesu wede timihirt bet yihedalu.",
            finalSentence = "እነሱ ወደ ትምህርት ቤት ይሄዳሉ።",
            explanation = "They ('እነሱ') go ('ይሄዳሉ') to ('ወደ') school ('ትምህርት ቤት')!"
        ),
        AmharicSentence(
            english = "The bee makes honey. 🐝",
            correctWords = listOf("ንብ", "ማር", "ትሰራለች።"),
            scrambledWords = listOf("ንብ", "ማር", "ትሰራለች።", "ስኳር", "ወተት"),
            translit = "Nib mar tiseralech.",
            finalSentence = "ንብ ማር ትሰራለች።",
            explanation = "A bee ('ንብ') makes ('ትሰራለች') honey ('ማር')!"
        ),
        AmharicSentence(
            english = "The table is wooden. 🪑",
            correctWords = listOf("ጠረጴዛው", "የእንጨት", "ነው።"),
            scrambledWords = listOf("ጠረጴዛው", "የእንጨት", "ነው።", "የብረት", "ወንበር"),
            translit = "Terepezaw yewinchet new.",
            finalSentence = "ጠረጴዛው የእንጨት ነው።",
            explanation = "The table ('ጠረጴዛው') is ('ነው') wooden ('የእንጨት')!"
        ),
        AmharicSentence(
            english = "I want cold water. 💧",
            correctWords = listOf("ቀዝቃዛ", "ውሃ", "እፈልጋለሁ።"),
            scrambledWords = listOf("ቀዝቃዛ", "ውሃ", "እፈልጋለሁ።", "ትኩስ", "ሻይ"),
            translit = "Qezqaza wuha ifeligalehu.",
            finalSentence = "ቀዝቃዛ ውሃ እፈልጋለሁ።",
            explanation = "I want ('እፈልጋለሁ') cold ('ቀዝቃዛ') water ('ውሃ')!"
        ),
        AmharicSentence(
            english = "The moon shines at night. 🌙",
            correctWords = listOf("ጨረቃ", "በሌሊት", "ታበራለች።"),
            scrambledWords = listOf("ጨረቃ", "በሌሊት", "ታበራለች።", "በቀን", "ፀሐይ"),
            translit = "Chereqa belelit taberalech.",
            finalSentence = "ጨረቃ በሌሊት ታበራለች።",
            explanation = "The moon ('ጨረቃ') shines ('ታበራለች') at night ('በሌሊት')!"
        ),
        AmharicSentence(
            english = "I like to sing. 🎤",
            correctWords = listOf("መዘመር", "እወዳለሁ።"),
            scrambledWords = listOf("መዘመር", "እወዳለሁ።", "መሮጥ", "መደነስ"),
            translit = "Mezemir iwedalehu.",
            finalSentence = "መዘመር እወዳለሁ።",
            explanation = "I like ('እወዳለሁ') to sing ('መዘመር')!"
        ),
        AmharicSentence(
            english = "He has a big dog. 🐕",
            correctWords = listOf("እሱ", "ትልቅ", "ውሻ", "አለው።"),
            scrambledWords = listOf("እሱ", "ትልቅ", "ውሻ", "አለው።", "ትንሽ", "ድመት"),
            translit = "Isu tiliq wusha alew.",
            finalSentence = "እሱ ትልቅ ውሻ አለው።",
            explanation = "He ('እሱ') has ('አለው') a big ('ትልቅ') dog ('ውሻ')!"
        ),
        AmharicSentence(
            english = "The soup is tasty. 🍲",
            correctWords = listOf("ሾርባው", "ጣፋጭ", "ነው።"),
            scrambledWords = listOf("ሾርባው", "ጣፋጭ", "ነው።", "መራራ", "ውሃ"),
            translit = "Shorbaw tafach new.",
            finalSentence = "ሾርባው ጣፋጭ ነው።",
            explanation = "The soup ('ሾርባው') is ('ነው') tasty ('ጣፋጭ')!"
        ),
        AmharicSentence(
            english = "I love my family. 👨‍👩‍👧‍👦",
            correctWords = listOf("ቤተሰቤን", "እወዳለሁ።"),
            scrambledWords = listOf("ቤተሰቤን", "እወዳለሁ።", "ጓደኛዬን", "ውሻውን"),
            translit = "Beteseben iwedalehu.",
            finalSentence = "ቤተሰቤን እወዳለሁ።",
            explanation = "I love ('እወዳለሁ') my family ('ቤተሰቤን')!"
        ),
        AmharicSentence(
            english = "The tomato is red. 🍅",
            correctWords = listOf("ቲማቲሙ", "ቀይ", "ነው።"),
            scrambledWords = listOf("ቲማቲሙ", "ቀይ", "ነው።", "ቢጫ", "ካሮት"),
            translit = "Timatimu qey new.",
            finalSentence = "ቲማቲሙ ቀይ ነው።",
            explanation = "The tomato ('ቲማቲሙ') is ('ነው') red ('ቀይ')!"
        ),
        AmharicSentence(
            english = "We wash our hands. 🧼",
            correctWords = listOf("እኛ", "እጃችንን", "እንታጠባለን።"),
            scrambledWords = listOf("እኛ", "እጃችንን", "እንታጠባለን።", "እግራችንን", "እንበላለን"),
            translit = "Inya ejachinin unintatebalen.",
            finalSentence = "እኛ እጃችንን እንታጠባለን።",
            explanation = "We ('እኛ') wash ('እንታጠባለን') our hands ('እጃችንን')!"
        ),
        AmharicSentence(
            english = "The lion is sleeping. 🦁",
            correctWords = listOf("አንበሳው", "እየተኛ", "ነው።"),
            scrambledWords = listOf("አንበሳው", "እየተኛ", "ነው።", "እየሮጠ", "ዝሆን"),
            translit = "Anbesaw eyetenya new.",
            finalSentence = "አንበሳው እየተኛ ነው።",
            explanation = "The lion ('አንበሳው') is ('ነው') sleeping ('እየተኛ')!"
        )
    )
}
