package twitter;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * Testing Strategy:
     *
     * guessFollowsGraph(List<Tweet> tweets):
     * - Partition by:
     *   1. No tweets
     *   2. Single tweet, no mentions
     *   3. Single tweet, with mentions
     *   4. Multiple tweets, mixed mentions
     *   5. Multiple tweets, self-mentions
     *
     * influencers(Map<String, Set<String>> followsGraph):
     * - Partition by:
     *   1. Empty graph
     *   2. Graph with no influencers
     *   3. Graph with one influencer
     *   4. Graph with multiple influencers
     *   5. Graph with multiple users but only one influencer
     */

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // Test for guessFollowsGraph()
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());

        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraphSingleTweetNoMentions() {
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet(1, "user1", "Just a tweet.", null));

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraphSingleTweetWithMentions() {
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet(1, "user1", "Hello @user2!", null));

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("expected graph to contain user1", followsGraph.containsKey("user1"));
        assertTrue("expected user1 to follow user2", followsGraph.get("user1").contains("user2"));
    }

    @Test
    public void testGuessFollowsGraphMultipleTweetsMixedMentions() {
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet(1, "user1", "Hello @user2!", null));
        tweets.add(new Tweet(2, "user2", "Hey @user1, what's up?", null));

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("expected graph to contain user1", followsGraph.containsKey("user1"));
        assertTrue("expected graph to contain user2", followsGraph.containsKey("user2"));
        assertTrue("expected user1 to follow user2", followsGraph.get("user1").contains("user2"));
        assertTrue("expected user2 to follow user1", followsGraph.get("user2").contains("user1"));
    }

    @Test
    public void testGuessFollowsGraphSelfMentions() {
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet(1, "user1", "I am @user1!", null));

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("expected graph to contain user1", followsGraph.containsKey("user1"));
        assertFalse("user1 should not follow themselves", followsGraph.get("user1").contains("user1"));
    }
    
    
    // The following test are for influencers()
    
    @Test
    public void testInfluencersEmptyGraph() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertTrue("expected empty list", influencers.isEmpty());
    }

    @Test
    public void testInfluencersOneInfluencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        Set<String> following = new HashSet<>(Arrays.asList("bbitdiddle"));
        followsGraph.put("alyssa", following);
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        for (int i = 0; i < influencers.size(); i++) {
            influencers.set(i, influencers.get(i).toLowerCase());
        }
        
        assertEquals("expected list of size 1", 1, influencers.size());
    }

    @Test
    public void testInfluencersMultipleInfluencers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        
        Set<String> Following1 = new HashSet<>(Arrays.asList("alyssa"));
        Set<String> Following2 = new HashSet<>(Arrays.asList("bbitdiddle"));
        
        followsGraph.put("alyssa", Following2);
        followsGraph.put("bbitdiddle", Following1);
        followsGraph.put("mike", Following2);
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        for (int i = 0; i < influencers.size(); i++) {
            influencers.set(i, influencers.get(i).toLowerCase());
        }
        
        assertEquals("expected list of size 2", 2, influencers.size());
        assertEquals("expected same order", 0, influencers.indexOf("bbitdiddle"));
        assertEquals("expected same order", 1, influencers.indexOf("alyssa"));
    }

    @Test
    public void testInfluencersMultipleUsersOneInfluencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("user1", Set.of("user2"));
        followsGraph.put("user3", Set.of("user2"));  // user3 follows user2 but has no followers
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals("expected one influencer", 1, influencers.size());
        assertEquals("expected influencer to be user2", "user2", influencers.get(0));
    }
}