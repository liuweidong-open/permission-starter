package org.permission.domain;

public class ResourceActionDO {

    private String resource;

    private int action;

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }
}
