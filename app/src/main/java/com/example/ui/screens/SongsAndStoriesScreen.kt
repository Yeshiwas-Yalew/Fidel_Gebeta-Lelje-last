package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import androidx.compose.foundation.horizontalScroll
import com.example.ui.ChildSong
import com.example.ui.FidelViewModel
import com.example.ui.InstrumentStyle
import org.json.JSONObject
import org.json.JSONArray

data class SongModel(
    val id: String,
    val titleAmharic: String,
    val titleEnglish: String,
    val emoji: String,
    val childSong: ChildSong,
    val lyrics: List<LyricsLine>
)

data class LyricsLine(
    val amharic: String,
    val translit: String,
    val english: String,
    val ttsSpeech: String
)

data class StoryModel(
    val id: String,
    val titleAmharic: String,
    val titleEnglish: String,
    val emoji: String,
    val moralAmharic: String,
    val paragraphs: List<String>,
    val englishParagraphs: List<String>
)

val staticSongsList = listOf(
    SongModel(
        "yegna_dimet",
        "የእኛ ድመት",
        "Our Beautiful Cat",
        "🐈",
        ChildSong.YEGNA_DIMET,
        listOf(
            LyricsLine("የእኛ ድመት ቆንጆ ናት፥", "Yegna dimet qonjo nat", "Our cat is very beautiful,", "የእኛ ድመት ቆንጆ ናት"),
            LyricsLine("ያየቻትን አይጥ ሁሉ ትይዛለች፥", "Yayechatin ayt hulu tiyizalech", "She catches every mouse she sees,", "ያየቻትን አይጥ ሁሉ ትይዛለች"),
            LyricsLine("በጣም ትወደናለች፥", "Betam tiwedenalech", "She loves us very much,", "በጣም ትወደናለች"),
            LyricsLine("ሚዩ ሚዩ ትላለች።", "Miyu miyu tilalech", "Saying meow, meow!", "ሚዩ ሚዩ ትላለች")
        )
    ),
    SongModel(
        "ababa_tesfaye",
        "አባባ ተስፋዬ",
        "Dear Father Tesfaye",
        "🐴",
        ChildSong.ABABA_TESFAYE,
        listOf(
            LyricsLine("አባባ ተስፋዬ፥ በቅሎው አገላ፥", "Ababa Tesfaye beqlow agela", "Father Tesfaye on his brown mule,", "አባባ ተስፋዬ በቅሎው አገላ"),
            LyricsLine("ጭነው ጭነው፥ ወደ ገበያ፥", "Chinew chinew wede gebeya", "Packing items to go to the market,", "ጭነው ጭነው ወደ ገበያ"),
            LyricsLine("ቆንጆ ሙዝ፥ ገዙልኝ ለእኔ፥", "Qonjo muz gezulign leyne", "He bought tasty bananas for me,", "ቆንጆ ሙዝ ገዙልኝ ለእኔ"),
            LyricsLine("እወዳቸዋለሁ፥ እኔ በሆዴ።", "Iwedachewalehu ine behode", "I love him from the bottom of my heart!", "እወዳቸዋለሁ እኔ በሆዴ")
        )
    ),
    SongModel(
        "fidel_desta",
        "ፊደላት በደስታ",
        "Letters of Joy",
        "📚",
        ChildSong.FIDEL_DESTA,
        listOf(
            LyricsLine("ሀ ሁ ሂ ሃ ሄ ህ ሆ፥", "Ha Hu Hi Ha He Hi Ho", "Ha Hu Hi Ha He Hi Ho,", "ሀ ሁ ሂ ሃ ሄ ህ ሆ"),
            LyricsLine("ፊደላትን እንማር፥", "Fidelatin innimar", "Let's learn our letters,", "ፊደላትን እንማር"),
            LyricsLine("ለ ሉ ሊ ላ ሌ ል ሎ፥", "La Lu Li La Le Li Lo", "La Lu Li La Le Li Lo,", "ለ ሉ ሊ ላ ሌ ል ሎ"),
            LyricsLine("እውቀት ይጨምርልን።", "Iwqet yichemrilin", "May our wisdom grow!", "እውቀት ይጨምርልን")
        )
    ),
    SongModel(
        "twinkle",
        "ብልጭ ብልጭ ኮከብ",
        "Twinkle Twinkle",
        "🌟",
        ChildSong.TWINKLE,
        listOf(
            LyricsLine("ብልጭ ብልጭ ኮከብ፥", "Bilich bilich kokeb", "Twinkle twinkle little star,", "ብልጭ ብልጭ ኮከብ"),
            LyricsLine("በሰማይ ላይ የምታበራ፥", "Besemay lay yemtabera", "Shining bright in the night sky,", "በሰማይ ላይ የምታበራ"),
            LyricsLine("እንደ አልማዝ ውብ ሆነህ፥", "Inde almaz wub honeh", "Like a beautiful diamond,", "እንደ አልማዝ ውብ ሆነህ"),
            LyricsLine("ሁሉንም ታስደስታለህ።", "Hulunim tasdestaleh", "You bring joy to everyone.", "ሁሉንም ታስደስታለህ")
        )
    ),
    SongModel(
        "mary_lamb",
        "ትንሿ በግ",
        "Little Lamb",
        "🐑",
        ChildSong.MARY_LAMB,
        listOf(
            LyricsLine("ማርያም ትንሽ በግ አላት፥", "Maryam tinish beg alat", "Mary had a little lamb,", "ማርያም ትንሽ በግ አላት"),
            LyricsLine("ሱፍዋ እንደ በረዶ የነጣ፥", "Sufwa inde beredo yeneta", "Whose fleece was white as snow,", "ሱፍዋ እንደ በረዶ የነጣ"),
            LyricsLine("የትም ቦታ ብትሄድ፥", "Yetim bota bithihed", "And everywhere that Mary went,", "የትም ቦታ ብትሄድ"),
            LyricsLine("በጓ አትለይማት።", "Begwa atleyimat", "The lamb was sure to go.", "በጓ አትለይማት")
        )
    ),
    SongModel(
        "row_boat",
        "ጀልባህን ንዳ",
        "Row Your Boat",
        "🚣",
        ChildSong.ROW_BOAT,
        listOf(
            LyricsLine("ንዳ ንዳ ጀልባህን፥", "Nida nida jelbahin", "Row, row, row your boat,", "ንዳ ንዳ ጀልባህን"),
            LyricsLine("በቀስታ በወንዙ ላይ፥", "Beqesta bewenzu lay", "Gently down the stream,", "በቀስታ በወንዙ ላይ"),
            LyricsLine("በደስታ በደስታ፥", "Bedesta bedesta", "Merrily, merrily, merrily,", "በደስታ በደስታ"),
            LyricsLine("ሕይወት ደስ የሚል ህልም ናት።", "Hiywet des yemil hilm nat", "Life is but a beautiful dream.", "ሕይወት ደስ የሚል ህልም ናት")
        )
    ),
    SongModel(
        "brother_john",
        "ወንድም ዮሐንስ",
        "Brother John",
        "🔔",
        ChildSong.BROTHER_JOHN,
        listOf(
            LyricsLine("ወንድም ዮሐንስ፥ ወንድም ዮሐንስ፥", "Wendim Yohannis Wendim Yohannis", "Are you sleeping, Brother John?", "ወንድም ዮሐንስ ወንድም ዮሐንስ"),
            LyricsLine("ገና ተኝተሃል? ገና ተኝተሃል?", "Gena tegnytehal Gena tegnytehal", "Are you still sleeping? Are you still sleeping?", "ገና ተኝተሃል? ገና ተኝተሃል?"),
            LyricsLine("ደወሉ ይደውላል፥ ደወሉ ይደውላል፥", "Dewelu yidewilal Dewelu yidewilal", "The morning bells are ringing,", "ደወሉ ይደውላል ደወሉ ይደውላል"),
            LyricsLine("ዲንግ ዲንግ ዶንግ! ዲንግ ዲንግ ዶንግ!", "Ding ding dong Ding ding dong", "Ding, ding, dong! Ding, ding, dong!", "ዲንግ ዲንግ ዶንግ ዲንግ ዲንግ ዶንግ")
        )
    )
)

