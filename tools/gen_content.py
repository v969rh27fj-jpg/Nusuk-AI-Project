import json, sys, os
out = sys.argv[1]
os.makedirs(out, exist_ok=True)

TALBIYAH = "لَبَّيْكَ اللَّهُمَّ لَبَّيْكَ، لَبَّيْكَ لَا شَرِيكَ لَكَ لَبَّيْكَ، إِنَّ الْحَمْدَ وَالنِّعْمَةَ لَكَ وَالْمُلْكَ، لَا شَرِيكَ لَكَ"
JABIR = "Sahih Muslim, hadith of Jabir (Muslim 1218)"

def T(id, cat, order, arabic, title, summary, steps, refs):
    return {"id": id, "category": cat, "order": order, "arabic": arabic,
            "title": dict(zip(("bn","en","ar"), title)),
            "summary": dict(zip(("bn","en","ar"), summary)),
            "steps": dict(zip(("bn","en","ar"), steps)),
            "references": refs}

topics = []
topics.append(T("hajj_ihram","hajj",1,"الإحرام",
 ("ইহরাম","Ihram","الإحرام"),
 ("ইহরাম হলো হজ বা উমরাহ শুরুর নিয়ত করে বিশেষ পবিত্র অবস্থায় প্রবেশ করা।",
  "Ihram is making the intention to begin Hajj or Umrah and entering the special state of sanctity.",
  "الإحرام هو نيّة الدخول في نسك الحج أو العمرة والدخول في حالة الإحرام."),
 ([ "গোসল বা পরিচ্ছন্নতা অর্জন করুন; পুরুষেরা সেলাইবিহীন দুই টুকরো সাদা কাপড় (ইযার ও রিদা) পরেন, নারীরা শালীন সাধারণ পোশাক পরেন।",
    "মীকাতে পৌঁছে বা তার আগে হজ/উমরাহর নিয়ত করুন।",
    "তালবিয়া পাঠ করুন: " + TALBIYAH,
    "ইহরাম অবস্থায় নিষিদ্ধ কাজ (যেমন চুল-নখ কাটা, সুগন্ধি ব্যবহার, শিকার, স্বামী-স্ত্রীর মেলামেশা, ঝগড়া-বিবাদ) থেকে বিরত থাকুন।"],
  [ "Take a ghusl or freshen up. Men wear two unstitched white sheets (izar and rida); women wear ordinary modest clothing.",
    "Make the intention for Hajj or Umrah at the Miqat (or before reaching it).",
    "Recite the Talbiyah: " + TALBIYAH,
    "Avoid what is prohibited in Ihram (such as cutting hair or nails, perfume, hunting, marital relations, arguing)."],
  [ "اغتسل أو تنظّف؛ ويلبس الرجل إزارًا ورداءً أبيضين غير مخيطين، وتلبس المرأة ثيابها الساترة المعتادة.",
    "انوِ الحج أو العمرة عند الميقات (أو قبل الوصول إليه).",
    "لبِّ بالتلبية: " + TALBIYAH,
    "اجتنب محظورات الإحرام (كقص الشعر والأظافر، والطيب، والصيد، والجماع، والجدال)."]),
 ["Qur'an 2:196-197", "Sahih al-Bukhari & Sahih Muslim, hadith of Ibn Umar on what a muhrim wears", JABIR]))

topics.append(T("hajj_miqat","hajj",2,"الميقات",
 ("মীকাত","Miqat","الميقات"),
 ("মীকাত হলো সেই নির্ধারিত সীমানা, যেখান থেকে মক্কার উদ্দেশে আসা হজ/উমরাহযাত্রী ইহরাম ছাড়া অতিক্রম করতে পারেন না।",
  "The Miqat is the appointed boundary that a pilgrim heading to Makkah for Hajj or Umrah must not pass without entering Ihram.",
  "الميقات هو الحدّ المعيّن الذي لا يجوز لمن قصد مكة للحج أو العمرة أن يتجاوزه دون إحرام."),
 ([ "মদিনার দিক থেকে আগতদের মীকাত যুল-হুলাইফা (আবইয়ার আলী); সিরিয়া/মিসর/মাগরিবের দিক থেকে আল-জুহফা; নজদের দিক থেকে কারনুল-মানাযিল; ইয়েমেনের দিক থেকে ইয়ালামলাম।",
    "যারা এই সীমানার ভেতরে থাকেন, তারা নিজ বাসস্থান থেকে ইহরাম বাঁধেন; মক্কাবাসী হজের ইহরাম মক্কা থেকেই বাঁধেন।",
    "বিমানযাত্রীরা সাধারণত মীকাত বরাবর পৌঁছানোর আগেই ইহরামের প্রস্তুতি নেন; এয়ারলাইন বা দলনেতার ঘোষণা অনুসরণ করুন।"],
  [ "For those coming from the direction of Madinah the Miqat is Dhul-Hulayfah (Abyar Ali); from Syria/Egypt/the Maghrib it is Al-Juhfah; from Najd it is Qarn al-Manazil; from Yemen it is Yalamlam.",
    "Those living inside these boundaries start Ihram from where they live; people of Makkah start the Hajj Ihram from Makkah.",
    "Air travellers usually prepare for Ihram before the plane reaches the Miqat line; follow the airline's or your group leader's announcement."],
  [ "ميقات القادمين من جهة المدينة ذو الحليفة (أبيار علي)، ومن جهة الشام ومصر والمغرب الجحفة، ومن نجد قرن المنازل، ومن اليمن يلملم.",
    "من كان داخل هذه المواقيت أحرم من مكانه، وأهل مكة يُحرمون بالحج من مكة.",
    "يستعد المسافرون جوًّا للإحرام قبل محاذاة الميقات؛ فاتبع إعلان شركة الطيران أو مسؤول المجموعة."]),
 ["Sahih al-Bukhari & Sahih Muslim, hadith of Ibn Abbas on the mawaqit (Book of Hajj)"]))

