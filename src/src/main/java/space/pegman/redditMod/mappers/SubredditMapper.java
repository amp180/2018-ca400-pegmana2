package space.pegman.redditMod.mappers;

import org.apache.ibatis.annotations.*;
import space.pegman.redditMod.domain.Database.Subreddit;

@Mapper
public interface SubredditMapper {

    @Select("SELECT subreddit as sub FROM Subreddits;")
    Subreddit[] getSubreddits();

    @Select("SELECT subreddit as sub FROM Subreddits where sub=#{sub};")
    Subreddit getSubreddit(String sub);

    @Insert("INSERT INTO Subreddits (subreddit) VALUES (#{sub.sub});")
    Long insertSubreddit(@Param("sub") Subreddit sub);

    @Delete("DELETE FROM Subreddits WHERE subreddit=#{sub.sub};")
    Long deleteSubreddit(@Param("sub") Subreddit sub);

}
