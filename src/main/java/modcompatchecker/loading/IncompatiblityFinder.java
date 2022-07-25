package modcompatchecker.loading;

import modcompatchecker.Lang;
import modcompatchecker.mod.Dependency;
import modcompatchecker.mod.Incompatibility;
import modcompatchecker.mod.Mod;
import modcompatchecker.mod.VersionCompare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncompatiblityFinder {


    public static List<Incompatibility> getDependencyIncompatibilities(List<Mod> modList) {
        List<Incompatibility> incompats = new ArrayList<>();
        //probably not the most effient way of doing this but you wont notice unless youre upwards of a thousand mods
        for (int i = 0; i < modList.size(); i++) {
            Mod mod1 = modList.get(i);
            for (int j = 0; j < modList.size(); j++) {
                Mod mod2 = modList.get(j);
                if (i != j) {
                    incompats.addAll(mod1.getIncompatibilitiesWith(mod2));
                }
            }
        }
        return incompats;
    }

    public static List<String> getAllModloaders(List<Mod> modList) {
        List<String> modloaders = new ArrayList<>();
        for (Mod mod : modList) {
            if (!modloaders.contains(mod.getModloader())) {
                modloaders.add(mod.getModloader());
            }
        }
        return modloaders;
    }

    public static Map<String, Dependency> getDependenciesVersionsForAllMods(List<Mod> modList) {
        Map<String, Dependency> map = new HashMap<>();
        for (Mod mod : modList) {
            for (Dependency dep : mod.getAllDependencies()) {
                if (dep != null) {
                    if (map.containsKey(dep.getModId())) {
                        Dependency prev = map.get(dep.getModId());
                        String min = prev.getMinVersion();
                        String max = prev.getMaxVersion();
                        if (!min.equals(Lang.UNSPECIFIED) && !dep.getMinVersion().equals(Lang.UNSPECIFIED)) {
                            if (VersionCompare.compare(min, dep.getMinVersion()) < 0) {
                                min = dep.getMinVersion();
                            }
                        }
                        if (!max.equals(Lang.UNSPECIFIED) && !dep.getMaxVersion().equals(Lang.UNSPECIFIED)) {
                            if (VersionCompare.compare(max, dep.getMaxVersion()) > 0) {
                                max = dep.getMaxVersion();
                            }
                        }
                        map.put(dep.getModId(), new Dependency(dep.getModId(), min, max));
                    } else {
                        map.put(dep.getModId(), new Dependency(dep.getModId(), dep.getMinVersion(), dep.getMaxVersion()));
                    }
                }
            }
        }

        return map;
    }
}