topics.append(T("hajj_mina","hajj",3,"منى",
 ("মিনা","Mina","منى"),
 ("মিনা মক্কার কাছে একটি উপত্যকা। হজের দিনগুলোতে হাজিরা এখানে অবস্থান ও নামায আদায় করেন।",
  "Mina is a valley near Makkah where pilgrims stay and pray during the days of Hajj.",
  "منى وادٍ قرب مكة يقيم فيه الحجاج ويصلّون خلال أيام الحج."),
 ([ "৮ জিলহজ (ইয়াউমুত-তারউইয়াহ) মিনায় যান এবং সেখানে যোহর, আসর, মাগরিব, ইশা ও ফজর আদায় করুন; চার রাকাতের নামায কসর (দুই রাকাত) করুন, জমা নয়।",
    "৯ জিলহজ সূর্যোদয়ের পর আরাফাতের উদ্দেশে রওনা দিন।",
    "মুযদালিফা থেকে ফিরে ১০ জিলহজ জামারাতে কঙ্কর নিক্ষেপ করুন এবং আইয়ামে তাশরীকের (১১, ১২ ও যারা থাকেন তাদের ১৩ তারিখ) রাতগুলো মিনায় কাটান।"],
  [ "On 8 Dhul Hijjah (Yawm at-Tarwiyah) go to Mina and pray Dhuhr, Asr, Maghrib, Isha and Fajr there; shorten the four-rak'ah prayers to two, without combining them.",
    "After sunrise on 9 Dhul Hijjah set out for Arafat.",
    "After Muzdalifah return to Mina to stone the Jamarat on 10 Dhul Hijjah and spend the nights of the days of Tashreeq there (11th, 12th, and the 13th for those who stay)."],
  [ "في اليوم الثامن من ذي الحجة (يوم التروية) اذهب إلى منى وصلِّ فيها الظهر والعصر والمغرب والعشاء والفجر، مع قصر الرباعية دون جمع.",
    "بعد طلوع شمس اليوم التاسع انطلق إلى عرفات.",
    "بعد مزدلفة ارجع إلى منى لرمي الجمرات يوم العيد، وبِت فيها ليالي أيام التشريق (11 و12 ولمن تأخر 13)."]),
 ["Qur'an 2:203", JABIR]))

topics.append(T("hajj_arafat","hajj",4,"عرفات",
 ("আরাফাত","Arafat","عرفات"),
 ("৯ জিলহজ আরাফাতের ময়দানে অবস্থান (উকুফ) হজের প্রধান রুকন। হাদিসে এসেছে: \"হজ হলো আরাফাহ\"।",
  "Standing at Arafat on 9 Dhul Hijjah is the main pillar of Hajj. The hadith says: \"Hajj is Arafah.\"",
  "الوقوف بعرفة يوم التاسع من ذي الحجة هو الركن الأعظم في الحج؛ قال النبي ﷺ: «الحج عرفة»."),
 ([ "আরাফাতের নির্ধারিত সীমানার ভেতরে অবস্থান করুন।",
    "সুন্নাহ অনুযায়ী যোহরের সময় যোহর ও আসর একসাথে কসর করে পড়া হয়।",
    "কিবলামুখী হয়ে দোয়া, যিকির, তালবিয়া ও কুরআন তিলাওয়াতে সময় কাটান।",
    "সূর্যাস্তের আগে আরাফাত ত্যাগ করবেন না।"],
  [ "Make sure you are inside the marked boundaries of Arafat.",
    "Following the Sunnah, Dhuhr and Asr are prayed together, shortened, at the time of Dhuhr.",
    "Spend the time facing the qiblah in dua, dhikr, talbiyah and Quran recitation.",
    "Do not leave Arafat before sunset."],
  [ "تأكد أنك داخل حدود عرفة المحدّدة.",
    "السنّة أن تُجمع الظهر والعصر قصرًا جمع تقديم في وقت الظهر.",
    "أكثر من الدعاء والذكر والتلبية وتلاوة القرآن مستقبلًا القبلة.",
    "لا تغادر عرفة قبل غروب الشمس."]),
 ["Qur'an 2:198-199", "Hadith \"Al-Hajju Arafah\" (Ahmad, Abu Dawud, at-Tirmidhi, an-Nasa'i)", JABIR]))

topics.append(T("hajj_muzdalifah","hajj",5,"مزدلفة",
 ("মুযদালিফা","Muzdalifah","مزدلفة"),
 ("আরাফাত থেকে ফেরার পথে মুযদালিফায় রাত কাটানো হয়। এখানেই মাশআরুল হারাম অবস্থিত।",
  "On the way back from Arafat pilgrims spend the night in Muzdalifah, where Al-Mash'ar al-Haram is located.",
  "بعد الدفع من عرفة يبيت الحجاج في مزدلفة، وفيها المشعر الحرام."),
 ([ "সূর্যাস্তের পর শান্তভাবে আরাফাত থেকে মুযদালিফায় যান।",
    "পৌঁছে মাগরিব ও ইশা একসাথে আদায় করুন (ইশা কসর)।",
    "রাত কাটিয়ে ফজর আদায় করুন এবং আলো ফর্সা হওয়া পর্যন্ত যিকির ও দোয়া করুন।",
    "দুর্বল ও বয়স্কদের জন্য আগে রওনা দেওয়ার অনুমতির বিষয়ে আলেমদের মতভেদ ও শর্ত আছে; আপনার দলের আলেমের নির্দেশনা অনুসরণ করুন।"],
  [ "After sunset leave Arafat calmly for Muzdalifah.",
    "On arrival pray Maghrib and Isha together (Isha shortened).",
    "Spend the night, pray Fajr, and make dhikr and dua until the sky is bright.",
    "Scholars have given conditions for the weak and elderly to leave earlier; follow the guidance of your group's scholar."],
  [ "بعد غروب الشمس انفِر من عرفة بسكينة إلى مزدلفة.",
    "عند الوصول صلِّ المغرب والعشاء جمعًا (مع قصر العشاء).",
    "بِت بها وصلِّ الفجر، وأكثر من الذكر والدعاء حتى يُسفر الصبح.",
    "للعلماء تفصيل في أحوال الضعفة وكبار السن في الخروج مبكرًا؛ فاتبع إرشاد عالم مجموعتك."]),
 ["Qur'an 2:198", JABIR]))

