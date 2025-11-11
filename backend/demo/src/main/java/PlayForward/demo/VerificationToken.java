package PlayForward.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class VerificationToken {
    @Id @GeneratedValue
    private Long id;
    private String token;

    @OneToOne
    private User user;

	public User getUser() {
		// TODO Auto-generated method stub
		return user;
	}

	public void setToken(String token2) {
		token=token2;
		
	}

	public void setUser(User user2) {
		user=user2;
		
	}
}
