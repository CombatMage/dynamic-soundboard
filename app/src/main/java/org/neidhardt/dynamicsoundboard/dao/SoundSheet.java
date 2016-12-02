package org.neidhardt.dynamicsoundboard.dao;

import org.greenrobot.greendao.annotation.*;
import org.neidhardt.dynamicsoundboard.daohelper.DaoHelperKt;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

/**
 * Entity mapped to table "SOUND_SHEET".
 */
@Entity
public class SoundSheet {

    @Id
    private Long id;

    @NotNull
    @Unique
    private String fragmentTag;

    @NotNull
    private String label;
    private boolean isSelected;

    @Transient
    private boolean isSelectedForDeletion;

    @Generated(hash = 2002530563)
    public SoundSheet() {
    }

    public SoundSheet(Long id) {
        this.id = id;
    }

    @Generated(hash = 1986408884)
    public SoundSheet(Long id, @NotNull String fragmentTag, @NotNull String label,
            boolean isSelected) {
        this.id = id;
        this.fragmentTag = fragmentTag;
        this.label = label;
        this.isSelected = isSelected;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getFragmentTag() {
        return fragmentTag;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setFragmentTag(@NotNull String fragmentTag) {
        this.fragmentTag = fragmentTag;
    }

    @NotNull
    public String getLabel() {
        return label;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setLabel(@NotNull String label) {
        this.label = label;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    // KEEP METHODS - put your custom methods here

	public void insertItemInDatabaseAsync()
	{
		DaoHelperKt.insertAsync(this).subscribe();
	}

	public void updateItemInDatabaseAsync()
	{
		DaoHelperKt.updateAsync(this).subscribe();
	}

	public boolean getIsSelectedForDeletion() {
		return isSelectedForDeletion;
	}

	public void setIsSelectedForDeletion(boolean isSelectedForDeletion) {
		this.isSelectedForDeletion = isSelectedForDeletion;
	}

	@Override
	public String toString() {
		return "SoundSheet{" +
				"id=" + id +
				", fragmentTag='" + fragmentTag + '\'' +
				", label='" + label + '\'' +
				", isSelected=" + isSelected +
				", getIsSelectedForDeletion=" + isSelectedForDeletion +
				'}';
	}
    // KEEP METHODS END

}
