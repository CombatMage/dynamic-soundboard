package org.neidhardt.dynamicsoundboard.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import org.neidhardt.dynamicsoundboard.dao.daohelper.DaoHelperKt;
import org.neidhardt.dynamicsoundboard.soundlayoutmanagement.model.SoundLayoutsManager;
// KEEP INCLUDES END
/**
 * Entity mapped to table "SOUND_LAYOUT".
 */
public class SoundLayout {

    private Long id;
    /** Not-null value. */
    private String label;
    /** Not-null value. */
    private String databaseId;
    private boolean isSelected;

    // KEEP FIELDS - put your custom fields here
    private boolean isSelectedForDeletion = false;
    // KEEP FIELDS END

    public SoundLayout() {
    }

    public SoundLayout(Long id) {
        this.id = id;
    }

    public SoundLayout(Long id, String label, String databaseId, boolean isSelected) {
        this.id = id;
        this.label = label;
        this.databaseId = databaseId;
        this.isSelected = isSelected;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getLabel() {
        return label;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setLabel(String label) {
        this.label = label;
    }

    /** Not-null value. */
    public String getDatabaseId() {
        return databaseId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    // KEEP METHODS - put your custom methods here
    public boolean isDefaultLayout()
    {
        return this.databaseId.equals(SoundLayoutsManager.DB_DEFAULT);
    }

    public boolean isSelectedForDeletion() {
        return isSelectedForDeletion;
    }

    public void setIsSelectedForDeletion(boolean isSelectedForDeletion) {
        this.isSelectedForDeletion = isSelectedForDeletion;
    }

	public void updateItemInDatabaseAsync()
	{
		DaoHelperKt.updateDatabaseAsync(this);
	}
    // KEEP METHODS END

}
