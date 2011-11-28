package com.thoughtworks.go.visitor.criteria;

import com.thoughtworks.go.domain.FeedEntry;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MatchingStageVisitorTest {

    @Test
    public void shouldReturnIfAStageShouldBeVisited() {
        MatchingStageVisitor visitor = new MatchingStageVisitor("stage");
        assertThat(visitor.shouldVisit(new FeedEntry("pipeline(9) stage stage(1) Failed", "", "", "", new ArrayList<String>(), new ArrayList<FeedEntry.CardDetail>())), is(true));
        assertThat(visitor.shouldVisit(new FeedEntry("pipeline(9) stage stage_another(1) Failed", "", "", "", new ArrayList<String>(), new ArrayList<FeedEntry.CardDetail>())), is(false));
    }
}
