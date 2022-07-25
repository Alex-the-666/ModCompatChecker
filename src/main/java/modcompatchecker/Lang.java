package modcompatchecker;

public class Lang {
    public static final String PROGRAM_NAME = "Mod Version Compatibility Checker";
    public static final String UNKNOWN = "unknown";
    public static final String UNSPECIFIED = "unspecified";
    public static final String OPEN_FOLDER = "...";
    public static final String SELECT_MODS_FOLDER = "Select mods folder: ";
    public static final String LOADING = "Loading...";
    public static final String MODS_FOLDER_INFO = "This is your mods folder, usually within your .minecraft directory from which the game runs.";
    public static final String SELECT_A_MOD = "Double-click a mod to view its dependencies.";
    public static final String VIEW = "View";
    public static final String SHOW_MODS = "Mod list";
    public static final String MODID = "Mod ID: ";
    public static final String VERSION_DESC = ", Mod Version: ";
    public static final String MODLOADER = "Mod Loader: ";
    public static final String DEPENDENCIES = "Required Dependencies: ";
    public static final String VERSION = "version ";
    public static final String VERSION_BELOW = " and below";
    public static final String VERSION_ABOVE = " and above";
    public static final String MODLOADERS_DETECTED = " modloader(s) detected.";
    public static final String MODLOADERS_INFO = "Usually, only one modloader can be used at a time, and mods from varying modloaders are often incompatible, or simply won't be recognized by the other loader. However, some mods work for multiple loaders, and thus will appear to require more than one modloader.";
    public static final String DEPENDENCIES_DETECTED = " dependencies detected.";
    public static final String DEPENDENCY_DETECTED = " dependency detected.";
    public static final String DEPENDENCY_INFO = "Many mods require other mods in order to run. These can be library mods, content mods, and modloaders. Forge, Fabric and Quilt are all included as dependencies.";
    public static final String INCOMPATIBILITIES_DETECTED = " incompatibilities detected.";
    public static final String INCOMPATIBILITY_DETECTED = " incompatibility detected.";
    public static final String INCOMPATIBILITY_INFO = "The kind of mod incompatibility detected by this program is a conflict of dependency versions, and duplicates of mods. Other incompatibilities are not foreseen - but could definitely exist.";
    public static final String SHOWN_DEPENDENCY_INFO = "The mods (and versions of them) listed are required. Note that modloader mods that are not in use many not crash - they could also just be ignored by the modloader in use. Some of these dependencies are also internal(such as java, minecraft, etc).";

    public static final String INCOMPATIBLITY_VERSION_0 = "version mismatch for dependency ";
    public static final String INCOMPATIBLITY_VERSION_1 = " wants ";
    public static final String INCOMPATIBLITY_DUPE = "duplicate mod id ";

}
