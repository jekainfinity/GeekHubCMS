DROP TABLE IF EXISTS USER_ROLES;
DROP TABLE If EXISTS ROLES;
ALTER TABLE USERS ADD COLUMN USER_ROLE varchar(15) NOT NULL DEFAULT 'ROLE_STUDENT';
ALTER TABLE COURSES ADD COLUMN COURSE_DESCRIPTION blob NOT NULL;
