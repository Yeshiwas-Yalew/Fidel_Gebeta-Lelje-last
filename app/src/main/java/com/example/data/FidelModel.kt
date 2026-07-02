package com.example.data

data class AmharicLetter(
    val character: String,
    val order: Int, // 1 to 7
    val phonetic: String,
    val orderName: String, // "Ge'ez", "Ka'ib", "Salis", "Rabi'", "Hamis", "Sadis", "Sab'i"
    val ttsPhonetic: String, // Simplified spelling for reliable TTS speech guides
    val word: String = "",
    val translit: String = "",
    val english: String = "",
    val emoji: String = "✨"
)

data class FidelFamily(
    val mainConsonant: String,
    val familyName: String, // e.g., "HA (ሀ)", "LA (ለ)"
    val exampleWord: String, // e.g., "ላም"
    val exampleTranslit: String, // "Lam"
    val exampleEnglish: String, // "Cow"
    val exampleEmoji: String, // "🐄"
    val strokePathsHint: List<String>, // simplified visual handwriting guide points list (relative coordinates description)
    val letters: List<AmharicLetter>
)

object FidelData {
    private val rawFamilies = listOf(
        FidelFamily(
            mainConsonant = "ሀ",
            familyName = "HA (ሀ)",
            exampleWord = "ሆድ",
            exampleTranslit = "Hod",
            exampleEnglish = "Belly / Tummy",
            exampleEmoji = "👶",
            strokePathsHint = listOf("Draw a vertical line on the left", "Short horizontal bar top", "Vertical line on the right"),
            letters = listOf(
                AmharicLetter("ሀ", 1, "Hä", "Ge'ez", "ha"),
                AmharicLetter("ሁ", 2, "Hu", "Ka'ib", "who"),
                AmharicLetter("ሂ", 3, "Hi", "Salis", "hee"),
                AmharicLetter("ሃ", 4, "Ha", "Rabi'", "hah"),
                AmharicLetter("ሄ", 5, "He", "Hamis", "hay"),
                AmharicLetter("ህ", 6, "Hə", "Sadis", "heh"),
                AmharicLetter("ሆ", 7, "Ho", "Sab'i", "ho")
            )
        ),
        FidelFamily(
            mainConsonant = "ለ",
            familyName = "LA (ለ)",
            exampleWord = "ላም",
            exampleTranslit = "Lam",
            exampleEnglish = "Cow",
            exampleEmoji = "🐄",
            strokePathsHint = listOf("Start at the top left, slide down", "Loop and go down right", "Crossbar in middle"),
            letters = listOf(
                AmharicLetter("ለ", 1, "Lä", "Ge'ez", "le"),
                AmharicLetter("ሉ", 2, "Lu", "Ka'ib", "loo"),
                AmharicLetter("ሊ", 3, "Li", "Salis", "lee"),
                AmharicLetter("ላ", 4, "La", "Rabi'", "lah"),
                AmharicLetter("ሌ", 5, "Le", "Hamis", "lay"),
                AmharicLetter("ል", 6, "Lə", "Sadis", "ll"),
                AmharicLetter("ሎ", 7, "Lo", "Sab'i", "lo")
            )
        ),
        FidelFamily(
            mainConsonant = "ሐ",
            familyName = "HHA (ሐ)",
            exampleWord = "ሐመልማል",
            exampleTranslit = "Hamlemal",
            exampleEnglish = "Green leaf / Plant",
            exampleEmoji = "🌿",
            strokePathsHint = listOf("Draw a horizontal bridge top", "Drop left foot vertical", "Drop right foot vertical"),
            letters = listOf(
                AmharicLetter("ሐ", 1, "H'ä", "Ge'ez", "ha"),
                AmharicLetter("ሑ", 2, "H'u", "Ka'ib", "who"),
                AmharicLetter("ሒ", 3, "H'i", "Salis", "hee"),
                AmharicLetter("ሓ", 4, "H'a", "Rabi'", "hah"),
                AmharicLetter("ሔ", 5, "H'e", "Hamis", "hay"),
                AmharicLetter("ሕ", 6, "H'ə", "Sadis", "heh"),
                AmharicLetter("ሖ", 7, "H'o", "Sab'i", "ho")
            )
        ),
        FidelFamily(
            mainConsonant = "መ",
            familyName = "MA (መ)",
            exampleWord = "መኪና",
            exampleTranslit = "Mekina",
            exampleEnglish = "Car",
            exampleEmoji = "🚗",
            strokePathsHint = listOf("Curve down left like a hook", "Curve up and loop into a round body"),
            letters = listOf(
                AmharicLetter("መ", 1, "Mä", "Ge'ez", "me"),
                AmharicLetter("ሙ", 2, "Mu", "Ka'ib", "moo"),
                AmharicLetter("ሚ", 3, "Mi", "Salis", "mee"),
                AmharicLetter("ማ", 4, "Ma", "Rabi'", "mah"),
                AmharicLetter("ሜ", 5, "Me", "Hamis", "may"),
                AmharicLetter("ም", 6, "Mə", "Sadis", "mm"),
                AmharicLetter("ሞ", 7, "Mo", "Sab'i", "mo")
            )
        ),
        FidelFamily(
            mainConsonant = "ሠ",
            familyName = "SŚA (ሠ)",
            exampleWord = "ሠረገላ",
            exampleTranslit = "Heregela",
            exampleEnglish = "Horse Carriage / Chariot",
            exampleEmoji = "🐎",
            strokePathsHint = listOf("Draw three vertical comb teeth", "Connect them at the bottom with a flat bar"),
            letters = listOf(
                AmharicLetter("ሠ", 1, "Śä", "Ge'ez", "se"),
                AmharicLetter("ሡ", 2, "Śu", "Ka'ib", "soo"),
                AmharicLetter("ሢ", 3, "Śi", "Salis", "see"),
                AmharicLetter("ሣ", 4, "Śa", "Rabi'", "sah"),
                AmharicLetter("ሤ", 5, "Śe", "Hamis", "say"),
                AmharicLetter("ሥ", 6, "Śə", "Sadis", "ss"),
                AmharicLetter("ሦ", 7, "Śo", "Sab'i", "so")
            )
        ),
        FidelFamily(
            mainConsonant = "ረ",
            familyName = "RA (ረ)",
            exampleWord = "ርግብ",
            exampleTranslit = "Rigib",
            exampleEnglish = "Dove / Pigeon",
            exampleEmoji = "🕊️",
            strokePathsHint = listOf("Draw a curved diagonal slide to the right", "Add a tiny foot tail at bottom right"),
            letters = listOf(
                AmharicLetter("ረ", 1, "Rä", "Ge'ez", "re"),
                AmharicLetter("ሩ", 2, "Ru", "Ka'ib", "roo"),
                AmharicLetter("ሪ", 3, "Ri", "Salis", "ree"),
                AmharicLetter("ራ", 4, "Ra", "Rabi'", "rah"),
                AmharicLetter("ሬ", 5, "Re", "Hamis", "ray"),
                AmharicLetter("ር", 6, "Rə", "Sadis", "rr"),
                AmharicLetter("ሮ", 7, "Ro", "Sab'i", "ro")
            )
        ),
        FidelFamily(
            mainConsonant = "ሰ",
            familyName = "SA (ሰ)",
            exampleWord = "ሰዓት",
            exampleTranslit = "Se'at",
            exampleEnglish = "Clock / Watch",
            exampleEmoji = "⏰",
            strokePathsHint = listOf("Draw two loops or hills next to each other", "Add a vertical stem connecting down"),
            letters = listOf(
                AmharicLetter("ሰ", 1, "Sä", "Ge'ez", "se"),
                AmharicLetter("ሱ", 2, "Su", "Ka'ib", "soo"),
                AmharicLetter("ሲ", 3, "Si", "Salis", "see"),
                AmharicLetter("ሳ", 4, "Sa", "Rabi'", "sah"),
                AmharicLetter("ሴ", 5, "Se", "Hamis", "say"),
                AmharicLetter("ስ", 6, "Sə", "Sadis", "ss"),
                AmharicLetter("ሶ", 7, "So", "Sab'i", "so")
            )
        ),
        FidelFamily(
            mainConsonant = "ሸ",
            familyName = "SHA (ሸ)",
            exampleWord = "ሸረሪት",
            exampleTranslit = "Shererit",
            exampleEnglish = "Spider",
            exampleEmoji = "🕷️",
            strokePathsHint = listOf("Similar to ሰ (SA)", "Add a small crown hat at the very top"),
            letters = listOf(
                AmharicLetter("ሸ", 1, "Shä", "Ge'ez", "she"),
                AmharicLetter("ሹ", 2, "Shu", "Ka'ib", "shoo"),
                AmharicLetter("ሺ", 3, "Shi", "Salis", "shee"),
                AmharicLetter("ሻ", 4, "Sha", "Rabi'", "shah"),
                AmharicLetter("ሼ", 5, "She", "Hamis", "shay"),
                AmharicLetter("ሽ", 6, "Shə", "Sadis", "shh"),
                AmharicLetter("ሾ", 7, "Sho", "Sab'i", "sho")
            )
        ),
        FidelFamily(
            mainConsonant = "ቀ",
            familyName = "QA (ቀ)",
            exampleWord = "ቅጠል",
            exampleTranslit = "Qitel",
            exampleEnglish = "Leaf",
            exampleEmoji = "🍃",
            strokePathsHint = listOf("Draw a clean circle at the top", "Add a straight stick drop in the middle"),
            letters = listOf(
                AmharicLetter("ቀ", 1, "Qä", "Ge'ez", "qe"),
                AmharicLetter("ቁ", 2, "Qu", "Ka'ib", "qoo"),
                AmharicLetter("ቂ", 3, "Qi", "Salis", "qee"),
                AmharicLetter("ቃ", 4, "Qa", "Rabi'", "qah"),
                AmharicLetter("ቄ", 5, "Qe", "Hamis", "qay"),
                AmharicLetter("ቅ", 6, "Qə", "Sadis", "qq"),
                AmharicLetter("ቆ", 7, "Qo", "Sab'i", "qo")
            )
        ),
        FidelFamily(
            mainConsonant = "በ",
            familyName = "BA (በ)",
            exampleWord = "በግ",
            exampleTranslit = "Beg",
            exampleEnglish = "Sheep",
            exampleEmoji = "🐑",
            strokePathsHint = listOf("Draw a U-shape container", "Close bottom right with a small base plate step"),
            letters = listOf(
                AmharicLetter("በ", 1, "Bä", "Ge'ez", "be"),
                AmharicLetter("ቡ", 2, "Bu", "Ka'ib", "boo"),
                AmharicLetter("ቢ", 3, "Bi", "Salis", "bee"),
                AmharicLetter("ባ", 4, "Ba", "Rabi'", "bah"),
                AmharicLetter("ቤ", 5, "Be", "Hamis", "bay"),
                AmharicLetter("ብ", 6, "Bə", "Sadis", "bb"),
                AmharicLetter("ቦ", 7, "Bo", "Sab'i", "bo")
            )
        ),
        FidelFamily(
            mainConsonant = "ተ",
            familyName = "TA (ተ)",
            exampleWord = "ቲማቲም",
            exampleTranslit = "Timatim",
            exampleEnglish = "Tomato",
            exampleEmoji = "🍅",
            strokePathsHint = listOf("Draw a vertical pillar", "Cross bar horizontally on top like a plus sign"),
            letters = listOf(
                AmharicLetter("ተ", 1, "Tä", "Ge'ez", "te"),
                AmharicLetter("ቱ", 2, "Tu", "Ka'ib", "too"),
                AmharicLetter("ቲ", 3, "Ti", "Salis", "tee"),
                AmharicLetter("ታ", 4, "Ta", "Rabi'", "tah"),
                AmharicLetter("ቴ", 5, "Te", "Hamis", "tay"),
                AmharicLetter("ት", 6, "Tə", "Sadis", "tt"),
                AmharicLetter("ቶ", 7, "To", "Sab'i", "to")
            )
        ),
        FidelFamily(
            mainConsonant = "ቸ",
            familyName = "CHA (ቸ)",
            exampleWord = "ቸኮሌት",
            exampleTranslit = "Chekolet",
            exampleEnglish = "Chocolate",
            exampleEmoji = "🍫",
            strokePathsHint = listOf("Draw standard ተ shape first", "Add a tilted flag hook at the top left"),
            letters = listOf(
                AmharicLetter("ቸ", 1, "Chä", "Ge'ez", "che"),
                AmharicLetter("ቹ", 2, "Chu", "Ka'ib", "choo"),
                AmharicLetter("ቺ", 3, "Chi", "Salis", "chee"),
                AmharicLetter("ቻ", 4, "Cha", "Rabi'", "chah"),
                AmharicLetter("ቼ", 5, "Che", "Hamis", "chay"),
                AmharicLetter("ች", 6, "Chə", "Sadis", "chh"),
                AmharicLetter("ቾ", 7, "Cho", "Sab'i", "cho")
            )
        ),
        FidelFamily(
            mainConsonant = "ኀ",
            familyName = "KHA (ኀ)",
            exampleWord = "ኀብስት",
            exampleTranslit = "Habist",
            exampleEnglish = "Traditional Bread",
            exampleEmoji = "🍞",
            strokePathsHint = listOf("Draw a downward hook curve on the left", "Circular loop foot on the bottom right"),
            letters = listOf(
                AmharicLetter("ኀ", 1, "Hä", "Ge'ez", "ha"),
                AmharicLetter("ኁ", 2, "Hu", "Ka'ib", "who"),
                AmharicLetter("ኂ", 3, "Hi", "Salis", "hee"),
                AmharicLetter("ኃ", 4, "Ha", "Rabi'", "hah"),
                AmharicLetter("ኄ", 5, "He", "Hamis", "hay"),
                AmharicLetter("ኅ", 6, "Hə", "Sadis", "heh"),
                AmharicLetter("ኆ", 7, "Ho", "Sab'i", "ho")
            )
        ),
        FidelFamily(
            mainConsonant = "ነ",
            familyName = "NA (ነ)",
            exampleWord = "ነብር",
            exampleTranslit = "Nebir",
            exampleEnglish = "Leopard",
            exampleEmoji = "🐆",
            strokePathsHint = listOf("Draw left diagonal stroke", "High peak hook and straight drop right leg"),
            letters = listOf(
                AmharicLetter("ነ", 1, "Nä", "Ge'ez", "ne"),
                AmharicLetter("ኑ", 2, "Nu", "Ka'ib", "noo"),
                AmharicLetter("ኒ", 3, "Ni", "Salis", "nee"),
                AmharicLetter("ና", 4, "Na", "Rabi'", "nah"),
                AmharicLetter("ኔ", 5, "Ne", "Hamis", "nay"),
                AmharicLetter("ን", 6, "Nə", "Sadis", "nn"),
                AmharicLetter("ኖ", 7, "No", "Sab'i", "no")
            )
        ),
        FidelFamily(
            mainConsonant = "ኘ",
            familyName = "NYA (ኘ)",
            exampleWord = "ኛ",
            exampleTranslit = "Nya",
            exampleEnglish = "First Place / Best",
            exampleEmoji = "🥇",
            strokePathsHint = listOf("Like ነ but with a small horizontal tilde bar on top"),
            letters = listOf(
                AmharicLetter("ኘ", 1, "Nyä", "Ge'ez", "nye"),
                AmharicLetter("ኙ", 2, "Nyu", "Ka'ib", "nyoo"),
                AmharicLetter("ኚ", 3, "Nyi", "Salis", "nyee"),
                AmharicLetter("ኛ", 4, "Nya", "Rabi'", "nyah"),
                AmharicLetter("ኜ", 5, "Nye", "Hamis", "nyay"),
                AmharicLetter("ኝ", 6, "Nyə", "Sadis", "ny"),
                AmharicLetter("ኞ", 7, "Nyo", "Sab'i", "nyo")
            )
        ),
        FidelFamily(
            mainConsonant = "አ",
            familyName = "A (አ)",
            exampleWord = "አንበሳ",
            exampleTranslit = "Anbessa",
            exampleEnglish = "Lion",
            exampleEmoji = "🦁",
            strokePathsHint = listOf("Left angle wall", "Cross through center", "Small right support bar"),
            letters = listOf(
                AmharicLetter("አ", 1, "Ä", "Ge'ez", "ah"),
                AmharicLetter("ኡ", 2, "U", "Ka'ib", "oo"),
                AmharicLetter("ኢ", 3, "I", "Salis", "ee"),
                AmharicLetter("ኣ", 4, "A", "Rabi'", "hah"),
                AmharicLetter("ኤ", 5, "E", "Hamis", "ay"),
                AmharicLetter("እ", 6, "Ə", "Sadis", "eh"),
                AmharicLetter("ኦ", 7, "O", "Sab'i", "oh")
            )
        ),
        FidelFamily(
            mainConsonant = "ከ",
            familyName = "KA (ከ)",
            exampleWord = "ከበሮ",
            exampleTranslit = "Kebero",
            exampleEnglish = "Drum",
            exampleEmoji = "🥁",
            strokePathsHint = listOf("Left vertical back", "Curve high shelf top right", "Drop right foot leg"),
            letters = listOf(
                AmharicLetter("ከ", 1, "Kä", "Ge'ez", "ke"),
                AmharicLetter("ኩ", 2, "Ku", "Ka'ib", "koo"),
                AmharicLetter("ኪ", 3, "Ki", "Salis", "kee"),
                AmharicLetter("ካ", 4, "Ka", "Rabi'", "kah"),
                AmharicLetter("ኬ", 5, "Ke", "Hamis", "kay"),
                AmharicLetter("ክ", 6, "Kə", "Sadis", "kk"),
                AmharicLetter("ኮ", 7, "Ko", "Sab'i", "ko")
            )
        ),
        FidelFamily(
            mainConsonant = "ኸ",
            familyName = "KXA (ኸ)",
            exampleWord = "ኸሚስ",
            exampleTranslit = "Khemis",
            exampleEnglish = "Thursday",
            exampleEmoji = "📅",
            strokePathsHint = listOf("Draw complete ከ first", "Add a horizontal bar on top center"),
            letters = listOf(
                AmharicLetter("ኸ", 1, "Khä", "Ge'ez", "khe"),
                AmharicLetter("ኹ", 2, "Khu", "Ka'ib", "khoo"),
                AmharicLetter("ኺ", 3, "Khi", "Salis", "khee"),
                AmharicLetter("ኻ", 4, "Kha", "Rabi'", "khah"),
                AmharicLetter("ኼ", 5, "Khe", "Hamis", "khay"),
                AmharicLetter("ኽ", 6, "Khə", "Sadis", "khh"),
                AmharicLetter("ኾ", 7, "Kho", "Sab'i", "kho")
            )
        ),
        FidelFamily(
            mainConsonant = "ወ",
            familyName = "WA (ወ)",
            exampleWord = "ወፍ",
            exampleTranslit = "Wef",
            exampleEnglish = "Bird",
            exampleEmoji = "🐦",
            strokePathsHint = listOf("Draw a rounded cup shape", "Add a dividing center pillar"),
            letters = listOf(
                AmharicLetter("ወ", 1, "Wä", "Ge'ez", "we"),
                AmharicLetter("ዉ", 2, "Wu", "Ka'ib", "woo"),
                AmharicLetter("ዊ", 3, "Wi", "Salis", "wee"),
                AmharicLetter("ዋ", 4, "Wa", "Rabi'", "wah"),
                AmharicLetter("ዌ", 5, "We", "Hamis", "way"),
                AmharicLetter("ው", 6, "Wə", "Sadis", "ww"),
                AmharicLetter("ዎ", 7, "Wo", "Sab'i", "wo")
            )
        ),
        FidelFamily(
            mainConsonant = "ዐ",
            familyName = "AA (ዐ)",
            exampleWord = "ዓይን",
            exampleTranslit = "Ayn",
            exampleEnglish = "Eye",
            exampleEmoji = "👁️",
            strokePathsHint = listOf("Draw pharyngeal loop", "Open top split path"),
            letters = listOf(
                AmharicLetter("ዐ", 1, "Ä", "Ge'ez", "ah"),
                AmharicLetter("ዑ", 2, "U", "Ka'ib", "oo"),
                AmharicLetter("ዒ", 3, "I", "Salis", "ee"),
                AmharicLetter("ዓ", 4, "A", "Rabi'", "hah"),
                AmharicLetter("ዔ", 5, "E", "Hamis", "ay"),
                AmharicLetter("ዕ", 6, "Ə", "Sadis", "eh"),
                AmharicLetter("ዖ", 7, "O", "Sab'i", "oh")
            )
        ),
        FidelFamily(
            mainConsonant = "ዘ",
            familyName = "ZA (ዘ)",
            exampleWord = "ዘፈን",
            exampleTranslit = "Zefen",
            exampleEnglish = "Song / Music",
            exampleEmoji = "🎶",
            strokePathsHint = listOf("Slanted ladder step top left down right", "Connect zig zag bars"),
            letters = listOf(
                AmharicLetter("ዘ", 1, "Zä", "Ge'ez", "ze"),
                AmharicLetter("ዙ", 2, "Zu", "Ka'ib", "zoo"),
                AmharicLetter("ዚ", 3, "Zi", "Salis", "zee"),
                AmharicLetter("ዛ", 4, "Za", "Rabi'", "zah"),
                AmharicLetter("ዜ", 5, "Ze", "Hamis", "zay"),
                AmharicLetter("ዝ", 6, "Zə", "Sadis", "zz"),
                AmharicLetter("ዞ", 7, "Zo", "Sab'i", "zo")
            )
        ),
        FidelFamily(
            mainConsonant = "ዠ",
            familyName = "ZHA (ዠ)",
            exampleWord = "ዠንጥላ",
            exampleTranslit = "Zhentila",
            exampleEnglish = "Umbrella",
            exampleEmoji = "☔",
            strokePathsHint = listOf("Like ዘ but with a cute crown horizontal bar on top"),
            letters = listOf(
                AmharicLetter("ዠ", 1, "Zhä", "Ge'ez", "zhe"),
                AmharicLetter("ዡ", 2, "Zhu", "Ka'ib", "zhoo"),
                AmharicLetter("ዢ", 3, "Zhi", "Salis", "zhee"),
                AmharicLetter("ዣ", 4, "Zha", "Rabi'", "zhah"),
                AmharicLetter("ዤ", 5, "Zhe", "Hamis", "zhay"),
                AmharicLetter("ዥ", 6, "Zhə", "Sadis", "zhh"),
                AmharicLetter("ዦ", 7, "Zho", "Sab'i", "zho")
            )
        ),
        FidelFamily(
            mainConsonant = "የ",
            familyName = "YA (የ)",
            exampleWord = "የቀን",
            exampleTranslit = "Yeqen",
            exampleEnglish = "Day / Sun",
            exampleEmoji = "☀️",
            strokePathsHint = listOf("Draw left open container loop", "Descend a right side curve tail"),
            letters = listOf(
                AmharicLetter("የ", 1, "Yä", "Ge'ez", "ye"),
                AmharicLetter("ዩ", 2, "Yu", "Ka'ib", "yoo"),
                AmharicLetter("ዪ", 3, "Yi", "Salis", "yee"),
                AmharicLetter("ያ", 4, "Ya", "Rabi'", "yah"),
                AmharicLetter("ዬ", 5, "Ye", "Hamis", "yay"),
                AmharicLetter("ይ", 6, "Yə", "Sadis", "yy"),
                AmharicLetter("ዮ", 7, "Yo", "Sab'i", "yo")
            )
        ),
        FidelFamily(
            mainConsonant = "ደ",
            familyName = "DA (ደ)",
            exampleWord = "ደብዳቤ",
            exampleTranslit = "Debdabe",
            exampleEnglish = "Letter / Envelope",
            exampleEmoji = "✉️",
            strokePathsHint = listOf("Left hook curve starting top", "Connect right loop at bottom base"),
            letters = listOf(
                AmharicLetter("ደ", 1, "Dä", "Ge'ez", "de"),
                AmharicLetter("ዱ", 2, "Du", "Ka'ib", "doo"),
                AmharicLetter("ዲ", 3, "Di", "Salis", "dee"),
                AmharicLetter("ዳ", 4, "Da", "Rabi'", "dah"),
                AmharicLetter("ዴ", 5, "De", "Hamis", "day"),
                AmharicLetter("ድ", 6, "Də", "Sadis", "dd"),
                AmharicLetter("ዶ", 7, "Do", "Sab'i", "do")
            )
        ),
        FidelFamily(
            mainConsonant = "ጀ",
            familyName = "JA (ጀ)",
            exampleWord = "ጀልባ",
            exampleTranslit = "Jelba",
            exampleEnglish = "Sailing Boat",
            exampleEmoji = "⛵",
            strokePathsHint = listOf("Like ደ but with a small flat horizontal hat bar on top"),
            letters = listOf(
                AmharicLetter("ጀ", 1, "Jä", "Ge'ez", "je"),
                AmharicLetter("ጁ", 2, "Ju", "Ka'ib", "joo"),
                AmharicLetter("ጂ", 3, "Ji", "Salis", "jee"),
                AmharicLetter("ጃ", 4, "Ja", "Rabi'", "jah"),
                AmharicLetter("ጄ", 5, "Je", "Hamis", "jay"),
                AmharicLetter("ጅ", 6, "Jə", "Sadis", "j"),
                AmharicLetter("ጆ", 7, "Jo", "Sab'i", "jo")
            )
        ),
        FidelFamily(
            mainConsonant = "ገ",
            familyName = "GA (ገ)",
            exampleWord = "ገበታ",
            exampleTranslit = "Gebeta",
            exampleEnglish = "Tray / Bowl",
            exampleEmoji = "🍲",
            strokePathsHint = listOf("Top hook curved left", "Straight vertical leg split into two feet"),
            letters = listOf(
                AmharicLetter("ገ", 1, "Gä", "Ge'ez", "ge"),
                AmharicLetter("ጉ", 2, "Gu", "Ka'ib", "goo"),
                AmharicLetter("ጊ", 3, "Gi", "Salis", "gee"),
                AmharicLetter("ጋ", 4, "Ga", "Rabi'", "gah"),
                AmharicLetter("ጌ", 5, "Ge", "Hamis", "gay"),
                AmharicLetter("ግ", 6, "Gə", "Sadis", "gg"),
                AmharicLetter("ጎ", 7, "Go", "Sab'i", "go")
            )
        ),
        FidelFamily(
            mainConsonant = "ጠ",
            familyName = "TA (ጠ)",
            exampleWord = "ጠጅ",
            exampleTranslit = "Tej",
            exampleEnglish = "Honey wine",
            exampleEmoji = "🍯",
            strokePathsHint = listOf("Draw a flat wide bottom bowl", "Add a central single stick crown rising up"),
            letters = listOf(
                AmharicLetter("ጠ", 1, "T'ä", "Ge'ez", "ta"),
                AmharicLetter("ጡ", 2, "T'u", "Ka'ib", "too"),
                AmharicLetter("ጢ", 3, "T'i", "Salis", "tee"),
                AmharicLetter("ጣ", 4, "T'a", "Rabi'", "tah"),
                AmharicLetter("ጤ", 5, "T'e", "Hamis", "tay"),
                AmharicLetter("ጥ", 6, "T'ə", "Sadis", "t"),
                AmharicLetter("ጦ", 7, "T'o", "Sab'i", "to")
            )
        ),
        FidelFamily(
            mainConsonant = "ጨ",
            familyName = "CH'A (ጨ)",
            exampleWord = "ጨረቃ",
            exampleTranslit = "Chereqa",
            exampleEnglish = "Crescent Moon",
            exampleEmoji = "🌙",
            strokePathsHint = listOf("Like ጠ but with a horizontal crown cross bar on the top stem"),
            letters = listOf(
                AmharicLetter("ጨ", 1, "Ch'ä", "Ge'ez", "cha"),
                AmharicLetter("ጩ", 2, "Ch'u", "Ka'ib", "choo"),
                AmharicLetter("ጪ", 3, "Ch'i", "Salis", "chee"),
                AmharicLetter("ጫ", 4, "Ch'a", "Rabi'", "chah"),
                AmharicLetter("ጬ", 5, "Ch'e", "Hamis", "chay"),
                AmharicLetter("ጭ", 6, "Ch'ə", "Sadis", "ch"),
                AmharicLetter("ጮ", 7, "Ch'o", "Sab'i", "cho")
            )
        ),
        FidelFamily(
            mainConsonant = "ጰ",
            familyName = "P'A (ጰ)",
            exampleWord = "ጰራቅሊጦስ",
            exampleTranslit = "Peraqlitos",
            exampleEnglish = "Pentecost / Church",
            exampleEmoji = "⛪",
            strokePathsHint = listOf("Double layered circular rounded bubbles stacked nicely"),
            letters = listOf(
                AmharicLetter("ጰ", 1, "P'ä", "Ge'ez", "pa"),
                AmharicLetter("ጱ", 2, "P'u", "Ka'ib", "poo"),
                AmharicLetter("ጲ", 3, "P'i", "Salis", "pee"),
                AmharicLetter("ጳ", 4, "P'a", "Rabi'", "pah"),
                AmharicLetter("ጴ", 5, "P'e", "Hamis", "pay"),
                AmharicLetter("ጵ", 6, "P'ə", "Sadis", "p"),
                AmharicLetter("ጶ", 7, "P'o", "Sab'i", "po")
            )
        ),
        FidelFamily(
            mainConsonant = "ጸ",
            familyName = "TS'A (ጸ)",
            exampleWord = "ጸሎት",
            exampleTranslit = "Tselot",
            exampleEnglish = "Prayer / Faith",
            exampleEmoji = "🙏",
            strokePathsHint = listOf("Left loop hook curve", "Right diagonal leg descending to the ground"),
            letters = listOf(
                AmharicLetter("ጸ", 1, "Ts'ä", "Ge'ez", "tsa"),
                AmharicLetter("ጹ", 2, "Ts'u", "Ka'ib", "tsoo"),
                AmharicLetter("ጺ", 3, "Ts'i", "Salis", "tsee"),
                AmharicLetter("ጻ", 4, "Ts'a", "Rabi'", "tsah"),
                AmharicLetter("ጼ", 5, "Ts'e", "Hamis", "tsay"),
                AmharicLetter("ጽ", 6, "Ts'ə", "Sadis", "ts"),
                AmharicLetter("ጾ", 7, "Ts'o", "Sab'i", "tso")
            )
        ),
        FidelFamily(
            mainConsonant = "ፀ",
            familyName = "TS'S'A (ፀ)",
            exampleWord = "ፀሐይ",
            exampleTranslit = "Tsehay",
            exampleEnglish = "Shining Sun",
            exampleEmoji = "☀️",
            strokePathsHint = listOf("A cross T-shape top support structure", "Pulsing loop circle foot at the bottom"),
            letters = listOf(
                AmharicLetter("ፀ", 1, "Ts'ä", "Ge'ez", "tsa"),
                AmharicLetter("ፁ", 2, "Ts'u", "Ka'ib", "tsoo"),
                AmharicLetter("ፂ", 3, "Ts'i", "Salis", "tsee"),
                AmharicLetter("ፃ", 4, "Ts'a", "Rabi'", "tsah"),
                AmharicLetter("ፄ", 5, "Ts'e", "Hamis", "tsay"),
                AmharicLetter("ፅ", 6, "Ts'ə", "Sadis", "ts"),
                AmharicLetter("ፆ", 7, "Ts'o", "Sab'i", "tso")
            )
        ),
        FidelFamily(
            mainConsonant = "ፈ",
            familyName = "FA (ፈ)",
            exampleWord = "ፈረስ",
            exampleTranslit = "Feres",
            exampleEnglish = "Horse",
            exampleEmoji = "🐎",
            strokePathsHint = listOf("An open horizontal roof bar with hooks", "Two symmetrical vertical drop foot stems"),
            letters = listOf(
                AmharicLetter("ፈ", 1, "Fä", "Ge'ez", "fe"),
                AmharicLetter("ፉ", 2, "Fu", "Ka'ib", "foo"),
                AmharicLetter("ፊ", 3, "Fi", "Salis", "fee"),
                AmharicLetter("ፋ", 4, "Fa", "Rabi'", "fah"),
                AmharicLetter("ፌ", 5, "Fe", "Hamis", "fay"),
                AmharicLetter("ፍ", 6, "Fə", "Sadis", "ff"),
                AmharicLetter("ፎ", 7, "Fo", "Sab'i", "fo")
            )
        ),
        FidelFamily(
            mainConsonant = "ፐ",
            familyName = "PA (ፐ)",
            exampleWord = "ፓፓያ",
            exampleTranslit = "Papaya",
            exampleEnglish = "Papaya Fruit",
            exampleEmoji = "🥭",
            strokePathsHint = listOf("Top horizontal line", "Three vertical legs dangling downwards"),
            letters = listOf(
                AmharicLetter("ፐ", 1, "Pä", "Ge'ez", "pe"),
                AmharicLetter("ፑ", 2, "Pu", "Ka'ib", "poo"),
                AmharicLetter("ፒ", 3, "Pi", "Salis", "pee"),
                AmharicLetter("ፓ", 4, "Pa", "Rabi'", "pah"),
                AmharicLetter("ፔ", 5, "Pe", "Hamis", "pay"),
                AmharicLetter("ፕ", 6, "Pə", "Sadis", "pp"),
                AmharicLetter("ፖ", 7, "Po", "Sab'i", "po")
            )
        )
    )

