package modcompatchecker.mod;

import modcompatchecker.Lang;

import java.util.ArrayList;
import java.util.List;

public class Mod {

    private String modid;
    private String fileName;
    private String version;
    private String modloader;
    private List<Dependency> dependencies;

    public Mod(String modid, String fileName, String version, String modloader) {
        this.modid = modid;
        this.fileName = fileName;
        this.version = version;
        this.modloader = modloader;
        this.dependencies = new ArrayList<>();
    }

    public String getModid() {
        return modid;
    }

    public void setModid(String modid) {
        this.modid = modid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getModloader() {
        return modloader;
    }

    public void setModloader(String modloader) {
        this.modloader = modloader;
    }

    public void addDependency(Dependency dependency) {
        dependencies.add(dependency);
    }

    public Dependency getDependency(String name){
        for(Dependency dependency : dependencies){
            if(dependency.getModId().equals(name)){
                return dependency;
            }
        }
        return null;
    }

    public List<Dependency> getAllDependencies(){
        return dependencies;
    }

    @Override
    public String toString() {
        return fileName;
    }

    public String getFullDesc(){
        String dependsOn = "";
        for(Dependency dependency : dependencies){
            dependsOn += dependency.toString();
        }
        return fileName + " | " + modloader + " | REQUIRES (" + dependsOn + ")";
    }

    public List<Incompatibility> getIncompatibilitiesWith(Mod mod) {
        List<Incompatibility> incompats = new ArrayList<>();
        for(Dependency dependency : this.getAllDependencies()){
            for(Dependency otherDep : mod.getAllDependencies()) {
                if (!dependency.isCompatible(otherDep)){
                    incompats.add(new Incompatibility(mod, this, Lang.INCOMPATIBLITY_VERSION_0 + otherDep.getModId() + " | " + this.getModid() + Lang.INCOMPATIBLITY_VERSION_1 + dependency.getVersionRange(false) + ", " + mod.getModid() + Lang.INCOMPATIBLITY_VERSION_1 + otherDep.getVersionRange(false)));
                }
            }
        }
        if (mod.getModid().equals(this.getModid())) {
            incompats.add(new Incompatibility(mod, this, Lang.INCOMPATIBLITY_DUPE));
        }
        return incompats;
    }

    public String getSelectedDesc() {
        return fileName + "\n" + getModid() + "-" + getVersion();
    }
}
