package Test;

import com.woting.common.base64.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class TestCodecccc {
    public static void main(String[] args) {
        String test="这是一个测试呀";
        System.out.println("原||"+test);
//        String _t = MyBase64.encode(test.getBytes());
//        System.out.println("编||"+_t);
//        byte b[] = MyBase64.decode(_t);
//        String _t = Base64.encodeBase64String(test.getBytes());
//        System.out.println("编||"+_t);
//        byte b[] = Base64.decodeBase64(_t);
        String _t = Base64.encode(test.getBytes());
        System.out.println("编||"+_t);
        byte b[] = Base64.decode(_t);
        System.out.println("解||"+new String(b));
        File f = new File("D:\\workIDE\\2437290ac99146eabb5f10dde99cb7c2.amr");
        InputStream in = null;
        FileOutputStream out = null;

        try {
        	in = new FileInputStream(f);
        	b = new byte[(int)f.length()];
			int _d = in.read(), j=0;
			while (_d!=-1) {
				b[j++]=(byte)_d;
				_d = in.read();
			}

			System.out.println(j+":==:"+f.length());
            //System.out.println("原||"+new String(b));
            _t = Base64.encode(b);
            //System.out.println("编||"+_t);

            String json="";
            json="{\"AudioData\":\""+_t+"\"}";
            json = new String(json.getBytes(), "UTF-8");
            System.out.println(json);

            b = Base64.decode(_t);
            //System.out.println("解||"+new String(b));
            File outputFile = new File("D:\\workIDE\\out.amr");
            outputFile.createNewFile();
            out = new FileOutputStream(outputFile);
            out.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {if (in!=null) in.close();} catch(Exception e){} finally{in=null;};
            try {if (out!=null) out.close();} catch(Exception e){} finally{out=null;};
        }
    }
}