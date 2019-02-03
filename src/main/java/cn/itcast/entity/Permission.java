package cn.itcast.entity;

import java.io.Serializable;

public class Permission implements Serializable {
	private static final long serialVersionUID = -3530078280580727663L;
	private Integer pid;
	private String permissionName;

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

}
