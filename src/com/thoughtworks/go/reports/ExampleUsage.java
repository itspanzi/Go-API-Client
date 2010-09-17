package com.thoughtworks.go.reports;

import com.thoughtworks.go.http.HttpClientWrapper;
import com.thoughtworks.go.TalkToGo;
import com.thoughtworks.go.domain.Stage;
import com.thoughtworks.go.domain.Job;

import java.util.List;

public class ExampleUsage {

    public static void main(String[] args) {
        HttpClientWrapper wrapper = new HttpClientWrapper("go03.thoughtworks.com", 8153);
        TalkToGo talkToGo = new TalkToGo(wrapper, false);
        Stage stage = talkToGo.latestStageFor("pair02", "build");
        System.out.println("The latest stage of pair02/build has " + stage.getResult());
        System.out.println(String.format("The latest stage of pair02/build is at counter %s inside pipeline with label %s and counter %s", stage.getCounter(), stage.getPipelineLabel(), stage.getPipelineCounter()));
        stage.using(wrapper);
        List<Job> jobs = stage.getJobs();
        System.out.println("jobs = " + jobs);
    }
}