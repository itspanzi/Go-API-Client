package com.thoughtworks.go.visitor.criteria;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import com.thoughtworks.go.domain.FeedEntry;

import java.util.ArrayList;
import java.util.List;

public class MatchingStageCriteriaTest {

    private List<FeedEntry.CardDetail> cardDetails;

    @Before
    public void setUp() throws Exception {
        cardDetails = new ArrayList<FeedEntry.CardDetail>();
    }

    @Test
    public void shouldReturnTrueIfFeedEntryIsForAGivenStage() throws Exception {
        assertThat(new MatchingStageVisitor("stage").shouldVisit(new FeedEntry("pipeline(1) stage stage(1) Failed", "", "id", "", new ArrayList<String>(), cardDetails)), is(true));
        assertThat(new MatchingStageVisitor("stage-1").shouldVisit(new FeedEntry("stage(1) stage stage-1(1) Passed", "", "id", "", new ArrayList<String>(), cardDetails)), is(true));
    }

    @Test
    public void shouldReturnFalseIfFeedEntryIsForNotAGivenStage() throws Exception {
        assertThat(new MatchingStageVisitor("stage").shouldVisit(new FeedEntry("stage(1) stage pipeline(1) Passed", "", "id", "", new ArrayList<String>(), cardDetails)), is(false));
        assertThat(new MatchingStageVisitor("stage-2").shouldVisit(new FeedEntry("stage(1) stage stage-1(1) Cancelled", "", "id", "", new ArrayList<String>(), cardDetails)), is(false));
    }
}
