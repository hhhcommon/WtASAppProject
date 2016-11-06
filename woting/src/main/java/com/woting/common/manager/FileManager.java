package com.woting.common.manager;

/**
 * 作者：xinlong on 2016/10/21 12:35
 * 邮箱：645700751@qq.com
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.woting.common.util.ResourceUtil;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class FileManager {
    private static String TAG = "FileManager";
    private static final int FILE_BUFFER_SIZE = 51200;

    public FileManager() {
    }

    public static String getImageSaveFilePath(Context context) {
        return hasSDCard()?getRootFilePath() + context.getResources().getString(ResourceUtil.getStringId(context, "app_local_path")) + "/image/":getRootFilePath() + context.getResources().getString(ResourceUtil.getStringId(context, "app_local_path")) + "/image/";
    }

    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals("mounted");
    }

    public static String getRootFilePath() {
        return hasSDCard()?Environment.getExternalStorageDirectory().getAbsolutePath() + "/":Environment.getDataDirectory().getAbsolutePath() + "/data/";
    }

    public static boolean fileIsExist(String filePath) {
        if(filePath != null && filePath.length() >= 1) {
            File f = new File(filePath);
            return f.exists();
        } else {
            Log.e(TAG, "param invalid, filePath: " + filePath);
            return false;
        }
    }

    public static InputStream readFile(String filePath) {
        if(filePath == null) {
            Log.e(TAG, "Invalid param. filePath: " + filePath);
            return null;
        } else {
            FileInputStream is = null;

            try {
                if(fileIsExist(filePath)) {
                    File ex = new File(filePath);
                    is = new FileInputStream(ex);
                    return is;
                } else {
                    return null;
                }
            } catch (Exception var3) {
                Log.e(TAG, "Exception, ex: " + var3.toString());
                return null;
            }
        }
    }

    public static boolean createDirectory(String filePath) {
        if(filePath == null) {
            return false;
        } else {
            File file = new File(filePath);
            return file.exists()?true:file.mkdirs();
        }
    }

    public static boolean deleteDirectory(String filePath) {
        if(filePath == null) {
            Log.e(TAG, "Invalid param. filePath: " + filePath);
            return false;
        } else {
            File file = new File(filePath);
            if(file != null && file.exists()) {
                if(file.isDirectory()) {
                    File[] list = file.listFiles();

                    for(int i = 0; i < list.length; ++i) {
                        Log.d(TAG, "delete filePath: " + list[i].getAbsolutePath());
                        if(list[i].isDirectory()) {
                            deleteDirectory(list[i].getAbsolutePath());
                        } else {
                            list[i].delete();
                        }
                    }
                }

                Log.d(TAG, "delete filePath: " + file.getAbsolutePath());
                file.delete();
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean writeFile(String filePath, InputStream inputStream) {
        if(filePath != null && filePath.length() >= 1) {
            try {
                File e = new File(filePath);
                if(e.exists()) {
                    deleteDirectory(filePath);
                }

                String pth = filePath.substring(0, filePath.lastIndexOf("/"));
                boolean ret = createDirectory(pth);
                if(!ret) {
                    Log.e(TAG, "createDirectory fail path = " + pth);
                    return false;
                } else {
                    boolean ret1 = e.createNewFile();
                    if(!ret) {
                        Log.e(TAG, "createNewFile fail filePath = " + filePath);
                        return false;
                    } else {
                        FileOutputStream fileOutputStream = new FileOutputStream(e);
                        byte[] buf = new byte[1024];

                        for(int c = inputStream.read(buf); -1 != c; c = inputStream.read(buf)) {
                            fileOutputStream.write(buf, 0, c);
                        }

                        fileOutputStream.flush();
                        fileOutputStream.close();
                        return true;
                    }
                }
            } catch (Exception var9) {
                var9.printStackTrace();
                return false;
            }
        } else {
            Log.e(TAG, "Invalid param. filePath: " + filePath);
            return false;
        }
    }

    public static boolean writeFile(String filePath, String fileContent) {
        return writeFile(filePath, fileContent, false);
    }

    public static boolean writeFile(String filePath, String fileContent, boolean append) {
        if(filePath != null && fileContent != null && filePath.length() >= 1 && fileContent.length() >= 1) {
            try {
                File ioe = new File(filePath);
                if(!ioe.exists() && !ioe.createNewFile()) {
                    return false;
                } else {
                    BufferedWriter output = new BufferedWriter(new FileWriter(ioe, append));
                    output.write(fileContent);
                    output.flush();
                    output.close();
                    return true;
                }
            } catch (IOException var5) {
                Log.e(TAG, "writeFile ioe: " + var5.toString());
                return false;
            }
        } else {
            Log.e(TAG, "Invalid param. filePath: " + filePath + ", fileContent: " + fileContent);
            return false;
        }
    }

    public static long getFileSize(String filePath) {
        if(filePath == null) {
            Log.e(TAG, "Invalid param. filePath: " + filePath);
            return 0L;
        } else {
            File file = new File(filePath);
            return file != null && file.exists()?file.length():0L;
        }
    }

    public static long getFileModifyTime(String filePath) {
        if(filePath == null) {
            Log.e(TAG, "Invalid param. filePath: " + filePath);
            return 0L;
        } else {
            File file = new File(filePath);
            return file != null && file.exists()?file.lastModified():0L;
        }
    }

    public static boolean setFileModifyTime(String filePath, long modifyTime) {
        if(filePath == null) {
            Log.e(TAG, "Invalid param. filePath: " + filePath);
            return false;
        } else {
            File file = new File(filePath);
            return file != null && file.exists()?file.setLastModified(modifyTime):false;
        }
    }

    public static boolean copyFile(ContentResolver cr, String fromPath, String destUri) {
        if(cr != null && fromPath != null && fromPath.length() >= 1 && destUri != null && destUri.length() >= 1) {
            FileInputStream is = null;
            Object os = null;

            try {
                is = new FileInputStream(fromPath);
                if(is != null) {
                    String ex = null;
                    Uri uri = null;
                    String lwUri = destUri.toLowerCase();
                    if(lwUri.startsWith("content://")) {
                        uri = Uri.parse(destUri);
                    } else if(lwUri.startsWith("file://")) {
                        uri = Uri.parse(destUri);
                        ex = uri.getPath();
                    } else {
                        ex = destUri;
                    }

                    if(ex != null) {
                        File dat = new File(ex);
                        String i = ex.substring(0, ex.lastIndexOf("/"));
                        File pf = new File(i);
                        if(pf.exists() && !pf.isDirectory()) {
                            pf.delete();
                        }

                        pf = new File(i + File.separator);
                        if(!pf.exists() && !pf.mkdirs()) {
                            Log.e(TAG, "Can\'t make dirs, path=" + i);
                        }

                        pf = new File(ex);
                        if(pf.exists()) {
                            if(pf.isDirectory()) {
                                deleteDirectory(ex);
                            } else {
                                pf.delete();
                            }
                        }

                        os = new FileOutputStream(ex);
                        dat.setLastModified(System.currentTimeMillis());
                    } else {
                        os = new ParcelFileDescriptor.AutoCloseOutputStream(cr.openFileDescriptor(uri, "w"));
                    }

                    byte[] dat1 = new byte[1024];

                    for(int i1 = is.read(dat1); -1 != i1; i1 = is.read(dat1)) {
                        ((OutputStream)os).write(dat1, 0, i1);
                    }

                    is.close();
                    is = null;
                    ((OutputStream)os).flush();
                    ((OutputStream)os).close();
                    os = null;
                    return true;
                }

                Log.e(TAG, "Failed to open inputStream: " + fromPath + "->" + destUri);
                return false;
            } catch (Exception var25) {
                Log.e(TAG, "Exception, ex: " + var25.toString());
            } finally {
                if(is != null) {
                    try {
                        is.close();
                    } catch (Exception var24) {
                        ;
                    }
                }

                if(os != null) {
                    try {
                        ((OutputStream)os).close();
                    } catch (Exception var23) {
                        ;
                    }
                }

            }

            return false;
        } else {
            Log.e(TAG, "copyFile Invalid param. cr=" + cr + ", fromPath=" + fromPath + ", destUri=" + destUri);
            return false;
        }
    }

    public static byte[] readAll(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        byte[] buf = new byte[1024];

        for(int c = is.read(buf); -1 != c; c = is.read(buf)) {
            baos.write(buf, 0, c);
        }

        baos.flush();
        baos.close();
        return baos.toByteArray();
    }

    public static byte[] readFile(Context ctx, Uri uri) {
        if(ctx != null && uri != null) {
            InputStream is = null;
            String scheme = uri.getScheme().toLowerCase();
            if(scheme.equals("file")) {
                is = readFile(uri.getPath());
            }

            try {
                is = ctx.getContentResolver().openInputStream(uri);
                if(is == null) {
                    return null;
                }

                byte[] ex = readAll(is);
                is.close();
                is = null;
                byte[] var6 = ex;
                return var6;
            } catch (FileNotFoundException var17) {
                Log.e(TAG, "FilNotFoundException, ex: " + var17.toString());
            } catch (Exception var18) {
                Log.e(TAG, "Exception, ex: " + var18.toString());
            } finally {
                if(is != null) {
                    try {
                        is.close();
                    } catch (Exception var16) {
                        ;
                    }
                }

            }

            return null;
        } else {
            Log.e(TAG, "Invalid param. ctx: " + ctx + ", uri: " + uri);
            return null;
        }
    }

    public static boolean writeFile(String filePath, byte[] content) {
        if(filePath != null && content != null) {
            FileOutputStream fos = null;

            try {
                String ex = filePath.substring(0, filePath.lastIndexOf("/"));
                File pf = null;
                pf = new File(ex);
                if(pf.exists() && !pf.isDirectory()) {
                    pf.delete();
                }

                pf = new File(filePath);
                if(pf.exists()) {
                    if(pf.isDirectory()) {
                        deleteDirectory(filePath);
                    } else {
                        pf.delete();
                    }
                }

                pf = new File(ex + File.separator);
                if(!pf.exists() && !pf.mkdirs()) {
                    Log.e(TAG, "Can\'t make dirs, path=" + ex);
                }

                fos = new FileOutputStream(filePath);
                fos.write(content);
                fos.flush();
                fos.close();
                fos = null;
                pf.setLastModified(System.currentTimeMillis());
                return true;
            } catch (Exception var13) {
                Log.e(TAG, "Exception, ex: " + var13.toString());
            } finally {
                if(fos != null) {
                    try {
                        fos.close();
                    } catch (Exception var12) {
                        ;
                    }
                }

            }

            return false;
        } else {
            Log.e(TAG, "Invalid param. filePath: " + filePath + ", content: " + content);
            return false;
        }
    }

    public static boolean readZipFile(String zipFileName, StringBuffer crc) {
        try {
            ZipInputStream ex = new ZipInputStream(new FileInputStream(zipFileName));

            ZipEntry entry;
            while((entry = ex.getNextEntry()) != null) {
                long size = entry.getSize();
                crc.append(entry.getCrc() + ", size: " + size);
            }

            ex.close();
            return true;
        } catch (Exception var6) {
            Log.e(TAG, "Exception: " + var6.toString());
            return false;
        }
    }

    public static byte[] readGZipFile(String zipFilePath) {
        if(fileIsExist(zipFilePath)) {
            Log.i(TAG, "zipFileName: " + zipFilePath);

            try {
                FileInputStream ex = new FileInputStream(zipFilePath);
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int size;
                while((size = ex.read(buffer, 0, buffer.length)) != -1) {
                    baos.write(buffer, 0, size);
                }

                return baos.toByteArray();
            } catch (Exception var5) {
                Log.i(TAG, "read zipRecorder file error");
            }
        }

        return null;
    }

    public static boolean zipFile(String baseDirName, String fileName, String targerFileName) throws IOException {
        if(baseDirName != null && !"".equals(baseDirName)) {
            File baseDir = new File(baseDirName);
            if(baseDir.exists() && baseDir.isDirectory()) {
                String baseDirPath = baseDir.getAbsolutePath();
                File targerFile = new File(targerFileName);
                ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targerFile));
                File file = new File(baseDir, fileName);
                boolean zipResult = false;
                if(file.isFile()) {
                    zipResult = fileToZip(baseDirPath, file, out);
                } else {
                    zipResult = dirToZip(baseDirPath, file, out);
                }

                out.close();
                return zipResult;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean unZipFile(String fileName, String unZipDir) throws Exception {
        File f = new File(unZipDir);
        if(!f.exists()) {
            f.mkdirs();
        }

        BufferedInputStream is = null;
        ZipFile zipfile = new ZipFile(fileName);
        Enumeration enumeration = zipfile.entries();
        byte[] data = new byte['저'];
        Log.i(TAG, "unZipDir: " + unZipDir);

        while(true) {
            while(enumeration.hasMoreElements()) {
                ZipEntry entry = (ZipEntry)enumeration.nextElement();
                if(entry.isDirectory()) {
                    File count1 = new File(unZipDir + "/" + entry.getName());
                    Log.i(TAG, "entry.isDirectory XXX " + count1.getPath());
                    if(!count1.exists()) {
                        count1.mkdirs();
                    }
                } else {
                    is = new BufferedInputStream(zipfile.getInputStream(entry));
                    String name = unZipDir + "/" + entry.getName();
                    RandomAccessFile m_randFile = null;
                    File file = new File(name);
                    if(file.exists()) {
                        file.delete();
                    }

                    file.createNewFile();
                    m_randFile = new RandomAccessFile(file, "rw");

                    int count;
                    for(int begin = 0; (count = is.read(data, 0, '저')) != -1; begin += count) {
                        try {
                            m_randFile.seek((long)begin);
                        } catch (Exception var14) {
                            Log.e(TAG, "exception, ex: " + var14.toString());
                        }

                        m_randFile.write(data, 0, count);
                    }

                    file.delete();
                    m_randFile.close();
                    is.close();
                }
            }

            return true;
        }
    }

    private static boolean fileToZip(String baseDirPath, File file, ZipOutputStream out) throws IOException {
        FileInputStream in = null;
        ZipEntry entry = null;
        byte[] buffer = new byte['저'];

        try {
            in = new FileInputStream(file);
            entry = new ZipEntry(getEntryName(baseDirPath, file));
            out.putNextEntry(entry);

            int bytes_read;
            while((bytes_read = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytes_read);
            }

            out.closeEntry();
            in.close();
            return true;
        } catch (IOException var11) {
            Log.e(TAG, "Exception, ex: " + var11.toString());
        } finally {
            if(out != null) {
                out.closeEntry();
            }

            if(in != null) {
                in.close();
            }

        }

        return false;
    }

    private static boolean dirToZip(String baseDirPath, File dir, ZipOutputStream out) throws IOException {
        if(!dir.isDirectory()) {
            return false;
        } else {
            File[] files = dir.listFiles();
            if(files.length == 0) {
                ZipEntry i = new ZipEntry(getEntryName(baseDirPath, dir));

                try {
                    out.putNextEntry(i);
                    out.closeEntry();
                } catch (IOException var6) {
                    Log.e(TAG, "Exception, ex: " + var6.toString());
                }
            }

            for(int var7 = 0; var7 < files.length; ++var7) {
                if(files[var7].isFile()) {
                    fileToZip(baseDirPath, files[var7], out);
                } else {
                    dirToZip(baseDirPath, files[var7], out);
                }
            }

            return true;
        }
    }

    private static String getEntryName(String baseDirPath, File file) {
        if(!baseDirPath.endsWith(File.separator)) {
            baseDirPath = baseDirPath + File.separator;
        }

        String filePath = file.getAbsolutePath();
        if(file.isDirectory()) {
            filePath = filePath + "/";
        }

        int index = filePath.indexOf(baseDirPath);
        return filePath.substring(index + baseDirPath.length());
    }
}
