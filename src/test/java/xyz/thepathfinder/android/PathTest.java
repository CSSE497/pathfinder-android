package xyz.thepathfinder.android;

import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;

public class PathTest {
    @Test
    public void testChildPath() {
        Path path = new Path("/grand/parent/child", ModelType.CLUSTER);
        Assert.assertEquals("/grand/parent/child/baby", path.getChildPath("baby", ModelType.CLUSTER).getPathName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPath(){
        Path path = new Path("", ModelType.CLUSTER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidName() {
        Path path = new Path("/hi/there", ModelType.CLUSTER);
        path.getChildPath("la/de", ModelType.CLUSTER);
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidChildPathType() {
        Path path = new Path("/hi/there", ModelType.COMMODITY);
        path.getChildPath("lade", ModelType.COMMODITY);
    }

    @Test
    public void testGetName() {
        Path path = new Path("/time/dime/mine", ModelType.COMMODITY);
        Assert.assertEquals("mine", path.getName());
    }

    @Test
    public void testGetParentPath() {
        Path path = new Path("/time/dime/mine", ModelType.CLUSTER);

        Path dime = path.getParentPath();
        Assert.assertEquals("/time/dime", dime.getPathName());

        Path time = dime.getParentPath();
        Assert.assertEquals("/time", time.getPathName());

        //Path def = time.getParentPath();
        //Assert.assertNull(def);
    }

    @Test
    public void testPathEquals() {
        Path p1 = new Path("/blah/blob", ModelType.CLUSTER);
        Path p2 = new Path("/blah/blob", ModelType.CLUSTER);
        Path p3 = new Path("/blah/blob", ModelType.COMMODITY);
        Path p4 = new Path("/blah/blob", ModelType.TRANSPORT);
        Path p5 = new Path("/blah/blobs", ModelType.CLUSTER);

        Assert.assertTrue(p1.equals(p2));
        Assert.assertFalse(p1.equals(p3));
        Assert.assertFalse(p1.equals(p4));
        Assert.assertFalse(p1.equals(p5));
    }
}
