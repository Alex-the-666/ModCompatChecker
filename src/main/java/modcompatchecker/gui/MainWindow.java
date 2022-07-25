/*
    Yes. this class is ugly as sin. But it gets the job done.
 */
package modcompatchecker.gui;

import modcompatchecker.Lang;
import modcompatchecker.Main;
import modcompatchecker.loading.IncompatiblityFinder;
import modcompatchecker.mod.Dependency;
import modcompatchecker.mod.Incompatibility;
import modcompatchecker.mod.LoaderType;
import modcompatchecker.mod.Mod;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainWindow extends JFrame {

    private static boolean isLoadingComplete = false;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    //the loading icon
    private final Icon loadingImage = new ImageIcon(getClass().getResource("/loading.gif"));
    private Font font;
    private Mod[] mods;
    private List<Incompatibility> incompats;
    private Map<String, Dependency> dependencies;
    private JPanel centerPanel = null;
    private JPanel bottomPanel = null;
    private JLabel loadingLabel = null;
    private List<Mod> modList = Main.getModList();

    public MainWindow() {
        super(Lang.PROGRAM_NAME);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon(getClass().getResource("/icon.png"));
        this.setIconImage(icon.getImage());
        Background backgroundImage = new Background(false);
        this.setContentPane(backgroundImage);
        RelativeLayout rl = new RelativeLayout(RelativeLayout.Y_AXIS);
        rl.setFill(true);
        this.getContentPane().setLayout(rl);

        loadingLabel = new JLabel(Lang.LOADING, loadingImage, 0);
        this.getContentPane().add(this.buildModFolderPanel(), 10F);
        this.buildBottomPanel();
        this.getContentPane().add(this.buildModListPanel(), 65F);
        this.getContentPane().add(bottomPanel, 25F);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
        this.pack();
        this.validate();
        //schedule the hiding of the loading icon
        scheduler.scheduleAtFixedRate(() -> loadingLabel.setVisible(!isLoadingComplete), 0, 1, TimeUnit.SECONDS);
    }

    private JPanel buildModListPanel() {
        centerPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        refreshMods();
        return centerPanel;
    }

    private JPanel buildBottomPanel() {
        bottomPanel = new JPanel(new GridLayout(3, 1));
        bottomPanel.setOpaque(false);
        return bottomPanel;
    }

    private void onUpdateModList(boolean onlyCenter) {
        centerPanel.removeAll();
        centerPanel.repaint();
        centerPanel.setLayout(new GridLayout(1, 3, 5, 5));
        centerPanel.setOpaque(false);
        JList list = new JList(mods == null ? new Mod[0] : mods);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        JScrollPane modListScroller = new JScrollPane(list);
        modListScroller.setPreferredSize(new Dimension(250, 250));

        JPanel modDescPanel = new JPanel();
        modDescPanel.setLayout(new BoxLayout(modDescPanel, BoxLayout.Y_AXIS));
        JLabel modsDescLabel = new JLabel(Lang.SELECT_A_MOD);
        setFontOf(modsDescLabel, Font.PLAIN, 12f);
        modDescPanel.add(modsDescLabel);

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                JList theList = (JList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        Object clicked = theList.getModel().getElementAt(index);
                        if (clicked instanceof Mod) {
                            Mod mod = (Mod) clicked;
                            modDescPanel.removeAll();
                            modDescPanel.repaint();
                            JLabel fileName = new JLabel(mod.getFileName());
                            setFontOf(fileName, Font.BOLD, 15f);
                            modDescPanel.add(fileName);
                            JLabel modid = new JLabel(Lang.MODID + mod.getModid() + Lang.VERSION_DESC + mod.getVersion());
                            setFontOf(modid, Font.PLAIN, 12f);
                            modDescPanel.add(modid);
                            LoaderType loaderType = LoaderType.fromLoaderName(mod.getModloader());
                            JLabel modloader = new JLabel(Lang.MODLOADER + loaderType.toString().toLowerCase(Locale.ROOT) + " (" + mod.getModloader() + ")");
                            setFontOf(modloader, Font.PLAIN, 12f);
                            modDescPanel.add(modloader);
                            if (!mod.getAllDependencies().isEmpty()) {
                                JLabel dependenciesDesc = new JLabel(Lang.DEPENDENCIES);
                                setFontOf(dependenciesDesc, Font.BOLD, 14f);
                                modDescPanel.add(dependenciesDesc);
                                for (Dependency dependency : mod.getAllDependencies()) {
                                    JLabel dependencyDesc = new JLabel("\u2022 " + dependency.getModId() + " [" + dependency.getVersionRange(false) + "]");
                                    setFontOf(dependencyDesc, Font.PLAIN, 12f);
                                    modDescPanel.add(dependencyDesc);
                                }
                            }
                            modDescPanel.validate();
                        }
                    }
                }
            }
        });

        centerPanel.add(modListScroller);
        centerPanel.add(modDescPanel);
        centerPanel.validate();
        if (!onlyCenter) {
            bottomPanel.removeAll();
            JPanel modloadersPanel = new JPanel();
            modloadersPanel.setOpaque(false);
            JPanel dependenciesPanel = new JPanel();
            dependenciesPanel.setOpaque(false);
            JPanel incompatibilityPanel = new JPanel();
            incompatibilityPanel.setOpaque(false);
            if (modList != null) {
                List<String> allModloaders = IncompatiblityFinder.getAllModloaders(modList);
                JLabel modloaderCountLabel = new JLabel(allModloaders.size() + Lang.MODLOADERS_DETECTED);
                String tooltip = "";
                for (String loader : allModloaders) {
                    tooltip += loader + " ";
                }
                modloaderCountLabel.setToolTipText(tooltip);
                setFontOf(modloaderCountLabel, Font.BOLD, 15f);
                modloadersPanel.add(modloaderCountLabel);
                modloadersPanel.add(createInfoTooltip(Lang.MODLOADERS_INFO));

                dependencies = IncompatiblityFinder.getDependenciesVersionsForAllMods(modList);
                JLabel dependencyCountLabel = new JLabel(dependencies.size() + (dependencies.size() == 1 ? Lang.DEPENDENCY_DETECTED : Lang.DEPENDENCIES_DETECTED));
                setFontOf(dependencyCountLabel, Font.BOLD, 15f);
                dependenciesPanel.add(dependencyCountLabel);
                JButton viewDependencies = new JButton(Lang.VIEW);
                JButton viewIncompats = new JButton(Lang.VIEW);
                if (!dependencies.isEmpty()) {
                    viewDependencies.addActionListener(e -> onViewDependencies(viewDependencies, viewIncompats));
                    dependenciesPanel.add(viewDependencies);
                }
                dependenciesPanel.add(createInfoTooltip(Lang.DEPENDENCY_INFO));

                incompats = IncompatiblityFinder.getDependencyIncompatibilities(modList);
                JLabel incompatCountLabel = new JLabel(incompats.size() + (incompats.size() == 1 ? Lang.INCOMPATIBILITY_DETECTED : Lang.INCOMPATIBILITIES_DETECTED));
                setFontOf(incompatCountLabel, Font.BOLD, 15f);
                incompatibilityPanel.add(incompatCountLabel);
                if (!incompats.isEmpty()) {
                    viewIncompats.addActionListener(e -> onViewIncompatibilities(viewDependencies, viewIncompats));
                    incompatibilityPanel.add(viewIncompats);
                }
                incompatibilityPanel.add(createInfoTooltip(Lang.INCOMPATIBILITY_INFO));

            }
            bottomPanel.add(modloadersPanel);
            bottomPanel.add(dependenciesPanel);
            bottomPanel.add(incompatibilityPanel);
            bottomPanel.validate();
        }
    }

    private void onViewIncompatibilities(JButton viewDeps, JButton viewIncs) {
        viewDeps.setEnabled(false);
        viewIncs.setEnabled(false);
        RelativeLayout rl = new RelativeLayout(RelativeLayout.X_AXIS);
        rl.setFill(true);
        centerPanel.setLayout(rl);
        centerPanel.removeAll();
        centerPanel.add(createInfoTooltip(Lang.INCOMPATIBILITY_INFO), 5F);

        JList list = new JList(incompats.toArray(new Incompatibility[0]));
        list.setSelectionModel(new NoSelectionModel());
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        JScrollPane modListScroller = new JScrollPane(list);
        modListScroller.setPreferredSize(new Dimension(250, 210));
        centerPanel.add(modListScroller, 80F);
        JButton showModlistButton = new JButton(Lang.SHOW_MODS);
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        showModlistButton.addActionListener(e -> {
            onUpdateModList(true);
            viewDeps.setEnabled(true);
            viewIncs.setEnabled(true);
        });
        buttonPanel.add(showModlistButton);
        centerPanel.add(buttonPanel, 15F);
        centerPanel.repaint();
        centerPanel.validate();
    }

    private void onViewDependencies(JButton viewDeps, JButton viewIncs) {
        viewDeps.setEnabled(false);
        viewIncs.setEnabled(false);
        RelativeLayout rl = new RelativeLayout(RelativeLayout.X_AXIS);
        rl.setFill(true);
        centerPanel.setLayout(rl);
        centerPanel.removeAll();
        List<Dependency> dependencyCollection = new ArrayList<>(dependencies.values());
        Collections.sort(dependencyCollection);
        JList list = new JList(dependencyCollection.toArray(new Dependency[0]));
        list.setSelectionModel(new NoSelectionModel());
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        JScrollPane modListScroller = new JScrollPane(list);
        modListScroller.setPreferredSize(new Dimension(300, 210));
        centerPanel.add(createInfoTooltip(Lang.SHOWN_DEPENDENCY_INFO), 5F);
        centerPanel.add(modListScroller, 80F);
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        JButton showModlistButton = new JButton(Lang.SHOW_MODS);
        showModlistButton.addActionListener(e -> {
            onUpdateModList(true);
            viewDeps.setEnabled(true);
            viewIncs.setEnabled(true);
        });
        buttonPanel.add(showModlistButton);
        centerPanel.add(buttonPanel, 15F);
        centerPanel.repaint();
        centerPanel.validate();
    }


    private JPanel buildModFolderPanel() {
        JPanel modsFolderPanel = new JPanel();
        modsFolderPanel.setOpaque(false);
        JLabel modsFolderLabel = new JLabel(Lang.SELECT_MODS_FOLDER);
        setFontOf(modsFolderLabel, Font.PLAIN, 15f);
        JButton selectFolderButton = new JButton(Lang.OPEN_FOLDER);

        JTextField modsFolderField = new JTextField();
        modsFolderField.setToolTipText(Main.modsFolderLoc.toString());
        modsFolderField.setText(Main.modsFolderLoc.toString());
        modsFolderField.setColumns(30);

        modsFolderField.addActionListener(e -> {
            isLoadingComplete = false;
            loadingLabel.setVisible(true);
            loadingLabel.repaint();
            SwingUtilities.invokeLater(() -> {
                File test = new File(modsFolderField.getText());
                Main.modsFolderLoc = test.exists() && test.isDirectory() ? test : Main.getDefaultModDirectory();
                refreshMods();
            });
        });
        selectFolderButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            File test = new File(modsFolderField.getText());
            fileChooser.setCurrentDirectory(test.isDirectory() ? test : Main.getDefaultModDirectory());
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int ret = fileChooser.showDialog(null, "Open mods folder");

            if (ret == JFileChooser.APPROVE_OPTION) {
                isLoadingComplete = false;
                loadingLabel.setVisible(true);
                loadingLabel.repaint();
                SwingUtilities.invokeLater(() -> {
                    File file = fileChooser.getSelectedFile();
                    modsFolderField.setText(file.toString());
                    Main.modsFolderLoc = file;
                    refreshMods();
                });
            }
        });

        modsFolderPanel.add(modsFolderLabel);
        modsFolderPanel.add(modsFolderField);
        modsFolderPanel.add(selectFolderButton);
        modsFolderPanel.add(createInfoTooltip(Lang.MODS_FOLDER_INFO));
        modsFolderPanel.add(loadingLabel);
        return modsFolderPanel;
    }

    //create a tooltip with the following text
    private JLabel createInfoTooltip(String tooltip) {
        ImageIcon icon = new ImageIcon(getClass().getResource("/infobox.png"));
        JLabel infobox = new JLabel(icon);
        infobox.setToolTipText("<html><p width=\"400\">" + tooltip + "</p></html>");
        return infobox;
    }

    //set the font
    private void setFontOf(JComponent label, int style, float size) {
        if (font == null) {
            try {
                for (Font listed : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
                    System.out.println(font.getFontName());
                    if (font.getFontName().equals("Bahnschrift")) {
                        font = listed;
                        break;
                    }
                }
            } catch (Exception e) {
                font = label.getFont();
            }
        }
        if (font != null) {
            label.setFont(font.deriveFont(style, size));
        }
    }

    //recompiles the modlist. overall, the process can take multiple seconds.
    private void refreshMods() {
        modList = Main.getModList();
        if (modList == null) {
            mods = null;
        } else if (modList.isEmpty()) {
            mods = new Mod[0];
        } else {
            mods = modList.toArray(new Mod[0]);
        }
        long startTime = System.nanoTime();
        if (centerPanel != null) {
            onUpdateModList(false);
        }
        System.out.println("refreshed display in " + ((System.nanoTime() - startTime) / 1000000000F) + " seconds");
        isLoadingComplete = true;
    }
}
