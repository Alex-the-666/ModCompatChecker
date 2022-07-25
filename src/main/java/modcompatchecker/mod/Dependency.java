package modcompatchecker.mod;

import modcompatchecker.Lang;

public class Dependency implements Comparable<Dependency> {
    private String modId;
    private String minVersion;
    private String maxVersion;

    public Dependency(String modId, String minVersion, String maxVersion) {
        this.modId = modId;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
    }

    public String getModId() {
        return modId;
    }

    public void setModId(String modId) {
        this.modId = modId;
    }

    public String getMinVersion() {
        return minVersion;
    }

    public void setMinVersion(String minVersion) {
        this.minVersion = minVersion;
    }

    public String getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(String maxVersion) {
        this.maxVersion = maxVersion;
    }

    public String getVersionRange(boolean simplified) {
        String min = this.getMinVersion();
        String max = this.getMaxVersion();
        String below = simplified ? "<= " : Lang.VERSION_BELOW;
        String above = simplified ? ">= "  : Lang.VERSION_ABOVE;
        String through = " - ";
        if (min.equals(Lang.UNSPECIFIED) && max.equals(Lang.UNSPECIFIED)) {
            return Lang.UNSPECIFIED;
        } else if (min.equals(Lang.UNSPECIFIED) && !max.equals(Lang.UNSPECIFIED)) {
            return simplified ? below + max : Lang.VERSION + max + below;
        } else if (!min.equals(Lang.UNSPECIFIED) && max.equals(Lang.UNSPECIFIED)) {
            return simplified ? above + min : Lang.VERSION + min + above;
        } else {
            String def = min + through + max;
            return simplified ? def : Lang.VERSION + def;
        }
    }

    public boolean isCompatible(Dependency other) {
        if (this.getModId().equals(other.getModId())) {
            if (!this.getMinVersion().equals(Lang.UNSPECIFIED) && !other.getMaxVersion().equals(Lang.UNSPECIFIED)) { //we have a minimum version
                if(VersionCompare.compare(this.getMinVersion(), other.getMaxVersion()) > 0){
                    return false;
                }
            }
        }
        // build a real list of mods that actually/purposely conflict with eachother...
        return true;

    }

    @Override
    public String toString() {
        return this.getModId() + "[" + this.getVersionRange(false) + "]";
    }

    @Override
    public int compareTo(Dependency o) {
        if(LoaderType.fromModId(this.getModId()) != LoaderType.UNKNOWN){
            return -1;
        }
        if(LoaderType.fromModId(o.getModId()) != LoaderType.UNKNOWN){
            return 1;
        }
        return this.getModId().compareTo(o.getModId());
    }
}
