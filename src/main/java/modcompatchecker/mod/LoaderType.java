package modcompatchecker.mod;

import java.util.Locale;

public enum LoaderType {
    FORGE("javafml"),
    FABRIC("fabricloader"),
    QUILT("quilt_loader"),
    UNKNOWN("");

    private String loaderName;

    LoaderType(String loaderName) {
        this.loaderName = loaderName;
    }

    public static LoaderType fromLoaderName(String name){
        for(LoaderType type : LoaderType.values()){
            if(name.equals(type.loaderName)){
                return type;
            }
        }
        return UNKNOWN;
    }

    public static LoaderType fromModId(String modid){
        String lower = modid.toLowerCase(Locale.ROOT);
        if(lower.equals("forge")){
            return FORGE;
        }else if(lower.equals("fabric") || lower.equals("fabricloader")){
            return FABRIC;
        }else if(lower.equals("quilt") || lower.equals("quilt_loader")){
            return QUILT;
        }
        return UNKNOWN;
    }
}
