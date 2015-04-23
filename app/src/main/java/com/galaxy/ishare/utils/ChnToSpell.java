package com.galaxy.ishare.utils;


import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Hashtable;

import android.content.Context;

public class ChnToSpell {

	private static final String CHN_DATABASE_NAME = "py.db"; //汉字索引文件�?
	
	public static final int TRANS_MODE_QUAN_PIN = 1;          //全拼
	public static final int TRANS_MODE_PINYIN_INITIAL = 2;    //全拼的首字母
	
	//汉字基本发音
	static String PyMusicCode[] = 
	 {
	    "a", "ai", "an", "ang", "ao", "ba", "bai", "ban", "bang", "bao",
	    "bei", "ben", "beng", "bi", "bian", "biao", "bie", "bin", "bing", "bo",
	    "bu", "ca", "cai", "can", "cang", "cao", "ce", "ceng", "cha", "chai",
	    "chan", "chang", "chao", "che", "chen", "cheng", "chi", "chong", "chou", "chu",
	    "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci", "cong", "cou", "cu",
	    "cuan", "cui", "cun", "cuo", "da", "dai", "dan", "dang", "dao", "de",
	    "deng", "di", "dian", "diao", "die", "ding", "diu", "dong", "dou", "du",
	    "duan", "dui", "dun", "duo", "e", "en", "er", "fa", "fan", "fang",
	    "fei", "fen", "feng", "fu", "fou", "ga", "gai", "gan", "gang", "gao",
	    "ge", "ji", "gen", "geng", "gong", "gou", "gu", "gua", "guai", "guan",
	    "guang", "gui", "gun", "guo", "ha", "hai", "han", "hang", "hao", "he",
	    "hei", "hen", "heng", "hong", "hou", "hu", "hua", "huai", "huan", "huang",
	    "hui", "hun", "huo", "jia", "jian", "jiang", "qiao", "jiao", "jie", "jin",
	    "jing", "jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai", "kan",
	    "kang", "kao", "ke", "ken", "keng", "kong", "kou", "ku", "kua", "kuai",
	    "kuan", "kuang", "kui", "kun", "kuo", "la", "lai", "lan", "lang", "lao",
	    "le", "lei", "leng", "li", "lia", "lian", "liang", "liao", "lie", "lin",
	    "ling", "liu", "long", "lou", "lu", "luan", "lue", "lun", "luo", "ma",
	    "mai", "man", "mang", "mao", "me", "mei", "men", "meng", "mi", "mian",
	    "miao", "mie", "min", "ming", "miu", "mo", "mou", "mu", "na", "nai",
	    "nan", "nang", "nao", "ne", "nei", "nen", "neng", "ni", "nian", "niang",
	    "niao", "nie", "nin", "ning", "niu", "nong", "nu", "nuan", "nue", "yao",
	    "nuo", "o", "ou", "pa", "pai", "pan", "pang", "pao", "pei", "pen",
	    "peng", "pi", "pian", "piao", "pie", "pin", "ping", "po", "pou", "pu",
	    "qi", "qia", "qian", "qiang", "qie", "qin", "qing", "qiong", "qiu", "qu",
	    "quan", "que", "qun", "ran", "rang", "rao", "re", "ren", "reng", "ri",
	    "rong", "rou", "ru", "ruan", "rui", "run", "ruo", "sa", "sai", "san",
	    "sang", "sao", "se", "sen", "seng", "sha", "shai", "shan", "shang", "shao",
	    "she", "shen", "sheng", "shi", "shou", "shu", "shua", "shuai", "shuan", "shuang",
	    "shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui", "sun",
	    "suo", "ta", "tai", "tan", "tang", "tao", "te", "teng", "ti", "tian",
	    "tiao", "tie", "ting", "tong", "tou", "tu", "tuan", "tui", "tun", "tuo",
	    "wa", "wai", "wan", "wang", "wei", "wen", "weng", "wo", "wu", "xi",
	    "xia", "xian", "xiang", "xiao", "xie", "xin", "xing", "xiong", "xiu", "xu",
	    "xuan", "xue", "xun", "ya", "yan", "yang", "ye", "yi", "yin", "ying",
	    "yo", "yong", "you", "yu", "yuan", "yue", "yun", "za", "zai", "zan",
	    "zang", "zao", "ze", "zei", "zen", "zeng", "zha", "zhai", "zhan", "zhang",
	    "zhao", "zhe", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu", "zhua", "zhuai",
	    "zhuan", "zhuang", "zhui", "zhun", "zhuo", "zi", "zong", "zou", "zu", "zuan",
	    "zui", "zun", "zuo", "", "ei", "m", "n", "dia", "cen", "nou",
	 "jv", "qv", "xv", "lv", "nv"
	 };

	//汉字索引数组�?w汉字因此从文件读�?	//static public short PyCodeIndex[][] = new short[126][191];
	static public short PyCodeIndex[][] = null;

	//罗马数字
	final static  String CharIndex[] = 
	 {
	 "1","2","3","4","5","6","7","8","9","10","","","","","","",
	    "1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20",
	    "1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20",
	    "1","2","3","4","5","6","7","8","9","10","","",
	    "1","2","3","4","5","6","7","8","9","10","","",
	    "1","2","3","4","5","6","7","8","9","10","11","12","",""
	 };

	//希腊数字
	final static String CharIndex2[] =
	 {
	 "a","b","g","d","e","z","e","th","i","k","l","m","n","x","o","p","r",
	    "s","t","u","ph","kh","ps","o"
	 };
	
