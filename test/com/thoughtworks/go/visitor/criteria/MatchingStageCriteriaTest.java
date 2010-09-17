package com.thoughtworks.go.visitor.criteria;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import com.thoughtworks.go.domain.FeedEntry;

public class MatchingStageCriteriaTest {

    @Test
    public void shouldReturnTrueIfFeedEntryIsForAGivenStage() throws Exception {
        assertThat(new MatchingStageVisitor("stage").shouldVisit(new FeedEntry("pipeline/1/stage/1", "", -1L, "")), is(true));
        assertThat(new MatchingStageVisitor("stage-1").shouldVisit(new FeedEntry("stage/1/stage-1/1", "", -1L, "")), is(true));
    }

    @Test
    public void shouldReturnFalseIfFeedEntryIsForNotAGivenStage() throws Exception {
        assertThat(new MatchingStageVisitor("stage").shouldVisit(new FeedEntry("stage/1/pipeline/1", "", -1L, "")), is(false));
        assertThat(new MatchingStageVisitor("stage-2").shouldVisit(new FeedEntry("stage/1/stage-1/1", "", -1L, "")), is(false));
    }
}
