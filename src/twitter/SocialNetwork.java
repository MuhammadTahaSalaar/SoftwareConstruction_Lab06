/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
	
	public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
	    Map<String, Set<String>> follows_graph = new HashMap<>();
	    
	    // Pattern to match mentions
	    Pattern mention_pattern = Pattern.compile("@(\\w+)", Pattern.CASE_INSENSITIVE);

	    for (Tweet tweet : tweets) {
	        String author = tweet.getAuthor().toLowerCase();
	        Matcher matcher = mention_pattern.matcher(tweet.getText());
	        
	        // Check if there are mentions in the tweet
	        boolean has_mentions = false;

	        while (matcher.find()) {
	            String mentioned_user = matcher.group(1).toLowerCase();
	            // Only add to the followsGraph if the mentioned user is not the author
	            if (!mentioned_user.equals(author)) {
	                follows_graph.putIfAbsent(author, new HashSet<>());
	                follows_graph.get(author).add(mentioned_user);
	                has_mentions = true; // Mark that there are mentions
	            }
	            // Ensure the mentioned user is in the graph
	            follows_graph.putIfAbsent(mentioned_user, new HashSet<>());
	        }

	        // Only add the author to the graph if they mentioned someone
	        if (has_mentions) {
	            follows_graph.putIfAbsent(author, new HashSet<>());
	        }
	    }

	    return follows_graph;
	}

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
	public static List<String> influencers(Map<String, Set<String>> followsGraph) {
	    Map<String, Integer> followerCount = new HashMap<>();

	    // Count followers for each user
	    for (String user : followsGraph.keySet()) {
	        for (String followed : followsGraph.get(user)) {
	            followerCount.put(followed, followerCount.getOrDefault(followed, 0) + 1);
	        }
	    }

	    // Ensure every user is included in the followerCount map
	    for (String user : followsGraph.keySet()) {
	        followerCount.putIfAbsent(user, 0);
	    }

	    // Collect influencers: those with more than 0 followers
	    List<String> influencers = new ArrayList<>();
	    for (Map.Entry<String, Integer> entry : followerCount.entrySet()) {
	        if (entry.getValue() > 0) { // Only include users with at least one follower
	            influencers.add(entry.getKey());
	        }
	    }

	    // Sort influencers by follower count in descending order
	    influencers.sort((a, b) -> followerCount.get(b) - followerCount.get(a));

	    return influencers;
	}

}
