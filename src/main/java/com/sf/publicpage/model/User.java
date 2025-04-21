package com.sf.publicpage.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
// import jakarta.persistence.Column;

// import java.util.Optional;

@Entity
@Table(name = "publicpage_staff")
public class User {

	@Id
    private String username;
    private String password;
    private String email;
    private String role;
    private String staffid;
    private String hopo;
    private String remark;
    private String activeFlag;
    private String lastIp;
    private String lastLogin;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStaffid() {
        return staffid;
    }

    public void setStaffid(String staffid) {
        this.staffid = staffid;
    }

    public String getHopo() {
        return hopo;
    }

    public void setHopo(String hopo) {
        this.hopo = hopo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

	public String getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}

	public String getLastIp() {
		return lastIp;
	}

	public void setLastIp(String lastIp) {
		this.lastIp = lastIp;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}
    
}
