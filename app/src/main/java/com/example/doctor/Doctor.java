package com.example.doctor;

import java.util.List;

public class Doctor {
    private int id;
    private String name;
    private String title;
    private String experience;
    private String role;
    private List<String> specialties;
    private String address;
    private int avatarResId;
    private String note;
    private String phone;
    private float rating;
    private int reviewCount;
    private String consultFee;
    private String biography;

    public Doctor(int id, String name, String title, String experience,
                  String role, List<String> specialties, String address,
                  int avatarResId, String note, String phone,
                  float rating, int reviewCount, String consultFee, String biography) {
        this.id = id; this.name = name; this.title = title;
        this.experience = experience; this.role = role;
        this.specialties = specialties; this.address = address;
        this.avatarResId = avatarResId; this.note = note;
        this.phone = phone; this.rating = rating;
        this.reviewCount = reviewCount; this.consultFee = consultFee;
        this.biography = biography;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getTitle() { return title; }
    public String getExperience() { return experience; }
    public String getRole() { return role; }
    public List<String> getSpecialties() { return specialties; }
    public String getAddress() { return address; }
    public int getAvatarResId() { return avatarResId; }
    public String getNote() { return note; }
    public String getPhone() { return phone; }
    public float getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
    public String getConsultFee() { return consultFee; }
    public String getBiography() { return biography; }
}