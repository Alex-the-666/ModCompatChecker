package modcompatchecker;

import modcompatchecker.gui.MainWindow;
import modcompatchecker.loading.ModSearcher;
import modcompatchecker.mod.Mod;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static File modsFolderLoc = getDefaultModDirectory();
    public static final int MAX_THREADS_TO_USE = 15;

    public static void main(String[] args) {
        try {
            //copy the gui of the system
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //run the gui
        SwingUtilities.invokeLater(() -> new MainWindow());
    }

    public static File getDefaultModDirectory() {
        return new File(System.getenv("APPDATA") + "\\.minecraft\\mods");
    }

    public static List<Mod> getModList() {
        long startTime = System.nanoTime();
        List<Mod> list = new ArrayList<>();
        try {
            List<Mod> found = ModSearcher.getAllMods(modsFolderLoc);
            list.addAll(found);
            //the mods found
            System.out.println(found.size() + " mods found in " + ((System.nanoTime() - startTime) / 1000000000F) + " seconds");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;

    }
}
