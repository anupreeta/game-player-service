package com.comeon.assignment.player.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;

@Entity
@Table(name = "players")
public class Player {
        @Id
        private String email;

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

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getSurname() {
                return surname;
        }

        public void setSurname(String surname) {
                this.surname = surname;
        }

        public String getDob() {
                return dob;
        }

        public void setDob(String dob) {
                this.dob = dob;
        }

        public String getAddress() {
                return address;
        }

        public void setAddress(String address) {
                this.address = address;
        }

        public Long getTimeLimit() {
                return timeLimit;
        }

        public void setTimeLimit(Long timeLimit) {
                this.timeLimit = timeLimit;
        }

        public LocalDateTime getSessionStart() {
                return sessionStart;
        }

        public void setSessionStart(LocalDateTime sessionStart) {
                this.sessionStart = sessionStart;
        }

        private String password;
        private String name;
        private String surname;
        private String dob;
        private String address;
        private Long timeLimit;
        private LocalDateTime sessionStart;

        public Player(@NotEmpty String email, String password, String name, String surname, String dob, String address, Long timeLimit, LocalDateTime sessionStart) {
                this.email = email;
                this.password = password;
                this.name = name;
                this.surname = surname;
                this.dob = dob;
                this.address = address;
                this.timeLimit = timeLimit;
                this.sessionStart = sessionStart;
        }

        public Player() {

        }
}

