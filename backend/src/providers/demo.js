'use strict';

const NOTICE = {
  bn: 'ডেমো মোড: এই backend-এ এখনো কোনো AI provider ও API key যুক্ত করা হয়নি, তাই এটি আসল AI উত্তর নয়। সেটআপের জন্য backend/README.md দেখুন। ইতোমধ্যে অ্যাপের "শিখুন" অংশে হজ ও উমরাহর ধাপগুলো পড়তে পারেন।',
  en: 'Demo mode: no AI provider or API key is configured on this backend, so this is not a real AI answer. See backend/README.md to set it up. Meanwhile, you can read the Hajj and Umrah steps in the Learn section of the app.',
  ar: 'الوضع التجريبي: لم يتم ضبط مزوّد ذكاء اصطناعي أو مفتاح API في هذا الخادم، لذا هذه ليست إجابة حقيقية. راجع backend/README.md للإعداد. وفي الأثناء يمكنك قراءة خطوات الحج والعمرة في قسم «تعلّم» بالتطبيق.',
};

module.exports = {
  name: 'demo',
  async complete({ language }) {
    return NOTICE[language] || NOTICE.bn;
  },
};
