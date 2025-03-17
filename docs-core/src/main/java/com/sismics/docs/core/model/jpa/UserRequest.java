package com.sismics.docs.core.model.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "T_USER_REQUEST")
public class UserRequest {
    @Id
    @Column(name = "URQ_ID_C", length = 36)
    private String id;

    @Column(name = "URQ_NAME_C", nullable = false, length = 50)
    private String name;

    @Column(name = "URQ_EMAIL_C", nullable = false, length = 100)
    private String email;

    @Column(name = "URQ_MESSAGE_C", length = 1000)
    private String message;

    @Column(name = "URQ_PASSWORD_C", nullable = false, length = 100)
    private String password;

    @Column(name = "URQ_STATUS_C", nullable = false, length = 10)
    private String status;

    @Column(name = "URQ_CREATEDATE_D", nullable = false)
    private Date createDate;

    @Column(name = "URQ_MODIFICATIONDATE_D")
    private Date modificationDate;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Date getCreateDate() { return createDate; }
    public void setCreateDate(Date createDate) { this.createDate = createDate; }
    
    public Date getModificationDate() { return modificationDate; }
    public void setModificationDate(Date modificationDate) { this.modificationDate = modificationDate; }
}