package PlayForward.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isVerified() {
		return verified;
	}
	
    public User() { }
    
	public User(Long id, String email, boolean verified, String password) {
		super();
		this.id = id;
		this.email = email;
		this.verified = verified;
		this.password = password;
	}
	@Id @GeneratedValue
    private Long id;
    private String email;
    private boolean verified = false;
    private String password;
	public void setVerified(boolean b) {
		verified = true;
		
	}
}
