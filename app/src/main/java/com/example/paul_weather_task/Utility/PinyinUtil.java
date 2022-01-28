package com.example.paul_weather_task.Utility;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtil {
    public static String ToPinyin(String chinese){
        if(!TextUtils.isEmpty(chinese)) {
            StringBuilder sb = new StringBuilder();
            char[] chars = chinese.toCharArray();
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            format.setVCharType(HanyuPinyinVCharType.WITH_V);
            for (int i = 0; i < chars.length; i++) {
                char word = chars[i];
                if (word > 128) {
                    try {
                        sb.append(PinyinHelper.toHanyuPinyinStringArray(word, format)[0] + " ");
// Get the first pinyin for multipul choices
//多音字取第一个

                    } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                        badHanyuPinyinOutputFormatCombination.printStackTrace();
                    }
                } else {
                    sb.append(word);
                }
            }
            return sb.toString();
        }else{
            return "";
        }
    }
    public static String ToPinyinFirstLetter(String chinese){
        if(!TextUtils.isEmpty(chinese)) {
            StringBuilder sb = new StringBuilder();
            String s = ToPinyin(chinese);
            String[] split = s.split("\\s");
            for (int i = 0; i < split.length; i++) {
                sb.append(split[i].charAt(0));
            }
            return sb.toString();
        }else {
            return "";
        }
    }
}
