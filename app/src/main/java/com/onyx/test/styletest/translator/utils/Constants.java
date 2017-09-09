package com.onyx.test.styletest.translator.utils;

import com.onyx.test.styletest.translator.Language;
import com.onyx.test.styletest.translator.TranslatePlatform;
import com.onyx.test.styletest.translator.xmlImpl.BaiduXMLTranslator;
import com.onyx.test.styletest.translator.xmlImpl.GoogleXMLTranslator;
import com.onyx.test.styletest.translator.xmlImpl.XMLTranslator;
import com.onyx.test.styletest.translator.xmlImpl.YouDaoXMLTranslator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lion on 2016/10/29.
 * 常用变量集中配置管理
 */
public class Constants {

    //百度翻译平台账号信息
    public static final String BAIDU_APP_ID = "";
    public static final String BAIDU_APP_SECRET = "";
    //google翻译平台所有api key
    public static final String GOOGLE_API_KEY = "";
    //有道翻译平台api key
    public static final String YOUDAO_API_KEY = "";

    //翻译平台配置，方便拓展所以放在这里
    public static final Map<TranslatePlatform, Class<? extends XMLTranslator>>
            platformConfig = new HashMap<>();

    static {
        platformConfig.put(TranslatePlatform.BAIDU, BaiduXMLTranslator.class);
        platformConfig.put(TranslatePlatform.GOOGLE, GoogleXMLTranslator.class);
        platformConfig.put(TranslatePlatform.YOUDAO, YouDaoXMLTranslator.class);
    }

    //不同语言对应的文件夹名称，如果你需要其他的语言你需要在这里添加
    public static final String VALUES_AF = "values-af";
    public static final String VALUES_SQ = "values-sq";
    public static final String VALUES_AR = "values-ar";
    public static final String VALUES_HY = "values-hy";
    public static final String VALUES_AZ = "values-az";
    public static final String VALUES_EU = "values-eu";
    public static final String VALUES_BE = "values-be";
    public static final String VALUES_BN = "values-bn";
    public static final String VALUES_BS = "values-bs";
    public static final String VALUES_BG = "values-bg";
    public static final String VALUES_CA = "values-ca";
    public static final String VALUES_CEB = "values-ceb";
    public static final String VALUES_NY = "values-ny";
    public static final String VALUES_ZH_CN = "values-zh-rCN";
    public static final String VALUES_ZH_TW = "values-zh-rTW";
    public static final String VALUES_HR = "values-hr";
    public static final String VALUES_CS = "values-cs";
    public static final String VALUES_DA = "values-da";
    public static final String VALUES_NL = "values-nl";
    public static final String VALUES_EN = "values-en";
    public static final String VALUES_EO = "values-eo";
    public static final String VALUES_ET = "values-et";
    public static final String VALUES_TL = "values-tl";
    public static final String VALUES_FI = "values-fi";
    public static final String VALUES_FR = "values-fr";
    public static final String VALUES_GL = "values-gl";
    public static final String VALUES_KA = "values-ka";
    public static final String VALUES_DE = "values-de";
    public static final String VALUES_EL = "values-el";
    public static final String VALUES_GU = "values-gu";
    public static final String VALUES_HT = "values-ht";
    public static final String VALUES_HA = "values-ha";
    public static final String VALUES_IW = "values-iw";
    public static final String VALUES_HI = "values-hi";
    public static final String VALUES_HMN = "values-hmn";
    public static final String VALUES_HU = "values-hu";
    public static final String VALUES_IS = "values-is";
    public static final String VALUES_IG = "values-ig";
    public static final String VALUES_ID = "values-id";
    public static final String VALUES_GA = "values-ga";
    public static final String VALUES_IT = "values-it";
    public static final String VALUES_JA = "values-ja";
    public static final String VALUES_JW = "values-jw";
    public static final String VALUES_KN = "values-kn";
    public static final String VALUES_KK = "values-kk";
    public static final String VALUES_KM = "values-km";
    public static final String VALUES_KO = "values-ko";
    public static final String VALUES_LO = "values-lo";
    public static final String VALUES_LA = "values-la";
    public static final String VALUES_LV = "values-lv";
    public static final String VALUES_LT = "values-lt";
    public static final String VALUES_MK = "values-mk";
    public static final String VALUES_MG = "values-mg";
    public static final String VALUES_MS = "values-ms";
    public static final String VALUES_ML = "values-ml";
    public static final String VALUES_MT = "values-mt";
    public static final String VALUES_MI = "values-mi";
    public static final String VALUES_MR = "values-mr";
    public static final String VALUES_MN = "values-mn";
    public static final String VALUES_MY = "values-my";
    public static final String VALUES_NE = "values-ne";
    public static final String VALUES_NO = "values-no";
    public static final String VALUES_FA = "values-fa";
    public static final String VALUES_PL = "values-pl";
    public static final String VALUES_PT = "values-pt";
    public static final String VALUES_RO = "values-ro";
    public static final String VALUES_RU = "values-ru";
    public static final String VALUES_SR = "values-sr";
    public static final String VALUES_ST = "values-st";
    public static final String VALUES_SI = "values-si";
    public static final String VALUES_SK = "values-sk";
    public static final String VALUES_SL = "values-sl";
    public static final String VALUES_SO = "values-so";
    public static final String VALUES_ES = "values-es";
    public static final String VALUES_SU = "values-su";
    public static final String VALUES_SW = "values-sw";
    public static final String VALUES_SV = "values-sv";
    public static final String VALUES_TG = "values-tg";
    public static final String VALUES_TA = "values-ta";
    public static final String VALUES_TE = "values-te";
    public static final String VALUES_TH = "values-th";
    public static final String VALUES_TR = "values-tr";
    public static final String VALUES_UK = "values-uk";
    public static final String VALUES_UR = "values-ur";
    public static final String VALUES_UZ = "values-uz";
    public static final String VALUES_VI = "values-vi";
    public static final String VALUES_CY = "values-cy";
    public static final String VALUES_YI = "values-yi";
    public static final String VALUES_YO = "values-yo";
    public static final String VALUES_ZU = "values-zu";

