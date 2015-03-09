package edu.umass.ckc.wo.tutor.probSel;

import edu.umass.ckc.wo.cache.ProblemMgr;
import edu.umass.ckc.wo.content.Problem;
import edu.umass.ckc.wo.event.tutorhut.NextProblemEvent;
import edu.umass.ckc.wo.smgr.SessionManager;
import edu.umass.ckc.wo.smgr.StudentState;
import edu.umass.ckc.wo.tutconfig.TutorModelParameters;
import edu.umass.ckc.wo.tutor.pedModel.ProblemGrader;
import edu.umass.ckc.wo.tutor.pedModel.TopicSelectorImpl;
import edu.umass.ckc.wo.tutormeta.ProblemSelector;
import edu.umass.ckc.wo.tutormeta.TopicSelector;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: marshall
 * Date: 11/26/12
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReviewModeProblemSelector  implements ProblemSelector {

    public static final String END_PAGE = "reviewDone.html";
    private static Logger logger = Logger.getLogger(ReviewModeProblemSelector.class);

    TutorModelParameters parameters;
    TopicSelector topicSelector;
    private SessionManager smgr;

    public ReviewModeProblemSelector(SessionManager smgr, TopicSelectorImpl topicSelector, TutorModelParameters params) {
        this.smgr = smgr;
        this.topicSelector = topicSelector;
        this.parameters=params;
    }


    @Override
    public void init(SessionManager smgr) throws Exception {

    }

    @Override
    public void setParameters(TutorModelParameters params) {
        this.parameters = params;
    }

    @Override
    public Problem selectProblem(SessionManager smgr, NextProblemEvent e, ProblemGrader.difficulty nextProblemDesiredDifficulty) throws Exception {
        StudentState state = smgr.getStudentState();
        List<Integer> topicProbIds = topicSelector.getClassTopicProblems(state.getCurTopic(), smgr.getClassID(), smgr.isTestUser());
//        List<Problem> topicProblems = xx;
        int nextIx = state.getCurProblemIndexInTopic();
        nextIx++;
        state.setCurProblemIndexInTopic(nextIx);
        int nextProbId = topicProbIds.get(nextIx);
        Problem p = ProblemMgr.getProblem(nextProbId);
        p.setMode(Problem.PRACTICE);
        return p;
    }




}