topics.append(T("hajj_jamarat","hajj",6,"الجمرات",
 ("জামারাত (কঙ্কর নিক্ষেপ)","Jamarat (stoning)","الجمرات (رمي الجمار)"),
 ("মিনায় তিনটি জামারায় কঙ্কর নিক্ষেপ করা হজের একটি ওয়াজিব আমল।",
  "Stoning the three Jamarat in Mina is an obligatory rite of Hajj.",
  "رمي الجمرات الثلاث في منى من واجبات الحج."),
 ([ "১০ জিলহজ শুধু বড় জামারা (জামরাতুল আকাবা) তে সাতটি কঙ্কর নিক্ষেপ করুন; প্রতিটির সাথে \"আল্লাহু আকবার\" বলুন।",
    "১১ ও ১২ জিলহজ (এবং ১৩ তারিখ, যারা থাকেন) সূর্য ঢলার পর ক্রমানুসারে ছোট, মধ্যম ও বড় জামারায় সাতটি করে কঙ্কর মারুন।",
    "যারা তাড়াতাড়ি ফিরতে চান, তারা ১২ তারিখ সূর্যাস্তের আগে মিনা ত্যাগ করতে পারেন।",
    "ভিড় নিয়ন্ত্রণে কর্তৃপক্ষের নির্ধারিত সময় ও পথনির্দেশ মেনে চলুন।"],
  [ "On 10 Dhul Hijjah stone only the large Jamrah (Jamrat al-Aqabah) with seven pebbles, saying \"Allahu Akbar\" with each.",
    "On the 11th and 12th (and the 13th for those who stay), after the sun passes its zenith, stone the small, middle and large Jamarat in that order, seven pebbles each.",
    "Those who wish to hurry may leave Mina on the 12th before sunset.",
    "Follow the official crowd-management timings and routes."],
  [ "يوم العيد (10 ذو الحجة) يُرمى جمرة العقبة الكبرى فقط بسبع حصيات، مكبّرًا مع كل حصاة.",
    "في 11 و12 (و13 لمن تأخر) بعد الزوال يُرمى الجمرات: الصغرى فالوسطى فالكبرى، سبع حصيات لكل جمرة.",
    "من أراد التعجّل خرج من منى يوم 12 قبل الغروب.",
    "التزم بالأوقات والمسارات التي تحددها الجهات المنظِّمة لتفادي الزحام."]),
 ["Qur'an 2:203", JABIR]))

topics.append(T("hajj_tawaf","hajj",7,"الطواف",
 ("তাওয়াফ (হজে)","Tawaf (in Hajj)","الطواف (في الحج)"),
 ("হজে তাওয়াফের কয়েকটি রূপ আছে। তাওয়াফে ইফাযা হজের রুকন।",
  "Hajj includes several kinds of Tawaf. Tawaf al-Ifadah is a pillar of Hajj.",
  "للحج أنواع من الطواف، وطواف الإفاضة ركن من أركانه."),
 ([ "তাওয়াফুল কুদুম: মক্কায় পৌঁছানোর পর (সুন্নাহ)।",
    "তাওয়াফে ইফাযা: ১০ জিলহজ বা তার পর কঙ্কর নিক্ষেপ ও হালক/তাকসীরের পর; এটি হজের রুকন।",
    "তাওয়াফুল বিদা: মক্কা ছাড়ার আগে বিদায়ী তাওয়াফ; অধিকাংশ আলেমের মতে ওয়াজিব (ঋতুমতী নারীর জন্য ছাড় আছে)।",
    "তাওয়াফের পদ্ধতির জন্য উমরাহর \"তাওয়াফ\" অংশ দেখুন।"],
  [ "Tawaf al-Qudum: on arrival in Makkah (Sunnah).",
    "Tawaf al-Ifadah: on or after 10 Dhul Hijjah, after stoning and shaving/trimming; it is a pillar of Hajj.",
    "Tawaf al-Wada: the farewell Tawaf before leaving Makkah; obligatory according to most scholars (a woman in menstruation is excused).",
    "For the method of Tawaf see the \"Tawaf\" topic under Umrah."],
  [ "طواف القدوم: عند الوصول إلى مكة (سنّة).",
    "طواف الإفاضة: يوم النحر أو بعده بعد الرمي والحلق/التقصير، وهو ركن في الحج.",
    "طواف الوداع: عند مغادرة مكة، وهو واجب عند جمهور العلماء (ويُعفى منه الحائض).",
    "لصفة الطواف راجع موضوع «الطواف» في العمرة."]),
 ["Qur'an 22:29", "Sahih al-Bukhari & Sahih Muslim, hadith of Ibn Abbas on the farewell Tawaf", JABIR]))

topics.append(T("hajj_sai","hajj",8,"السعي",
 ("সাঈ (হজে)","Sa'i (in Hajj)","السعي (في الحج)"),
 ("সাফা ও মারওয়ার মাঝে সাতবার যাতায়াত হজের আমল।",
  "Walking seven times between Safa and Marwa is a rite of Hajj.",
  "السعي سبعة أشواط بين الصفا والمروة من أعمال الحج."),
 ([ "সাফা থেকে শুরু করে মারওয়ায় শেষ করুন; একবার সাফা থেকে মারওয়া এক চক্কর।",
    "হজের ধরন (তামাত্তু, কিরান, ইফরাদ) অনুযায়ী সাঈ কখন করতে হবে তা ভিন্ন হতে পারে; যিনি তাওয়াফুল কুদুমের সাথে করেননি, তিনি তাওয়াফে ইফাযার পর করেন।",
    "আপনার হজের ধরনে ঠিক কী করণীয়, তা দলের আলেম বা গাইডের কাছ থেকে নিশ্চিত হয়ে নিন।"],
  [ "Start at Safa and finish at Marwa; each trip from one hill to the other counts as one lap.",
    "When Sa'i is done depends on the type of Hajj (Tamattu', Qiran, Ifrad); whoever has not performed it after Tawaf al-Qudum performs it after Tawaf al-Ifadah.",
    "Confirm with your group's scholar or guide exactly what applies to your type of Hajj."],
  [ "ابدأ من الصفا وانتهِ بالمروة؛ وكل ذهاب من أحدهما إلى الآخر شوط.",
    "يختلف وقت السعي بحسب نوع الحج (تمتع أو قران أو إفراد)؛ ومن لم يسعَ مع طواف القدوم سعى بعد طواف الإفاضة.",
    "اسأل عالم مجموعتك أو مرشدك لتتأكد مما يخص نوع حجّك."]),
 ["Qur'an 2:158", JABIR]))

