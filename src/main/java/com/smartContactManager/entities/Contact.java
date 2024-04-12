package com.smartContactManager.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;
	@NotBlank(message = "required")
	@Size(message = "characters between 2 to 10",min = 2,max = 10)
	private String firstName;
	@NotBlank(message = "required")
	@Size(message = "characters between 2 to 10",min = 2,max = 10)
	private String secondName;
	@NotBlank(message = "required")
	private String email;
	@NotBlank(message = "required")
	@Pattern(regexp = "[0-9]{10}",message = "10 characters required")
	private String phone;
	@NotBlank(message = "required")
	private String work;

	private String image;
	@Column(length = 500)
	@NotBlank(message = "required")
	private String description;

	@ManyToOne
	private User user;

	public int getcId() {
		return cId;
	}

	public void setcId(int cId) {
		this.cId = cId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Contact [cId=" + cId + ", firstName=" + firstName + ", secondName=" + secondName + ", work=" + work
				+ ", email=" + email + ", phone=" + phone + ", image=" + image + ", description=" + description
				+ ", user=" + user + "]";
	}

//	@Override
//	public boolean equals(Object obj) {
//		// TODO Auto-generated method stub
//		return this.cId==((Contact)obj).getcId();
//	}


}
