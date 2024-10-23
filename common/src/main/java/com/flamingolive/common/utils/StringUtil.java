package com.flamingolive.common.utils;

import com.flamingolive.common.CommonAppConfig;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by cxf on 2018/9/28.
 */

public class StringUtil {
    private static final DecimalFormat sDecimalFormat;
    private static final DecimalFormat sDecimalFormat2;
    // private static Pattern sPattern;
    private static final Pattern sIntPattern;
    private static final Random sRandom;
    private static final StringBuilder sStringBuilder;


    static {
        sDecimalFormat = new DecimalFormat("#.#");
        sDecimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        sDecimalFormat2 = new DecimalFormat("#.##");
        sDecimalFormat2.setRoundingMode(RoundingMode.DOWN);
        //sPattern = Pattern.compile("[\u4e00-\u9fa5]");
        sIntPattern = Pattern.compile("^[-\\+]?[\\d]*$");
        sRandom = new Random();
        sStringBuilder = new StringBuilder();
    }

    public static String format(double value) {
        return sDecimalFormat.format(value);
    }

    /**
     * 把数字转化成多少万
     */
    public static String toWan(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat.format(num / 10000d) + "W";
    }


    /**
     * 把数字转化成多少万
     */
    public static String toWan2(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat.format(num / 10000d);
    }

    /**
     * 把数字转化成多少万
     */
    public static String toWan3(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat2.format(num / 10000d) + "w";
    }

//    /**
//     * 判断字符串中是否包含中文
//     */
//    public static boolean isContainChinese(String str) {
//        Matcher m = sPattern.matcher(str);
//        if (m.find()) {
//            return true;
//        }
//        return false;
//    }

    /**
     * 判断一个字符串是否是数字
     */
    public static boolean isInt(String str) {
        return sIntPattern.matcher(str).matches();
    }


    /**
     * 把一个long类型的总毫秒数转成时长
     */
    public static String getDurationText(long mms) {
        int hours = (int) (mms / (1000 * 60 * 60));
        int minutes = (int) ((mms % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((mms % (1000 * 60)) / 1000);
        sStringBuilder.delete(0, sStringBuilder.length());
        if (hours > 0) {
            if (hours < 10) {
                sStringBuilder.append("0");
            }
            sStringBuilder.append(String.valueOf(hours));
            sStringBuilder.append(":");
        }
        if (minutes > 0) {
            if (minutes < 10) {
                sStringBuilder.append("0");
            }
            sStringBuilder.append(String.valueOf(minutes));
            sStringBuilder.append(":");
        } else {
            sStringBuilder.append("00:");
        }
        if (seconds > 0) {
            if (seconds < 10) {
                sStringBuilder.append("0");
            }
            sStringBuilder.append(String.valueOf(seconds));
        } else {
            sStringBuilder.append("00");
        }
        return sStringBuilder.toString();
    }


    /**
     * 把秒数转成时长
     */
    public static String getDurationText2(int secondCount,StringBuilder sb) {
        int hours = secondCount / 3600;
        int minutes = 0;
        int last = secondCount % 3600;
        if (last > 0) {
            minutes = last / 60;
        }
        int seconds = secondCount % 60;
        sb.delete(0, sb.length());
        if (hours > 0) {
            if (hours < 10) {
                sb.append("0");
            }
            sb.append(String.valueOf(hours));
            sb.append(":");
        }
        if (minutes > 0) {
            if (minutes < 10) {
                sb.append("0");
            }
            sb.append(String.valueOf(minutes));
            sb.append(":");
        } else {
            sb.append("00:");
        }
        if (seconds > 0) {
            if (seconds < 10) {
                sb.append("0");
            }
            sb.append(String.valueOf(seconds));
        } else {
            sb.append("00");
        }


//        String s = sb.toString();
//        L.e("getDurationText---hours---->" + hours + "---minutes-->" + minutes + "---seconds-->" + seconds + "--s-->" + s);
        return sb.toString();
    }


    /**
     * 设置视频输出路径
     */
    public static String generateVideoOutputPath() {
        String outputDir = CommonAppConfig.VIDEO_PATH;
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        String videoName = DateFormatUtil.getVideoCurTimeString() + sRandom.nextInt(9999);
        return contact(outputDir, "/android_", CommonAppConfig.getInstance().getUid(), "_", videoName, ".mp4");
    }


    /**
     * 获取随机文件名
     */
    public static String generateFileName() {
        return contact("android_",
                CommonAppConfig.getInstance().getUid(),
                "_",
                DateFormatUtil.getVideoCurTimeString(),
                String.valueOf(sRandom.nextInt(9999)));
    }


    /**
     * 多个字符串拼接
     */
    public static String contact(String... args) {
        sStringBuilder.delete(0, sStringBuilder.length());
        for (String s : args) {
            sStringBuilder.append(s);
        }
        return sStringBuilder.toString();
    }


    /*比较字符串*/
    public static boolean compareString(String var1, String var2) {
        if (var1 == null && var2 == null) {
            return true;
        } else if (var1 != null && var2 != null) {
            return var1.equals(var2);
        } else {
            return false;
        }
    }

    public static int getRandomInt(int bound){
        return sRandom.nextInt(bound);
    }
}
