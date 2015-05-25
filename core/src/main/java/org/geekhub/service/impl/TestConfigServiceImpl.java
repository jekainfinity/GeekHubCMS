package org.geekhub.service.impl;

import org.geekhub.hibernate.bean.CourseBean;
import org.geekhub.hibernate.bean.TestConfigBeen;
import org.geekhub.hibernate.dao.CourseDao;
import org.geekhub.hibernate.dao.TestConfigDao;
import org.geekhub.hibernate.entity.Course;
import org.geekhub.hibernate.entity.TestConfig;
import org.geekhub.hibernate.entity.TestStatus;
import org.geekhub.service.CourseService;
import org.geekhub.service.TestConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TestConfigServiceImpl implements TestConfigService {
    @Autowired
    private CourseDao courseDao;
    @Autowired
    private CourseService courseService;
    @Autowired
    private TestConfigDao testConfigDao;

    @Override
    public TestConfigBeen getTestConfigById(int testConfigId) {
        TestConfig testConfig = (TestConfig) testConfigDao.read(testConfigId, TestConfig.class);

        return toBeen(testConfig);
    }

    private TestConfigBeen toBeen(TestConfig testConfig) {
        TestConfigBeen testConfigBeen = new TestConfigBeen(testConfig.getId(),
                testConfig.getTitle(),
                testConfig.getQuestionCount(),
                testConfig.getDateStart(),
                testConfig.getDateFinish(),
                testConfig.getTimeToTest(), testConfig.getStatus());
        return testConfigBeen;
    }

    public List<TestConfigBeen> getTestConfigBeens(int courseId) {
        Course course = (Course) courseDao.read(courseId, Course.class);
        CourseBean courseBean = courseService.toBean(course);
        List<TestConfig> testConfigList = course.getTestConfig();
        List<TestConfigBeen> testConfigBeenList = new ArrayList<>();
        for (TestConfig testConfig : testConfigList) {
            testConfigBeenList.add(new TestConfigBeen(testConfig.getTitle(),
                    testConfig.getQuestionCount(),
                    testConfig.getDateStart(),
                    testConfig.getDateFinish(),
                    testConfig.getTimeToTest(),
                    testConfig.getStatus(),
                    courseBean));
        }
        return testConfigBeenList;
    }

    @Override
    public void createTestConfig(TestConfigBeen testConfigBeen) {
        TestConfig testConfig = new TestConfig();
        testConfig.setTitle(testConfigBeen.getTittle());
        testConfig.setQuestionCount(testConfigBeen.getQuestionCount());
        testConfig.setDateStart(testConfigBeen.getDateStart());
        testConfig.setDateFinish(testConfigBeen.getDateFinish());
        testConfig.setTimeToTest(testConfigBeen.getTimeToTest());
        testConfig.setStatus(testConfigBeen.getStatus());
        Course course = (Course)courseDao.read(testConfigBeen.getCourseBean().getId(),Course.class);
        testConfig.setCourse(course);
        testConfigDao.create(testConfig);
        course.getTestConfig().add(testConfig);
        courseDao.update(course);
    }

    @Override
    public void update(TestConfigBeen testConfigBeen) {
        TestConfig testConfig = (TestConfig) testConfigDao.read(testConfigBeen.getId(), TestConfig.class);
        testConfig.setQuestionCount(testConfigBeen.getQuestionCount());
        testConfig.setTimeToTest(testConfigBeen.getTimeToTest());
        testConfig.setDateStart(testConfigBeen.getDateStart());
        testConfig.setDateFinish(testConfigBeen.getDateFinish());
        testConfig.setTitle(testConfigBeen.getTittle());
        testConfig.setStatus(testConfig.getStatus());
        testConfigDao.update(testConfig);
    }

    @Override
    public void delete(TestConfigBeen testConfigBeen) {
        TestConfig testConfig = (TestConfig)testConfigDao.read(testConfigBeen.getId(), TestConfig.class);
        testConfigDao.delete(testConfig);
    }

    public List<TestConfigBeen> getTestConfigBeensEnable(int courseId) {
        Course course = (Course) courseDao.read(courseId, Course.class);
        CourseBean courseBean = courseService.toBean(course);
        List<TestConfig> testConfigList = course.getTestConfig();
        List<TestConfigBeen> testConfigBeenList = new ArrayList<>();
        for (TestConfig testConfig : testConfigList) {
            if (testConfig.getStatus().equals(TestStatus.ENABLED)) {
                testConfigBeenList.add(new TestConfigBeen(testConfig.getId(),
                        testConfig.getTitle(),
                        testConfig.getQuestionCount(),
                        testConfig.getDateStart(),
                        testConfig.getDateFinish(),
                        testConfig.getTimeToTest(),
                        testConfig.getStatus(),
                        courseBean));
            }
        }
        return testConfigBeenList;
    }
}
