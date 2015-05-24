package org.geekhub.controllers;

import org.geekhub.hibernate.bean.CourseBean;
import org.geekhub.hibernate.bean.Page;
import org.geekhub.hibernate.bean.QuestionBean;
import org.geekhub.hibernate.bean.TestConfigBeen;
import org.geekhub.hibernate.entity.Course;
import org.geekhub.hibernate.entity.Question;
import org.geekhub.hibernate.entity.TestStatus;
import org.geekhub.hibernate.entity.User;
import org.geekhub.hibernate.exceptions.CourseNotFoundException;
import org.geekhub.service.*;
import org.geekhub.util.CommonUtil;
import org.geekhub.wrapper.UserTestResultWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private CourseService courseService;
    @Autowired
    private TestConfigService testConfigService;

    @Autowired
    private UserService userService;

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
        u.setLastName("Test");


        u.setPassword("1234512");
        u.setRegistrationDate(new Date());
        u.setPhoneNumber("+380(93)145-1514");
        for (int i = 0; i < 5; i++) users.add(u);
        model.addAttribute("users", users);
        return "adminpanel/users";
    }


    @RequestMapping(value = "/users/{userId}/edit", method = RequestMethod.GET)
    public String getEditUserPage(@PathVariable("userId") Integer userId, ModelMap model) throws Exception {
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
            u.setLastName("Test");


            u.setPassword("1234512");
            u.setRegistrationDate(new Date());
            //  u.setCourses(courses);
            u.setPhoneNumber("931451514");

            model.addAttribute("courseList", courses);
            model.addAttribute("user", u);
            return "adminpanel/user-edit";
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String editUser(@RequestParam("id") String id,
                           @RequestParam("first-name") String firstName,
                           @RequestParam("last-name") String lastName,
                           @RequestParam("email") String email,
                           @RequestParam("skype") String skype,
                           @RequestParam("phone") String phone,
                           @RequestParam("birthday") String birthday,
                           @RequestParam("role") String role,
                           @RequestParam("courses[]") String[] courses,
                           @RequestParam(value = "avatar", required = false) MultipartFile avatar,
                           ModelMap model) {
        try {
            Date date = CommonUtil.getFormattedDate(birthday);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "redirect:/admin/users/" + id + "/edit";
    }


    @RequestMapping(value = "/course/list", method = RequestMethod.GET)
    public String coursesList(@RequestParam(value = "p", required = true, defaultValue = "1") Integer p,
                              @RequestParam(value = "results", defaultValue = "5", required = false) Integer recPerPage,
                              ModelMap modelMap) {

        Page<CourseBean> page = courseService.getAll(p, recPerPage);
        modelMap.addAttribute("page", page);
        return "adminpanel/courses";
    }

    @RequestMapping(value = "/course/create", method = RequestMethod.GET)
    public ModelAndView createPage() throws CourseNotFoundException {
        ModelAndView model = new ModelAndView("adminpanel/course-create");
        model.addObject("enumStatus", TestStatus.values());
        model.addObject("course", new Course());
        return model;
    }

    @RequestMapping(value = "/course/{courseId}/edit", method = RequestMethod.GET)
    public ModelAndView editCourses(@PathVariable("courseId") Integer courseId) throws Exception {
        ModelAndView model = new ModelAndView("adminpanel/course-edit");
        try {

            CourseBean course = courseService.getById(courseId);
            model.addObject("course", course);
            model.addObject("enumStatus", TestStatus.values());
            return model;
        } catch (CourseNotFoundException ex) {

        } catch (Exception ex) {
            throw new Exception(ex);
        }
        return model;
    }

    @RequestMapping(value = "/course/{courseId}", method = RequestMethod.POST)
    public String editCourses(@PathVariable("courseId") Integer courseId,
                              @RequestParam("name") String name,
                              @RequestParam("description") String description) throws Exception {

        CourseBean courseBean = new CourseBean(courseId, name, description);
        courseService.update(courseBean);
        return "redirect:/admin/course/" + courseId + "/edit";
    }

    @RequestMapping(value = "/course", method = RequestMethod.POST)
    public String createCourse(@RequestParam("name") String name,
                               @RequestParam("description") String description,
                               @RequestParam("title") String title,
                               @RequestParam("questionCount") int questionCount,
                               @RequestParam("dateStart") String dateStart,
                               @RequestParam("dateFinish") String dateFinish,
                               @RequestParam("timeToTest") int timeToTest,
                                   @RequestParam("status") TestStatus status) throws Exception {


        try {
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd");
            Date startDate = new Date();
            if (!startDate.equals("")) {
                startDate = dt.parse(dateStart);
            }
            Date finishDate = new Date();
            if (!finishDate.equals("")) {
                finishDate = dt.parse(dateFinish);
            }

            CourseBean courseBean = new CourseBean(name, description);
            TestConfigBeen testConfigBeen = new TestConfigBeen(title, questionCount,startDate,finishDate,timeToTest, status,courseBean);
            courseBean.getTestConfigListBeens().add(testConfigBeen);
            courseService.create(courseBean, testConfigBeen);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        return "redirect:/admin/course/list";
    }

    @RequestMapping(value = "/course-remove/{courseId}", method = RequestMethod.GET)
    public String createCourse(@PathVariable("courseId") Integer courseId) throws Exception {

        try {
            courseService.delete(courseId);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        return "redirect:/admin/course/list";
    }


    @RequestMapping(value = "/userTestResult", method = RequestMethod.GET)
    public String getUserTestResult(Map<String, Object> model) throws Exception {
        model.put("coursesList", courseService.getAllBeans());
        return "adminpanel/userTestResult";
    }

    @RequestMapping(value = "/userTestResult/{course}", method = RequestMethod.GET)
    public String getUserTestResultWithCourse(@RequestParam(value = "p", required = true, defaultValue = "1") Integer p,
                                              @RequestParam(value = "results", defaultValue = "4", required = false) Integer recPerPage,
                                              @PathVariable String course, Map<String, Object> model) throws Exception {
        model.put("coursesList", courseService.getAllBeans());
        model.put("courseName", course);
        Page<UserTestResultWrapper> page = userService.getPageUserTestResultWrapperListByCourseName(course, p, recPerPage);
        model.put("page", page);
        return "adminpanel/userTestResult";
    }

    // START QUESTION CONTROLLER
    @RequestMapping(value = "/questions", method = RequestMethod.GET)
    public String questions(ModelMap model) {
        List<Question> list = questionService.getAll();
        model.addAttribute("questions", list);
        List<CourseBean> listCourse = courseService.getAllBeans();
        model.addAttribute("courses", listCourse);
        return "adminpanel/questions";
    }

    //////////////////////////////////////////////////////////////////
//    @RequestMapping(value = "/questions/{courseId}", method = RequestMethod.GET)
//    public String questionsByCourse(@PathVariable("courseId") int courseId,ModelMap model) throws CourseNotFoundException {
//        CourseBean courseBean = courseService.getById(courseId);
//        List<Question> list = questionService.getByCourse(courseBean);
////        model.addAttribute("questions", list);
//
//        List<CourseBean> listCourse = courseService.getAllBeans();
//        model.addAttribute("courses", listCourse);
//        return "adminpanel/questions";
//}
///////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/question/create", method = RequestMethod.GET)
    public String createQuestionPage(ModelMap model) {
        model.addAttribute("action", "create");
        model.addAttribute("question", new Question());
        return "adminpanel/question-edit";
    }

    //////////////////////////////////////////////////////////////
    @RequestMapping(value = "/course/{courseId}/question/create", method = RequestMethod.GET)
    public String createQuestionPageByCourse(@PathVariable("courseId") int courseId, ModelMap model) {
        model.addAttribute("action", "create");
        model.addAttribute("question", new Question());
        model.addAttribute("courseId", courseId);
        try {
            model.addAttribute("courseName", courseService.getById(courseId).getName());
        } catch (CourseNotFoundException ex) {
        }
        return "adminpanel/question-edit";
    }

    ////////////////////////////////////////////////////////////
    @RequestMapping(value = "/course/{courseId}/question/create", method = RequestMethod.POST)
    public String createQuestion(@RequestParam("questionText") String questionText,
                                 @RequestParam("questionWeight") byte questionWeight,
                                 @RequestParam("questionStatus") boolean questionStatus,
                                 @RequestParam("myAnswer") boolean myAnswer,
                                 @PathVariable("courseId") int courseId) {
        QuestionBean questionBean = new QuestionBean(questionText, questionWeight, questionStatus, myAnswer, courseId);
        int questionId = questionService.create(questionBean);
        System.out.println("Question text " + questionText + "   Question Weight " + questionWeight);
        return "redirect:/admin/course/" + courseId + "/question/" + questionId + "/edit";
    }



    @RequestMapping(value = "/course/{courseId}/question/{questionId}/edit", method = RequestMethod.GET)
    public String editQuestion(@PathVariable("questionId") int questionId,
                               @PathVariable("courseId") int courseId,
                               ModelMap model) {
        model.addAttribute("question", questionService.read(questionId));
        model.addAttribute("answers", answerService.getAnswersByQuestion(questionId));
        model.addAttribute("courseId", courseId);
        return "adminpanel/question-edit";
    }

    @RequestMapping(value = "/course/{courseId}/question/{questionId}/delete", method = RequestMethod.GET)
    public String deleteQuestion(@PathVariable("questionId") int questionId) {
        questionService.delete(questionId);
        return "redirect:/admin/questions";
    }

    @RequestMapping(value = "/course/{courseId}/question/{questionId}/edit", method = RequestMethod.POST)
    public String editQuestion(@RequestParam("questionText") String questionText,
                               @RequestParam("questionWeight") byte questionWeight,
                               @RequestParam("questionStatus") boolean questionStatus,
                               @RequestParam("myAnswer") boolean myAnswer,
                               @PathVariable("courseId") int courseId) {
        QuestionBean questionBean = new QuestionBean(courseId, questionText, questionWeight, questionStatus, myAnswer, courseId);
        questionService.update(questionBean);
        return "redirect:/admin/course/" + questionBean.getCourse() + "/question/" + questionBean.getId() + "/edit";
    }

    // END QUESTION CONTROLLER

    //START ANSWER CONTROLLER
    @RequestMapping(value = "/course/{courseId}/question/{questionId}/answer/{answerId}", method = RequestMethod.POST)
    public String editAnswer(@PathVariable("questionId") int questionId,
                             @RequestParam("answerId") String answerId,
                             @PathVariable("courseId") int courseId,
                             @RequestParam("answerText") String answerText,
                             @RequestParam("answerRight") String answerRight) {

      //  answerService.update()
        return "redirect:/admin/course/" + courseId + "/question/" + questionId + "/answer/" + answerId + "/edit";
    }

    @RequestMapping(value = "/course/{courseId}/question/{questionId}/answer/{answerId}/edit", method = RequestMethod.GET)
    public String editAnswer(@PathVariable("questionId") int questionId,
                             @PathVariable("courseId") int courseId,
                             @PathVariable("answerId") int answerId,
                             ModelMap model) {
        model.addAttribute("question", questionService.read(questionId));
        model.addAttribute("answers", answerService.getAnswersByQuestion(questionId));
        model.addAttribute("answerSelect", answerService.read(answerId));
        return "adminpanel/answer-edit";
    }

    @RequestMapping(value = "/course/{courseId}/question/{questionId}/answer/create", method = RequestMethod.POST)
    public String createAnswer(@PathVariable("questionId") int questionId,
                               @PathVariable("courseId") int courseId,
                               @RequestParam("answerText") String answerText,
                               @RequestParam("answerRight") boolean answerRight) {

        answerService.create(questionId, answerText, answerRight);
        return "redirect:/admin/course/" + courseId + "/question/" + questionId + "/edit";
    }

    @RequestMapping(value = "/course/{courseId}/question/{questionId}/answer/{answerId}/delete", method = RequestMethod.GET)
    public String deleteAnswer(@PathVariable("questionId") int questionId,
                               @PathVariable("courseId") int courseId,
                               @PathVariable("answerId") int answerId,
                               ModelMap model) {
        answerService.delete(answerId);
        model.addAttribute("question", questionService.read(questionId));
        return "redirect:/admin/course/"+ courseId + "/question/" + questionId + "/edit";
    }

    // END ANSWER CONTROLLER


    @RequestMapping(value = "/testConfig/{courseId}/create", method = RequestMethod.GET)
    public ModelAndView createTestConfig (@PathVariable int courseId) {
        ModelAndView model = new ModelAndView("adminpanel/testConfig-create");
        model.addObject("enumStatus", TestStatus.values());
        model.addObject("courseId", courseId);
        return model;
    }

    @RequestMapping(value = "/testConfig/{courseId}/create", method = RequestMethod.POST)
    public ModelAndView createTestConfig (@PathVariable int courseId,
                                          @RequestParam("title") String title,
                                          @RequestParam("questionCount") int questionCount,
                                          @RequestParam("dateStart") String dateStart,
                                          @RequestParam("dateFinish") String dateFinish,
                                          @RequestParam("timeToTest") int timeToTest,
                                          @RequestParam("status") TestStatus status) throws Exception {
        ModelAndView model = new ModelAndView("redirect:/admin/course/" + courseId + "/edit");
        try {
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd");
            Date startDate = new Date();
            if (!startDate.equals("")) {
                startDate = dt.parse(dateStart);
            }
            Date finishDate = new Date();
            if (!finishDate.equals("")) {
                finishDate = dt.parse(dateFinish);
                CourseBean courseBean = courseService.getById(courseId);
                TestConfigBeen testConfigBeen = new TestConfigBeen(title, questionCount, startDate, finishDate, timeToTest, status,courseBean);
                testConfigService.createTestConfig(testConfigBeen);
            }
        } catch (CourseNotFoundException ex) {

        } catch (Exception ex) {
            throw new Exception(ex);
        }
        return model;
    }

    @RequestMapping(value = "/testConfig/{courseId}/{testConfigId}/edit", method = RequestMethod.GET)
    public ModelAndView editTestConfig(@PathVariable int testConfigId,
                                       @PathVariable int courseId) {
        ModelAndView model = new ModelAndView("adminpanel/testConfig-edit");
        model.addObject("courseId",courseId);
        TestConfigBeen testConfigBeen = testConfigService.getTestConfigById(testConfigId);
        model.addObject("testConfigBeen", testConfigBeen);
        model.addObject("enumStatus", TestStatus.values());
        return model;
    }

    @RequestMapping(value ="/testConfig/{courseId}/{testConfigId}/delete")
    public ModelAndView deleteTestConfig(@PathVariable int testConfigId,
                                         @PathVariable int courseId) {
        ModelAndView model = new ModelAndView("redirect:/admin/course/" + courseId + "/edit");
        TestConfigBeen testConfigBeen = (TestConfigBeen) testConfigService.getTestConfigById(testConfigId);
        testConfigService.delete(testConfigBeen);
        return model;
    }
    @RequestMapping(value = "/testConfig/{courseId}/{testConfigId}/edit", method = RequestMethod.POST)
    public ModelAndView editTestConfig (@PathVariable int testConfigId,
                                        @PathVariable int courseId,
                                        @RequestParam("title") String title,
                                        @RequestParam("questionCount") int questionCount,
                                        @RequestParam("dateStart") String dateStart,
                                        @RequestParam("dateFinish") String dateFinish,
                                        @RequestParam("timeToTest") int timeToTest,
                                        @RequestParam("status") TestStatus status) {

        ModelAndView model = new ModelAndView("redirect:/admin/course/" + courseId + "/edit");
        Date startDate = new Date();
        Date finishDate = new Date();
        try {
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd");
            if (!startDate.equals("")) {
                startDate = dt.parse(dateStart);
            }
            if (!finishDate.equals("")) {
                finishDate = dt.parse(dateFinish);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TestConfigBeen testConfigBeen = new TestConfigBeen(testConfigId,title, questionCount,startDate,finishDate,timeToTest, status);
        testConfigService.update(testConfigBeen);
        return model;
    }

    @RequestMapping(value = "/createFeedback/{userid}", method = RequestMethod.GET)
    public String createFeedback(@PathVariable("userid")Integer userid, HttpServletRequest request){
        String feedback = request.getParameter("feedback");
        userService.setFeedback(userid,feedback);
        return "redirect:/admin/users/" + userid + "/edit";
    }

}