topics.append(T("hajj_halq","hajj",9,"الحلق والتقصير",
 ("হালক ও তাকসীর","Halq and Taqsir","الحلق والتقصير"),
 ("হালক হলো মাথা মুণ্ডন; তাকসীর হলো চুল ছোট করা। এর মাধ্যমে ইহরামের নিষেধাজ্ঞা থেকে মুক্ত হওয়া শুরু হয়।",
  "Halq is shaving the head; Taqsir is trimming the hair. Through it the Ihram restrictions begin to be lifted.",
  "الحلق هو حلق الرأس، والتقصير هو قصّ الشعر، وبه يبدأ التحلّل من محظورات الإحرام."),
 ([ "১০ জিলহজ বড় জামারায় কঙ্কর মারার পর (এবং কুরবানি প্রযোজ্য হলে তার পর) পুরুষেরা মাথা মুণ্ডন করেন, যা উত্তম; অথবা চুল ছোট করেন।",
    "নারীরা চুলের প্রান্ত থেকে আঙুলের এক গিরা পরিমাণ কাটেন।",
    "তামাত্তু ও কিরান হজে হাদী (কুরবানি) ওয়াজিব; প্রথম পর্যায়ের হালালের পর তাওয়াফে ইফাযা করলে পূর্ণ হালাল হওয়া যায়।"],
  [ "On 10 Dhul Hijjah, after stoning Jamrat al-Aqabah (and the sacrifice where applicable), men shave their heads, which is better, or trim.",
    "Women trim a fingertip's length from the ends of their hair.",
    "The Hady (sacrifice) is obligatory for Tamattu' and Qiran; after the first release, performing Tawaf al-Ifadah completes the release from Ihram."],
  [ "يوم النحر بعد رمي جمرة العقبة (وبعد الهدي لمن عليه هدي) يحلق الرجل رأسه وهو الأفضل أو يقصّر.",
    "والمرأة تقصّر قدر أنملة من أطراف شعرها.",
    "الهدي واجب على المتمتع والقارن؛ وبعد التحلل الأول يتم التحلل الكامل بطواف الإفاضة."]),
 ["Qur'an 48:27", "Qur'an 2:196", "Sahih al-Bukhari & Sahih Muslim, hadith on supplication for those who shave and those who trim"]))

# ---- Umrah ----
topics.append(T("umrah_prep","umrah",1,"التحضير",
 ("প্রস্তুতি","Preparation","الاستعداد"),
 ("উমরাহর আগে নিয়ত, জ্ঞান, বৈধ আয় ও সরকারি নিয়ম মেনে প্রস্তুতি নিন।",
  "Before Umrah, prepare your intention, knowledge, lawful funds and official requirements.",
  "قبل العمرة استعدّ بالنية والعلم والمال الحلال والالتزام بالأنظمة الرسمية."),
 ([ "একমাত্র আল্লাহর সন্তুষ্টির জন্য নিয়ত খাঁটি করুন এবং তাওবা করুন।",
    "হালাল অর্থ ব্যবহার করুন এবং পাওনা/ঋণ ও দায়িত্ব সম্পর্কে যথাসাধ্য ব্যবস্থা নিন।",
    "উমরাহর আমলগুলো আগে শিখে নিন।",
    "ভিসা, পারমিট, স্বাস্থ্যসংক্রান্ত শর্ত ও বুকিং সরকার-অনুমোদিত অফিসিয়াল মাধ্যমে যাচাই করুন।"],
  [ "Make your intention sincere for Allah alone and repent.",
    "Use lawful funds and, as far as you can, settle debts and responsibilities.",
    "Learn the rites of Umrah beforehand.",
    "Check visas, permits, health requirements and bookings through official, government-approved channels."],
  [ "أخلص النية لله وحده وتب إليه.",
    "استعمل مالًا حلالًا، وأدِّ ما استطعت من الحقوق والديون.",
    "تعلّم أعمال العمرة قبل السفر.",
    "تحقق من التأشيرة والتصاريح والمتطلبات الصحية والحجوزات عبر القنوات الرسمية المعتمدة."]),
 ["Qur'an 2:196", "General guidance; verify requirements with official Saudi authorities"]))

topics.append(T("umrah_ihram","umrah",2,"الإحرام",
 ("উমরাহর ইহরাম","Ihram for Umrah","الإحرام للعمرة"),
 ("উমরাহর নিয়ত করে মীকাত থেকে ইহরামে প্রবেশ করুন।",
  "Enter Ihram for Umrah from the Miqat with the intention to perform Umrah.",
  "ادخل في الإحرام للعمرة من الميقات ناويًا العمرة."),
 ([ "গোসল বা ওযু করে ইহরামের পোশাক পরুন।",
    "মীকাতে মনে মনে উমরাহর নিয়ত করুন এবং বলতে পারেন: \"লাব্বাইকা উমরাতান\"।",
    "তাওয়াফ শুরু করা পর্যন্ত বেশি বেশি তালবিয়া পড়ুন: " + TALBIYAH],
  [ "Take a ghusl or wudu and put on the Ihram clothes.",
    "At the Miqat make the intention for Umrah and you may say: \"Labbayka Umratan\".",
    "Recite the Talbiyah often until you begin Tawaf: " + TALBIYAH],
  [ "اغتسل أو توضأ والبس ثياب الإحرام.",
    "عند الميقات انوِ العمرة ويمكنك أن تقول: «لبّيك عمرةً».",
    "أكثر من التلبية حتى تبدأ الطواف: " + TALBIYAH]),
 ["Sahih Muslim, hadith of Jabir (Muslim 1218)", "Qur'an 2:196"]))

