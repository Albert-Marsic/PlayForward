package PlayForward.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    private String email;
    private boolean verified = false;
	public void setVerified(boolean b) {
		verified = true;
		
	}
}