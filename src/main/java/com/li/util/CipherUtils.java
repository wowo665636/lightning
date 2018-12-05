package com.li.util;

import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @Package: com.sohu.util
 * @date: 2017/10/23 16:50
 * @Des: ...
 */
public class CipherUtils {

//    public static String RULES = "46356afe55fa3cea9cbe73ad442cad47";
//    public static String IV_PARAM = "FUCKSHITFUCKSHIT";

    public static String RULES = null;
    public static String IV_PARAM = null;
    public static final String SPACE = " ";
    private static final String AES = "AES";
    private static final String CIPHER = "AES/CBC/PKCS5Padding";
    private static final String E = "e=";
    private static final String ADD = "+";
    private static final String ENCODE = "utf-8";
    public static final String _t = "\t";
    public static final String DEFAULT = "";

    public static void initDecodeParameters(String rule, String ivParam) {
        CipherUtils.RULES = rule;
        IV_PARAM = ivParam;
    }

    public static String urlDecode(String content) {
        String str = null;
        try {
            str = URLDecoder.decode(content, ENCODE);
        } catch (UnsupportedEncodingException e) {
        }
        String tmp = str.replace(SPACE, ADD);//
        if (tmp.startsWith(E)) {
            tmp = tmp.substring(2);
        }
        String other = StringUtils.EMPTY;
        int idx = tmp.indexOf(_t);
        if (idx != -1) {
            other = tmp.substring(idx);
            tmp = tmp.substring(0, idx);
        }
        String dstr;
        //长度低于一定范围
        if (tmp.length() > 10) {
            dstr = AESDecode(tmp);
            if (dstr == null || dstr.equals(StringUtils.EMPTY)) {
                dstr = DEFAULT;
            }
        } else {
            dstr = DEFAULT;
        }
        return dstr + other;
    }

    public static String AESEncode(String content) {
        byte[] bs = hex2Bytes(RULES);
        return AESEncode(bs, IV_PARAM, content);
    }

    public static String AESDecode(String content) {
        byte[] raw = hex2Bytes(RULES);
        return AESDecode(raw, IV_PARAM, content);
    }

    public static String AESEncode(String rules, String ivParam, String content) {
        byte[] raw = rules.getBytes();
        return AESDecode(raw, ivParam, content);
    }

    public static String AESEncode(byte[] raw, String ivParam, String content) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(CIPHER);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
        IvParameterSpec iv = new IvParameterSpec(ivParam.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        try {
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        byte[] encrypted = new byte[0];
        try {
            encrypted = cipher.doFinal(content.getBytes(ENCODE));
        } catch (IllegalBlockSizeException e) {
        } catch (BadPaddingException e) {
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new BASE64Encoder().encode(encrypted);//此处使用BASE64做转码。
    }

    public static String AESDecode(String rules, String ivParam, String content) {
        byte[] raw = new byte[0];
        try {
            raw = rules.getBytes(ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return AESDecode(raw, ivParam, content);
    }

    public static String AESDecode(byte[] raw, String ivParam, String content) {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(CIPHER);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        IvParameterSpec iv = new IvParameterSpec(ivParam.getBytes());
        try {
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        byte[] encrypted1 = new byte[0];//先用base64解密
        try {
            encrypted1 = new BASE64Decoder().decodeBuffer(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] original = new byte[0];
        try {
            original = cipher.doFinal(encrypted1);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        String res = null;
        try {
            res = new String(original, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static byte[] hex2Bytes(String src) {
        byte[] ret = new byte[16];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < 16; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

}
