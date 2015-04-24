
package com.galaxy.util.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.security.MessageDigest;

import com.galaxy.ishare.IShareContext;

public class FileUtil {
    public static boolean CreateParentFolder(File file) {
        File parent = file.getParentFile();
        if (parent.exists()) {
            return true;
        } else {
            return parent.mkdirs();
        }
    }

    public static String getTempFilePath(URI uri) {
        return IShareContext.mContext.getFilesDir() + "/temp/" + StringUtil.toMd5(uri.toASCIIString());
    }

    public static String getImageFilePath(URI uri) {
        return IShareContext.mContext.getFilesDir() + "/img/" + StringUtil.toMd5(uri.toASCIIString());
    }

    public static synchronized File makeDIRAndCreateFile(String filePath) throws Exception {
        File file = new File(filePath);
        String parent = file.getParent();
        File parentFile = new File(parent);
        if (!parentFile.exists()) {
            if (parentFile.mkdirs()) {
                file.createNewFile();
            } else {
                throw new IOException("创建文件失败！");
            }
        } else {
            if (!file.exists()) {
                file.createNewFile();
            }
        }
        return file;
    }

    public static synchronized boolean makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    public static synchronized File findFile(String dirName, String fileName) {
        if (dirName == null || fileName == null || dirName.equals("") || fileName.equals("")) {
            return null;
        }
        File dir = new File(dirName);
        if (!dir.exists()) {
            return null;
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }
        for (File file : files) {
            if (fileName.equals(file.getName())) {
                return file;
            }
        }
        return null;
    }

    public static synchronized boolean copyFile(String oldPath, String newPath) {
        InputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                inStream = new FileInputStream(oldPath);
                outStream = new FileOutputStream(newPath);
                byte[] buffer = new byte[4096];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    System.out.println(bytesum);
                    outStream.write(buffer, 0, byteread);
                }

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (Exception e2) {
                }
            }
            if (outStream != null) {
                try {
                    inStream.close();
                } catch (Exception e2) {
                }
            }
        }

        return false;
    }

//   public void copyFolder(String oldPath, String newPath) {   
//  
//       try {   
//           (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹   
//           File a=new File(oldPath);   
//           String[] file=a.list();   
//           File temp=null;   
//           for (int i = 0; i < file.length; i++) {   
//               if(oldPath.endsWith(File.separator)){   
//                   temp=new File(oldPath+file[i]);   
//               }   
//               else{   
//                   temp=new File(oldPath+File.separator+file[i]);   
//               }   
//  
//               if(temp.isFile()){   
//                   FileInputStream input = new FileInputStream(temp);   
//                   FileOutputStream output = new FileOutputStream(newPath + "/" +   
//                           (temp.getName()).toString());   
//                   byte[] b = new byte[1024 * 5];   
//                   int len;   
//                   while ( (len = input.read(b)) != -1) {   
//                       output.write(b, 0, len);   
//                   }   
//                   output.flush();   
//                   output.close();   
//                   input.close();   
//               }   
//               if(temp.isDirectory()){//如果是子文件夹   
//                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);   
//               }   
//           }   
//       }   
//       catch (Exception e) {   
//           System.out.println("复制整个文件夹内容操作出错");   
//           e.printStackTrace();   
//  
//       }   
//  
//   }

    public static boolean isSDAvailable() {
        File f = new File("sdcard");
        return f.exists();
    }

    public static <T> T loadObjFromFile(String filePath) {
        T sDownload = null;
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        ObjectInputStream ois = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            sDownload = (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e2) {
            }
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (Exception e3) {
            }
        }

        return sDownload;

    }

    public static <T> boolean saveObj2File(T obj, String filePath) {
        File file = new File(filePath);
        FileUtil.CreateParentFolder(file);
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e2) {
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e3) {
            }
        }

        return true;
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return StringUtil.toHexString(digest.digest(), "");
    }
}
