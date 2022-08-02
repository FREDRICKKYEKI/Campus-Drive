package com.isaaco.campusdrive;

public class CampDriveUsers {

    // variables for storing our data.
        private String UserId, Name, Course, School, Email, Url, FolderCode;

        public CampDriveUsers() {
            // empty constructor
            // required for Firebase.
        }

        // Constructor for all variables.
        public CampDriveUsers(String UserId, String Name, String Course, String School, String Email, String Url, String FolderCode) {
            this.UserId = UserId;
            this.Name = Name;
            this.Course = Course;
            this.School = School;
            this.Email= Email;
            this.Url=Url;
            this.FolderCode = FolderCode;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }


        public String getname() {
            return Name;
        }

        public void setname(String Name) {
            this.Name = Name;
        }


        public String getCourse() {
            return Course;
        }

        public void setCourse(String Course) {
            this.Course = Course;
        }


        public String getSchool() {
            return School;
        }

        public void setSchool(String School) {
            this.Course = School;
        }


        public String getEmail() {
            return Email;
        }

        public void setEmail(String Email) {
                this.Email= Email;
        }


        public String getUrl() {
            return Url;
        }

        public void setUrl(String Url) {
            this.Url= Url;
        }


        public String getFolderCode(){return FolderCode;}

        public void  setFolderCode (String FolderCode) {this.FolderCode = FolderCode;}
}
