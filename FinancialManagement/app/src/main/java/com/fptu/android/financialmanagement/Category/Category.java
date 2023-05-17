package com.fptu.android.financialmanagement.Category;

public class Category {

    private String UserID;
    private String CategoryID;
    private String CategoryName;
    private String ParentID;
    private String Level;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getParentID() {
        return ParentID;
    }

    public void setParentID(String parentID) {
        ParentID = parentID;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public Category() {
    }

    public Category(String userID, String categoryID, String categoryName, String parentID, String level) {
        UserID = userID;
        CategoryID = categoryID;
        CategoryName = categoryName;
        ParentID = parentID;
        Level = level;
    }

    @Override
    public String toString() {
        return "Category{" +
                "UserID='" + UserID + '\'' +
                ", CategoryID='" + CategoryID + '\'' +
                ", CategoryName='" + CategoryName + '\'' +
                ", ParentID='" + ParentID + '\'' +
                ", Level='" + Level + '\'' +
                '}';
    }
}
