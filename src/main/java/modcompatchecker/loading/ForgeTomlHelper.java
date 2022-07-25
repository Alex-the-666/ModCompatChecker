package modcompatchecker.loading;

import com.moandjiezana.toml.Toml;
import modcompatchecker.Lang;
import modcompatchecker.mod.Dependency;
import modcompatchecker.mod.Mod;

import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ForgeTomlHelper {

    /*
        Debug method to fancy-print out toml contents
     */
    private static void exploreToml(Toml toml, int indent) {
        String indentStr = "";
        for (int i = 0; i < indent; i++) {
            indentStr += "\t";
        }
        for (Map.Entry<String, Object> entry : toml.entrySet()) {
            if (entry.getValue() instanceof List) {
                System.out.println(indentStr + entry.getKey() + ": ");
                indent++;
                for (Object obj : (List) entry.getValue()) {
                    if (obj instanceof Toml) {
                        exploreToml((Toml) obj, indent);
                    } else {
                        System.out.println(indentStr + obj);
                    }
                }
            } else if (entry.getValue() instanceof Toml) {
                System.out.println(indentStr + entry.getKey() + ": ");
                exploreToml((Toml) entry.getValue(), indent + 1);
            } else {
                System.out.println(indentStr + entry.getKey() + ": " + entry.getValue());
            }
        }
    }


    public static Mod buildMod(InputStreamReader isr, File subFile) {
        Toml toml = new Toml().read(isr);
        String modid = toml.getString("mods[0].modId", Lang.UNKNOWN);
        String version = toml.getString("mods[0].version", Lang.UNKNOWN);
        String modloader = toml.getString("modLoader", Lang.UNKNOWN);
        String fileName = subFile.getName();
        //some forge mods read their version from the file
        if(version.equals("${file.jarVersion}")){
            version = subFile.getName().toLowerCase(Locale.ROOT).replace(modid + "-", "").replaceAll(".jar", "");
        }
        Mod mod = new Mod(modid, fileName, version, modloader);
        boolean listedForgeDependency = false;
        try {
            Toml dependencies = toml.getTable("dependencies");
            if (dependencies != null) {
                for (Map.Entry<String, Object> dependency : dependencies.entrySet()) {
                    if (dependency.getKey().equals(modid)) {
                        if (dependency.getValue() instanceof List) {
                            for (Object value : ((List) dependency.getValue())) {
                                if (value instanceof Toml) {
                                    Toml dependencyToml = (Toml) value;
                                    if (addDependency(mod, dependencyToml)) {
                                        listedForgeDependency = true;
                                    }
                                }
                            }
                        } else if (dependency.getValue() instanceof Toml) {
                            if (addDependency(mod, (Toml) dependency.getValue())) {
                                listedForgeDependency = true;
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            //happens occasionally with some poorly-written tomls
            System.err.println(subFile + " did not properly list dependencies as a table in their mod.toml file. Ignoring dependencies...");
        }
        if(!listedForgeDependency){
            //add forge dependency if unspeficied. it is a forge mod after all
            String loaderVersion = toml.getString("loaderVersion", Lang.UNSPECIFIED);
            String[] parsedVersion = parseVersion(loaderVersion);
            mod.addDependency(new Dependency("forge", parsedVersion[0], parsedVersion[1]));
        }
        return mod;
    }

    private static boolean addDependency(Mod mod, Toml dependencyToml) {
        String modid = dependencyToml.getString("modId", Lang.UNKNOWN);
        if(dependencyToml.getBoolean("mandatory")){ //only list required dependencies
            String loaderVersion = dependencyToml.getString("versionRange");
            if(loaderVersion != null){
                String[] parsedVersion = parseVersion(loaderVersion);
                Dependency dependency = new Dependency(modid, parsedVersion[0], parsedVersion[1]);
                mod.addDependency(dependency);
            }
        }
        return "forge".equals(modid);
    }

    private static String[] parseVersion(String loaderVersion){
        String minVersion = Lang.UNSPECIFIED;
        String maxVersion = Lang.UNSPECIFIED;
        String[] split = loaderVersion.split(",");
        for(String versionElement : split){
            if(versionElement.endsWith("]")){
                String newMax = versionElement.substring(0, versionElement.length() - 1);
                if(!newMax.isBlank()){
                    maxVersion = newMax;
                }
            }else if(versionElement.startsWith("[")){
                String newMin = versionElement.substring(1);
                if(!newMin.isBlank()){
                    minVersion = newMin;
                }
            }
        }
        return new String[]{minVersion, maxVersion};
    }
}
