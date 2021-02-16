package org.permission.enums;

public enum PermissionActionEnum {

    CREATE(1), DELETE(2), UPDATE(4), QUERY(8);

    int action;

    PermissionActionEnum(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }
}