    //Language对应的文件夹名称，当你需要拓展的时候你必须在这里配置
    public static final Map<Language, String> valuesFolders = new HashMap<>();

    static {
        valuesFolders.put(Language.AF, VALUES_AF);
        valuesFolders.put(Language.SQ, VALUES_SQ);
        valuesFolders.put(Language.AR, VALUES_AR);
        valuesFolders.put(Language.HY, VALUES_HY);
        valuesFolders.put(Language.AZ, VALUES_AZ);
        valuesFolders.put(Language.EU, VALUES_EU);
        valuesFolders.put(Language.BE, VALUES_BE);
        valuesFolders.put(Language.BN, VALUES_BN);
        valuesFolders.put(Language.BS, VALUES_BS);
        valuesFolders.put(Language.BG, VALUES_BG);
        valuesFolders.put(Language.CA, VALUES_CA);
        valuesFolders.put(Language.CEB, VALUES_CEB);
        valuesFolders.put(Language.NY, VALUES_NY);
        valuesFolders.put(Language.ZH_CN, VALUES_ZH_CN);
        valuesFolders.put(Language.ZH_TW, VALUES_ZH_TW);
        valuesFolders.put(Language.HR, VALUES_HR);
        valuesFolders.put(Language.CS, VALUES_CS);
        valuesFolders.put(Language.DA, VALUES_DA);
        valuesFolders.put(Language.NL, VALUES_NL);
        valuesFolders.put(Language.EN, VALUES_EN);
        valuesFolders.put(Language.EO, VALUES_EO);
        valuesFolders.put(Language.ET, VALUES_ET);
        valuesFolders.put(Language.TL, VALUES_TL);
        valuesFolders.put(Language.FI, VALUES_FI);
        valuesFolders.put(Language.FR, VALUES_FR);
        valuesFolders.put(Language.GL, VALUES_GL);
        valuesFolders.put(Language.KA, VALUES_KA);
        valuesFolders.put(Language.DE, VALUES_DE);
        valuesFolders.put(Language.EL, VALUES_EL);
        valuesFolders.put(Language.GU, VALUES_GU);
        valuesFolders.put(Language.HT, VALUES_HT);
        valuesFolders.put(Language.HA, VALUES_HA);
        valuesFolders.put(Language.IW, VALUES_IW);
        valuesFolders.put(Language.HI, VALUES_HI);
        valuesFolders.put(Language.HMN, VALUES_HMN);
        valuesFolders.put(Language.HU, VALUES_HU);
        valuesFolders.put(Language.IS, VALUES_IS);
        valuesFolders.put(Language.IG, VALUES_IG);
        valuesFolders.put(Language.ID, VALUES_ID);
        valuesFolders.put(Language.GA, VALUES_GA);
        valuesFolders.put(Language.IT, VALUES_IT);
        valuesFolders.put(Language.JA, VALUES_JA);
        valuesFolders.put(Language.JW, VALUES_JW);
        valuesFolders.put(Language.KN, VALUES_KN);
        valuesFolders.put(Language.KK, VALUES_KK);
        valuesFolders.put(Language.KM, VALUES_KM);
        valuesFolders.put(Language.KO, VALUES_KO);
        valuesFolders.put(Language.LO, VALUES_LO);
        valuesFolders.put(Language.LA, VALUES_LA);
        valuesFolders.put(Language.LV, VALUES_LV);
        valuesFolders.put(Language.LT, VALUES_LT);
        valuesFolders.put(Language.MK, VALUES_MK);
        valuesFolders.put(Language.MG, VALUES_MG);
        valuesFolders.put(Language.MS, VALUES_MS);
        valuesFolders.put(Language.ML, VALUES_ML);
        valuesFolders.put(Language.MT, VALUES_MT);
        valuesFolders.put(Language.MI, VALUES_MI);
        valuesFolders.put(Language.MR, VALUES_MR);
        valuesFolders.put(Language.MN, VALUES_MN);
        valuesFolders.put(Language.MY, VALUES_MY);
        valuesFolders.put(Language.NE, VALUES_NE);
        valuesFolders.put(Language.NO, VALUES_NO);
        valuesFolders.put(Language.FA, VALUES_FA);
        valuesFolders.put(Language.PL, VALUES_PL);
        valuesFolders.put(Language.PT, VALUES_PT);
        valuesFolders.put(Language.RO, VALUES_RO);
        valuesFolders.put(Language.RU, VALUES_RU);
        valuesFolders.put(Language.SR, VALUES_SR);
        valuesFolders.put(Language.ST, VALUES_ST);
        valuesFolders.put(Language.SI, VALUES_SI);
        valuesFolders.put(Language.SK, VALUES_SK);
        valuesFolders.put(Language.SL, VALUES_SL);
        valuesFolders.put(Language.SO, VALUES_SO);
        valuesFolders.put(Language.ES, VALUES_ES);
        valuesFolders.put(Language.SU, VALUES_SU);
        valuesFolders.put(Language.SW, VALUES_SW);
        valuesFolders.put(Language.SV, VALUES_SV);
        valuesFolders.put(Language.TG, VALUES_TG);
        valuesFolders.put(Language.TA, VALUES_TA);
        valuesFolders.put(Language.TE, VALUES_TE);
        valuesFolders.put(Language.TH, VALUES_TH);
        valuesFolders.put(Language.TR, VALUES_TR);
        valuesFolders.put(Language.UK, VALUES_UK);
        valuesFolders.put(Language.UR, VALUES_UR);
        valuesFolders.put(Language.UZ, VALUES_UZ);
        valuesFolders.put(Language.VI, VALUES_VI);
        valuesFolders.put(Language.CY, VALUES_CY);
        valuesFolders.put(Language.YI, VALUES_YI);
        valuesFolders.put(Language.YO, VALUES_YO);
        valuesFolders.put(Language.ZU, VALUES_ZU);
    }
}
