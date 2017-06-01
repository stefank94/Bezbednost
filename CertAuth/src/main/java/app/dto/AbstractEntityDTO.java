package app.dto;

public abstract class AbstractEntityDTO {

    protected int id;
    protected boolean deleted;

    public AbstractEntityDTO() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

}
