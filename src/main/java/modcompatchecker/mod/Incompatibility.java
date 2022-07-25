package modcompatchecker.mod;

public class Incompatibility {

    private Mod mod1;
    private Mod mod2;
    private String issue;

    public Incompatibility(Mod mod1, Mod mod2, String issue) {
        this.mod1 = mod1;
        this.mod2 = mod2;
        this.issue = issue;
    }

    public Mod getMod1() {
        return mod1;
    }

    public void setMod1(Mod mod1) {
        this.mod1 = mod1;
    }

    public Mod getMod2() {
        return mod2;
    }

    public void setMod2(Mod mod2) {
        this.mod2 = mod2;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    @Override
    public String toString(){
        return mod1.getFileName() + " conflicts with " + mod2.getFileName() + ": " + issue;
    }
}
