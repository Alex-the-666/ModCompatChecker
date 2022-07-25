package modcompatchecker.loading;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import modcompatchecker.Lang;
import modcompatchecker.mod.Dependency;
import modcompatchecker.mod.Mod;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

public class FabricJsonHelper {

    public static Mod buildFabricMod(InputStreamReader isr, File subFile) {
        JsonParser jsonParser = new JsonParser();
        JsonObject object = jsonParser.parse(isr).getAsJsonObject();
        String modid = object.get("id").getAsString();
        String version = object.get("version").getAsString();
        //implied by fabric.mod.json
        String modLoader = "fabricloader";
        JsonObject dependencies = object.getAsJsonObject("depends");
        String fileName = subFile.getName();
        Mod mod = new Mod(modid, fileName, version, modLoader);
        boolean listedFabricDependency = false;

        for(Map.Entry<String, JsonElement> dependency : dependencies.entrySet()){
            String[] parsedVersion = parseVersion(dependency.getValue().getAsString());
            mod.addDependency(new Dependency(dependency.getKey(), parsedVersion[0], parsedVersion[1]));
            if(dependency.getKey().equals("fabric")){
                listedFabricDependency = true;
            }
        }
        if(!listedFabricDependency){
            mod.addDependency(new Dependency("fabric", Lang.UNSPECIFIED, Lang.UNSPECIFIED));
        }
        return mod;
    }

    public static Mod buildQuiltMod(InputStreamReader isr, File subFile) {
        JsonParser jsonParser = new JsonParser();
        JsonObject object = jsonParser.parse(isr).getAsJsonObject().getAsJsonObject("quilt_loader");
        String modid = object.get("id").getAsString();
        String version = object.get("version").getAsString();
        //implied by quilt json
        String modLoader = "quilt_loader";
        JsonArray dependencies = object.getAsJsonArray("depends");
        String fileName = subFile.getName();
        Mod mod = new Mod(modid, fileName, version, modLoader);
        boolean listedQuiltDependency = false;

        for(JsonElement dependency : dependencies){
            String dependencyId = dependency.getAsJsonObject().get("id").getAsString();
            String dependencyVersion = dependency.getAsJsonObject().get("versions").getAsString();
            String[] parsedVersion = parseVersion(dependencyVersion);
            mod.addDependency(new Dependency(dependencyId, parsedVersion[0], parsedVersion[1]));
            if(dependencyId.equals("quilt_loader") || dependencyId.equals("quilted_fabric_api")){
                listedQuiltDependency = true;
            }
        }
        if(!listedQuiltDependency){
            mod.addDependency(new Dependency("quilt_loader", Lang.UNSPECIFIED, Lang.UNSPECIFIED));
        }
        return mod;
    }


    private static String[] parseVersion(String loaderVersion) {
        String min = Lang.UNSPECIFIED;
        String max = Lang.UNSPECIFIED;
        if(loaderVersion.startsWith("^") || loaderVersion.startsWith("~")){
            min = loaderVersion.substring(1);
        }else if(loaderVersion.startsWith("\u003e\u003d")){
            min = loaderVersion.substring(2);
        }else if(loaderVersion.startsWith("\u003c\u003d")){
            max = loaderVersion.substring(2);
        }else if(!loaderVersion.equals("*")){
            min = loaderVersion;
        }
        return new String[]{min, max};
    }
}