topics.append(T("umrah_tawaf","umrah",3,"الطواف",
 ("তাওয়াফ","Tawaf","الطواف"),
 ("কাবাঘরকে বামে রেখে সাতবার প্রদক্ষিণ করাকে তাওয়াফ বলে।",
  "Tawaf is circling the Ka'bah seven times, keeping it on your left.",
  "الطواف هو سبعة أشواط حول الكعبة تجعلها عن يسارك."),
 ([ "ওযু অবস্থায় হাজরে আসওয়াদের বরাবর থেকে শুরু করুন; তার দিকে মুখ করে \"বিসমিল্লাহি আল্লাহু আকবার\" বলুন (স্পর্শ বা চুমু সম্ভব না হলে হাত দিয়ে ইশারা করুন)।",
    "কাবা বাম পাশে রেখে সাত চক্কর দিন; পুরুষেরা ইযতিবা (ডান কাঁধ খোলা) করেন এবং প্রথম তিন চক্করে রমল (দ্রুত ছোট পদক্ষেপ) করেন।",
    "রুকনে ইয়ামানি ও হাজরে আসওয়াদের মাঝে পড়ুন: \"রব্বানা আতিনা ফিদ-দুনইয়া হাসানাহ…\"",
    "শেষে সম্ভব হলে মাকামে ইব্রাহিমের কাছে দুই রাকাত নামায পড়ুন এবং যমযমের পানি পান করুন।"],
  [ "In a state of wudu, begin in line with the Black Stone; face it and say \"Bismillahi, Allahu Akbar\" (touch or kiss it if possible, otherwise gesture with your hand).",
    "Circle the Ka'bah seven times with it on your left; men uncover the right shoulder (idtiba) and walk briskly (ramal) in the first three circuits.",
    "Between the Yemeni Corner and the Black Stone recite: \"Rabbana atina fid-dunya hasanah…\" (Qur'an 2:201).",
    "Afterwards, if possible, pray two rak'ahs near Maqam Ibrahim and drink Zamzam."],
  [ "وأنت على وضوء ابدأ محاذيًا للحجر الأسود؛ استقبله وقل «بسم الله والله أكبر» (والمس أو قبّل إن تيسر وإلا أشر بيدك).",
    "اجعل الكعبة عن يسارك وطف سبعة أشواط؛ ويضطبع الرجل ويرمل في الأشواط الثلاثة الأولى.",
    "بين الركن اليماني والحجر الأسود قل: «ربنا آتنا في الدنيا حسنة…» (البقرة 201).",
    "ثم صلِّ ركعتين خلف مقام إبراهيم إن تيسر، واشرب من ماء زمزم."]),
 ["Qur'an 2:125", "Qur'an 2:201", "Qur'an 22:29", JABIR]))

topics.append(T("umrah_sai","umrah",4,"السعي",
 ("সাঈ","Sa'i","السعي"),
 ("সাফা ও মারওয়া পাহাড়ের মাঝে সাতবার যাতায়াত করাকে সাঈ বলে।",
  "Sa'i is walking seven times between the hills of Safa and Marwa.",
  "السعي هو سبعة أشواط بين جبلي الصفا والمروة."),
 ([ "সাফা থেকে শুরু করুন; সাফার কাছে পৌঁছে পড়ুন: \"ইন্নাস-সাফা ওয়াল-মারওয়াতা মিন শাআইরিল্লাহ\"।",
    "কাবার দিকে মুখ করে তাকবীর বলুন ও দোয়া করুন, তারপর মারওয়ার দিকে হাঁটুন।",
    "পুরুষেরা সবুজ বাতি চিহ্নিত অংশে দ্রুত হাঁটেন।",
    "সপ্তম চক্কর মারওয়ায় শেষ করুন।"],
  [ "Start at Safa; as you approach it recite: \"Inna as-Safa wal-Marwata min sha'a'irillah\".",
    "Face the Ka'bah, say takbir and make dua, then walk towards Marwa.",
    "Men walk briskly between the green markers.",
    "Finish the seventh lap at Marwa."],
  [ "ابدأ من الصفا؛ وإذا دنوت منه فاقرأ: «إن الصفا والمروة من شعائر الله».",
    "استقبل الكعبة وكبّر وادعُ، ثم امشِ نحو المروة.",
    "يسعى الرجل سعيًا شديدًا بين العلمين الأخضرين.",
    "اختم الشوط السابع عند المروة."]),
 ["Qur'an 2:158", JABIR]))

topics.append(T("umrah_halq","umrah",5,"الحلق والتقصير",
 ("হালক ও তাকসীর","Halq and Taqsir","الحلق والتقصير"),
 ("সাঈর পর চুল মুণ্ডন বা ছোট করে উমরাহ সম্পন্ন করা হয়।",
  "After Sa'i, shaving or trimming the hair completes the Umrah.",
  "بعد السعي يُحلق الشعر أو يُقصّر فتتم العمرة."),
 ([ "পুরুষেরা পুরো মাথার চুল মুণ্ডন করেন (উত্তম) অথবা সমানভাবে ছোট করেন।",
    "নারীরা চুলের প্রান্ত থেকে আঙুলের এক গিরা পরিমাণ কাটেন।",
    "এরপর ইহরামের নিষেধাজ্ঞা শেষ হয় এবং উমরাহ সম্পন্ন হয়।"],
  [ "Men shave the whole head (better) or trim evenly.",
    "Women trim a fingertip's length from the ends of their hair.",
    "The Ihram restrictions then end and the Umrah is complete."],
  [ "يحلق الرجل رأسه كله وهو الأفضل أو يقصّر من جميعه.",
    "وتقصّر المرأة قدر أنملة من أطراف شعرها.",
    "وبذلك ينتهي الإحرام وتتم العمرة."]),
 ["Qur'an 48:27", "Sahih al-Bukhari & Sahih Muslim, hadith on supplication for those who shave and those who trim"]))

json.dump({"version": 1, "reviewed": False, "topics": topics},
          open(f"{out}/learning_content.json", "w", encoding="utf-8"), ensure_ascii=False, indent=1)

