package space.pegman.redditMod.mappers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import space.pegman.redditMod.domain.Database.CommentSampleSet;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCommentSampleSetMapper {

    @Autowired
    CommentSampleSetMapper mapper;

    CommentSampleSet set;
    Long id;

    @Before
    public void insertCommentSampleSet(){
        this.set = new CommentSampleSet(0L, "name", false, null);
        this.id = mapper.insertSampleSet(set);
    }

    @Test
    public void getCommentSampleSetByName(){
        Assert.assertEquals(set, mapper.getCommentSampleSetByName("name"));
    }

    @Test
    public void updateCommentSampleSet(){
        set.setName("cats");
        Assert.assertEquals(1L, mapper.updateSampleSet(set).longValue());
    }

    @After
     public void deleteCommentSampleSet() {
        Assert.assertEquals(1L, mapper.deleteSampleSet(set).longValue());
    }

}
