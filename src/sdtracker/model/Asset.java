/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.model;

/**
 *
 * @author Tim Smith
 */
public class Asset {
    private int id;
    private String name;
    private String assetNumber;
    private AssetType assetType;
    private String modelNumber;
    private String serialNumber;
    private Mfg mfg;
    private AppUser assignedToAppUser;

    public Asset() {
    }
    
    public Asset(int id) {
        this.id = id;
    }

    public Asset(int id, String name, AssetType assetType) {
        this.id = id;
        this.name = name;
        this.assetType = assetType;
    }

    public Asset(int id, String name, String assetNumber, AssetType assetType, String modelNumber, 
            String serialNumber, Mfg mfg, AppUser assignedToAppUser) {
        this.id = id;
        this.name = name;
        this.assetNumber = assetNumber;
        this.assetType = assetType;
        this.modelNumber = modelNumber;
        this.serialNumber = serialNumber;
        this.mfg = mfg;
        this.assignedToAppUser = assignedToAppUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssetNumber() {
        return assetNumber;
    }

    public void setAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Mfg getMfg() {
        return mfg;
    }

    public void setMfg(Mfg mfg) {
        this.mfg = mfg;
    }

    public AppUser getAssignedToAppUser() {
        return assignedToAppUser;
    }

    public void setAssignedToAppUser(AppUser assignedToAppUser) {
        this.assignedToAppUser = assignedToAppUser;
    }
    
}