# ---------------- Question bank ----------------
Q = []
def q(id, topic, qt, opts, ans, expl, ref):
    Q.append({"id": id, "topic": topic,
      "q": dict(zip(("bn","en","ar"), qt)),
      "options": {"bn": opts[0], "en": opts[1], "ar": opts[2]},
      "answer": ans,
      "explanation": dict(zip(("bn","en","ar"), expl)),
      "reference": ref})

q("q01","umrah_tawaf",("তাওয়াফে কাবাঘরকে কতবার প্রদক্ষিণ করতে হয়?","How many circuits of the Ka'bah make up one Tawaf?","كم شوطًا يُطاف حول الكعبة في الطواف الواحد؟"),
  (["৩ বার","৫ বার","৭ বার","৯ বার"],["3","5","7","9"],["3","5","7","9"]),2,
  ("তাওয়াফ সাত চক্করে সম্পন্ন হয়।","Tawaf is completed in seven circuits.","يتم الطواف بسبعة أشواط."),"Sahih Muslim 1218")
q("q02","umrah_sai",("সাঈ-তে সাফা ও মারওয়ার মাঝে মোট কতবার যাতায়াত করা হয়?","How many laps are walked between Safa and Marwa in Sa'i?","كم شوطًا يُسعى بين الصفا والمروة؟"),
  (["৩","৫","৭","১০"],["3","5","7","10"],["3","5","7","10"]),2,
  ("সাঈ সাত চক্করের; সাফা থেকে মারওয়া এক চক্কর।","Sa'i is seven laps; each trip from Safa to Marwa or back counts as one.","السعي سبعة أشواط، وكل ذهاب أو إياب شوط."),"Sahih Muslim 1218")
q("q03","umrah_sai",("সাঈ কোথা থেকে শুরু হয়?","Where does Sa'i begin?","من أين يبدأ السعي؟"),
  (["মারওয়া","সাফা","মিনা","আরাফাত"],["Marwa","Safa","Mina","Arafat"],["المروة","الصفا","منى","عرفات"]),1,
  ("সাঈ সাফা থেকে শুরু হয় এবং মারওয়ায় শেষ হয়।","Sa'i starts at Safa and ends at Marwa.","يبدأ السعي من الصفا وينتهي بالمروة."),"Qur'an 2:158")
q("q04","hajj_miqat",("মদিনার দিক থেকে আগত হাজিদের মীকাত কোনটি?","Which is the Miqat for pilgrims coming from the direction of Madinah?","ما ميقات القادمين من جهة المدينة؟"),
  (["ইয়ালামলাম","কারনুল-মানাযিল","যুল-হুলাইফা","আল-জুহফা"],["Yalamlam","Qarn al-Manazil","Dhul-Hulayfah","Al-Juhfah"],["يلملم","قرن المنازل","ذو الحليفة","الجحفة"]),2,
  ("মদিনাবাসী ও সেদিক থেকে আগতদের মীকাত যুল-হুলাইফা।","The Miqat for the people of Madinah, and those coming that way, is Dhul-Hulayfah.","ميقات أهل المدينة ومن جاء من جهتهم ذو الحليفة."),"Sahih al-Bukhari & Sahih Muslim, hadith of Ibn Abbas")
q("q05","hajj_arafat",("আরাফাতের দিন কোন তারিখে?","On which date is the day of Arafat?","في أي تاريخ يقع يوم عرفة؟"),
  (["৮ জিলহজ","৯ জিলহজ","১০ জিলহজ","১২ জিলহজ"],["8 Dhul Hijjah","9 Dhul Hijjah","10 Dhul Hijjah","12 Dhul Hijjah"],["8 ذو الحجة","9 ذو الحجة","10 ذو الحجة","12 ذو الحجة"]),1,
  ("আরাফাতের উকুফ ৯ জিলহজ।","Standing at Arafat takes place on 9 Dhul Hijjah.","الوقوف بعرفة يكون في التاسع من ذي الحجة."),"Sahih Muslim 1218")
q("q06","hajj_arafat",("কোন আমলকে হাদিসে \"হজ\" বলে আখ্যায়িত করা হয়েছে (\"আল-হাজ্জু আরাফাহ\")?","Which rite does the hadith describe as \"Hajj\" (\"Al-Hajju Arafah\")?","أي عمل وصفه الحديث بأنه «الحج» (الحج عرفة)؟"),
  (["আরাফাতে অবস্থান","কঙ্কর নিক্ষেপ","বিদায়ী তাওয়াফ","মাথা মুণ্ডন"],["Standing at Arafat","Stoning the Jamarat","The farewell Tawaf","Shaving the head"],["الوقوف بعرفة","رمي الجمرات","طواف الوداع","حلق الرأس"]),0,
  ("আরাফাতে অবস্থান হজের প্রধান রুকন।","Standing at Arafat is the main pillar of Hajj.","الوقوف بعرفة هو الركن الأعظم للحج."),"Ahmad, Abu Dawud, at-Tirmidhi, an-Nasa'i")
q("q07","hajj_muzdalifah",("আরাফাতের পর হাজিরা রাত কোথায় কাটান?","Where do pilgrims spend the night after Arafat?","أين يبيت الحجاج بعد عرفة؟"),
  (["মিনায়","মুযদালিফায়","মক্কার হারামে","মদিনায়"],["In Mina","In Muzdalifah","In the Haram of Makkah","In Madinah"],["في منى","في مزدلفة","في الحرم المكي","في المدينة"]),1,
  ("৯ জিলহজ রাতে মুযদালিফায় অবস্থান করা হয়।","The night of 9 Dhul Hijjah is spent in Muzdalifah.","يُبات في مزدلفة ليلة العاشر."),"Qur'an 2:198; Sahih Muslim 1218")
q("q08","hajj_jamarat",("প্রতিটি জামারায় সাধারণত কতটি কঙ্কর নিক্ষেপ করা হয়?","How many pebbles are thrown at each Jamrah?","كم حصاة تُرمى عند كل جمرة؟"),
  (["৩টি","৫টি","৭টি","২১টি"],["3","5","7","21"],["3","5","7","21"]),2,
  ("প্রতিটি জামারায় সাতটি করে কঙ্কর মারা হয়।","Seven pebbles are thrown at each Jamrah.","يُرمى بسبع حصيات عند كل جمرة."),"Sahih Muslim 1218")
