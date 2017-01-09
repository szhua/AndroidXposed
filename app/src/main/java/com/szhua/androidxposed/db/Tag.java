package com.szhua.androidxposed.db;

/**
 * AndroidXposed
 * Create   2017/1/6 17:45;
 * https://github.com/szhua
 *
 * @author sz.hua
 */
public class Tag {
    int id ;
    String tag ;
    int is_show ;

    public Tag(int id ,String tag, int is_show) {
        this.id =id ;
        this.tag = tag;
        this.is_show = is_show;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getIs_show() {
        return is_show;
    }

    public void setIs_show(int is_show) {
        this.is_show = is_show;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", tag='" + tag + '\'' +
                ", is_show=" + is_show +
                '}';
    }
}
