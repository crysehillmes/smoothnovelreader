package org.cryse.changelog;

import java.util.ArrayList;
import java.util.List;

public class Version {
    private int versionCode;
    private String versionName;
    private List<VersionDescription> versionDescriptions;
    private List<VersionChange> versionChanges;

    public Version() {
        this.versionDescriptions = new ArrayList<VersionDescription>();
        this.versionChanges = new ArrayList<VersionChange>();
    }

    public Version(List<VersionDescription> versionDescriptions, List<VersionChange> versionChanges) {
        this.versionDescriptions = versionDescriptions;
        this.versionChanges = versionChanges;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public List<VersionDescription> getVersionDescriptions() {
        return versionDescriptions;
    }

    public void setVersionDescriptions(List<VersionDescription> versionDescriptions) {
        this.versionDescriptions = versionDescriptions;
    }

    public List<VersionChange> getVersionChanges() {
        return versionChanges;
    }

    public void setVersionChanges(List<VersionChange> versionChanges) {
        this.versionChanges = versionChanges;
    }
}