q("q09","hajj_jamarat",("১০ জিলহজ কোন জামারায় কঙ্কর নিক্ষেপ করা হয়?","Which Jamrah is stoned on 10 Dhul Hijjah?","أي جمرة تُرمى يوم العاشر من ذي الحجة؟"),
  (["ছোট জামারা","মধ্যম জামারা","বড় জামারা (আকাবা)","তিনটিই"],["The small Jamrah","The middle Jamrah","The large Jamrah (Aqabah)","All three"],["الجمرة الصغرى","الجمرة الوسطى","الجمرة الكبرى (العقبة)","الثلاث جميعًا"]),2,
  ("ঈদের দিন শুধু জামরাতুল আকাবা।","On the day of Eid only Jamrat al-Aqabah is stoned.","يوم العيد تُرمى جمرة العقبة وحدها."),"Sahih Muslim 1218")
q("q10","hajj_jamarat",("আইয়ামে তাশরীকে জামারাতের ক্রম কী?","What is the order of the Jamarat on the days of Tashreeq?","ما ترتيب رمي الجمرات في أيام التشريق؟"),
  (["বড়, মধ্যম, ছোট","ছোট, মধ্যম, বড়","মধ্যম, ছোট, বড়","যেকোনো ক্রমে"],["Large, middle, small","Small, middle, large","Middle, small, large","Any order"],["الكبرى ثم الوسطى ثم الصغرى","الصغرى ثم الوسطى ثم الكبرى","الوسطى ثم الصغرى ثم الكبرى","بأي ترتيب"]),1,
  ("ছোট, তারপর মধ্যম, তারপর বড় জামারা।","First the small, then the middle, then the large Jamrah.","تُرمى الصغرى ثم الوسطى ثم الكبرى."),"Sahih Muslim 1218")
q("q11","hajj_ihram",("পুরুষের ইহরামের পোশাক কী?","What is the Ihram clothing for men?","ما لباس الإحرام للرجال؟"),
  (["সেলাই করা সাদা জামা","সেলাইবিহীন দুই টুকরো সাদা কাপড়","যেকোনো রঙের পাঞ্জাবি","সবুজ চাদর"],["A stitched white shirt","Two unstitched white sheets","A kurta of any colour","A green cloak"],["قميص أبيض مخيط","إزار ورداء أبيضان غير مخيطين","جلباب بأي لون","عباءة خضراء"]),1,
  ("পুরুষেরা সেলাইবিহীন ইযার ও রিদা পরেন।","Men wear an unstitched izar and rida.","يلبس الرجل إزارًا ورداءً غير مخيطين."),"Sahih al-Bukhari & Sahih Muslim, hadith of Ibn Umar")
q("q12","umrah_tawaf",("তাওয়াফ কোথা থেকে শুরু হয়?","Where does Tawaf begin?","من أين يبدأ الطواف؟"),
  (["মাকামে ইব্রাহিম থেকে","হাজরে আসওয়াদ বরাবর","সাফা থেকে","যমযম কূপ থেকে"],["From Maqam Ibrahim","In line with the Black Stone","From Safa","From the Zamzam well"],["من مقام إبراهيم","من محاذاة الحجر الأسود","من الصفا","من بئر زمزم"]),1,
  ("তাওয়াফ হাজরে আসওয়াদ বরাবর থেকে শুরু ও সেখানেই শেষ হয়।","Tawaf begins and ends in line with the Black Stone.","يبدأ الطواف وينتهي عند محاذاة الحجر الأسود."),"Sahih Muslim 1218")
q("q13","umrah_tawaf",("তাওয়াফের সময় কাবা কোন দিকে থাকে?","On which side is the Ka'bah during Tawaf?","في أي جهة تكون الكعبة أثناء الطواف؟"),
  (["ডানে","বামে","সামনে","পেছনে"],["On your right","On your left","In front","Behind"],["عن يمينك","عن يسارك","أمامك","خلفك"]),1,
  ("কাবা বাম পাশে রেখে প্রদক্ষিণ করা হয়।","The Ka'bah is kept on the left.","تُجعل الكعبة عن اليسار."),"Sahih Muslim 1218")
q("q14","umrah_tawaf",("তাওয়াফ শেষে দুই রাকাত নামায কোথায় পড়া সুন্নাহ?","Where is it Sunnah to pray two rak'ahs after Tawaf?","أين تُسنّ ركعتا الطواف؟"),
  (["মাকামে ইব্রাহিমের কাছে","মিনায়","আরাফাতে","সাফা পাহাড়ে"],["Near Maqam Ibrahim","In Mina","In Arafat","On Mount Safa"],["عند مقام إبراهيم","في منى","في عرفة","على جبل الصفا"]),0,
  ("সম্ভব হলে মাকামে ইব্রাহিমের কাছে; না হলে হারামের অন্য স্থানে পড়া যায়।","Near Maqam Ibrahim if possible; otherwise elsewhere in the Haram.","خلف المقام إن تيسر وإلا ففي أي مكان من الحرم."),"Qur'an 2:125")
q("q15","hajj_ihram",("তালবিয়া কোন বাক্য দিয়ে শুরু হয়?","With which phrase does the Talbiyah begin?","بأي عبارة تبدأ التلبية؟"),
  (["সুবহানাল্লাহ","লাব্বাইকাল্লাহুম্মা লাব্বাইক","আল্লাহু আকবার","আলহামদুলিল্লাহ"],["Subhan Allah","Labbayka Allahumma labbayk","Allahu Akbar","Alhamdulillah"],["سبحان الله","لبيك اللهم لبيك","الله أكبر","الحمد لله"]),1,
  ("তালবিয়া \"লাব্বাইকাল্লাহুম্মা লাব্বাইক\" দিয়ে শুরু।","The Talbiyah begins with \"Labbayka Allahumma labbayk\".","تبدأ التلبية بـ «لبيك اللهم لبيك»."),"Sahih al-Bukhari & Sahih Muslim")
