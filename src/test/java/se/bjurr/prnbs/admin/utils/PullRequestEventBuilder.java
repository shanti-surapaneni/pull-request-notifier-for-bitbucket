package se.bjurr.prnbs.admin.utils;

import static com.atlassian.bitbucket.pull.PullRequestAction.COMMENTED;
import static com.atlassian.bitbucket.pull.PullRequestAction.RESCOPED;
import static java.lang.Boolean.TRUE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.bjurr.prnbs.admin.utils.PullRequestRefBuilder.pullRequestRefBuilder;

import com.atlassian.bitbucket.comment.Comment;
import com.atlassian.bitbucket.event.pull.PullRequestCommentAddedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestEvent;
import com.atlassian.bitbucket.event.pull.PullRequestRescopedEvent;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestAction;
import com.atlassian.bitbucket.pull.PullRequestParticipant;
import com.atlassian.bitbucket.pull.PullRequestState;

public class PullRequestEventBuilder {
 public static final String PREVIOUS_TO_HASH = "previousToHash";
 public static final String PREVIOUS_FROM_HASH = "previousFromHash";
 private PullRequestAction pullRequestAction;
 private PullRequestRefBuilder toRef = pullRequestRefBuilder();
 private PullRequestRefBuilder fromRef = pullRequestRefBuilder();
 private PullRequestParticipant author;
 private String commentText;
 private final PrnfbTestBuilder prnfbTestBuilder;
 private boolean beingClosed;
 private final boolean beingOpen = TRUE;
 private Long pullRequestId = 0L;
 private PullRequestState pullRequestState;

 private PullRequestEventBuilder(PrnfbTestBuilder prnfbTestBuilder) {
  this.prnfbTestBuilder = prnfbTestBuilder;
 }

 public PullRequestEventBuilder withFromRef(PullRequestRefBuilder fromRef) {
  this.fromRef = fromRef;
  return this;
 }

 public PullRequestEventBuilder withToRef(PullRequestRefBuilder toRef) {
  this.toRef = toRef;
  return this;
 }

 public static PullRequestEventBuilder pullRequestEventBuilder() {
  return new PullRequestEventBuilder(null);
 }

 public static PullRequestEventBuilder pullRequestEventBuilder(PrnfbTestBuilder prnfbTestBuilder) {
  return new PullRequestEventBuilder(prnfbTestBuilder);
 }

 public PrnfbTestBuilder getPrnfbTestBuilder() {
  return prnfbTestBuilder;
 }

 public PullRequestRefBuilder withFromRefPullRequestRefBuilder() {
  PullRequestRefBuilder ref = pullRequestRefBuilder(this);
  this.withFromRef(ref);
  return ref;
 }

 public PullRequestRefBuilder withToRefPullRequestRefBuilder() {
  PullRequestRefBuilder ref = pullRequestRefBuilder(this);
  this.withToRef(ref);
  return ref;
 }

 public PullRequestEventBuilder withPullRequestAction(PullRequestAction pullRequestAction) {
  this.pullRequestAction = pullRequestAction;
  return this;
 }

 public PullRequestEventBuilder withAuthor(PullRequestParticipant author) {
  this.author = author;
  return this;
 }

 public PullRequestEventBuilder withCommentText(String commentText) {
  this.commentText = commentText;
  return this;
 }

 public PullRequestEvent build() {
  PullRequestEvent pullRequestEvent = mock(PullRequestEvent.class);
  if (pullRequestAction == RESCOPED) {
   PullRequestRescopedEvent event = mock(PullRequestRescopedEvent.class);
   when(event.getPreviousFromHash()).thenReturn(PREVIOUS_FROM_HASH);
   when(event.getPreviousToHash()).thenReturn(PREVIOUS_TO_HASH);
   pullRequestEvent = event;
  } else if (pullRequestAction == COMMENTED) {
   PullRequestCommentAddedEvent event = mock(PullRequestCommentAddedEvent.class);
   Comment comment = mock(Comment.class);
   when(event.getComment()).thenReturn(comment);
   when(event.getComment().getText()).thenReturn(commentText);
   pullRequestEvent = event;
  }
  final PullRequest pullRequest = mock(PullRequest.class);
  when(pullRequest.isClosed()).thenReturn(beingClosed);
  when(pullRequest.isOpen()).thenReturn(beingOpen);
  when(pullRequest.getId()).thenReturn(pullRequestId);
  when(pullRequest.getState()).thenReturn(pullRequestState);
  when(pullRequestEvent.getAction()).thenReturn(pullRequestAction);
  when(pullRequestEvent.getPullRequest()).thenReturn(pullRequest);
  when(pullRequestEvent.getPullRequest().getAuthor()).thenReturn(author);
  when(pullRequestEvent.getPullRequest().getFromRef()).thenReturn(fromRef);
  when(pullRequestEvent.getPullRequest().getToRef()).thenReturn(toRef);
  return pullRequestEvent;
 }

 public PrnfbTestBuilder triggerEvent() {
  return prnfbTestBuilder.trigger(build());
 }

 public PullRequestEventBuilder beingClosed() {
  beingClosed = true;
  return this;
 }

 public PullRequestEventBuilder withPullRequestId(Long id) {
  this.pullRequestId = id;
  return this;
 }

 public PullRequestEventBuilder withPullRequestInState(PullRequestState pullRequestState) {
  this.pullRequestState = pullRequestState;
  return this;
 }
}
