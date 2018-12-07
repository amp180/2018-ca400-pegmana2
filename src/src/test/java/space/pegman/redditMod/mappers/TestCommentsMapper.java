package space.pegman.redditMod.mappers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import space.pegman.redditMod.domain.Database.Comment;
import space.pegman.redditMod.domain.Database.CommentSampleSet;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCommentsMapper {

    @Autowired
    CommentSampleSetMapper setMapper;

    @Autowired
    CommentsMapper mapper;

    CommentSampleSet commentSet;
    Long setId;

    static Comment comment;
    static Long id;

    @Before
    public void insertComment(){
        commentSet = new CommentSampleSet(0L, "name", false, null);
        setId = setMapper.insertSampleSet(commentSet);

        comment = new Comment(0L, "user", "t1_8294", setId, 5L);
        mapper.insertComment(comment);
        id=comment.getId();
        assert(id!= null);
    }

    @Test
    public void getCommentSampleSetByName(){
        Assert.assertEquals(comment.toString(), mapper.getComment(this.id).toString());
    }

    @Test
    public void updateCommentSampleSet(){
        comment.setUser("u/user2");
        Assert.assertEquals(1L, mapper.updateComment(comment).longValue());
    }

    @After
    public void deleteCommentSampleSet() {
        Assert.assertEquals(1L, mapper.deleteComment(comment).longValue());
        Assert.assertEquals(1L, setMapper.deleteSampleSet(commentSet).longValue());
    }
}
