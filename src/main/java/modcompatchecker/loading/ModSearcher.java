package modcompatchecker.loading;

import modcompatchecker.Main;
import modcompatchecker.mod.Mod;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public class ModSearcher {

    public static List<Mod> getAllMods(File dirFile) throws IOException {
        List<Mod> mods = Collections.synchronizedList(new ArrayList<Mod>());
        ExecutorService es = Executors.newFixedThreadPool(Main.MAX_THREADS_TO_USE);
        List<Callable<Object>> todo = new ArrayList<Callable<Object>>();
        if (dirFile.exists()) {
            //marginally faster than File.listFiles
            dirFile.list((dir, name) -> {
                File subFile = new File(dir, name);
                if (subFile.exists() && name.endsWith(".jar")) {
                    IndexModFile task = new IndexModFile(subFile, mods);
                    todo.add(Executors.callable(task)); //add task to executor
                }
                return false;
            });
        }
        try {
            //invoke and wait for all jar scans
            es.invokeAll(todo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mods;
    }

    private static class IndexModFile implements Runnable {

        private final File subFile;
        private final List<Mod> mods;

        public IndexModFile(File subFile, List<Mod> mods) {
            this.subFile = subFile;
            this.mods = mods;
        }

        private static void processFabricOrQuiltJson(List<Mod> mods, InputStream json, File subFile, boolean quilt) throws IOException {
            InputStreamReader isr = new InputStreamReader(json);
            Mod mod = quilt ? FabricJsonHelper.buildQuiltMod(isr, subFile) : FabricJsonHelper.buildFabricMod(isr, subFile);
            if (mod != null) {
                mods.add(mod);
            }
        }

        private static void processForgeToml(List<Mod> mods, InputStream tomlStream, File subFile) throws IOException {
            InputStreamReader isr = new InputStreamReader(tomlStream);
            Mod mod = ForgeTomlHelper.buildMod(isr, subFile);
            if (mod != null) {
                mods.add(mod);
            }
        }

        @Override
        public void run() {
            JarInputStream jar = null;
            try {
                jar = new JarInputStream(new FileInputStream(subFile));

                JarFile jarFile = new JarFile(subFile);
                JarEntry jarEntry = jar.getNextJarEntry();
                boolean isMod = false;
                while (jarEntry != null) {
                    if (!jarEntry.isDirectory()) {
                        String str = jarEntry.getName();
                        //quilt_loader mod's manifest
                        if (str.startsWith("quilt.mod.json")) {
                            isMod = true;
                            try {
                                InputStream json = jarFile.getInputStream(jarEntry);
                                processFabricOrQuiltJson(mods, json, subFile, true);
                                break;
                            } catch (Exception e) {
                                System.err.println("error indexing mod " + subFile.getName());
                                e.printStackTrace();
                            }
                        }
                        //fabric mod's manifest
                        if (str.startsWith("fabric.mod.json")) {
                            isMod = true;
                            try {
                                InputStream json = jarFile.getInputStream(jarEntry);
                                processFabricOrQuiltJson(mods, json, subFile, false);
                                break;
                            } catch (Exception e) {
                                System.err.println("error indexing mod " + subFile.getName());
                                e.printStackTrace();
                            }
                        }
                        //forge mod's manifest
                        if (str.startsWith("META-INF/mods.toml")) {
                            isMod = true;
                            try {
                                InputStream toml = jarFile.getInputStream(jarEntry);
                                processForgeToml(mods, toml, subFile);
                                break;
                            } catch (Exception e) {
                                System.err.println("error indexing mod " + subFile.getName());
                                e.printStackTrace();
                            }
                        }
                    }
                    jarEntry = jar.getNextJarEntry();
                }
                jar.close();
                if (!isMod) {
                    System.out.println("Non-mod jar file detected: " + subFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}