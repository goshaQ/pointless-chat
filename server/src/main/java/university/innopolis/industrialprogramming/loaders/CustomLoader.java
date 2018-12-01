package university.innopolis.industrialprogramming.loaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import university.innopolis.industrialprogramming.Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CustomLoader extends ClassLoader {
    private String libPath;

    public CustomLoader(String libPath) {
        this.libPath = libPath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            JarFile jarLib = null;
            JarEntry jarEntry = null;
            String className = name.replace(".", "/") + ".class";

            File dir = new File(libPath);
            String[] files = dir.list();

            if (files != null) {
                for (String filePath : files) {
                    if (filePath.endsWith(".jar")) {
                        jarLib = new JarFile(new File(libPath, filePath));
                        jarEntry = jarLib.getJarEntry(className);

                        if (jarEntry != null) break;
                    }
                }
            }

            if (jarEntry != null) {
                InputStream inputStream = jarLib.getInputStream(jarEntry);

                byte[] classBytes = new byte[(int) jarEntry.getSize()];
                if (inputStream.read(classBytes) != classBytes.length) {
                    throw new IOException();
                }

                return defineClass(name, classBytes, 0, classBytes.length);
            } else {
                throw new IOException();
            }
        } catch (FileNotFoundException e) {
            System.out.printf("Jar File %s is not found\n", libPath);
            throw new ClassNotFoundException(e.getMessage(), e);
        } catch (IOException e) {
            System.out.printf("An error occurred while reading the lib path %s\n", libPath);
            throw new ClassNotFoundException(e.getMessage(), e);
        }
    }
}
