package com.thoughtworks.go.reports;

import com.thoughtworks.go.*;

import java.util.*;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public class AgentUtilization {

    public static void main(String[] args) {
        HttpClientWrapper wrapper = new HttpClientWrapper("go03.thoughtworks.com", 8153);
        TalkToGo talkToGo = new TalkToGo(wrapper);
        final Map<String, List<Job>> agentToJobs = new HashMap<String, List<Job>>();
        talkToGo.visitAllStages(new StageVisitor() {
            public void visitStage(Stage stage) {
                for (Job job : stage.getJobs()) {
                    String uuid = job.getUUID();
                    List<Job> jobs = agentToJobs.get(uuid);
                    if (jobs == null) {
                        agentToJobs.put(uuid, jobs = new ArrayList<Job>());
                    }
                    jobs.add(job);
                }
            }

            public void visitPipeline(Pipeline pipeline) {
            }
        });
        for (String uuid : agentToJobs.keySet()) {
            for (Job job : agentToJobs.get(uuid)) {
                double time = job.timeSpentOnAgent();
            }
        }
    }
}
