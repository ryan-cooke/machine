package Machine.desktop;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by Omaha on 1/1/2017.
 */
public class JarInspector {
    public static void some()throws Exception{
        List<String> classNames = new ArrayList<String>();
        ZipFile zip = new JarFile(new File("/path/to/jar/file.jar"));
        ZipEntry something = zip.getEntry("");
        zip.getInputStream(something);
//        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
//            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
//                // This ZipEntry represents a class. Now, what class does it represent?
//                String className = entry.getName().replace('/', '.'); // including ".class"
//                classNames.add(className.substring(0, className.length() - ".class".length()));
//            }
//        }


    }
}