    val families: List<FidelFamily> = rawFamilies.map { family ->
        family.copy(
            letters = family.letters.map { letter ->
                val details = FidelVocabulary.getWord(letter.character)
                letter.copy(
                    word = details.word,
                    translit = details.translit,
                    english = details.english,
                    emoji = details.emoji
                )
            }
        )
    }
}

data class WordDetail(
    val word: String,
    val translit: String,
    val english: String,
    val emoji: String
)

object FidelVocabulary {
    private val map = mapOf(
        // HA (ሀ) family
        "ሀ" to WordDetail("ሀገር", "Hager", "Country / Nation", "🗺️"),
        "ሁ" to WordDetail("ሁለተኛ", "Huletenya", "Second / Twice", "🥈"),
        "ሂ" to WordDetail("ሂሳብ", "Hisab", "Math / Arithmetic", "🧮"),
        "ሃ" to WordDetail("ሃሳብ", "Hasab", "Idea / Thought", "💡"),
        "ሄ" to WordDetail("ሄደ", "Hede", "Went / Departed", "🚶"),
        "ህ" to WordDetail("ህፃን", "Hitsan", "Baby / Toddler", "👶"),
        "ሆ" to WordDetail("ሆድ", "Hod", "Belly / Stomach", "🤰"),

        // LA (ለ) family
        "ለ" to WordDetail("ለምለም", "Lemlem", "Lush / Green", "🌱"),
        "ሉ" to WordDetail("ሉል", "Lul", "Pearl / Globe", "🌐"),
        "ሊ" to WordDetail("ሊቅ", "Liq", "Scholar / Expert", "🎓"),
        "ላ" to WordDetail("ላም", "Lam", "Cow", "🐄"),
        "ሌ" to WordDetail("ሌባ", "Leba", "Thief", "🥷"),
        "ል" to WordDetail("ልብ", "Lib", "Heart / Core", "❤️"),
        "ሎ" to WordDetail("ሎሚ", "Lomi", "Lemon / Lime", "🍋"),

        // HHA (ሐ) family
        "ሐ" to WordDetail("ሐረግ", "Hareg", "Vine / Climber", "🌿"),
        "ሑ" to WordDetail("ሑረት", "Huret", "Journey / Travel", "🗺️"),
        "ሒ" to WordDetail("ሒሳብ", "Hisab", "Account / Billing", "💳"),
        "ሓ" to WordDetail("ሓውልት", "Hawilt", "Monument / Statue", "🗿"),
        "ሔ" to WordDetail("ሔዋን", "Hewan", "Eve / Life", "👩"),
        "ሕ" to WordDetail("ሕግ", "Hig", "Law / Rule", "⚖️"),
        "ሖ" to WordDetail("ሖረ", "Hore", "Travelled / Departed", "🚶‍♂️"),

        // MA (መ) family
        "መ" to WordDetail("መኪና", "Mekina", "Car / Vehicle", "🚗"),
        "ሙ" to WordDetail("ሙዝ", "Muz", "Banana", "🍌"),
        "ሚ" to WordDetail("ሚዛን", "Mizan", "Scale / Balance", "⚖️"),
        "ማ" to WordDetail("ማር", "Mar", "Honey", "🍯"),
        "ሜ" to WordDetail("ሜዳ", "Meda", "Field / Plain", "⛳"),
        "ም" to WordDetail("ምስል", "Misil", "Picture / Image", "🖼️"),
        "ሞ" to WordDetail("ሞባይል", "Mobile", "Phone / Cellular", "📱"),

        // SŚA (ሠ) family
        "ሠ" to WordDetail("ሠረገላ", "Heregela", "Horse Carriage / Chariot", "🐎"),
        "ሡ" to WordDetail("ሡራፊ", "Surafi", "Seraph / Angel", "👼"),
        "ሢ" to WordDetail("ሢመት", "Simet", "Appointment / Rank", "🎖️"),
        "ሣ" to WordDetail("ሣጥን", "Satin", "Box / Chest", "📦"),
        "ሤ" to WordDetail("ሤራ", "Sera", "Plot / Scheme", "🕵️"),
        "ሥ" to WordDetail("ሥጋ", "Siga", "Meat", "🥩"),
        "ሦ" to WordDetail("ሦስት", "Sost", "Three", "3️⃣"),

        // RA (ረ) family
        "ረ" to WordDetail("ረጅም", "Rejim", "Tall / Long", "🗼"),
        "ሩ" to WordDetail("ሩጫ", "Rucha", "Running / Race", "🏃"),
        "ሪ" to WordDetail("ሪከርድ", "Rikerd", "Record", "🏆"),
        "ራ" to WordDetail("ራስ", "Ras", "Head / Leader", "👑"),
        "ሬ" to WordDetail("ሬዲዮ", "Rediyo", "Radio", "📻"),
        "ር" to WordDetail("ርግብ", "Rigib", "Pigeon / Dove", "🕊️"),
        "ሮ" to WordDetail("ሮቦት", "Robot", "Robot", "🤖"),

        // SA (ሰ) family
        "ሰ" to WordDetail("ሰዓት", "Se'at", "Watch / Clock", "⏰"),
        "ሱ" to WordDetail("ሱቅ", "Suq", "Shop / Store", "🏪"),
        "ሲ" to WordDetail("ሲኒማ", "Sinima", "Cinema / Movie", "🎬"),
        "ሳ" to WordDetail("ሳቅ", "Saq", "Laughter / Smile", "😄"),
        "ሴ" to WordDetail("ሴት", "Set", "Woman", "👩"),
        "ስ" to WordDetail("ስልክ", "Silki", "Telephone", "📞"),
        "ሶ" to WordDetail("ሶፋ", "Sofa", "Couch / Sofa", "🛋️"),

        // SHA (ሸ) family
        "ሸ" to WordDetail("ሸረሪት", "Shererit", "Spider", "🕷️"),
        "ሹ" to WordDetail("ሹካ", "Shuka", "Fork", "🍴"),
        "ሺ" to WordDetail("ሺህ", "Shih", "Thousand", "🔢"),
        "ሻ" to WordDetail("ሻይ", "Shay", "Tea", "🍵"),
        "ሼ" to WordDetail("ሼፍ", "Chef", "Chef / Cook", "👨‍🍳"),
        "ሽ" to WordDetail("ሽምኩርት", "Shinkurt", "Onion", "🧅"),
        "ሾ" to WordDetail("ሾፌር", "Shofer", "Driver", "🧑‍✈️"),

        // QA (ቀ) family
        "ቀ" to WordDetail("ቀበሮ", "Kebero", "Fox", "🦊"),
        "ቁ" to WordDetail("ቁልፍ", "Qulf", "Key / Lock", "🔑"),
        "ቂ" to WordDetail("ቂጣ", "Qita", "Flatbread", "🥞"),
        "ቃ" to WordDetail("ቃል", "Qal", "Word / Speech", "💬"),
        "ቄ" to WordDetail("ቄስ", "Qes", "Priest", "⛪"),
        "ቅ" to WordDetail("ቅጠል", "Qitel", "Leaf", "🍃"),
        "ቆ" to WordDetail("ቆብ", "Qob", "Hat / Cap", "🎩"),

        // BA (በ) family
        "በ" to WordDetail("በግ", "Beg", "Sheep", "🐑"),
        "ቡ" to WordDetail("ቡና", "Buna", "Coffee", "☕"),
        "ቢ" to WordDetail("ቢራቢሮ", "Birabiro", "Butterfly", "🦋"),
        "ባ" to WordDetail("ባቡር", "Babur", "Train", "🚉"),
        "ቤ" to WordDetail("ቤት", "Bet", "House", "🏠"),
        "ብ" to WordDetail("ብርጭቆ", "Birchiko", "Glass / Tumbler", "🥛"),
        "ቦ" to WordDetail("ቦርሳ", "Borsa", "Bag / Handbag", "👜"),

        // TA (ተ) family
        "ተ" to WordDetail("ተራራ", "Terara", "Mountain", "⛰️"),
        "ቱ" to WordDetail("ቱሊፕ", "Tulip", "Tulip Flower", "🌷"),
        "ቲ" to WordDetail("ቲማቲም", "Timatim", "Tomato", "🍅"),
        "ታ" to WordDetail("ታሪክ", "Tarik", "History / Story", "📚"),
        "ቴ" to WordDetail("ቴሌቪዥን", "Televizhin", "Television", "📺"),
        "ት" to WordDetail("ትራስ", "Tiras", "Pillow / Cushion", "🛌"),
        "ቶ" to WordDetail("ቶፉ", "Tofu", "Tofu", "🧀"),

        // CHA (ቸ) family
        "ቸ" to WordDetail("ቸኮሌት", "Chekolet", "Chocolate", "🍫"),
        "ቹ" to WordDetail("ቹቸ", "Chuche", "Pacifier", "🍼"),
        "ቺ" to WordDetail("ቺፕስ", "Chips", "Potato Chips", "🍟"),
        "ቻ" to WordDetail("ቻው", "Chaw", "Goodbye", "👋"),
        "ቼ" to WordDetail("ቼዝ", "Chez", "Chess", "♟️"),
        "ች" to WordDetail("ችግኝ", "Chigni", "Sapling / Seedling", "🌱"),
        "ቾ" to WordDetail("ቾክ", "Chok", "Chalk", "🖍️"),

        // KHA (ኀ) family
        "ኀ" to WordDetail("ኀይል", "Hayl", "Power / Force", "⚡"),
        "ኁ" to WordDetail("ኁዳዴ", "Hudade", "Lent Fasting", "⛪"),
        "ኂ" to WordDetail("ኂሩት", "Hirut", "Goodness / Mercy", "🙏"),
        "ኃ" to WordDetail("ኃጢአት", "Hatiat", "Sin / Mistake", "🛑"),
        "ኄ" to WordDetail("ኄር", "Her", "Kind / Good", "😇"),
        "ኅ" to WordDetail("ኅብስት", "Hibist", "Traditional Bread", "🍞"),
        "ኆ" to WordDetail("ኆኅት", "Hoht", "Portal / Gateway", "⛩️"),

        // NA (ነ) family
        "ነ" to WordDetail("ነብር", "Nebir", "Leopard / Tiger", "🐆"),
        "ኑ" to WordDetail("ኑሮ", "Nuro", "Life / Living", "🏡"),
        "ኒ" to WordDetail("ኒዮን", "Niyon", "Neon / Light", "💡"),
        "ና" to WordDetail("ናስ", "Nas", "Bronze / Metal", "🥉"),
        "ኔ" to WordDetail("ኔትወርክ", "Netwerk", "Network", "🌐"),
        "ን" to WordDetail("ንብ", "Nib", "Bee", "🐝"),
        "ኖ" to WordDetail("ኖራ", "Nora", "Chalk / Limestone", "🐚"),

        // NYA (ኘ) family
        "ኘ" to WordDetail("ኘው", "Nyew", "Meow / Cat sound", "🐱"),
        "ኙ" to WordDetail("ኙክሊየር", "Nyukliyer", "Nuclear", "☢️"),
        "ኚ" to WordDetail("ኚኝ", "Nyiny", "Grumbly voice", "🗣️"),
        "ኛ" to WordDetail("ኛ", "Nya", "First / Trophy", "🥇"),
        "ኜ" to WordDetail("ኜራ", "Nyera", "Clay Baking pan", "🏺"),
        "ኝ" to WordDetail("ኝኝታ", "Nyinyta", "Humming / Buzzing", "📳"),
        "ኞ" to WordDetail("ኞኞ", "Nyonyo", "Cooing sound", "👶"),

        // A (አ) family
        "አ" to WordDetail("አንበሳ", "Anbessa", "Lion", "🦁"),
        "ኡ" to WordDetail("ኡራኤል", "Urael", "Archangel", "👼"),
        "ኢ" to WordDetail("ኢትዮጵያ", "Ityopya", "Ethiopia", "🇪🇹"),
        "ኣ" to WordDetail("ኣውቶቡስ", "Awtobus", "Bus", "🚌"),
        "ኤ" to WordDetail("ኤሊ", "Eli", "Turtle", "🐢"),
        "እ" to WordDetail("እግር", "Egir", "Foot / Leg", "🦶"),
        "ኦ" to WordDetail("ኦፔራ", "Opera", "Opera / Theater", "🎭"),

        // KA (ከ) family
        "ከ" to WordDetail("ከበሮ", "Kebero", "Drum", "🥁"),
        "ኩ" to WordDetail("ኩባያ", "Kubaya", "Cup / Mug", "☕"),
        "ኪ" to WordDetail("ኪስ", "Kis", "Pocket", "👖"),
        "ካ" to WordDetail("ካርታ", "Karta", "Map / Card", "🗺️"),
        "ኬ" to WordDetail("ኬክ", "Kek", "Cake", "🎂"),
        "ክ" to WordDetail("ክንፍ", "Kinf", "Wing", "🪶"),
        "ኮ" to WordDetail("ኮከብ", "Kokeb", "Star", "⭐"),

        // KXA (ኸ) family
        "ኸ" to WordDetail("ኸሚስ", "Khemis", "Thursday", "📅"),
        "ኹ" to WordDetail("ኹኔታ", "Khuneta", "State / Situation", "🎭"),
        "ኺ" to WordDetail("ኺድ", "Khid", "Go / Walk", "🚶"),
        "ኻ" to WordDetail("ኻላ", "Khala", "Aunt", "👩"),
        "ኼ" to WordDetail("ኼራ", "Khera", "Goodness / Welfare", "🕊️"),
        "ኽ" to WordDetail("ኽዳን", "Khidan", "Lid / Cover", "🪟"),
        "ኾ" to WordDetail("ኾነ", "Khone", "Happened / Became", "✨"),

        // WA (ወ) family
        "ወ" to WordDetail("ወፍ", "Wef", "Bird", "🐦"),
        "ዉ" to WordDetail("ዉሻ", "Wusha", "Dog", "🐶"),
        "ዊ" to WordDetail("ዊኬት", "Wiket", "Wicket / Cricket", "🏏"),
        "ዋ" to WordDetail("ዋንጫ", "Wancha", "Trophy / Cup", "🏆"),
        "ዌ" to WordDetail("ዌብሳይት", "Websayt", "Website", "💻"),
        "ው" to WordDetail("ውኃ", "Wuha", "Water", "💧"),
        "ዎ" to WordDetail("ዎክ", "Wok", "Wok Pan", "🍳"),

        // AA (ዐ) family
        "ዐ" to WordDetail("ዐቢይ", "Abiy", "Great / Grand", "🏔️"),
        "ዑ" to WordDetail("ዑደት", "Udet", "Cycle / Circuit", "🔄"),
        "ዒ" to WordDetail("ዒላማ", "Ilama", "Target / Bullseye", "🎯"),
        "ዓ" to WordDetail("ዓሣ", "Asa", "Fish", "🐟"),
        "ዔ" to WordDetail("ዔሊ", "Eli", "Tortoise", "🐢"),
        "ዕ" to WordDetail("ዕንቁ", "Enqu", "Gem / Jewel", "💎"),
        "ዖ" to WordDetail("ዖፍ", "Of", "Falcon / Raptor", "🦅"),

        // ZA (ዘ) family
        "ዘ" to WordDetail("ዘንዶ", "Zendo", "Python / Dragon", "🐉"),
        "ዙ" to WordDetail("ዙፋን", "Zufan", "Throne", "👑"),
        "ዚ" to WordDetail("ዚፕ", "Zip", "Zipper", "🤐"),
        "ዛ" to WordDetail("ዛፍ", "Zaf", "Tree", "🌳"),
        "ዜ" to WordDetail("ዜና", "Zena", "News", "📰"),
        "ዝ" to WordDetail("ዝሆን", "Zihon", "Elephant", "🐘"),
        "ዞ" to WordDetail("ዞን", "Zon", "Zone / District", "🗺️"),

        // ZHA (ዠ) family
        "ዠ" to WordDetail("ዠንጥላ", "Zhentila", "Umbrella", "☔"),
        "ዡ" to WordDetail("ዡል", "Zhool", "Joule energy", "⚡"),
        "ዢ" to WordDetail("ዢራፍ", "Zhiraf", "Giraffe", "🦒"),
        "ዣ" to WordDetail("ዣንጥላ", "Zhantila", "Umbrella", "☂️"),
        "ዤ" to WordDetail("ዤኔራል", "Zheneral", "General", "🎖️"),
        "ዥ" to WordDetail("ዥረት", "Zhiret", "Stream / Current", "🌊"),
        "ዦ" to WordDetail("ዦሮ", "Zhoro", "Ear / Hearing", "👂"),

        // YA (የ) family
        "የ" to WordDetail("የካቲት", "Yekatit", "February", "📅"),
        "ዩ" to WordDetail("ዩኒቨርሲቲ", "Yuniversiti", "University", "🎓"),
        "ዪ" to WordDetail("ዪኒ", "Yini", "Yin Yang", "☯️"),
        "ያ" to WordDetail("ያዕቆብ", "Yaqob", "Jacob", "🧔"),
        "ዬ" to WordDetail("ዬመን", "Yemen", "Yemen Nation", "🇾🇪"),
        "ይ" to WordDetail("ይቅርታ", "Yiqrta", "Forgiveness / Sorry", "🙏"),
        "ዮ" to WordDetail("ዮርዳኖስ", "Yordanos", "Jordan River", "🌊"),

        // DA (ደ) family
        "ደ" to WordDetail("ደሮ", "Doro", "Rooster / Chicken", "🐔"),
        "ዱ" to WordDetail("ዱባ", "Duba", "Pumpkin", "🎃"),
        "ዲ" to WordDetail("ዲያብሎ", "Diyablo", "Yo-yo toy", "🪀"),
        "ዳ" to WordDetail("ዳቦ", "Dabo", "Bread / Loaf", "🍞"),
        "ዴ" to WordDetail("ዴልታ", "Delta", "Delta / River mouth", "🛕"),
        "ድ" to WordDetail("ድመት", "Dimet", "Cat", "🐱"),
        "ዶ" to WordDetail("ዶልፊን", "Dolfin", "Dolphin", "🐬"),

        // JA (ጀ) family
        "ጀ" to WordDetail("ጀልባ", "Jelba", "Sailing Boat", "⛵"),
        "ጁ" to WordDetail("ጁስ", "Jus", "Fruit Juice", "🥤"),
        "ጂ" to WordDetail("ጂንስ", "Jins", "Jeans trousers", "👖"),
        "ጃ" to WordDetail("ጃኬት", "Jaket", "Jacket", "🧥"),
        "ጄ" to WordDetail("ጄሊ", "Jeli", "Jelly Sweets", "🍬"),
        "ጅ" to WordDetail("ጅብ", "Jib", "Hyena", "🐺"),
        "ጆ" to WordDetail("ጆሮ", "Joro", "Ear", "👂"),

        // GA (ገ) family
        "ገ" to WordDetail("ገበታ", "Gebeta", "Tray / Dish", "🍲"),
        "ጉ" to WordDetail("ጉማሬ", "Gumare", "Hippo / Hippopotamus", "🦛"),
        "ጊ" to WordDetail("ጊታር", "Gitar", "Guitar", "🎸"),
        "ጋ" to WordDetail("ጋሪ", "Gari", "Horse Cart", "🛒"),
        "ጌ" to WordDetail("ጌጥ", "Get", "Jewelry / Ornament", "👑"),
        "ግ" to WordDetail("ግመል", "Gimel", "Camel", "🐫"),
        "ጎ" to WordDetail("ጎጆ", "Gojo", "Hut / Cottage", "🛖"),

        // TA (ጠ) family
        "ጠ" to WordDetail("ጠጅ", "Tej", "Honey Mead wine", "🍯"),
        "ጡ" to WordDetail("ጡብ", "Tub", "Brick / Block", "🧱"),
        "ጢ" to WordDetail("ጢስ", "Tis", "Smoke", "💨"),
        "ጣ" to WordDetail("ጣውላ", "Tawla", "Wooden Board", "🪵"),
        "ጤ" to WordDetail("ጤና", "Tena", "Health / Wellness", "🏥"),
        "ጥ" to WordDetail("ጥርስ", "Tirs", "Tooth / Teeth", "🦷"),
        "ጦ" to WordDetail("ጦር", "Tor", "Spear / Arrow", "🏹"),

        // CH'A (ጨ) family
        "ጨ" to WordDetail("ጨው", "Chew", "Salt", "🧂"),
        "ጩ" to WordDetail("ጩኸት", "Chuhet", "Shouting / Cry", "📢"),
        "ጪ" to WordDetail("ጪስ", "Chis", "Fume / Vapor", "🌫️"),
        "ጫ" to WordDetail("ጫማ", "Chama", "Shoes", "👟"),
        "ጬ" to WordDetail("ጬቤ", "Chebe", "Local Flatbread", "🫓"),
        "ጭ" to WordDetail("ጭቃ", "Chika", "Mud / Wet clay", "🏺"),
        "ጮ" to WordDetail("ጮማ", "Choma", "Ribeye / Fat Steak", "🥩"),

        // P'A (ጰ) family
        "ጰ" to WordDetail("ጰራቅሊጦስ", "Peraqlitos", "Church / Chapel", "⛪"),
        "ጱ" to WordDetail("ጱጵና", "Pupsina", "Priestly rank", "📜"),
        "ጲ" to WordDetail("ጲላጦስ", "Pilatos", "Roman Governor", "🏛️"),
        "ጳ" to WordDetail("ጳጳስ", "Papas", "Pope / Bishop", "👑"),
        "ጴ" to WordDetail("ጴጥሮስ", "Petros", "Apostle Peter", "🗝️"),
        "ጵ" to WordDetail("ጵጵስና", "Pipsisna", "Episcopal status", "🎓"),
        "ጶ" to WordDetail("ጶሊስ", "Polis", "Police", "👮"),

        // TS'A (ጸ) family
        "ጸ" to WordDetail("ጸሎት", "Tselot", "Prayer / Devotion", "🙏"),
        "ጹ" to WordDetail("ጹፉህ", "Tsufuh", "Polished / Neat", "✨"),
        "ጺ" to WordDetail("ጺም", "Tsim", "Beard", "🧔"),
        "ጻ" to WordDetail("ጻድቅ", "Tsadik", "Saint / Righteous", "😇"),
        "ጼ" to WordDetail("ጼና", "Tsena", "Aroma / Incense scent", "🌸"),
        "ጽ" to WordDetail("ጽጌሬዳ", "Tsigereda", "Red Rose", "🌹"),
        "ጾ" to WordDetail("ጾም", "Tsom", "Fasting", "🥗"),

        // TS'S'A (ፀ) family
        "ፀ" to WordDetail("ፀሐይ", "Tsehay", "Sun / Sunlight", "☀️"),
        "ፁ" to WordDetail("ፁም", "Tsum", "Lent Season", "🥣"),
        "ፂ" to WordDetail("ፂም", "Tsim", "Mustache / Whiskers", "🧔"),
        "ፃ" to WordDetail("ፃዕር", "Tsa'ir", "Effort / Struggle", "🏋️"),
        "ፄ" to WordDetail("ፄና", "Tsena", "Liquid Fragrance", "🧴"),
        "ፅ" to WordDetail("ፅዋ", "Tsiwa", "Chalice / Cup", "🏆"),
        "ፆ" to WordDetail("ፆታ", "Tsota", "Gender", "🚻"),

        // FA (ፈ) family
        "ፈ" to WordDetail("ፈረስ", "Feres", "Horse", "🐎"),
        "ፉ" to WordDetail("ፉጨት", "Fuchet", "Whistles", "😗"),
        "ፊ" to WordDetail("ፊኛ", "Finya", "Balloon", "🎈"),
        "ፋ" to WordDetail("ፋኖስ", "Fanos", "Lantern", "🏮"),
        "ፌ" to WordDetail("ፌስቡክ", "Facebook", "Facebook App", "📱"),
        "ፍ" to WordDetail("ፍየል", "Fiyel", "Goat", "🐐"),
        "ፎ" to WordDetail("ፎቶ", "Foto", "Photograph / Camera", "📷"),

        // PA (ፐ) family
        "ፐ" to WordDetail("ፐርሰንት", "Persent", "Percentage", "📊"),
        "ፑ" to WordDetail("ፑል", "Pool", "Snooker Billiards", "🎱"),
        "ፒ" to WordDetail("ፒያኖ", "Piyano", "Piano / Keyboard", "🎹"),
        "ፓ" to WordDetail("ፓስታ", "Pasta", "Spaghetti Pasta", "🍝"),
        "ፔ" to WordDetail("ፔንሲል", "Pensil", "Lead Pencil", "✏️"),
        "ፕ" to WordDetail("ፕላኔት", "Planet", "Planet Earth", "🌍"),
        "ፖ" to WordDetail("ፖስታ", "Posta", "Post envelope", "✉️")
    )

    fun getWord(char: String): WordDetail {
        return map[char] ?: WordDetail(char, char, "Sample Word", "✨")
    }
}

