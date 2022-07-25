package modcompatchecker.mod;

import java.util.regex.Pattern;

public class VersionCompare {

    public static int compare(String v1, String v2) {
        v1 = v1.endsWith(" - ") ? v1.replace(" - ", "") : v1;
        v2 = v2.endsWith(" - ") ? v2.replace(" - ", "") : v2;
        v1 = v1.replace(".x", "").replace(".*", "");
        v2 = v2.replace(".x", "").replace(".*", "");
        int size = Math.max(v1.length(), v2.length());
        String s1 = normalisedVersion(v1, size);
        String s2 = normalisedVersion(v2, size);
        return s1.compareTo(s2);
    }

    private static String normalisedVersion(String version, int characterLength) {
        String[] split = Pattern.compile(".", Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + characterLength + 's', s));
        }
        return sb.toString();
    }
}