val staticStoriesList = listOf(
    StoryModel(
        "lion_mouse",
        "አንበሳውና አይጧ",
        "The Lion and the Mouse",
        "🦁",
        "ትንሽም ቢሆን ትልቅ እርዳታ ሊያደርግ ይችላል። (Even a small helper can make a big difference!)",
        listOf(
            "በአንድ ወቅት፥ አንድ ቶኒ የሚባል በጣም ሰነፍና የሚያንኮራፋ ትልቅ አንበሳ በጥልቁ ጫካ ውስጥ ይኖር ነበር። ቶኒ በየቀኑ ለ20 ሰዓታት ይተኛል፤ ሲያንኮራፋም ጫካው በሙሉ እንደ ቡና መፍጫ ማሽን ይንቀጠቀጥ ነበር። አንድ ቀን አንዲት ሮዝ የምትባል በጣም ቀልደኛና ፈጣን አይጥ በቶኒ ጅራት ላይ ስትጫወት ቆይታ፥ በድንገት አፍንጫው ላይ ወጥታ 'እንዳልንሸራተት ጫፉን ልያዘው!' ስትል አፍንጫውን ቆነጠጠችውና ከእንቅልፉ አነቃችው።",
            "ቶኒ በጣም ተቆጥቶ በታላቅ ድምፅ አገሳና ሮዝን በትላልቅ ጥፍሮቹ ይዞ 'አንቺ ትንሿ ፍጡር እንቅልፌን ያቋረጥሽው እራት ልትሆኚኝ ነው!' አላት። ሮዝ ግን በጭራሽ ሳትደነግጥ ሳቀችና 'አንበሳዬ እባክህ አትብላኝ! እኔ እኮ የጫካው ምርጥ ኮሜዲያን ነኝ፤ ዛሬ ብትለቀኝ በሚቀጥለው ጊዜ በአስቸጋሪ ሁኔታ ውስጥ ስትሆን በጣም የሚያስቅ ቀልድ ነግሬ አድንሃለሁ!' ብላ ለመነችው። ቶኒም 'የአይጥ ኮሜዲያን!' ብሎ በመገረም ስቆ ለቀቃት።",
            "ከጥቂት ቀናት በኋላ፥ ቶኒ ሰነፍ ስለነበር አይኑን ጨፍኖ ሲጓዝ በአዳኞች የተዘረጋ ትልቅ መረብ ውስጥ ተጠላለፈ። ምንም መውጫ አጥቶ እንደ መኪና ጥሪ ሲጮህ፥ ሮዝ ድምፁን ሰምታ በፍጥነት መጣች። ወዲያውኑ 'አይዞህ አንበሳዬ! አሁን መረቡን በጥርሴ እየቆረጥኩ ሳሳቅህ አያለሁ!' ብላ ጀመረች። ሮዝ መረቡን በጥርሷ እየቆረጠች 'ለምንድነው አንበሳ ከብት የማይበላው? ምክንያቱም የቬጀቴሪያን ኮፍያ ስላለው!' እያለች አስቂኝ ቀልዶችን ትነግረው ነበር። ቶኒ በሳቅ እየፈነዳ መረቡን ረሳውና ወዲያውኑ በነፃነት ተለቀቀ። ከዚያን ቀን ጀምሮ ቶኒና ሮዝ ምርጥ የኮሜዲ ጓደኛሞች ሆኑ!"
        ),
        listOf(
            "Once upon a time, there lived a very lazy and loud-snoring big lion named Tony in the deep forest. Tony slept for 20 hours a day, and when he snored, the entire forest shook like a coffee grinder. One day, a very playful and fast mouse named Rose was playing on Tony's tail, and suddenly ended up on his nose, pinching it to avoid slipping, which woke him up.",
            "Tony was extremely angry, roared loudly, and caught Rose with his giant claws, saying, 'You tiny creature, you interrupted my sleep to become my dinner!' But Rose, not panicking at all, giggled and said, 'Oh lion, please don't eat me! I am the forest's best comedian; if you let me go, I will save you next time with a super funny joke!' Tony laughed at the idea of a 'mouse comedian' and let her go.",
            "A few days later, because Tony was walking with his eyes closed out of laziness, he got tangled in a huge net set by hunters. Unable to escape, he roared like a car horn, and Rose heard him and rushed over. She said, 'Don't worry, my lion! I'll gnaw this net while telling you a hilarious joke!' While chewing the ropes, Rose joked, 'Why don't lions eat grass? Because they don't want to green-out!' Tony laughed so hard that he forgot his troubles, and was soon free. From that day on, Tony and Rose became the ultimate comedy duo!"
        )
    ),
    StoryModel(
        "fox_grapes",
        "ቀበሮዋና ወይኑ",
        "The Fox and the Grapes",
        "🦊",
        "ማግኘት ያልቻልነውን ነገር መጥፎ ነው ማለት ቀላል ነው። (It's easy to dislike what you cannot get.)",
        listOf(
            "ፊፊ የምትባል በጣም ብልጥ፣ ቄንጠኛና ቀይ ጫማ ማድረግ የምትወድ ቀበሮ በጥልቁ ጫካ ውስጥ ትኖር ነበር። ፊፊ ሁልጊዜ በጫካው እንስሳት ፊት ራሷን እንደ ታላቅ የላቀ ዘላይ አድርጋ ታቀርብ ነበር። አንድ ቀን በጣም ተርባ ሳለች፥ በአንድ ትልቅ አትክልት ስፍራ ውስጥ እጅግ በጣም ረጅም በሆነ ዛፍ ላይ የተንጠለጠሉ በጣም ትላልቅና የሚያብረቀርቁ ወይኖችን አየች። 'እነዚህን ወይኖች በአንድ ዝላይ ብቻ ወስጄ በዓለም ላይ ካሉት ምግቦች ሁሉ የሚጣፍጠውን እበላለሁ!' ብላ ወሰነች።",
            "ወዲያውኑ ፊፊ ቄንጠኛ ቀይ ጫማዎቿን አጥብቃ አስራ፥ ከሩቅ እየሮጠች 'ሁፕ!' ብላ ወደ ሰማይ ዘለለች። ነገር ግን ወይኑ በጣም ረጅም ስለነበር፥ ፊፊ በአየር ላይ ሆና በሁለት እጆቿ ወይኑን ለመያዝ ስትሞክር ሚዛኗን ስታ አንድ ትልቅና ጭቃ በበዛበት የውኃ ማጠራቀሚያ ገንዳ ውስጥ 'ፕላሽ!' ብላ ገባች። እንስሳቱ ሁሉ ይህንን አይተው በሳቅ መሬት ላይ ተንከባለሉ፤ ፊፊ ግን ፊቷን በቅጠል እየጠረገች 'ይህ እኮ የዳንሴ አካል ነው፤ ዝም ብዬ በጭቃ ለመታጠብ ፈልጌ ነው!' ብላ ዋሸች።",
            "ፊፊ ተስፋ ሳትቆርጥ ለሁለተኛና ለሦስተኛ ጊዜ ደጋግማ ዘለለች፤ ነገር ግን በእያንዳንዱ ዝላይ ራሷን በቅርንጫፍ ላይ እየመታች በመጨረሻም መሬት ላይ በሆዷ ተንሸራተተች። በጣም ከመድከሟና ሆዷ በረሃብ መጮህ ከመጀመሩ የተነሳ፥ ወይኑን እያየች አፏን አጣመመችና 'ይህ ወይን እኮ በጣም ኮምጣጣና የበሰበሰ ነው! በዚያ ላይ ወይን መብላት ለቀበሮዎች ጥርስ በጣም መጥፎ ነው፤ እኔ ደግሞ ጤናማ ሳር መብላት እመርጣለሁ!' ብላ ሳሯን እያኘከች በኩራት ሄደች። እንስሳቱ ሁሉ በፊፊ ኩራትና ቀልድ እየሳቁ አጃቢ አደረጉላት።"
        ),
        listOf(
            "Fifi was a very clever, stylish red fox who loved wearing fancy red shoes. Fifi always bragged to the other forest animals about being a world-class high-jumper. One day, while very hungry, she saw huge, sparkling grapes hanging from a very tall vine in a beautiful garden. 'I will grab these grapes in a single jump and eat the most delicious meal in the world!' she decided.",
            "Fifi tightened her red shoes, ran from a distance, and leaped high into the air with a loud 'Hoop!'. However, because the grapes were too high, Fifi lost her balance mid-air while trying to grab them and landed splash! into a large, muddy pool. All the watching animals rolled on the ground laughing, but Fifi, wiping her face with a leaf, lied, 'This was just part of my muddy dance routine!'",
            "Fifi didn't give up and jumped a second and third time, but with each jump, she bumped into branches and slid flat on her belly. Exhausted and with her stomach growling, she made a sour face at the grapes and declared, 'These grapes are extremely sour and rotten anyway! Besides, eating grapes is terrible for a fox's teeth; I prefer healthy grass instead!' and walked away proudly chewing on a leaf. All the animals laughed at Fifi's proud excuses."
        )
    ),
    StoryModel(
        "clever_rabbit",
        "ብልሃተኛው ጥንቸል",
        "The Clever Rabbit",
        "🐰",
        "ጉልበት ሳይሆን ብልሃት ያሸንፋል። (Wisdom and intelligence triumph over brute force.)",
        listOf(
            "በአንድ ጫካ ውስጥ ቦንጎ የሚባል እጅግ በጣም ክፉ፣ ቁጡና ምንም ቀልድ የማይወድ ትልቅ አንበሳ ይኖር ነበር። ቦንጎ ሁልጊዜ እንስሳቱን በኃይል እያስፈራራ በየቀኑ አንዱን እንስሳ ለእራት እንዲመጣለት ያዝዝ ነበር። እንስሳቱ በሙሉ በቦንጎ ቁጣና በየቀኑ በሚያሰማው ጩኸት እየተንቀጠቀጡ ይኖሩ ነበር። በአንድ ወቅት፥ ተራው በጣም ቀልደኛ、 ፈጣንና ትላልቅ ጆሮዎች ያሉት ፒተር ለሚባለው ጥንቸል ደረሰ። ፒተር ግን ለመሸበር ፈቃደኛ አልነበረም፤ ይልቁንም 'ዛሬ ለዚህ ቁጡ አንበሳ ጥሩ አስቂኝ ትምህርት እሰጠዋለሁ!' ብሎ አቀደ።",
            "ፒተር ሆን ብሎ በጣም ዘግይቶ እራት ሰዓት ሲያልፍ መጣ። ቦንጎ በረሃብ ሆዱ እየጮኸና በጣም ተቆጥቶ 'አንተ ትንሿ ጥንቸል! ለምንድነው የዘገየኸው? ዛሬ በአንድ ጉሮሮ ነው የምውጥህ!' ብሎ ጮኸ። ፒተር ግን በረጋ መንፈስ ቆሞ 'ጌታዬ አንበሳ፥ እኔ እኮ በጊዜ እመጣ ነበር፤ ነገር ግን በመንገድ ላይ ሌላ በጣም ትልቅና በራሱ ላይ የወርቅ አክሊል ያደረገ አንበሳ አገኘሁኝ። እሱም 'እኔ የዚህ ጫካ እውነተኛ ንጉስና ምርጥ ዳንሰኛ ነኝ፤ ያ ቦንጎ የሚባለው ሰነፍ አንበሳ ምንም አይችልም' ብሎ ሰደበህ!' አለው።",
            "ቦንጎ ይህንን ሲሰማ ጆሮዎቹን አቆመና 'ምን! በራሱ ላይ የወርቅ አክሊል ያደረገ ዝላይ አፍቃሪ አንበሳ? የት ነው ያለው? አሁኑኑ አሳየኝ!' እያለ በቁጣ ዘለለ። ፒተርም እየሳቀ ወደ አንድ ጥልቅና ንጹህ የውኃ ጉድጓድ ወሰደው። 'እዚህ ውስጥ ገብተህ እይ፥ እዚያው ተደብቆ ወርቁን እያጸዳ ነው!' አለው። ቦንጎ ወደ ጉድጓዱ ሲመለከት የራሱን የቁጣ ፊትና ጥላ አየ፤ ሌላኛው አንበሳ መስሎት 'ሄይ!' ብሎ ሲጮህ የራሱ ድምፅ በድምፅ ማሚቶ ተመልሶ መጣ። ቦንጎ በቁጣ 'አሁን አሳይሃለሁ!' ብሎ ወደ ጉድጓዱ ዘሎ ገባ። መውጣት አቅቶት በውኃው ውስጥ ሲንቦጫረቅ፥ ፒተር 'ንጉስ ቦንጎ ሆይ፥ በጉድጓዱ ውስጥ በደንብ ዋና ተለማመድ!' ብሎት እየሳቀ ወደ ጫካው ተመለሰ። እንስሳቱ በሙሉ በሰላምና በሳቅ ኖሩ።"
        ),
        listOf(
            "In the deep forest, there lived a very cruel, short-tempered big lion named Bongo who hated jokes. Bongo always terrified the animals, demanding that one of them come to him each day for dinner. All the animals lived in constant fear of his roaring. One day, the turn came for a very funny, quick, long-eared rabbit named Peter. Peter refused to panic, thinking, 'Today, I will teach this grumpy lion a hilarious lesson!'",
            "Peter intentionally arrived very late, long after dinnertime. Bongo, with a growling stomach and extreme anger, roared, 'You tiny rabbit! Why are you so late? I will swallow you in one gulp!' But Peter stood calmly and replied, 'My lord Bongo, I was coming on time, but on the way, I met another huge lion wearing a golden crown. He insulted you, saying: I am the real king and the best dancer of this forest, while Bongo is just a lazy amateur!'",
            "Hearing this, Bongo raised his ears in fury, shouting, 'What! A crown-wearing, jumping lion? Where is he? Show me right now!' Peter giggled and led him to a deep, crystal-clear water well. 'Look inside, he is hiding there polishing his crown!' Bongo looked down, saw his own angry reflection, and when he roared 'Hey!', his voice echoed back. Thinking it was the rival mocking him, Bongo jumped straight into the well. As he splashed around unable to climb out, Peter waved, 'King Bongo, enjoy your swimming lessons!' and happily hopped away. All the animals lived in peace and laughter."
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsAndStoriesScreen(
    viewModel: FidelViewModel,
    userProgress: UserProgress,
    onBack: () -> Unit
) {
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopBgMusicExiting()
        }
    }

    var activeTab by remember { mutableStateOf(0) } // 0 = Songs, 1 = Stories
    var selectedSong by remember { mutableStateOf<SongModel?>(null) }
    var selectedStory by remember { mutableStateOf<StoryModel?>(null) }
    
    // Custom Generated Story via Gemini State
    var geminiTopicInput by remember { mutableStateOf("") }
    var geminiGeneratedStory by remember { mutableStateOf<StoryModel?>(null) }
    var isGeminiLoading by remember { mutableStateOf(false) }
    var storySearchQuery by remember { mutableStateOf("") }

    // TTS Highlighting state for paragraph read aloud
    var activeLyricsIndex by remember { mutableStateOf<Int?>(null) }
    var activeParagraphIndex by remember { mutableStateOf<Int?>(null) }

    // Resolve the custom generated hero image safely with a reflex fallback
    val heroImageId = remember {
        try {
            com.example.R.drawable::class.java.getField("img_songs_stories_hero_1782983346265").getInt(null)
        } catch (e: Exception) {
            android.R.drawable.ic_menu_gallery
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ዜማና ተረት 🎶📖",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.stopStorySpeech()
                            if (selectedSong != null) {
                                selectedSong = null
                                activeLyricsIndex = null
                            } else if (selectedStory != null) {
                                selectedStory = null
                                activeParagraphIndex = null
                            } else if (geminiGeneratedStory != null) {
                                geminiGeneratedStory = null
                                activeParagraphIndex = null
                            } else {
                                onBack()
                            }
                        },
                        modifier = Modifier.testTag("songs_stories_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Check if we are viewing a specific song detail
            if (selectedSong != null) {
                val song = selectedSong!!
                SongDetailView(
                    song = song,
                    viewModel = viewModel,
                    activeLyricsIndex = activeLyricsIndex,
                    onLyricsClick = { index, lyrics ->
                        activeLyricsIndex = index
                        viewModel.speak(lyrics.ttsSpeech, lyrics.translit)
                    },
                    onClose = {
                        selectedSong = null
                        activeLyricsIndex = null
                    }
                )
            }
            // Check if we are viewing a specific story detail (static or generated)
            else if (selectedStory != null || geminiGeneratedStory != null) {
                val story = selectedStory ?: geminiGeneratedStory!!
                StoryDetailView(
                    story = story,
                    viewModel = viewModel,
                    activeParagraphIndex = activeParagraphIndex,
                    onParagraphClick = { index, text ->
                        activeParagraphIndex = index
                        viewModel.speak(text, text)
                    },
                    onTitleClick = {
                        viewModel.speakStory(story.paragraphs) { idx ->
                            activeParagraphIndex = idx
                        }
                    },
                    onClose = {
                        viewModel.stopStorySpeech()
                        selectedStory = null
                        geminiGeneratedStory = null
                        activeParagraphIndex = null
                    }
                )
            }
            // Main Selector / List Hub Screen
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Kids Illustration Banner
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = heroImageId),
                                contentDescription = "Happy Amharic Learning Banner",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.35f))
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = "Yeneta's Cozy Corner 🌟",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Sing lovely songs & listen to magical tales!",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    // Kid-Friendly Soft Tab Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                activeTab = 0
                                viewModel.speak("Let's sing children's songs!", "Let's sing children's songs!")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent,
                                contentColor = if (activeTab == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("tab_songs")
                        ) {
                            Text("መዝሙሮች 🎶", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Button(
                            onClick = {
                                activeTab = 1
                                viewModel.speak("Let's listen to moral stories!", "Let's listen to moral stories!")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeTab == 1) MaterialTheme.colorScheme.primary else Color.Transparent,
                                contentColor = if (activeTab == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("tab_stories")
                        ) {
                            Text("ተረቶች 📖", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    // Render selected tab list
                    if (activeTab == 0) {
                        // SONGS LIST
                        Text(
                            text = "Tap a song to play melody & sing along! 🎤🦁",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(bottom = 12.dp)
                        )

                        // Interactive Global Music Box Controls
                        val bgMusicEnabled by viewModel.bgMusicEnabled.collectAsState()
                        val currentMelody by viewModel.selectedSong.collectAsState()

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .testTag("global_music_box_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(
                                                    color = if (bgMusicEnabled) Color(currentMelody.colorHex) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (bgMusicEnabled) currentMelody.emoji else "🔇",
                                                fontSize = 24.sp
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = if (bgMusicEnabled) "Melody Playing: ${currentMelody.displayName}" else "Melody Paused",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                            Text(
                                                text = if (bgMusicEnabled) "Sing along with the notes below!" else "Tap play on any song to hear its tune!",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                            )
                                        }
                                    }

                                    Switch(
                                        checked = bgMusicEnabled,
                                        onCheckedChange = { viewModel.toggleBgMusic() },
                                        modifier = Modifier.testTag("global_music_switch")
                                    )
                                }

                                if (bgMusicEnabled) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.12f))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    
                                    val selectedInst by viewModel.selectedInstrument.collectAsState()
                                    
                                    Text(
                                        text = "የሙዚቃ ዓይነት (Instrument Sound Style):",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState())
                                    ) {
                                        InstrumentStyle.values().forEach { style ->
                                            val isSel = selectedInst == style
                                            FilterChip(
                                                selected = isSel,
                                                onClick = {
                                                    viewModel.selectInstrument(style)
                                                },
                                                label = { Text(style.displayName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                                leadingIcon = { Text(style.emoji, fontSize = 12.sp) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        staticSongsList.forEach { song ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clickable {
                                        selectedSong = song
                                        viewModel.selectSong(song.childSong)
                                        viewModel.speak("Let's sing ${song.titleEnglish}!", "Let's sing!")
                                    }
                                    .testTag("song_item_${song.id}"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                                ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(CircleShape)
                                                .background(Color(song.childSong.colorHex).copy(alpha = 0.3f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(song.emoji, fontSize = 32.sp)
                                        }

                                        Column {
                                            Text(
                                                text = song.titleAmharic,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = song.titleEnglish,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }

                                    FilledIconButton(
                                        onClick = {
                                            selectedSong = song
                                            viewModel.selectSong(song.childSong)
                                            viewModel.speak("Let's sing ${song.titleEnglish}!", "Let's sing!")
                                        },
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = Color(song.childSong.colorHex)
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Sing Along"
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // STORIES LIST (With custom Gemini generation)
                        val fullStoriesList = remember { staticStoriesList + funnyStoriesList }
                        val filteredStories = remember(storySearchQuery) {
                            if (storySearchQuery.isEmpty()) {
                                fullStoriesList
                            } else {
                                fullStoriesList.filter {
                                    it.titleAmharic.contains(storySearchQuery, ignoreCase = true) ||
                                    it.titleEnglish.contains(storySearchQuery, ignoreCase = true) ||
                                    it.id.contains(storySearchQuery, ignoreCase = true)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Choose a tale of wisdom below! 📖✨",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.weight(1f)
                            )

                            // Surprise Me / Random Story Button!
                            Button(
                                onClick = {
                                    val randomStory = fullStoriesList.random()
                                    selectedStory = randomStory
                                    viewModel.speak("Let's read ${randomStory.titleEnglish}!", "Let's read!")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("surprise_me_story_btn")
                            ) {
                                Text("🎲 Surprise Me!", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Search Text Field with high contrast Material 3 style
                        OutlinedTextField(
                            value = storySearchQuery,
                            onValueChange = { storySearchQuery = it },
                            placeholder = { Text("ተረቶችን ይፈልጉ / Search 50+ stories...", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            },
                            trailingIcon = {
                                if (storySearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { storySearchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear search",
                                            tint = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .testTag("story_search_input")
                        )

                        if (filteredStories.isEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "የሚስማማ ተረት አልተገኘም።",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "No matching story found. Try another word!",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        } else {
                            filteredStories.forEach { story ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                        .clickable {
                                            selectedStory = story
                                            viewModel.speak("Let's read ${story.titleEnglish}!", "Let's read!")
                                        }
                                        .testTag("story_item_${story.id}"),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                                    ),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(story.emoji, fontSize = 32.sp)
                                            }

                                            Column {
                                                Text(
                                                    text = story.titleAmharic,
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = story.titleEnglish,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                        }

                                        FilledIconButton(
                                            onClick = {
                                                selectedStory = story
                                                viewModel.speak("Let's read ${story.titleEnglish}!", "Let's read!")
                                            },
                                            colors = IconButtonDefaults.filledIconButtonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MenuBook,
                                                contentDescription = "Read Story"
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // GEMINI AI CUSTOM STORYTELLER CARD
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .testTag("gemini_storybuilder_card"),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🤖", fontSize = 26.sp)
                                    }
                                    Column {
                                        Text(
                                            text = "Yeneta's Custom Story Creator",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Ask our Gemini Tutor to write any story you want in Amharic!",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = geminiTopicInput,
                                    onValueChange = { geminiTopicInput = it },
                                    placeholder = { Text("E.g., A funny little monkey, or a brave little puppy", fontSize = 13.sp) },
                                    label = { Text("What should the story be about?", fontWeight = FontWeight.Bold) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("gemini_story_topic_input"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                if (isGeminiLoading) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    ) {
                                        CircularProgressIndicator()
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Yeneta is dipping her magical pen... ✍️✨",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            if (geminiTopicInput.trim().isEmpty()) {
                                                viewModel.speak("Please enter a story topic!", "Please enter a story topic!")
                                                return@Button
                                            }
                                            viewModel.speak("Generating your custom story with Gemini!", "Writing story!")
                                            isGeminiLoading = true
                                            viewModel.generateCustomStory(geminiTopicInput) { responseJson ->
                                                isGeminiLoading = false
                                                if (responseJson != null) {
                                                    try {
                                                        val title = responseJson.optString("title", "የልጅ ተረት")
                                                        val amhParagraphs = mutableListOf<String>()
                                                        val engParagraphs = mutableListOf<String>()
                                                        
                                                        val pars = responseJson.optJSONArray("paragraphs")
                                                        if (pars != null) {
                                                            for (i in 0 until pars.length()) {
                                                                amhParagraphs.add(pars.getString(i))
                                                            }
                                                        } else {
                                                            amhParagraphs.add("ተረት ተረት!")
                                                        }

                                                        val engPars = responseJson.optJSONArray("englishTranslation")
                                                        if (engPars != null) {
                                                            for (i in 0 until engPars.length()) {
                                                                engParagraphs.add(engPars.getString(i))
                                                            }
                                                        } else {
                                                            engParagraphs.add("Moral stories of wisdom.")
                                                        }

                                                        val moral = responseJson.optString("moral", "ጓደኝነት ትልቅ ነገር ነው።")

                                                        geminiGeneratedStory = StoryModel(
                                                            id = "gemini_story",
                                                            titleAmharic = title,
                                                            titleEnglish = "Your Custom Tale 🌟",
                                                            emoji = "🌟",
                                                            moralAmharic = moral,
                                                            paragraphs = amhParagraphs,
                                                            englishParagraphs = engParagraphs
                                                        )
                                                        viewModel.speak("Here is your story! Tap paragraphs to listen!", "Enjoy your story!")
                                                    } catch (e: Exception) {
                                                        viewModel.speak("Oh dear, the storybook got slightly wet. Try again!", "Sorry, please retry.")
                                                    }
                                                } else {
                                                    viewModel.speak("Unable to connect to the Story kingdom. Ensure internet is active!", "Please check connection.")
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("generate_story_btn"),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Tell me a Story! ✨", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun SongDetailView(
    song: SongModel,
    viewModel: FidelViewModel,
    activeLyricsIndex: Int?,
    onLyricsClick: (Int, LyricsLine) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Close / return button row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(
                onClick = onClose,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.outline),
                modifier = Modifier.testTag("close_song_detail")
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("ይመለሱ (Back to Songs)", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Big Song Display header Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(song.childSong.colorHex).copy(alpha = 0.15f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(song.childSong.colorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(song.emoji, fontSize = 52.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = song.titleAmharic,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(song.childSong.colorHex)
                )

                Text(
                    text = song.titleEnglish,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                val bgMusicEnabled by viewModel.bgMusicEnabled.collectAsState()

                Button(
                    onClick = {
                        viewModel.toggleBgMusic()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (bgMusicEnabled) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                        contentColor = if (bgMusicEnabled) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("song_melody_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (bgMusicEnabled) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (bgMusicEnabled) "Pause Melody" else "Play Melody"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (bgMusicEnabled) "የዜማ ሙዚቃውን አቁም (Stop Melody ⏸️)" else "ዜማውን አጫውት (Play Melody ▶️)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                if (bgMusicEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))

                    val selectedInstrument by viewModel.selectedInstrument.collectAsState()

                    Text(
                        text = "የሙዚቃ ዓይነት (Choose Instrument Style):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        InstrumentStyle.values().forEach { style ->
                            val isSel = selectedInstrument == style
                            FilterChip(
                                selected = isSel,
                                onClick = {
                                    viewModel.selectInstrument(style)
                                    viewModel.speak("Playing with ${style.displayName}", "Playing with ${style.displayName}")
                                },
                                label = { Text(style.displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                leadingIcon = { Text(style.emoji, fontSize = 14.sp) }
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "Tap any line below to hear pronunciation! 🎧👇",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 12.dp)
        )

        // Render each lyrics line nicely
        song.lyrics.forEachIndexed { index, line ->
            val isActive = activeLyricsIndex == index
            val cardBgColor = if (isActive) Color(song.childSong.colorHex).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            val borderModifier = if (isActive) Modifier.border(2.dp, Color(song.childSong.colorHex), RoundedCornerShape(16.dp)) else Modifier

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardBgColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .then(borderModifier)
                    .clickable { onLyricsClick(index, line) }
                    .testTag("lyrics_line_$index")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = line.amharic,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isActive) Color(song.childSong.colorHex) else MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = line.translit,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Text(
                            text = line.english,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    FilledIconButton(
                        onClick = { onLyricsClick(index, line) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (isActive) Color(song.childSong.colorHex) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            contentColor = if (isActive) Color.White else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak line"
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun StoryDetailView(
    story: StoryModel,
    viewModel: FidelViewModel,
    activeParagraphIndex: Int?,
    onParagraphClick: (Int, String) -> Unit,
    onTitleClick: () -> Unit,
    onClose: () -> Unit
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val isStoryPlaying by viewModel.isStoryPlayingFlow.collectAsState()

    val (tutorEmoji, tutorName, tutorRole) = when (userProgress.teachingVoice) {
        "KID" -> Triple("👧", "Mimi (ሚሚ)", "Storyteller Mimi")
        "BABA", "CHUNI" -> Triple("👦", "Baba (ባባ)", "Storyteller Baba")
        "ELDER" -> Triple("👴", "Yeneta (የኔታ)", "Grandfather Yeneta")
        "TEACHER" -> Triple("👩", "Almaz (አልማዝ)", "Teacher Almaz")
        else -> Triple("👩‍🏫", "Aster (አስቴር)", "Tutor Aster")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Return button row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(
                onClick = onClose,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.outline),
                modifier = Modifier.testTag("close_story_detail")
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("ይመለሱ (Back to Stories)", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Amharic Instructor Storytelling Panel
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(
                    width = 1.5.dp,
                    color = if (isStoryPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Instructor Avatar with animated sound waves if playing
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tutorEmoji,
                        fontSize = 32.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    if (isStoryPlaying) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$tutorName is telling this story!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isStoryPlaying) "🎙️ Reading paragraph ${activeParagraphIndex?.let { it + 1 } ?: 1}..." else "📖 Tap 'Play' or any paragraph below to listen!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isStoryPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }

                FilledIconButton(
                    onClick = { onTitleClick() },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isStoryPlaying) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (isStoryPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isStoryPlaying) "Stop Story" else "Play Story"
                    )
                }
            }
        }

        // Big Story Display header Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .clickable { onTitleClick() }
                .testTag("story_title_header_card")
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(story.emoji, fontSize = 42.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = story.titleAmharic,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = story.titleEnglish,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isStoryPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (isStoryPlaying) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isStoryPlaying) "ታሪኩን ለማቆም ይጫኑ / Tap to STOP reading!" else "ሙሉውን ለማድመጥ ይጫኑ / Tap to listen to the whole story!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isStoryPlaying) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Text(
            text = "Tap paragraphs to hear the narrative read aloud! 🎙️📖",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 12.dp)
        )

        // Render each story paragraph beautifully
        story.paragraphs.forEachIndexed { index, paragraph ->
            val isActive = activeParagraphIndex == index
            val englishTranslation = story.englishParagraphs.getOrNull(index) ?: ""

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isActive) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .then(
                        if (isActive) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                        else Modifier
                    )
                    .clickable { onParagraphClick(index, paragraph) }
                    .testTag("story_paragraph_$index")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "ክፍል ${index + 1} (Part ${index + 1})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline
                        )

                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak paragraph",
                            tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = paragraph,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 26.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (englishTranslation.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = englishTranslation,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        // Moral display Card at the bottom
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("💡", fontSize = 24.sp)
                    Text(
                        text = "የታሪኩ ምሳሌ (Moral of the Story):",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = story.moralAmharic,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}