q("q16","hajj_halq",("\"হালক\" শব্দের অর্থ কী?","What does \"Halq\" mean?","ما معنى «الحلق»؟"),
  (["চুল ছোট করা","মাথা মুণ্ডন","গোসল","কুরবানি"],["Trimming the hair","Shaving the head","Ghusl","Sacrifice"],["قصّ الشعر","حلق الرأس","الغسل","الأضحية"]),1,
  ("হালক = মাথা মুণ্ডন; তাকসীর = চুল ছোট করা।","Halq = shaving the head; Taqsir = trimming.","الحلق = إزالة شعر الرأس؛ والتقصير = قصّه."),"Qur'an 48:27")
q("q17","hajj_halq",("নারীদের জন্য ইহরাম থেকে হালাল হওয়ার সময় কী করণীয়?","What do women do to come out of Ihram?","ماذا تفعل المرأة للتحلل من الإحرام؟"),
  (["মাথা মুণ্ডন করেন","চুলের প্রান্ত থেকে আঙুলের এক গিরা পরিমাণ কাটেন","কিছুই করতে হয় না","সম্পূর্ণ চুল কেটে ফেলেন"],["Shave the head","Trim a fingertip's length from the ends of the hair","Nothing is required","Cut all the hair"],["تحلق رأسها","تقصّر قدر أنملة من أطراف الشعر","لا شيء عليها","تقصّ الشعر كله"]),1,
  ("নারীরা মাথা মুণ্ডন করেন না; চুলের প্রান্ত থেকে সামান্য কাটেন।","Women do not shave; they trim a little from the ends.","المرأة لا تحلق؛ بل تقصّر من أطراف شعرها."),"Sunan Abi Dawud (hadith on women and taqsir)")
q("q18","hajj_mina",("ইয়াউমুত-তারউইয়াহ কোন তারিখ?","Which date is Yawm at-Tarwiyah?","ما تاريخ يوم التروية؟"),
  (["৭ জিলহজ","৮ জিলহজ","৯ জিলহজ","১০ জিলহজ"],["7 Dhul Hijjah","8 Dhul Hijjah","9 Dhul Hijjah","10 Dhul Hijjah"],["7 ذو الحجة","8 ذو الحجة","9 ذو الحجة","10 ذو الحجة"]),1,
  ("৮ জিলহজ হাজিরা মিনায় যান।","On 8 Dhul Hijjah pilgrims go to Mina.","في الثامن من ذي الحجة يتوجه الحجاج إلى منى."),"Sahih Muslim 1218")
q("q19","umrah_prep",("নিচের কোনটি উমরাহর আমল নয়?","Which of these is NOT a rite of Umrah?","أي مما يلي ليس من أعمال العمرة؟"),
  (["ইহরাম","তাওয়াফ","আরাফাতে অবস্থান","সাঈ"],["Ihram","Tawaf","Standing at Arafat","Sa'i"],["الإحرام","الطواف","الوقوف بعرفة","السعي"]),2,
  ("আরাফাতে অবস্থান কেবল হজের আমল।","Standing at Arafat belongs to Hajj only.","الوقوف بعرفة خاص بالحج."),"General Hajj/Umrah fiqh references")
q("q20","hajj_tawaf",("বিদায়ী তাওয়াফের আরবি নাম কী?","What is the Arabic name of the farewell Tawaf?","ما اسم طواف الوداع بالعربية؟"),
  (["তাওয়াফুল ইফাযা","তাওয়াফুল কুদুম","তাওয়াফুল বিদা","তাওয়াফুন নফল"],["Tawaf al-Ifadah","Tawaf al-Qudum","Tawaf al-Wada'","Tawaf an-Nafl"],["طواف الإفاضة","طواف القدوم","طواف الوداع","طواف النفل"]),2,
  ("মক্কা ছাড়ার আগে তাওয়াফুল বিদা করা হয়।","Tawaf al-Wada' is done before leaving Makkah.","طواف الوداع يؤدى عند مغادرة مكة."),"Sahih al-Bukhari & Sahih Muslim, hadith of Ibn Abbas")
q("q21","hajj_muzdalifah",("মুযদালিফায় মাগরিব ও ইশা কীভাবে পড়া হয়?","How are Maghrib and Isha prayed in Muzdalifah?","كيف تُصلّى المغرب والعشاء في مزدلفة؟"),
  (["আলাদা সময়ে","একসাথে (জমা), ইশা কসর","শুধু মাগরিব","পড়তে হয় না"],["Separately at their times","Together, with Isha shortened","Only Maghrib","Not prayed"],["كلٌّ في وقته","جمعًا مع قصر العشاء","المغرب فقط","لا تُصلّى"]),1,
  ("সুন্নাহ হলো মুযদালিফায় পৌঁছে একসাথে পড়া।","The Sunnah is to pray them together on arrival in Muzdalifah.","السنّة أن يُجمعا عند الوصول إلى مزدلفة."),"Sahih Muslim 1218")
q("q22","hajj_mina",("মিনায় হাজিরা নামায কীভাবে পড়েন?","How do pilgrims pray in Mina?","كيف يصلّي الحجاج في منى؟"),
  (["সব নামায জমা করে","চার রাকাতের নামায কসর (২ রাকাত), জমা ছাড়া","শুধু ফরয","মসজিদে নামায নিষেধ"],["Combining all prayers","Shortening four-rak'ah prayers to two, without combining","Only obligatory prayers","Prayer in mosques is forbidden"],["جمع جميع الصلوات","قصر الرباعية دون جمع","الفرائض فقط","تُمنع الصلاة في المساجد"]),1,
  ("মিনায় কসর করা হয়, জমা নয়।","In Mina prayers are shortened, not combined.","في منى تُقصر الصلوات ولا تُجمع."),"Sahih Muslim 1218")

json.dump({"version": 1,
           "reviewed": False,
           "note": "Starter question bank. Not yet reviewed by a qualified scholar. Add more questions in this same format.",
           "questions": Q},
          open(f"{out}/questions.json", "w", encoding="utf-8"), ensure_ascii=False, indent=1)
print(len(topics), "topics,", len(Q), "questions")
