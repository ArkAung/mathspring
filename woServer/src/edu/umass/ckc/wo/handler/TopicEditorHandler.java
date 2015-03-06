package edu.umass.ckc.wo.handler;

import edu.umass.ckc.wo.beans.ClassInfo;
import edu.umass.ckc.wo.beans.Classes;
import ckc.servlet.servbase.View;
import edu.umass.ckc.wo.content.TopicMgr;
import edu.umass.ckc.wo.event.admin.AdminEditTopicsEvent;
import edu.umass.ckc.wo.event.admin.AdminReorderTopicsEvent;
import edu.umass.ckc.wo.event.admin.AdminTopicControlEvent;
import edu.umass.ckc.wo.beans.Topic;
import edu.umass.ckc.wo.db.DbTopics;
import edu.umass.ckc.wo.db.DbClass;
import edu.umass.ckc.wo.tutconfig.TopicModelParameters;
import edu.umass.ckc.wo.tutconfig.TutorModelParameters;
import edu.umass.ckc.wo.tutor.probSel.PedagogicalModelParameters;
import edu.umass.ckc.wo.tutormeta.frequency;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.io.IOException;

/**
 * <p> Created by IntelliJ IDEA.
 * User: david
 * Date: Jul 17, 2008
 * Time: 9:58:20 AM
 */
public class TopicEditorHandler {

    private String teacherId;
    private HttpSession sess;

    public static final String JSP = "/teacherTools/orderTopics.jsp";
    public static final String SELECT_PEDAGOGIES_JSP = "/teacherTools/selectPedagogies.jsp";
    public static final String CLASS_INFO_JSP = "/teacherTools/classInfo.jsp";

    public TopicEditorHandler () {}






    public View handleEvent(ServletContext sc, Connection conn, AdminEditTopicsEvent e, HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException, ServletException {
        if (e instanceof AdminReorderTopicsEvent) {
            TopicMgr topicMgr = new TopicMgr();
            AdminReorderTopicsEvent ee = (AdminReorderTopicsEvent) e;
            List<Topic> topics=null;
            if (ee.getDirection().equals("up") || ee.getDirection().equals("down"))
                 topics=topicMgr.moveTopic(conn,(AdminReorderTopicsEvent) e);
            else if (ee.getDirection().equals("omit")) {
                topics = topicMgr.omitTopic(conn,(AdminReorderTopicsEvent) e);
            }
            else if (ee.getDirection().equals("reactivate")) {
                topicMgr.reactivateTopic(conn,(AdminReorderTopicsEvent) e);
                topics = DbTopics.getClassActiveTopics(conn,ee.getClassId());
            }
            List<Topic> activetopics = DbTopics.getClassActiveTopics(conn,e.getClassId());
            List<Topic> inactiveTopics = DbTopics.getClassInactiveTopics(conn,ee.getClassId(), activetopics);
            ClassInfo classInfo = DbClass.getClass(conn,e.getClassId());
            ClassInfo[] classes = DbClass.getClasses(conn,e.getTeacherId());
            Classes bean = new Classes(classes);
            req.setAttribute("action","AdminEditTopics");
            req.setAttribute("topics",topics);
            req.setAttribute("inactiveTopics",inactiveTopics);
            req.setAttribute("classId",e.getClassId());
            req.setAttribute("teacherId",e.getTeacherId());
            req.setAttribute("classInfo", classInfo);
            req.setAttribute("bean", bean);
            TutorModelParameters params = DbClass.getTutorModelParameters(conn, e.getClassId());
            DbClass.setProblemSelectorParameters(conn,e.getClassId(), params);
            req.setAttribute("params",params);
            req.getRequestDispatcher(JSP).forward(req,resp);
        }
        else if (e instanceof AdminTopicControlEvent) {
            AdminTopicControlEvent ee = (AdminTopicControlEvent) e;
            List<Topic> topics = DbTopics.getClassActiveTopics(conn,e.getClassId());
            List<Topic> inactiveTopics = DbTopics.getClassInactiveTopics(conn,ee.getClassId(), topics);
            ClassInfo classInfo = DbClass.getClass(conn,e.getClassId());
            ClassInfo[] classes = DbClass.getClasses(conn,e.getTeacherId());
            Classes bean = new Classes(classes);
            req.setAttribute("action","AdminEditTopics");
            req.setAttribute("topics",topics);
            req.setAttribute("classId",e.getClassId());
            req.setAttribute("teacherId",e.getTeacherId());
            req.setAttribute("inactiveTopics",inactiveTopics);
            req.setAttribute("classInfo",classInfo);
            req.setAttribute("bean", bean);
            // send to contructor but time in topic is in incorrect units
            PedagogicalModelParameters params = new PedagogicalModelParameters(ee.getDifficultyRate(), ee.getExternalActivityTimeThreshold());
            TopicModelParameters topicModelParameters = new TopicModelParameters(frequency.always,frequency.always,ee.getMaxTimeInTopic(),
                    ee.getMinTimeInTopic(),ee.getMaxNumProbsPerTopic(),ee.getMinNumProbsPerTopic(),ee.getTopicMastery(),ee.getContentFailureThreshold());

            DbClass.setProblemSelectorParameters(conn, e.getClassId(), params);
            req.setAttribute("params", params);
            req.setAttribute("topicModelParams",topicModelParameters);
            req.getRequestDispatcher(JSP).forward(req,resp);
        }
        else {
            // fetch a list of topics for the class sorted in the order they will be presented
            List<Topic> topics = DbTopics.getClassActiveTopics(conn,e.getClassId());
            List<Topic> inactiveTopics = DbTopics.getClassInactiveTopics(conn,e.getClassId(), topics);
            ClassInfo[] classes = DbClass.getClasses(conn,e.getTeacherId());
            Classes bean = new Classes(classes);
            ClassInfo classInfo = DbClass.getClass(conn,e.getClassId());
            // forward to the JSP page that allows reordering the list and omitting topics.
            req.setAttribute("action","AdminEditTopics");
            req.setAttribute("bean",bean);
            req.setAttribute("topics",topics);
            req.setAttribute("inactiveTopics",inactiveTopics);
            req.setAttribute("teacherId",e.getTeacherId());
            req.setAttribute("classId",e.getClassId());
            req.setAttribute("classInfo",classInfo);
            TutorModelParameters params = DbClass.getTutorModelParameters(conn, e.getClassId());
            // If parameters are not stored for this particular class, a default set should be stored
            // in classconfig table for classId=1.   If nothing there, then use the defaults created
            // in the default PedagogicalModelParameters constructor
            if (params == null) {
                params = DbClass.getTutorModelParameters(conn, 1);
                if (params == null)
                    params = new PedagogicalModelParameters();
            }
            req.setAttribute("params",params);
            req.getRequestDispatcher(JSP).forward(req,resp);
        }

        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
