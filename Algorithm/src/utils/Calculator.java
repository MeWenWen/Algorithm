package utils;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
public class Calculator
{
    //榛樿闄ゆ硶杩愮畻绮惧害
    private static final int DEF_DIV_SCALE = 10;
    //鏋勯�犲櫒绉佹湁鍖栵紝璁╄繖涓被涓嶈兘瀹炰緥鍖�
    private Calculator(){}
    //鎻愪緵绮剧‘鐨勫姞娉曡繍绠�
    public static double add(double v1, double v2)
    {
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.add(b2).doubleValue();
    }
    //绮剧‘鐨勫噺娉曡繍绠�
    public static double sub(double v1, double v2)
    {
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.subtract(b2).doubleValue();
    }
    //绮剧‘鐨勪箻娉曡繍绠�
    public static double mul(double v1, double v2)
    {
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.multiply(b2).doubleValue();
    }
    //鎻愪緵锛堢浉瀵癸級绮剧‘鐨勯櫎娉曡繍绠楋紝褰撳彂鐢熼櫎涓嶅敖鐨勬儏鍐垫椂
    //绮剧‘鍒板皬鏁扮偣鍚�10浣嶇殑鏁板瓧鍥涜垗浜斿叆
    public static double div(double v1, double v2)
    {
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.divide(b2, DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double div(double d1,double d2,int len) {// 杩涜闄ゆ硶杩愮畻
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.divide(b2,len,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static void LogResult(String file, String conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));// true,杩涜杩藉姞鍐欍��
            out.write(conent+"\r\n");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//    public static void main(String[] args)
//    {
//        System.out.println("0.05 + 0.01 = " + Caculator.add(0.05, 0.01));
//        System.out.println("1.0 - 0.42 = " + Caculator.sub(1.0, 0.42));
//        System.out.println("4.015*100 = " + Caculator.mul(4.015, 100));
//        System.out.println("123.3/100 = " + Caculator.div(123.3, 100));
//    }
}