	//初始化索引数�?
	static public void initChnToSpellDB(Context cnt)
	{
		if(PyCodeIndex != null) {
			return;
		}
		
		PyCodeIndex = new short[126][191];
		int i;
		try {
			byte b [] = new byte[191 * 2];
			ByteBuffer buf = ByteBuffer.wrap(b);
			InputStream in = cnt.getResources().getAssets().open(CHN_DATABASE_NAME);
			BufferedInputStream is = new BufferedInputStream(in);
			for (i = 0; i < 126; i++) {
				buf.position(0);
				is.read(buf.array(), 0, 191 * 2);
				buf.position(0);
				buf.asShortBuffer().get(PyCodeIndex[i]);
			}
		} catch (Exception e) {
		}

	}

	private static Hashtable<String, String> sQuanpinCache = new Hashtable<String, String>(999);
	private static Hashtable<String, String> sInitCache = new Hashtable<String, String>(999);
	 /* 
	  * 函数名称: MakeSpellCode
	  * 参数: strText�?�?��转化的字符串
	  *       nMode: 转换模式
	  * nMode:                   
	  * 1: 获取汉字全拼
	  * 2: 获取汉字拼音首字母缩�?
	  *
	  */
	public static String MakeSpellCode(String strSrc, int nMode)
	{
		if(strSrc == null){
			return "";
		}
		if(nMode == ChnToSpell.TRANS_MODE_QUAN_PIN) {
			if(sQuanpinCache.containsKey(strSrc)) {
				return sQuanpinCache.get(strSrc);
			}
		} else if(nMode == ChnToSpell.TRANS_MODE_PINYIN_INITIAL) {
			if(sInitCache.containsKey(strSrc)) {
				return sInitCache.get(strSrc);
			}			
		}
		int i, Index;
		byte [] strText = "?".getBytes();
		try {
			strText = strSrc.getBytes("GBK");
		} catch (Exception e) {
		}
		StringBuilder APy = new StringBuilder();
		int fFlag1, fFlag2, fFlag3;
		int cTemp, cTemp1 = 0;
		String PyCode, strTemp;

		fFlag1 = ((nMode & 0x0001) == 1) ? 1 : 0;
		fFlag2 = ((nMode & 0x0002) == 2) ? 1 : 0;
		fFlag3 = ((nMode & 0x0004) == 4) ? 1 : 0;
		String Result = "";
		if (nMode < 0)
			return Result;
		i = 0;

		while (i < strText.length) {

			cTemp = strText[i];
			if ((i + 1) != strText.length)
				cTemp1 = strText[i + 1];
			cTemp=(cTemp+256)%256;
			cTemp1=(cTemp1+256)%256;
			if ((cTemp >= 129) && (cTemp1 > 64))  // 是否�?GBK 字符
			{
				switch (cTemp) {
				case (byte) 163: // 全角 ASCII
					APy.append((char) (cTemp1 - 128));
					
				 // 控制不能输出非数�? 字母的字�?
					if ((fFlag3 != 1)
							&& (APy.charAt(1) < 48 && APy.charAt(1) > 57)
							&& (APy.charAt(1) < 65 && APy.charAt(1) > 90)
							&& (APy.charAt(1) < 97 && APy.charAt(1) > 122))
						APy.delete(0, APy.length());
				
					break;
				case (byte) 162: // 罗马数字
					if ((strText[i + 1]) > 160)
						APy.append(CharIndex[cTemp1 - 161]);
					else if (fFlag2 != 0)
						APy.append("?");
					
					break;
				case (byte) 166:  // 希腊数字
					if (cTemp1 > 0xa1 && cTemp1 < 0xb8) {
						strTemp = CharIndex2[cTemp1 - 0xA0 - 1];
						strTemp = strTemp.toLowerCase();
						
						APy.append(strTemp);
						
					} else if (cTemp1 > 0xc1 && cTemp1 < 0xd8) {
						strTemp = CharIndex2[cTemp1 - 0xc0 - 1];
						strTemp = strTemp.toLowerCase();
						APy.append(strTemp);
						
					} else
						APy.append("?");

					
					break;

				default: // �?��汉字
					// 获得拼音索引
					if(cTemp>=255 || cTemp1>=255)
						break;
					Index = PyCodeIndex[cTemp - 129][cTemp1 - 64];
					if (Index == 0)
						Index = 1;
					PyCode = PyMusicCode[Index - 1];
					
					if (Index == 0) {
						if (fFlag2 != 0)
							APy.append("?"); 
			
					} else if (fFlag1 == 0) 
					{
						if(APy.length()<1) APy.append(PyCode.charAt(0));
						else APy.setCharAt(0, PyCode.charAt(0));
			
					} else {
						APy.append(PyCode);

					}
					Result = Result + APy;
					APy.setLength(0);
					break;
				}
				i += 2;
			} else {
				StringBuilder szTemp = new StringBuilder();
				
				szTemp.append((char)cTemp);
				Result = Result + szTemp.toString().toLowerCase();
				i++;
			}
		}
		
		if(nMode == ChnToSpell.TRANS_MODE_QUAN_PIN) {
			sQuanpinCache.put(strSrc, Result);
		} else if(nMode == ChnToSpell.TRANS_MODE_PINYIN_INITIAL) {
			sInitCache.put(strSrc, Result);
		}
		return Result;
	}

}
