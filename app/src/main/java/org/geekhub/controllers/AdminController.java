package org.geekhub.controllers;

import org.geekhub.hibernate.entity.Course;
import org.geekhub.hibernate.entity.Role;
import org.geekhub.hibernate.entity.User;
import org.geekhub.util.CommonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.*;


@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "adminpanel/index";
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String users(ModelMap model) {
        List<User> users = new ArrayList<>();
        User u = new User();
        u.setBirthDay(new Date());
        u.setId(1);
        u.setFirstName("Test1");
        u.setEmail("Ivan@mail.ru");
        u.setIcq("4118377166");
        u.setLastName("Test");
        u.setPatronymic("Test");
        u.setLogin("Ivan123");
        u.setPassword("1234512");
        u.setRegistrationDate(new Date());
        u.setPhoneNumber("+380(93)145-1514");
        for (int i = 0; i < 5; i++) users.add(u);
        model.addAttribute("users",users);
        return "adminpanel/users";
    }


    @RequestMapping(value = "/users/{userId}/edit", method = RequestMethod.GET)
    public String getEditUserPage(@PathVariable("userId")Integer userId, ModelMap model) throws Exception {
        try {


            Course cour = new Course();
            cour.setId(1);
            cour.setName("PHP");

            Course c2 = new Course();
            c2.setId(2);
            c2.setName("Java Script");

            Set<Course> courses = new HashSet<>();
            courses.add(c2);
            courses.add(cour);

            User u = new User();

            u.setBirthDay(new Date());
            u.setId(userId);
            u.setFirstName("Test1");
            u.setEmail("Ivan@mail.ru");
            u.setIcq("4118377166");
            u.setLastName("Test");
            u.setPatronymic("Test");
            u.setLogin("Ivan123");
            u.setPassword("1234512");
            u.setRegistrationDate(new Date());
            u.setCourses(courses);
            u.setPhoneNumber("931451514");

            model.addAttribute("courseList", courses);
            model.addAttribute("user", u);
            return "adminpanel/user-edit";
        }catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String editUser(@RequestParam("id")String id,
                           @RequestParam("login")String login,
                           @RequestParam("first-name")String firstName,
                           @RequestParam("patronymic")String patronymic,
                           @RequestParam("last-name")String lastName,
                           @RequestParam("email")String email,
                           @RequestParam("skype")String skype,
                           @RequestParam("phone")String phone,
                           @RequestParam("birthday")String birthday,
                           @RequestParam("role")String role,
                           @RequestParam("courses[]")String[] courses,
                           @RequestParam(value = "avatar", required = false)MultipartFile avatar,
                           ModelMap model) {
        try {
            Date date = CommonUtil.getFormattedDate(birthday);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "redirect:/admin/users/"+id+"/edit";
    }


    @RequestMapping(value = "/course/list", method = RequestMethod.GET)
    public String coursesList(ModelMap modelMap) {

        Course course = new Course();
        course.setId(1);
        course.setName("PHP");

        Course course1 = new Course();
        course1.setId(2);
        course1.setName("Java for Web");

        Course course2 = new Course();
        course2.setId(3);
        course2.setName("Front-end + CMS");


        List<Course> courses = Arrays.asList(course, course1, course2);
        modelMap.addAttribute("courses", courses);
        return "adminpanel/courses";
    }

    @RequestMapping(value = "/course/create", method = RequestMethod.GET)
    public String createPage(ModelMap model) {
        model.addAttribute("action", "create");
        model.addAttribute("course", new Course());
        return "adminpanel/course-edit";
    }
}
